/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 * This maded by Donalp012
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ProtocolManager {
    public static String BRAND = "vanilla";
    public static String OVERRIDE_NAME = "";
    public static int OVERRIDE_PROTOCOL = -1;
    public static int OVERRIDE_PACK_VERSION = -1;

    private static final File FILE = new File(System.getProperty("user.dir") + File.separator + "aoba" + File.separator + "protocol.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
// loading json
    static {
        load();
    }

    public static String getVersionName() {
        return (OVERRIDE_NAME != null && !OVERRIDE_NAME.isEmpty()) ? OVERRIDE_NAME : SharedConstants.getCurrentVersion().name();
    }

    public static int getProtocolVersion() {
        return OVERRIDE_PROTOCOL != -1 ? OVERRIDE_PROTOCOL : SharedConstants.getCurrentVersion().protocolVersion();
    }

    public static int getPackVersion() {
        if (OVERRIDE_PACK_VERSION != -1) {
            return OVERRIDE_PACK_VERSION;
        }
        try {
            Object packFormatObj = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);
            if (packFormatObj != null) {
                String rawNum = packFormatObj.toString().replaceAll("\\D+", "");
                if (!rawNum.isEmpty()) {
                    return Integer.parseInt(rawNum);
                }
            }
        } catch (Exception ignored) {}
        return 18;
    }

    public static String getClientBrand() {
        return (BRAND != null && !BRAND.isEmpty()) ? BRAND : "vanilla";
    }

   //saving configs in the json
    public static void save() {
        try {
            if (!FILE.getParentFile().exists()) {
                FILE.getParentFile().mkdirs();
            }
            JsonObject json = new JsonObject();
            json.addProperty("brand", BRAND);
            json.addProperty("override_name", OVERRIDE_NAME);
            json.addProperty("override_protocol", OVERRIDE_PROTOCOL);
            json.addProperty("override_pack_version", OVERRIDE_PACK_VERSION);

            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// loads the json without interfere with the client cycle
    public static void load() {
        try {
            if (!FILE.exists()) {
                save(); //creates the json if don´t exist
                return;
            }
            try (FileReader reader = new FileReader(FILE)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("brand")) BRAND = json.get("brand").getAsString();
                if (json.has("override_name")) OVERRIDE_NAME = json.get("override_name").getAsString();
                if (json.has("override_protocol")) OVERRIDE_PROTOCOL = json.get("override_protocol").getAsInt();
                if (json.has("override_pack_version")) OVERRIDE_PACK_VERSION = json.get("override_pack_version").getAsInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
