package com.worldchip.advertising.client.entity;

import com.worldchip.advertising.client.utils.MediaType;

public class DetailsInfo {

	private MediaType type;
	private String path;
	private MusicInfo musicInfo;

	public DetailsInfo(MediaType type, String path, MusicInfo musicInfo) {
		this.type = type;
		this.path = path;
		this.musicInfo = musicInfo;
	}

	public DetailsInfo() {

	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MusicInfo getMusicInfo() {
		return musicInfo;
	}

	public void setMusicInfo(MusicInfo musicInfo) {
		this.musicInfo = musicInfo;
	}
}