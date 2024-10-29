package net.aoba.utils.system;

import static net.aoba.AobaClient.MC;

import org.apache.commons.codec.digest.DigestUtils;

public class HWIDUtil {
	// I know its called getHWID, we just arent using the HWID anymore and i havent
	// renamed it.
	public static String getHWID() {
		return DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name")
				+ System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot")
				+ System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION")
				+ System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE")
				+ System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")))
				+ MC.getSession().getUsername();
	}
}
