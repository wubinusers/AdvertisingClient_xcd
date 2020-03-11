package com.worldchip.advertising.client.entity;

public class MenuItemInfo {

	public int Id;
	public String Key;
	public String Name;
	public int SelectedIndex;
	
	@Override
	public String toString() {
		return "Id:"+Id+"; Key:"+Key+"; Name:"+Name+"; SelectedIndex:"+SelectedIndex;
	}
}
