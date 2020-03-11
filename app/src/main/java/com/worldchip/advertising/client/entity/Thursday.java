package com.worldchip.advertising.client.entity;

import java.util.ArrayList;
import java.util.List;

public class Thursday {

	private String group;
	private List<Group> groups = new ArrayList<Group>();

	public void addGroup(Group group) {
		this.groups.add(group);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}