package net.aoba.settings;

public class ListSetting extends Setting {

	private String value;
	private int index;
	private final String defaultValue;
	private String[] options;

	public ListSetting(String name, String line, String[] options) {
		super(name, line);
		this.options = options;
		this.defaultValue = options[0];
		this.value = options[0];
		this.index = 0;
		// this.loadSetting();
	}

	public final String getValue() {
		return this.value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public void increment() {
		index++;
		if (index >= this.options.length) {
			index = 0;
		}
		this.value = this.options[index];
	}

	public void decrement() {
		index--;
		if (index < 0) {
			this.index = this.options.length - 1;
		}
		this.value = this.options[index];
	}

	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingString(this.getLine());
		} catch (Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
