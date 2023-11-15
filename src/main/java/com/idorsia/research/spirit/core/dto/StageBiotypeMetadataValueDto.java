package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class StageBiotypeMetadataValueDto implements Serializable {

	private static final long serialVersionUID = 1007657296508895348L;
	private Integer id = Constants.NEWTRANSIENTID;
	private StageDto stage;
	private BiotypeMetadataValueDto biotypeMetadataValue;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public StageBiotypeMetadataValueDto() {
	}
	public StageBiotypeMetadataValueDto(StageDto stage, BiotypeMetadataValueDto biotypeMetadataValue) {
		this.stage=stage;
		this.biotypeMetadataValue=biotypeMetadataValue;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public StageDto getStage() {
		return stage;
	}
	public void setStage(StageDto stage) {
		this.stage = stage;
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
