package net.aoba.settings;

public class BooleanSetting extends Setting{

	private boolean value;
	private final boolean defaultValue;
	
	public BooleanSetting(String name, String line) {
		super(name, line);
		this.defaultValue = false;
		this.loadSetting();
	}

	public final boolean getValue() {
		return this.value;
	}
	
	public final void setValue(boolean value) {
		this.value = value;
	}
	
	public final void toggleValue() {
		this.value = !value;
	}

	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingBoolean(this.getLine());
		}catch(Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
