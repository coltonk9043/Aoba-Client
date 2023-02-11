package net.aoba;

import net.fabricmc.api.ModInitializer;

public final class Aoba implements ModInitializer {

	public static AobaClient instance;
	
	@Override
	public void onInitialize()
	{
		instance = new AobaClient();
		instance.Init();
	}
	
	public static AobaClient getInstance() {
		return instance;
	}
}
