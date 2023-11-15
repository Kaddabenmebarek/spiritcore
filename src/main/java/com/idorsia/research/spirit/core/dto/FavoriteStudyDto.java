package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Study;

public class FavoriteStudyDto implements IObject, Serializable{

	private static final long serialVersionUID = 9173234245773551234L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer userId;
	private Study study;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}
}
