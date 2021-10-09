package net.aoba.settings;

public class ListSetting extends Setting{

	private String value;
	private final String defaultValue;
	private String[] options;
	
	public ListSetting(String name, String line, String[] options) {
		super(name, line);
		this.options = options;
		this.defaultValue = options[0];
		this.value = options[0];
		//this.loadSetting();
	}

	public final String getValue() {
		return this.value;
	}
	
	public final void setValue(String value) {
		this.value = value;
	}

	public void increment() {
		for(int i = 0; i < this.options.length; i++) {
			if(this.options[i].equalsIgnoreCase(this.value)) {
				this.value = this.options[(i + 1) / this.options.length];
			}
		}
	}
	
	public void decrement() {
		int index = options.length - 1;
		for(int i = 0; i < this.options.length; i++) {
			if(this.options[i].equalsIgnoreCase(this.value)) {
				index = i;
			}
		}
		if(index <= 0) {
			this.value = this.options[this.options.length - 1];
		}else {
			this.value = this.options[index - 1];
		}
	}
	
	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingString(this.getLine());
		}catch(Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
