package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class BiotypeMetadataValueDto implements IObject, Serializable{
	
	private static final long serialVersionUID = 6175763566534405756L;
	private Integer id = Constants.NEWTRANSIENTID;
    private String value;
    private BiotypeMetadataDto biotypemetadata;
	private Date credate;
	private String creuser;
	private Date upddate;
	private String upduser;
	
	public BiotypeMetadataValueDto() {
	}
	
    public BiotypeMetadataValueDto(BiotypeMetadataValueDto biotypeMetadataValue) {
    	this.value=biotypeMetadataValue.getValue();
    	this.biotypemetadata=biotypeMetadataValue.getBiotypeMetadata();
	}
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public BiotypeMetadataDto getBiotypeMetadata() {
		return biotypemetadata;
	}
	public void setBiotypeMetadata(BiotypeMetadataDto biotypeMetadata) {
		this.biotypemetadata = biotypeMetadata;
	}
	public Date getUpdDate() {
		return upddate;
	}
	public void setUpdDate(Date updDate) {
		this.upddate = updDate;
	}
	public String getCreUser() {
		return creuser;
	}
	public void setCreUser(String creUser) {
		this.creuser = creUser;
	}
	public Date getCreDate() {
		return credate;
	}
	public void setCreDate(Date creDate) {
		this.credate = creDate;
	}
	public String getUpdUser() {
		return upduser;
	}
	public void setUpdUser(String updUser) {
		this.upduser = updUser;
	}
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        BiotypeMetadataValueDto that = (BiotypeMetadataValueDto) o;
	        return getValue().equals(that.getValue()) &&
	                getBiotypeMetadata().equals(that.getBiotypeMetadata());
	    }
	
	@Override
	public String toString() {
		return getValue();
	}
}
