package com.idorsia.research.spirit.core.dto;

import java.util.Date;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;

public class SamplingMeasurementAttributeDto implements IObject, Comparable<SamplingMeasurementAttributeDto>{

	private Integer id = Constants.NEWTRANSIENTID;
	private AssayAttributeDto assayAttribute;
	private SamplingMeasurementDto samplingMeasurement;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public AssayAttributeDto getAssayAttribute() {
		return assayAttribute;
	}
	public void setAssayAttribute(AssayAttributeDto assayAttribute) {
		this.assayAttribute = assayAttribute;
	}
	public SamplingMeasurementDto getSamplingMeasurement() {
		return samplingMeasurement;
	}
	public void setSamplingMeasurement(SamplingMeasurementDto samplingMeasurement) {
		this.samplingMeasurement = samplingMeasurement;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	@Override
	public int compareTo(SamplingMeasurementAttributeDto o) {
		return CompareUtils.compare(getAssayAttribute(), o.getAssayAttribute());
	}
}
