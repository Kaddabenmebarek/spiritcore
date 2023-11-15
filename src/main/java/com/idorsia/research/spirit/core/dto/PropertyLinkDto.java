package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Property;

public class PropertyLinkDto implements IObject, Serializable{
	
	private static final long serialVersionUID = -8460007200288412863L;
	private Integer id = Constants.NEWTRANSIENTID;
	private StudyDto study;
	private Property property;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public PropertyLinkDto() {
	}

	public PropertyLinkDto(PropertyLinkDto propertyLink) {
		this.value=propertyLink.getValue();
		this.property=propertyLink.getProperty();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StudyDto getStudy() {
		return study;
	}

	/**
	 * Do not call this method directly but call the service insteed
	 * PropertyLinkService.setStudy(this,study);
	 * @param study
	 */
	@Deprecated
	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
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

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}
	
}
