package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class LinkedBiosampleDto implements Serializable, IObject {

	private static final long serialVersionUID = -1496130340168510421L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiosampleDto biosample;
	private BiosampleDto linkedBiosample;
	private BiotypeMetadataDto metadata;
	private Date creDate;
	private Date updDate;
	private String creUser;
	private String updUser;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BiosampleDto getBiosample() {
		return biosample;
	}
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}
	public BiosampleDto getLinkedBiosample() {
		return linkedBiosample;
	}
	public void setLinkedBiosample(BiosampleDto linkedBiosample) {
		this.linkedBiosample = linkedBiosample;
	}
	public BiotypeMetadataDto getMetadata() {
		return metadata;
	}
	public void setMetadata(BiotypeMetadataDto metadata) {
		this.metadata = metadata;
	}
	public Date getCreDate() {
		return creDate;
	}
	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	public Date getUpdDate() {
		return updDate;
	}
	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}
	public String getCreUser() {
		return creUser;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}
	public String getUpdUser() {
		return updUser;
	}
	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}
}
