package net.aoba.settings;

public abstract class Setting {
	private final String name;
	private final String line;
	
	public Setting(String name, String line) {
		this.name = name;
		this.line = line;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getLine() {
		return this.line;
	}
	
	public abstract void loadSetting();
}
