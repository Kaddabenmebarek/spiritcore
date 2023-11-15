package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.AssayService;

@Component
public class AssayDto implements IObject, Comparable<AssayDto>, Serializable{
	
	private static final long serialVersionUID = 3449110811345113656L;
	@Autowired
	private AssayService assayService;
	private Integer id=Constants.NEWTRANSIENTID;
	private String category;
	private String description;
	private Date creDate;
	private String creUser;
	private String name;
	private Date updDate;
	private String updUser;
	private Boolean disabled;
	private List<AssayAttributeDto> attributes;
	
	public AssayDto() {
	}
	
	public AssayDto(String name) {
		setName(name);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Boolean getDisabled() {
		return disabled==Boolean.TRUE;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	public List<AssayAttributeDto> getAttributes() {
		if(attributes == null) {
			getAssayService().mapAttributes(this);
		}
		return attributes;
	}
	@Deprecated
	public List<AssayAttributeDto> getAttributesNoMapping() {
		return attributes;
	}
	public void setAttributes(List<AssayAttributeDto> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(AssayDto t) {

		if(t==null) return -1;
		if(t==this) return 0;

		int c = getCategory()==null? (t.getCategory()==null?0:-1): getCategory().compareToIgnoreCase(t.getCategory());
		if(c!=0) return c;

		c = getName()==null? (t.getName()==null?0:-1): getName().compareToIgnoreCase(t.getName());
		return c;
	}

	public AssayService getAssayService() {
		if(assayService == null) {
			assayService = (AssayService) ContextShare.getContext().getBean("assayService");
		}
		return assayService;
	}
	
	
}
