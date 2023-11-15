package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class StudyDocumentDto implements Serializable {

	private static final long serialVersionUID = -6100165893449682814L;
	private Integer id = Constants.NEWTRANSIENTID;
	private StudyDto study;
	private DocumentDto document;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public StudyDocumentDto() {
	}

	public StudyDocumentDto(StudyDto study, DocumentDto document) {
		this.study = study;
		this.document = document;
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

	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(DocumentDto document) {
		this.document = document;
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
