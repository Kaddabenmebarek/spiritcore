package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class StagePatternDto implements IObject, Serializable{

	private static final long serialVersionUID = 1784821254085401120L;
	private Integer id = Constants.NEWTRANSIENTID;
	private ActionPatternsDto actionpattern;
	private StageDto stage;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public StagePatternDto() {
	}

	public StagePatternDto(StageDto stage, ActionPatternsDto actionPattern) {
		this.stage=stage;
		this.actionpattern=actionPattern;
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

	public StageDto getStage() {
		return stage;
	}

	public void setStage(StageDto stage) {
		this.stage = stage;
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
