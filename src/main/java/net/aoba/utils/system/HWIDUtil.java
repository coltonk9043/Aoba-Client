package net.aoba.utils.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HWIDUtil {
    public static String getHWID() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                command = "wmic csproduct get UUID";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                command = "cat /sys/class/dmi/id/product_uuid";
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (os.contains("win")) {
                reader.readLine();
            }
            return reader.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
