package com.worldchip.advertising.client.entity;

public class Caption {

	private String fgcolor;
	private String bgcolor;
	private float size;
	private String speed;
	private boolean isShow;
	private int showLocation;
	private int transparency;

	public String getFgcolor() {
		return fgcolor;
	}

	public void setFgcolor(String fgcolor) {
		this.fgcolor = fgcolor;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public int getShowLocation() {
		return showLocation;
	}

	public void setShowLocation(int showLocation) {
		this.showLocation = showLocation;
	}

	public int getTransparency() {
		return transparency;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}
}
