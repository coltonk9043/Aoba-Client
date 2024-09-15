/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A class to represent a system to manage Alt accounts.
 */
package net.aoba.altmanager;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.aoba.AobaClient;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.aoba.altmanager.login.MicrosoftAuth;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.utils.system.HWIDUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.util.Uuids;
import org.apache.commons.io.IOUtils;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AltManager {
    private final MinecraftClient mc;
    private final String apiURL = "https://auth.mcleaks.net/v1/";

    private ArrayList<Alt> alts = new ArrayList<Alt>();
    private String encryptKey;
    
    /**
     * Constructor for the Alt Manager system.
     */
    public AltManager() {
        mc = AobaClient.MC;
        this.encryptKey = generateEncryptionKey();
        readAlts();
    }

    /**
     * Generates a unique encryption key based on HWID.
     *
     * @return A unique encryption key.
     */
    private String generateEncryptionKey() {
        String hwid = HWIDUtil.getHWID();

        // Use the first 16 bytes of the HWID as the encryption key
        return hwid.length() >= 16 ? hwid.substring(0, 16) : String.format("%-16s", hwid).replace(' ', '0');
    }

    /**
     * Reads the Alts from a file.
     */
    public void readAlts() {
        try {
            // Finds the file and opens it.
            File altFile = new File("aoba_alts.json");
            if (!altFile.exists()) {
                LogUtils.getLogger().error("Alts file not found! Cannot load alts.");
                return;
            }

            // Read the JSON from the file
            FileReader reader = new FileReader(altFile);
            Gson gson = new Gson();
            Type altListType = new TypeToken<List<String>>(){}.getType();
            List<String> encryptedAltList = gson.fromJson(reader, altListType);
            reader.close();

            // Decrypt the alts and add them to the current alt list
            for (String encryptedAlt : encryptedAltList) {
                String decryptedAlt = decrypt(encryptedAlt);
                Alt alt = gson.fromJson(decryptedAlt, Alt.class);
                alts.add(alt);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves the Alts to a file.
     */
    public void saveAlts() {
        try {
            // Finds the file and opens it.
            File altFile = new File("aoba_alts.json");
            LogUtils.getLogger().info("[Aoba] Saving Alts");

            // Encrypt the alts and convert the alt list to JSON
            Gson gson = new Gson();
            List<String> encryptedAltList = new ArrayList<>();
            for (Alt alt : alts) {
                String altJson = gson.toJson(alt);
                String encryptedAlt = encrypt(altJson);
                encryptedAltList.add(encryptedAlt);
            }
            String json = gson.toJson(encryptedAltList);

            // Write the JSON to the file
            FileWriter writer = new FileWriter(altFile);
            writer.write(json);
            writer.close();
        } catch (IOException exception) {
            LogUtils.getLogger().error("[Aoba] Failed to save alts");
            exception.printStackTrace();
        }
    }

    /**
     * Encrypts a string using a defined encryption key.
     *
     * @param strToEncrypt The string to by encrypted.
     * @return The encrypted string.
     */
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

    /**
     * Decrypts a string using a defined encryption key.
     *
     * @return The decrypted string.
     */
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

    /**
     * Adds an Alt account to the Alt list.
     *
     * @param alt The Alt to be added.
     */
    public void addAlt(Alt alt) {
        alts.add(alt);
    }

    /**
     * Removes an Alt account from the Alt list.
     *
     * @param alt The Alt to be removed.
     */
    public void removeAlt(Alt alt) {
        alts.remove(alt);
    }

    /**
     * Gets the Alt list.
     *
     * @return The Alt list.
     */
    public ArrayList<Alt> getAlts() {
        return this.alts;
    }

    /**
     * @param alt Alt
     * @return login success state
     */
    public boolean login(Alt alt) {
        // Log in to the correct service depending on the Alt type.
    	MicrosoftAuth.login(alt);
    	return true;
    }

    

    public boolean loginCracked(String alt) {
        try {
            IMinecraftClient iMC = (IMinecraftClient) this.mc;
            UUID offlineAlt = Uuids.getOfflinePlayerUuid(alt);
            iMC.setSession(new Session(alt, offlineAlt, "", Optional.empty(), Optional.empty(), AccountType.MOJANG));
            LogUtils.getLogger().info("Logged in as " + alt);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void setSession() {
    	
    }
	/*
	 * public void loginMCLeaks(final String token) throws APIDownException,
	 * APIErrorException, InvalidResponseException, InvalidTokenException { if
	 * (token == null || token.length() != 16) { throw new InvalidTokenException();
	 * } JsonObject requestObject = new JsonObject(); requestObject.add("token", new
	 * GsonBuilder().create().toJsonTree(token)); String response; try { //response
	 * = postJson(apiURL + "redeem", requestObject.toString()); } catch (Exception
	 * e) { throw new APIDownException(); } JsonObject responseObject; try {
	 * responseObject = JsonParser.parseString(response).getAsJsonObject(); } catch
	 * (Exception e) { throw new InvalidResponseException(response); } if
	 * (!responseObject.get("success").getAsBoolean()) { throw new
	 * APIErrorException(responseObject.get("errorMessage").getAsString()); }
	 * JsonObject resultObject = responseObject.get("result").getAsJsonObject();
	 * 
	 * //MCLeaksSession = resultObject.get("session").getAsString(); }
	 */


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
            URI uri = new URI(url);
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        } catch (URISyntaxException e) {
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
