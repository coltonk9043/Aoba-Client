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
package net.aoba.managers.altmanager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;

import net.aoba.AobaClient;
import net.aoba.managers.altmanager.login.MicrosoftAuth;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.utils.system.HWIDUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import net.minecraft.util.Uuids;

/**
 * Class that manages all of the Alt accounts in Aoba.
 */
public class AltManager {
	// Constants
	private final MinecraftClient mc;

	// Varialbes
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
			Type altListType = new TypeToken<List<String>>() {
			}.getType();
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
	 * Logs in to an alt account.
	 * 
	 * @param alt Alt
	 * @return login success state
	 */
	public void login(Alt alt) {
		// Log in to the correct service depending on the Alt type.
		MicrosoftAuth.login(alt);
	}

	/**
	 * Logs in to a cracked alt account.
	 * 
	 * @param alt Alt
	 * @return login success state
	 */
	public void loginCracked(String alt) {
		try {
			IMinecraftClient iMC = (IMinecraftClient) this.mc;
			UUID offlineAlt = Uuids.getOfflinePlayerUuid(alt);
			iMC.setSession(new Session(alt, offlineAlt, "", Optional.empty(), Optional.empty(), AccountType.MOJANG));
			LogUtils.getLogger().info("Logged in as " + alt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
