package net.aoba.altmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.aoba.Aoba;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

public class AltManager {
	private File altFiles;
	private MinecraftClient mc;
	private ArrayList<Alt> alts = new ArrayList<Alt>();
	private String encryptKey = "B&E)H@McQeThWmZq";
	private static final String apiURL = "https://auth.mcleaks.net/v1/";
	public boolean isMCLeaks = false;
	
	public static String MCLeaksSession;
	
	
	public AltManager() {
		mc = MinecraftClient.getInstance();
		altFiles = new File(mc.runDirectory, "aoba_alts.txt");
		readAlts();
	}

	public void readAlts() {
		try {
			if (!this.altFiles.exists()) {
				return;
			}
			List<String> list = IOUtils.readLines(new FileInputStream(this.altFiles), StandardCharsets.UTF_8);
			for (String s : list) {
				String str = decrypt(s);
				String[] alt = str.split(":");
				try {
					Alt newAlt = new Alt(alt[0], alt[1], alt[2]);
					alts.add(newAlt);
				} catch (Exception e) {
					System.out.println("Skipping bad option: " + alt[0]);
				}
			}
		} catch (Exception exception) {
			System.out.println("[Aoba] Failed to load alts..");
		}

	}

	public void saveAlts() {
		PrintWriter printwriter = null;
		try {
			System.out.println("[Aoba] Saving Alts");
			printwriter = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(this.altFiles), StandardCharsets.UTF_8));
			for (Alt alt : alts) {
				String str = alt.getEmail() + ":" + alt.getPassword() + ":" + alt.getUsername();
				printwriter.println(encrypt(str));
			}
		} catch (Exception exception) {
			System.out.println("[Aoba] Failed to save alts");
		}
		IOUtils.closeQuietly((Writer) printwriter);
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

	public boolean login(Alt alt) {
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
				Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(alt.getEmail());
		auth.setPassword(alt.getPassword());
		try {
			auth.logIn();
			//mc.setSession(new Session(auth.getSelectedProfile().getName(),
					//auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang")); 
			alt.setUsername(mc.getSession().getUsername());
			isMCLeaks = false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loginCracked(String alt) {
		try {
			//mc.setSession(new Session(alt,"", "", "mojang"));
			System.out.println("Logged in as " + alt);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean login(String username, String password) {
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
				Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);

		auth.setUsername(username);
		auth.setPassword(password);
		try {
			auth.logIn();
			
			//MinecraftClient.getInstance().session = new Session(auth.getSelectedProfile().getName(),
			//		auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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

	public static void loginMCLeaks(final String token)
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
			responseObject = new JsonParser().parse(response).getAsJsonObject();
		} catch (Exception e) {
			throw new InvalidResponseException(response);
		}
		if (!responseObject.get("success").getAsBoolean()) {
			throw new APIErrorException(responseObject.get("errorMessage").getAsString());
		}
		JsonObject resultObject = responseObject.get("result").getAsJsonObject();

		Session session = new Session(resultObject.get("mcname").getAsString(),
				resultObject.get("session").getAsString(), token, "mojang");
		//MinecraftClient.getInstance().session = session;
		MCLeaksSession = resultObject.get("session").getAsString();
		Aoba.getInstance().am.isMCLeaks = true;
	}
	
	public void joinServer(Session session, final String server, final String serverHash)
			throws APIDownException, InvalidResponseException, APIErrorException {
		Gson gson = new GsonBuilder().create();
		JsonObject requestObject = new JsonObject();
		System.out.println(serverHash);
		System.out.println(server);
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
			responseObject = new JsonParser().parse(response).getAsJsonObject();
		} catch (Exception e) {
			throw new InvalidResponseException(response);
		}
		if (!responseObject.get("success").getAsBoolean()) {
			throw new APIErrorException(responseObject.get("errorMessage").getAsString());
		}
	}

	public static String postJson(final String urlString, final String content) throws Exception {
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
}
