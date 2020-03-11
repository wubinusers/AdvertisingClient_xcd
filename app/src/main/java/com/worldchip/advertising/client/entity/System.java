package com.worldchip.advertising.client.entity;

public class System {

	private Date date;
	private Time time;
	private int Vol;
	private int Angle;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public int getVol() {
		return Vol;
	}

	public void setVol(int vol) {
		Vol = vol;
	}

	public int getAngle() {
		return Angle;
	}

	public void setAngle(int angle) {
		Angle = angle;
	}
}
