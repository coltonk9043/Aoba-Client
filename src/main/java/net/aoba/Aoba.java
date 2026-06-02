/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba;

import net.fabricmc.api.ModInitializer;
import net.aoba.gui.screens.ProtocolScreen;

/**
 * Initializes and provides access to the Aoba Client singleton.
 */
public class Aoba implements ModInitializer {
	private static AobaClient INSTANCE;

	@Override
	public void onInitialize() {
		INSTANCE = new AobaClient();
		INSTANCE.Initialize();
		
		try {
			java.lang.reflect.Field field = net.minecraft.SharedConstants.class.getDeclaredField("CURRENT_VERSION");
			field.setAccessible(true);

			net.minecraft.WorldVersion originalVersion = (net.minecraft.WorldVersion) field.get(null);

			net.minecraft.WorldVersion proxyVersion = (net.minecraft.WorldVersion) java.lang.reflect.Proxy.newProxyInstance(
					net.minecraft.WorldVersion.class.getClassLoader(),
					new Class<?>[]{net.minecraft.WorldVersion.class},
					(proxy, method, args) -> {
						String mName = method.getName();

						// name interception
						if (mName.equals("name") || mName.equals("getName") || mName.equals("method_38023")) {
							return ProtocolScreen.OVERRIDE_NAME != null
									? ProtocolScreen.OVERRIDE_NAME
									: originalVersion.name();
						}

						// id interception
						if (mName.equals("protocolVersion") || mName.equals("getProtocolVersion") || mName.equals("method_38024")) {
							return ProtocolScreen.OVERRIDE_PROTOCOL != -1
									? ProtocolScreen.OVERRIDE_PROTOCOL
									: originalVersion.protocolVersion();
						}


						if (mName.equals("packVersion") || mName.equals("getPackVersion")) {
							if (ProtocolScreen.OVERRIDE_PACK_VERSION != -1) {
								Object originalPack = method.invoke(originalVersion, args);
								if (originalPack != null) {
			
									return java.lang.reflect.Proxy.newProxyInstance(
											originalPack.getClass().getClassLoader(),
											originalPack.getClass().getInterfaces().length > 0
													? originalPack.getClass().getInterfaces()
													: new Class<?>[]{originalPack.getClass().getSuperclass()},
											(pPack, mPack, aPack) -> {
												if (mPack.getReturnType() == int.class || mPack.getReturnType() == Integer.class) {
													return ProtocolScreen.OVERRIDE_PACK_VERSION;
												}
												return mPack.invoke(originalPack, aPack);
											}
									);
								}
							}
						}

						return method.invoke(originalVersion, args);
					}
			);

	
			try {
				java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
			} catch (Exception ignored) {}

			field.set(null, proxyVersion);
		} catch (Exception e) {
			if (AobaClient.LOGGER != null) {
				AobaClient.LOGGER.error("[Aoba] Failed to inject dynamic protocol bypass: ", e);
			} else {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Singleton instance of AobaClient.
	 */
	public static AobaClient getInstance() {
		return INSTANCE;
	}
}
