package net.aoba.altmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;

import org.apache.commons.io.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.aoba.interfaces.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Session.AccountType;

public class AltManager {
	private File altFiles;
	private MinecraftClient mc;
	private ArrayList<Alt> alts = new ArrayList<Alt>();
	private String encryptKey = "B&E)H@McQeThWmZq";
	private static final String apiURL = "https://auth.mcleaks.net/v1/";

	public String MCLeaksSession;

	// Big thanks to Wurst for the URLs and REGEX. What are you doing Microsoft?
	private final String CLIENT_ID = "00000000402b5328";
	private final String SCOPE_ENCODED = "service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL";
	private final String SCOPE_UNENCODED = "service::user.auth.xboxlive.com::MBI_SSL";
	private final String REDIRECT_URI_ENCODED = "https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
	private final URL LOGIN_URL = createURL("https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID
			+ "&response_type=code&scope=" + SCOPE_ENCODED + "&redirect_uri=" + REDIRECT_URI_ENCODED);
	private final URL AUTH_TOKEN_URL = createURL("https://login.live.com/oauth20_token.srf");
	private final URL XBL_TOKEN_URL = createURL("https://user.auth.xboxlive.com/user/authenticate");
	private final URL XSTS_TOKEN_URL = createURL("https://xsts.auth.xboxlive.com/xsts/authorize");
	private final URL MC_TOKEN_URL = createURL("https://api.minecraftservices.com/authentication/login_with_xbox");
	private final URL PROFILE_URL = createURL("https://api.minecraftservices.com/minecraft/profile");
	private final Pattern PPFT_REGEX = Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>");
	private final Pattern URLPOST_REGEX = Pattern.compile("urlPost:[ ]?'(.+?(?='))");
	private final Pattern AUTHCODE_REGEX = Pattern.compile("[?|&]code=([\\w.-]+)");

	public AltManager() {
		mc = MinecraftClient.getInstance();
		altFiles = new File(mc.runDirectory, "aoba_alts.txt");
		readAlts();
	}

	public void readAlts() {
		try {
			if (!this.altFiles.exists()) throw new IOException("File not found! Could not load alts...");
			
			List<String> list = IOUtils.readLines(new FileInputStream(this.altFiles), StandardCharsets.UTF_8);
			for (String s : list) {
				String str = decrypt(s);
				String[] alt = str.split(":");
				try {
					Alt newAlt = new Alt(alt[0], alt[1], alt[2], Boolean.parseBoolean(alt[3]));
					alts.add(newAlt);
				} catch (Exception e) {
					System.out.println("Skipping bad option: " + alt[0]);
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void saveAlts() {
		PrintWriter printwriter = null;
		try {
			System.out.println("[Aoba] Saving Alts");
			printwriter = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(this.altFiles), StandardCharsets.UTF_8));
			for (Alt alt : alts) {
				String str = alt.getEmail() + ":" + alt.getPassword() + ":" + alt.getUsername() + ":"
						+ alt.isMicrosoft();
				printwriter.println(encrypt(str));
			}
		} catch (Exception exception) {
			System.out.println("[Aoba] Failed to save alts");
		}
		printwriter.close();
	}

	public String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strToEncrypt;
	}

	public String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strToDecrypt;
	}

	public void addAlt(Alt alt) {
		alts.add(alt);
	}

	public void removeAlt(Alt alt) {
		alts.remove(alt);
	}

	public ArrayList<Alt> getAlts() {
		return this.alts;
	}

	public boolean login(Alt alt) {
		if (alt.isMicrosoft()) {
			return loginMicrosoft(alt);
		} else {
			return loginMinecraft(alt);
		}
	}

