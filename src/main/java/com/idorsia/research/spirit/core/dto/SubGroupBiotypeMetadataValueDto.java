package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class SubGroupBiotypeMetadataValueDto implements Serializable{

	private static final long serialVersionUID = 792190623333000457L;
	private Integer id = Constants.NEWTRANSIENTID;
	private SubGroupDto subGroup;
	private BiotypeMetadataValueDto biotypeMetadataValue;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public SubGroupBiotypeMetadataValueDto() {
	}
	public SubGroupBiotypeMetadataValueDto(SubGroupDto subGroup, BiotypeMetadataValueDto biotypeMetadataValue) {
		this.subGroup=subGroup;
		this.biotypeMetadataValue=biotypeMetadataValue;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public SubGroupDto getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(SubGroupDto subGroup) {
		this.subGroup = subGroup;
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
