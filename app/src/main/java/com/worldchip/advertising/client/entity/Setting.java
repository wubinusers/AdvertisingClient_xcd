package com.worldchip.advertising.client.entity;

public class Setting {
	
	private System system;
	private Caption caption;
	private PowerDayTime powerDayTime;
	private PowerWeekTime powerWeekTime;
	private String weather;
	
	public System getSystem() {
		return system;
	}
	public void setSystem(System system) {
		this.system = system;
	}
	public Caption getCaption() {
		return caption;
	}
	public void setCaption(Caption caption) {
		this.caption = caption;
	}
	public PowerDayTime getPowerDayTime() {
		return powerDayTime;
	}
	public void setPowerDayTime(PowerDayTime powerDayTime) {
		this.powerDayTime = powerDayTime;
	}
	public PowerWeekTime getPowerWeekTime() {
		return powerWeekTime;
	}
	public void setPowerWeekTime(PowerWeekTime powerWeekTime) {
		this.powerWeekTime = powerWeekTime;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
}