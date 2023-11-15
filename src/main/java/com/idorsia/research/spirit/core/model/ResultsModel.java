package com.idorsia.research.spirit.core.model;

public class ResultsModel {

	private int studyId;
	private int assayResultId;
	private int bioSampleId;
	private String bioSampleName;
	private String biosampleAssignmentName;
	private int phaseId;
	private String resultTextValue;
	private long resultNumericValue;
	private String assayAttributName;
	private String assayCategory;
	private String assayName;
	private String attributeDatatype;

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getAssayResultId() {
		return assayResultId;
	}

	public void setAssayResultId(int assayResultId) {
		this.assayResultId = assayResultId;
	}

	public int getBioSampleId() {
		return bioSampleId;
	}

	public void setBioSampleId(int bioSampleId) {
		this.bioSampleId = bioSampleId;
	}

	public String getBioSampleName() {
		return bioSampleName;
	}

	public void setBioSampleName(String bioSampleName) {
		this.bioSampleName = bioSampleName;
	}

	public String getBiosampleAssignmentName() {
		return biosampleAssignmentName;
	}

	public void setBiosampleAssignmentName(String biosampleAssignmentName) {
		this.biosampleAssignmentName = biosampleAssignmentName;
	}

	public int getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(int phaseId) {
		this.phaseId = phaseId;
	}

	public String getResultTextValue() {
		return resultTextValue;
	}

	public void setResultTextValue(String resultTextValue) {
		this.resultTextValue = resultTextValue;
	}

	public long getResultNumericValue() {
		return resultNumericValue;
	}

	public void setResultNumericValue(long resultNumericValue) {
		this.resultNumericValue = resultNumericValue;
	}

	public String getAssayAttributName() {
		return assayAttributName;
	}

	public void setAssayAttributName(String assayAttributName) {
		this.assayAttributName = assayAttributName;
	}

	public String getAssayCategory() {
		return assayCategory;
	}

	public void setAssayCategory(String assayCategory) {
		this.assayCategory = assayCategory;
	}

	public String getAssayName() {
		return assayName;
	}

	public void setAssayName(String assayName) {
		this.assayName = assayName;
	}

	public String getAttributeDatatype() {
		return attributeDatatype;
	}

	public void setAttributeDatatype(String attributeDatatype) {
		this.attributeDatatype = attributeDatatype;
	}

}
