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

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.aoba.AobaClient;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.aoba.mixin.interfaces.IMinecraftClient;
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
    private String encryptKey = "B&E)H@McQeThWmZq";
    private String MCLeaksSession;

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

    /**
     * Constructor for the Alt Manager system.
     */
    public AltManager() {
        mc = AobaClient.MC;
        readAlts();
    }

    /**
     * Reads the Alts from a file.
     */
    public void readAlts() {
        try {
            // Finds the file and opens it.
            File altFile = new File(mc.runDirectory, "aoba_alts.txt");
            if (!altFile.exists()) {
                LogUtils.getLogger().error("Alts file not found! Cannot load alts.");
                return;
            }

            List<String> list = IOUtils.readLines(new FileInputStream(altFile), StandardCharsets.UTF_8);

            // For every line in the file, decrypt and read the account information.
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

    /**
     * Saves the Alts to a file.
     */
    public void saveAlts() {
        PrintWriter printwriter = null;
        try {
            // Finds the file and opens it.
            File altFile = new File(mc.runDirectory, "aoba_alts.txt");
            System.out.println("[Aoba] Saving Alts");
            printwriter = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(altFile), StandardCharsets.UTF_8));

            // For every Alt in the current Alt list, print it to the file and encrypt it.
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
     * @param alt
     * @return
     */
    public boolean login(Alt alt) {
        // Log in to the correct service depending on the Alt type.
        return loginMicrosoft(alt);
    }

    /**
     * Logs in to a Microsoft Account.
     *
     * @param alt The Alt to be signed into.
     * @return A boolean signifying that the operation has completed.
     */
    private boolean loginMicrosoft(Alt alt) {
        try {
            IMinecraftClient iMC = (IMinecraftClient) this.mc;
            UUID uuid;
            String name;

            // Grabs login information from Microsoft's Servers.
            String authCode = getAuthCode(alt);
            String msftAccessToken = getMicrosoftAccessToken(authCode);
            XboxLiveToken xblToken = getXBLToken(msftAccessToken);
            String xstsToken = getXSTSToken(xblToken.getToken());
            String mcAccessToken = getMinecraftAccessToken(xblToken.getHash(), xstsToken);

            // Creates a connection to the login server.
            URLConnection connection = PROFILE_URL.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);

            // Retrieves the JsonObject from the login server.
            JsonObject json = parseConnectionToObject(connection);

            // Throw an error is the JsonObject contains an error.
            if (json.has("error"))
                throw new LoginException("Error message from api.minecraftservices.com:\n" + json.get("error"));

            // Otherwise, create a Session and log in.
            uuid = uuidFromJson(json.get("id").getAsString());
            name = json.get("name").getAsString();

            Session session = new Session(name, uuid, mcAccessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
            iMC.setSession(session);
            System.out.println("Logged in as " + alt.getUsername());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the Authentication Code from Microsoft's servers,
     *
     * @param alt The Alt to fetch the Auth code from.
     * @return The Auth Code.
     * @throws LoginException If the login has failed.
     */
    private String getAuthCode(Alt alt) throws LoginException {
        String cookie;
        String loginWebpage;
        try {
            URLConnection connection = LOGIN_URL.openConnection();
            cookie = connection.getHeaderField("set-cookie");
            try (InputStream input = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(reader);
                loginWebpage = bufferedReader.lines().collect(Collectors.joining("\n"));
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
            URI uri = URI.create(urlPost);
            URL url = uri.toURL();
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

    public boolean loginCracked(String alt) {
        try {
            IMinecraftClient iMC = (IMinecraftClient) this.mc;
            UUID offlineAlt = Uuids.getOfflinePlayerUuid(alt);
            iMC.setSession(new Session(alt, offlineAlt, "", Optional.empty(), Optional.empty(), AccountType.MOJANG));
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

    private String postJson(final String urlString, final String content) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
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
