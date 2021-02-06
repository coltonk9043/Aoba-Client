package aoba.main.altmanager;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
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
import aoba.main.altmanager.exceptions.APIDownException;
import aoba.main.altmanager.exceptions.APIErrorException;
import aoba.main.altmanager.exceptions.InvalidResponseException;
import aoba.main.altmanager.exceptions.InvalidTokenException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AltManager {
	private File altFiles;
	private Minecraft mc;
	private ArrayList<Alt> alts = new ArrayList<Alt>();
//	private String decryptKey = "aobachanuwu";
	private static final String apiURL = "https://auth.mcleaks.net/v1/";
	
	public AltManager() {
		mc = Minecraft.getInstance();
		altFiles = new File(mc.gameDir, "aoba_alts.txt");
		readAlts();
	}

	public void readAlts() {
		final Splitter COLON_SPLITTER = Splitter.on(':');
		try {
			if (!this.altFiles.exists()) {
				return;
			}
			List<String> list = IOUtils.readLines(new FileInputStream(this.altFiles), StandardCharsets.UTF_8);
			Hashtable<String, String> altList = new Hashtable<String, String>();
			for (String s : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.limit(2).split(s).iterator();
					altList.put(iterator.next(), iterator.next());
				} catch (Exception e) {
					Minecraft.LOGGER.warn("Skipping bad option: {}", (Object) s);
				}
			}
			for (String s1 : altList.keySet()) {
				String s2 = altList.get(s1);
				try {
					Alt alt = new Alt(s1, s2);
					alts.add(alt);
				} catch (Exception e) {
					Minecraft.LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
				}
			}
		} catch (Exception exception) {
			Minecraft.LOGGER.error("[Aoba] Failed to load alts", (Throwable) exception);
		}
		
	}

	public void saveAlts() {
		PrintWriter printwriter = null;
		try {
			Minecraft.LOGGER.info("[Aoba] Saving Alts");
			printwriter = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(this.altFiles), StandardCharsets.UTF_8));
			for (Alt alt : alts) {
				printwriter.println(alt.getUsername() + ":" + alt.getPassword());
			}
		} catch (Exception exception) {
			Minecraft.LOGGER.error("[Aoba] Failed to save alts", (Throwable) exception);
		} 
		IOUtils.closeQuietly((Writer) printwriter);
	}

	public boolean login(Alt alt) {
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(
				Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(alt.getUsername());
		auth.setPassword(alt.getPassword());
		try {
			auth.logIn();
			Minecraft.getInstance().session = new Session(auth.getSelectedProfile().getName(),
					auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(), "mojang");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loginCracked(String alt) {
		try {
			mc.session.setUsername(alt);
			Minecraft.LOGGER.info("Logged in as " + alt);
			return true;
		}catch(Exception e) {
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
			Minecraft.getInstance().session = new Session(auth.getSelectedProfile().getName(),
					auth.getSelectedProfile().getId().toString(),
					auth.getAuthenticatedToken(), "mojang");
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
	
	public ArrayList<Alt> getAlts(){
		return this.alts;
	}
	
	public static void loginMCLeaks(final String token) throws APIDownException, APIErrorException, InvalidResponseException, InvalidTokenException {
		if(token == null || token.length() != 16) {
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
		if(!responseObject.get("success").getAsBoolean()) {
			throw new APIErrorException(responseObject.get("errorMessage").getAsString());
		}
		JsonObject resultObject = responseObject.get("result").getAsJsonObject();
		
		Session session = new Session(resultObject.get("mcname").getAsString(), resultObject.get("session").getAsString(), token, "mojang");
		Minecraft.getInstance().session = session;
	}
	
	public void joinServer(Session session, final String server, final String serverHash) throws APIDownException, InvalidResponseException, APIErrorException {
		Gson gson = new GsonBuilder().create();
		JsonObject requestObject = new JsonObject();
		Minecraft.LOGGER.info(serverHash);
		Minecraft.LOGGER.info(server);
		requestObject.add("session", gson.toJsonTree(session.getSessionID()));
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
		if(!responseObject.get("success").getAsBoolean()) {
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
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		connection.disconnect();
		return builder.toString();
	}
}
