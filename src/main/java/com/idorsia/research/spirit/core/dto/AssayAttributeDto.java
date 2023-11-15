package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.constants.OutputType;
import com.idorsia.research.spirit.core.util.MiscUtils;

public class AssayAttributeDto implements IObject, Comparable<AssayAttributeDto>, Serializable{
	
	private static final long serialVersionUID = 1999279449182663477L;
	private Integer id = Constants.NEWTRANSIENTID;
	private AssayDto assay;
	private String name;
	private DataType dataType;
	private OutputType outputType;
	private boolean isRequired = false;
	private String parameters;
	private int idx;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public AssayAttributeDto() {
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

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public Integer getId() {
		return id;
	}

	public AssayDto getAssay() {
		return assay;
	}

	public void setAssay(AssayDto assay) {
		this.assay = assay;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public String[] getParametersArray() {
		if(this.parameters==null) return new String[0];
		return MiscUtils.split(this.parameters);
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public OutputType getOutputType() {
		return outputType;
	}
	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(AssayAttributeDto o) {
		int c = getAssay()==null? (o.getAssay()==null?0:1): getAssay().compareTo(o.getAssay());
		if(c!=0) return c;

		c = getOutputType().compareTo(o.getOutputType());
		if(c!=0) return c;

		c = getIdx() - o.getIdx();
		if(c!=0) return c;

		c = getName()==null? (o.getName()==null?0: 1): getName().compareTo(o.getName());
		return c;
	}
}
