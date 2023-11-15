package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class SubGroupPatternDto implements IObject, Serializable{

	private static final long serialVersionUID = -1429333064913063453L;
	private Integer id = Constants.NEWTRANSIENTID;
	private ActionPatternsDto actionpattern;
	private SubGroupDto subgroup;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public SubGroupPatternDto() {
		
	}
	
	public SubGroupPatternDto(SubGroupDto subgroup, ActionPatternsDto actionpattern) {
		this.subgroup=subgroup;
		this.actionpattern=actionpattern;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ActionPatternsDto getActionpattern() {
		return actionpattern;
	}
	public void setActionpattern(ActionPatternsDto actionpattern) {
		this.actionpattern = actionpattern;
	}
	public SubGroupDto getSubgroup() {
		return subgroup;
	}
	public void setSubgroup(SubGroupDto subgroup) {
		this.subgroup = subgroup;
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