	public boolean loginMicrosoft(Alt alt) {
		try {
			IMinecraftClient iMC = (IMinecraftClient) this.mc;

			UUID uuid;
			String name;

			String authCode = getAuthCode(alt);
			String msftAccessToken = getMicrosoftAccessToken(authCode);
			XboxLiveToken xblToken = getXBLToken(msftAccessToken);
			String xstsToken = getXSTSToken(xblToken.getToken());

			String mcAccessToken = getMinecraftAccessToken(xblToken.getUHS(), xstsToken);

			URLConnection connection = PROFILE_URL.openConnection();
			connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);

			JsonObject json = parseConnectionToObject(connection);

			if (json.has("error"))
				throw new LoginException("Error message from api.minecraftservices.com:\n" + json.get("error"));

			uuid = uuidFromJson(json.get("id").getAsString());
			name = json.get("name").getAsString();

			Session session = new Session(name, uuid.toString(), mcAccessToken, Optional.empty(), Optional.empty(),
					Session.AccountType.MOJANG);
			iMC.setSession(session);
			System.out.println("Logged in as " + alt.getUsername());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getAuthCode(Alt alt) throws LoginException {
		String cookie;
		String loginWebpage;
		try {
			URLConnection connection = LOGIN_URL.openConnection();
			cookie = connection.getHeaderField("set-cookie");
			try (InputStream input = connection.getInputStream()) {
				InputStreamReader reader = new InputStreamReader(input);
				BufferedReader bufferedReader = new BufferedReader(reader);
				loginWebpage =  bufferedReader.lines().collect(Collectors.joining("\n"));
			}
		} catch (Exception e) {
			throw new LoginException("Failed login");
		}

		Matcher matcher = PPFT_REGEX.matcher(loginWebpage);
		if (!matcher.find())
			throw new LoginException("sFTTag / PPFT regex failed.");

		String ppft = matcher.group(1);

		matcher = URLPOST_REGEX.matcher(loginWebpage);
		if (!matcher.find())
			throw new LoginException("urlPost regex failed.");

		String urlPost = matcher.group(1);

		return microsoftLogin(alt, cookie, ppft, urlPost);
	}

	private String microsoftLogin(Alt alt, String cookie, String ppft, String urlPost) throws LoginException {
		Map<String, String> postData = new HashMap<>();
		postData.put("login", alt.getEmail());
		postData.put("loginfmt", alt.getEmail());
		postData.put("passwd", alt.getPassword());
		postData.put("PPFT", ppft);

		byte[] encodedDataBytes = urlEncodeMap(postData).getBytes(StandardCharsets.UTF_8);

		try {
			URL url = new URL(urlPost);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			connection.setRequestProperty("Content-Length", "" + encodedDataBytes.length);
			connection.setRequestProperty("Cookie", cookie);

			connection.setDoInput(true);
			connection.setDoOutput(true);

			try (OutputStream out = connection.getOutputStream()) {
				out.write(encodedDataBytes);
			}

			int responseCode = connection.getResponseCode();
			if (responseCode >= 500 && responseCode <= 599)
				throw new LoginException("Servers are down (code " + responseCode + ").");

			if (responseCode != 200)
				throw new LoginException("Got code " + responseCode + " from urlPost.");

			String decodedUrl = URLDecoder.decode(connection.getURL().toString(), StandardCharsets.UTF_8.name());

			Matcher matcher = AUTHCODE_REGEX.matcher(decodedUrl);
			if (!matcher.find())
				throw new LoginException("Didn't get authCode. (Wrong email/password?)");

			return matcher.group(1);

		} catch (IOException e) {
			throw new LoginException("Connection failed: " + e);
		}
	}

	public boolean loginMinecraft(Alt alt) {
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
				Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(alt.getEmail());
		auth.setPassword(alt.getPassword());

		try {
			auth.logIn();
			IMinecraftClient iMC = (IMinecraftClient) this.mc;
			iMC.setSession(
					new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
							auth.getAuthenticatedToken(), Optional.empty(), Optional.empty(), AccountType.MOJANG));
			alt.setUsername(mc.getSession().getUsername());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loginCracked(String alt) {
		try {
			IMinecraftClient iMC = (IMinecraftClient) this.mc;
			iMC.setSession(new Session(alt, "", "", Optional.empty(), Optional.empty(), AccountType.MOJANG));
			System.out.println("Logged in as " + alt);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void loginMCLeaks(final String token)
			throws APIDownException, APIErrorException, InvalidResponseException, InvalidTokenException {
		if (token == null || token.length() != 16) {
			throw new InvalidTokenException();
		}
		JsonObject requestObject = new JsonObject();
		requestObject.add("token", new GsonBuilder().create().toJsonTree(token));
		String response;
		try {
			response = postJson(apiURL + "redeem", requestObject.toString());
		} catch (Exception e) {
			throw new APIDownException();
		}
		JsonObject responseObject;
		try {
			responseObject = JsonParser.parseString(response).getAsJsonObject();
		} catch (Exception e) {
			throw new InvalidResponseException(response);
		}
		if (!responseObject.get("success").getAsBoolean()) {
			throw new APIErrorException(responseObject.get("errorMessage").getAsString());
		}
		JsonObject resultObject = responseObject.get("result").getAsJsonObject();

		MCLeaksSession = resultObject.get("session").getAsString();
	}

	public void joinServer(Session session, final String server, final String serverHash)
			throws APIDownException, InvalidResponseException, APIErrorException {
		Gson gson = new GsonBuilder().create();
		JsonObject requestObject = new JsonObject();

		requestObject.add("session", gson.toJsonTree(MCLeaksSession));
		requestObject.add("mcname", gson.toJsonTree(session.getUsername()));
		requestObject.add("serverhash", gson.toJsonTree(serverHash));
		requestObject.add("server", gson.toJsonTree(server));
		String response;
		try {
			response = postJson(apiURL + "joinserver", requestObject.toString());
		} catch (Exception e) {
			throw new APIDownException();
		}
		JsonObject responseObject;
		try {
			responseObject = JsonParser.parseString(response).getAsJsonObject();
		} catch (Exception e) {
			throw new InvalidResponseException(response);
		}
		if (!responseObject.get("success").getAsBoolean()) {
			throw new APIErrorException(responseObject.get("errorMessage").getAsString());
		}
	}

	private XboxLiveToken getXBLToken(String msftAccessToken) throws LoginException {
		JsonObject properties = new JsonObject();
		properties.addProperty("AuthMethod", "RPS");
		properties.addProperty("SiteName", "user.auth.xboxlive.com");
		properties.addProperty("RpsTicket", msftAccessToken);

		JsonObject postData = new JsonObject();
		postData.addProperty("RelyingParty", "http://auth.xboxlive.com");
		postData.addProperty("TokenType", "JWT");
		postData.add("Properties", properties);

		String request = postData.toString();

		try {
			URLConnection connection = XBL_TOKEN_URL.openConnection();

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);

			try (OutputStream out = connection.getOutputStream()) {
				out.write(request.getBytes(StandardCharsets.US_ASCII));
			}

			JsonObject json = parseConnectionToObject(connection);

			String token = json.get("Token").getAsString();
			String uhs = json.get("DisplayClaims").getAsJsonObject().getAsJsonArray("xui").get(0).getAsJsonObject()
					.get("uhs").getAsString();

			return new XboxLiveToken(token, uhs);

		} catch (IOException e) {
			e.printStackTrace();
			throw new LoginException("Connection failed: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoginException("Server sent invalid JSON.");
		}
	}

	private String getXSTSToken(String xblToken) throws LoginException {
		JsonArray tokens = new JsonArray();
		tokens.add(xblToken);

		JsonObject properties = new JsonObject();
		properties.addProperty("SandboxId", "RETAIL");
		properties.add("UserTokens", tokens);

		JsonObject postData = new JsonObject();
		postData.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
		postData.addProperty("TokenType", "JWT");
		postData.add("Properties", properties);

		String request = postData.toString();

		try {
			URLConnection connection = XSTS_TOKEN_URL.openConnection();

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			connection.setDoOutput(true);

			try (OutputStream out = connection.getOutputStream()) {
				out.write(request.getBytes(StandardCharsets.US_ASCII));
			}

			JsonObject json = parseConnectionToObject(connection);
			return json.get("Token").getAsString();

		} catch (IOException e) {
			e.printStackTrace();
			throw new LoginException("Connection failed: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoginException("Server sent invalid JSON.");
		}
	}

	private String getMinecraftAccessToken(String uhs, String xstsToken) throws LoginException {
		JsonObject postData = new JsonObject();
		postData.addProperty("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);

		String request = postData.toString();

		try {
			URLConnection connection = MC_TOKEN_URL.openConnection();

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			connection.setDoOutput(true);

			try (OutputStream out = connection.getOutputStream()) {
				out.write(request.getBytes(StandardCharsets.US_ASCII));
			}

			JsonObject json = parseConnectionToObject(connection);
			return json.get("access_token").getAsString();

		} catch (IOException e) {
			throw new LoginException("Connection failed: " + e);
		} catch (Exception e) {
			throw new LoginException("Server sent invalid JSON.");
		}
	}

	private String getMicrosoftAccessToken(String authCode) throws LoginException {
		Map<String, String> postData = new HashMap<>();
		postData.put("client_id", CLIENT_ID);
		postData.put("code", authCode);
		postData.put("grant_type", "authorization_code");
		postData.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
		postData.put("scope", SCOPE_UNENCODED);

		byte[] encodedDataBytes = urlEncodeMap(postData).getBytes(StandardCharsets.UTF_8);

		try {
			HttpURLConnection connection = (HttpURLConnection) AUTH_TOKEN_URL.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			connection.setDoOutput(true);

			try (OutputStream out = connection.getOutputStream()) {
				out.write(encodedDataBytes);
			}

			JsonObject json = parseConnectionToObject(connection);
			return json.get("access_token").getAsString();

		} catch (IOException e) {
			throw new LoginException("Connection failed: " + e);

		} catch (Exception e) {
			throw new LoginException("Server sent invalid JSON.");
		}
	}

	public String postJson(final String urlString, final String content) throws Exception {
		URL url = new URL(urlString);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		writer.write(content);
		writer.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		connection.disconnect();
		return builder.toString();
	}

	private String urlEncodeMap(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (sb.length() > 0)
				sb.append("&");
			sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
			sb.append("=");
			sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
		}
		return sb.toString();
	}

	private UUID uuidFromJson(String jsonUUID) throws Exception {
		try {
			String withDashes = jsonUUID.replaceFirst(
					"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
			return UUID.fromString(withDashes);
		} catch (IllegalArgumentException e) {
			throw new Exception("Invalid UUID.", e);
		}
	}

	private URL createURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private JsonObject parseConnectionToObject(URLConnection connection) throws IOException {
		try (InputStream input = connection.getInputStream()) {
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(reader);
			JsonElement json = JsonParser.parseReader(bufferedReader);
			if (!json.isJsonObject())
				throw new Exception("JSON recieved is not object!");
			return json.getAsJsonObject();
		} catch (Exception e) {
			throw new IOException("Failed to read JSON.");
		}
	}
}
