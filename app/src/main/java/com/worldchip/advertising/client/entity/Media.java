package com.worldchip.advertising.client.entity;

import java.util.ArrayList;
import java.util.List;

public class Media {

	private String type;
	private int x;
	private int y;
	private int width;
	private int height;
	private List<Clip> clips = new ArrayList<Clip>();

	public void setMedia(String type, int x, int y, int width, int height) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void addClip(Clip clip) {
		this.clips.add(clip);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Clip> getClips() {
		return clips;
	}

	public void setList(List<Clip> clips) {
		this.clips = clips;
	}
}