package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class AssignmentPatternDto implements IObject, Serializable{

	private static final long serialVersionUID = -8285052095234469320L;
	private Integer id = Constants.NEWTRANSIENTID;
	private ActionPatternsDto actionpattern;
	private AssignmentDto assignment;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public AssignmentPatternDto() {
	}

	public AssignmentPatternDto(AssignmentDto assignment, ActionPatternsDto actionpattern) {
		this.assignment=assignment;
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

	public AssignmentDto getAssignment() {
		return assignment;
	}

	public void setAssignment(AssignmentDto assignment) {
		this.assignment = assignment;
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
