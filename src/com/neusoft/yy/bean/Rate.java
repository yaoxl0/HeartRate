package com.neusoft.yy.bean;

import java.io.Serializable;
import java.util.Date;

public class Rate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private Date heartTime;
	private String heartNumber;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getHeartTime() {
		return heartTime;
	}

	public void setHeartTime(Date heartTime) {
		this.heartTime = heartTime;
	}

	public String getHeartNumber() {
		return heartNumber;
	}

	public void setHeartNumber(String heartNumber) {
		this.heartNumber = heartNumber;
	}
	
}
