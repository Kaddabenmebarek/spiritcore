package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class GroupBiotypeMetadataValueDto implements Serializable {

	private static final long serialVersionUID = -522340872693389141L;
	private Integer id = Constants.NEWTRANSIENTID;
	private GroupDto group;
	private BiotypeMetadataValueDto biotypeMetadataValue;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public GroupBiotypeMetadataValueDto() {
	}
	public GroupBiotypeMetadataValueDto(GroupDto group, BiotypeMetadataValueDto biotypeMetadataValue) {
		this.group=group;
		this.biotypeMetadataValue=biotypeMetadataValue;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public GroupDto getGroup() {
		return group;
	}
	public void setGroup(GroupDto group) {
		this.group = group;
	}
	public BiotypeMetadataValueDto getBiotypeMetadataValue() {
		return biotypeMetadataValue;
	}
	public void setBiotypeMetadataValue(BiotypeMetadataValueDto biotypeMetadataValue) {
		this.biotypeMetadataValue = biotypeMetadataValue;
	}
	public Date getCreDate() {
		return creDate;
	}
	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	public String getCreUser() {
		return creUser;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}
	public Date getUpdDate() {
		return updDate;
	}
	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}
	public String getUpdUser() {
		return updUser;
	}
	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}
}
