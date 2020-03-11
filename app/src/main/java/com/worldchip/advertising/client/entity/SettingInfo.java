package com.worldchip.advertising.client.entity;

public class SettingInfo {
	
	private int _id;
	private int menuIndex;
   	private int subMenuIndex;
   	private int selectIndex;
   	
   	public SettingInfo(){};
   	
	public SettingInfo(int _id, int menuIndex, int subMenuIndex, int selectIndex) 
	{
		this._id = _id;
		this.menuIndex = menuIndex;
		this.subMenuIndex = subMenuIndex;
		this.selectIndex = selectIndex;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getMenuIndex() {
		return menuIndex;
	}

	public void setMenuIndex(int menuIndex) {
		this.menuIndex = menuIndex;
	}

	public int getSubMenuIndex() {
		return subMenuIndex;
	}

	public void setSubMenuIndex(int subMenuIndex) {
		this.subMenuIndex = subMenuIndex;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}
}