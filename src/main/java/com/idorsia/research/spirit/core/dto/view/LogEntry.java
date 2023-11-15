package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.LogAction;

public class LogEntry implements Serializable {

	private static final long serialVersionUID = 8980700913529460570L;
	private String creUser;
	private Date creDate;
	private LogAction action;
	private String comments;
	private String ipAddress;
	
	public String getCreUser() {
		return creUser;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}
	public Date getCreDate() {
		return creDate;
	}
	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	public LogAction getAction() {
		return action;
	}
	public void setAction(LogAction action) {
		this.action = action;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
