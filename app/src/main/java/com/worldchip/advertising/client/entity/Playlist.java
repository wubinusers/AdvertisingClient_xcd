package com.worldchip.advertising.client.entity;

import java.util.ArrayList;
import java.util.List;


public class Playlist {
	
	private String mDay;
	private String mMonth;
	private String mWeek;
	private String mStart;
	private String mEnd;
	private List<Program> programs = new ArrayList<Program>();
	
	public Playlist(){};
	
	public Playlist(String mDay, String mMonth, String mWeek, String mStart,String mEnd, List<Program> programs) 
	{
		this.mDay = mDay;
		this.mMonth = mMonth;
		this.mWeek = mWeek;
		this.mStart = mStart;
		this.mEnd = mEnd;
		this.programs = programs;
	}

	public void addProgram(Program program) 
	{  
        this.programs.add(program);  
    } 
	
	public void setDay(String day){
		this.mDay = day;
	}
	
	public String getDay(){
		return mDay;
	}
	
	public void setMonth(String month){
		this.mMonth = month;
	}
	
	public String getMonth(){
		return mMonth;
	}
	
	public void setWeek(String week){
		this.mWeek = week;
	}
	
	public String getWeek(){
		return mWeek;
	}
	
	public String getStart() {
		return mStart;
	}
	public void setStart(String start) {
		this.mStart = start;
	}
	public String getEnd() {
		return mEnd;
	}
	public void setEnd(String end) {
		this.mEnd = end;
	}
	public List<Program> getPrograms() {
		return programs;
	}
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}
}