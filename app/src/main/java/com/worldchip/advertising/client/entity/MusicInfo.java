package com.worldchip.advertising.client.entity;

public class MusicInfo {

	private String Album;
	private String title;
	private String display_name;
	private long duration;
	private String artist;
	private String data;

	public MusicInfo() {
	};

	public MusicInfo(String Album, String title, String display_name, long duration, String artist, String data) {
		this.Album = Album;
		this.title = title;
		this.display_name = display_name;
		this.duration = duration;
		this.artist = artist;
		this.data = data;
	}

	public String getAlbum() {
		return Album;
	}

	public void setAlbum(String album) {
		Album = album;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}