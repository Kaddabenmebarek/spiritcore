package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class BiotypeMetadataBiosampleDto implements IObject, Serializable{

	private static final long serialVersionUID = -1046920633531903635L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiosampleDto biosample;
	private BiotypeMetadataDto metadata;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public BiotypeMetadataBiosampleDto() {
	}
	
	public BiotypeMetadataBiosampleDto(BiosampleDto biosample, BiotypeMetadataDto metadata, String value) {
		this.biosample=biosample;
		this.metadata=metadata;
		this.value=value;
	}

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
	public BiotypeMetadataDto getMetadata() {
		return metadata;
	}
	public void setMetadata(BiotypeMetadataDto metadata) {
		this.metadata = metadata;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
