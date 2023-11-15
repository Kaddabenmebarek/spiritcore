package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;

public class AssayResultValueDto implements IObject, Comparable<AssayResultValueDto>, Serializable{
	private static final long serialVersionUID = -2670699115601061434L;
	private Integer id=Constants.NEWTRANSIENTID;
	private String textValue="";
	private AssayAttributeDto assayAttribute;
	private AssayResultDto assayResult;
	private DocumentDto document;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Double calculatedValue = null;
	private BiosampleDto linkedBiosample;

	public AssayResultValueDto() {
	}

	public AssayResultValueDto(AssayResultDto assayResult, AssayAttributeDto assayAttribute, String textValue) {
		this.assayResult = assayResult;
		this.assayAttribute = assayAttribute;
		this.textValue = textValue;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public AssayAttributeDto getAssayAttribute() {
		return assayAttribute;
	}

	public void setAssayAttribute(AssayAttributeDto assayAttribute) {
		this.assayAttribute = assayAttribute;
	}

	public AssayResultDto getAssayResult() {
		return assayResult;
	}

	public void setAssayResult(AssayResultDto assayResult) {
		this.assayResult = assayResult;
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
	

	public Double getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(Double calculatedValue) {
		this.calculatedValue = calculatedValue;
	}


	@Override
	public int compareTo(AssayResultValueDto o) {
		int c = CompareUtils.compare(getAssayAttribute(), o.getAssayAttribute());
		if(c!=0) return c;
		return CompareUtils.compare(getTextValue(), o.getTextValue());
	}

	public BiosampleDto getLinkedBiosample() {
		return linkedBiosample;
	}
	
	public void setLinkedBiosample(BiosampleDto linkedBiosample) {
		this.linkedBiosample=linkedBiosample;
	}
	
	@Override
	public String toString() {
		return getAssayAttribute().getName()+" : "+getTextValue();
	}
}
