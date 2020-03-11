package com.worldchip.advertising.client.entity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import com.worldchip.advertising.client.utils.PlayModel;

@SuppressLint("DefaultLocale")
public class Program {

	private PlayModel mPlayModel = PlayModel.PV;
	private int id;
	private String type;
	private String time;
	private String background;
	private List<Media> medias = new ArrayList<Media>();

	public void setProgramInfo(int id, String type, String time, String background) {
		this.id = id;
		this.type = type;
		this.time = time;
		this.background = background;
	}

	public void addMedia(Media media) {
		this.medias.add(media);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public PlayModel getPlayModel() {
		return mPlayModel;
	}

	public void setType(String type) {
		this.type = type;
		if (type.toUpperCase().contains("V")) {
			mPlayModel = PlayModel.V;
		} else if (type.toUpperCase().equals("P")) {
			mPlayModel = PlayModel.P;
		} else if (type.toUpperCase().equals("P+M") || type.toUpperCase().equals("M+P")) {
			mPlayModel = PlayModel.PM;
		} else if (type.toUpperCase().equals("P+C") || type.toUpperCase().equals("C+P")) {
			mPlayModel = PlayModel.PC;
		} else if (type.toUpperCase().equals("P+M+C") || type.toUpperCase().equals("M+P+C")
				|| type.toUpperCase().equals("C+M+P")) {
			mPlayModel = PlayModel.PMC;
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<Media> getMedias() {
		return medias;
	}

	public void setMedias(List<Media> medias) {
		this.medias = medias;
	}
}
