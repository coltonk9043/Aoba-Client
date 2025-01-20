/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings;

import java.io.File;
import java.io.IOException;

public class SettingsContainer {
	public String configName;
	public File configFolder;
	public File configFile;

	public SettingsContainer(String configName) throws IOException {
		this.configName = configName;
		// configFolder = new File();
		// configFile = new File();

		if (!configFolder.exists() && !configFolder.mkdirs()) {
			throw new IOException("Failed to create config folder: " + configFolder.getAbsolutePath());
		}

		if (!configFile.exists() && !configFile.createNewFile()) {
			throw new IOException("Failed to create config file: " + configFile.getAbsolutePath());
		}
	}

	@Override
	public int hashCode() {
		return configName.hashCode();
	}
}
