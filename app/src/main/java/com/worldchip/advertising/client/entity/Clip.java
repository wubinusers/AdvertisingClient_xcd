package com.worldchip.advertising.client.entity;

public class Clip {
	
	private int time;
	private String path;
	
	public void setClip(int time)
	{
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
