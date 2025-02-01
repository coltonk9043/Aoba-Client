/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba;

import net.fabricmc.api.ModInitializer;

/**
 * Initializes and provides access to the Aoba Client singleton.
 */
public class Aoba implements ModInitializer {
	private static AobaClient INSTANCE;

	@Override
	public void onInitialize() {
		INSTANCE = new AobaClient();
		INSTANCE.Initialize();
	}

	/**
	 * @return Singleton instance of AobaClient.
	 */
	public static AobaClient getInstance() {
		return INSTANCE;
	}
}
