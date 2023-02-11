package net.aoba.settings;

public class SliderSetting extends Setting{
	
	private double value;
	private final double defaultValue;
	private final double minValue;
	private final double maxValue;
	private final double increment;
	
	public SliderSetting(String name, String line, double value, double min, double max, double increment) {
		super(name, line);
		this.value = value;
		this.defaultValue = value;
		this.minValue = min;
		this.maxValue	=max;
		this.increment = increment;
	}
	
	public final double getValue() {
		return this.value;
	}
	
	public final float getValueFloat() {
		return (float)this.value;
	}
	
	public final int getValueInt() {
		return (int)this.value;
	}
	
	public final void setValue(double value)
	{
		this.value = this.increment*(Math.ceil(Math.abs(value/this.increment)));;
	}
	
	public final double getMinimum()
	{
		return this.minValue;
	}
	
	public final double getMaximum()
	{
		return this.minValue;
	}
	
	public final double getIncrement()
	{
		return increment;
	}
	
	public final double getRange()
	{
		return this.maxValue - this.minValue;
	}
	
	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingFloat(this.getLine());
		}catch(Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
