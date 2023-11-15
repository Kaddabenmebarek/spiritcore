package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.MiscUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;

public class BiotypeMetadataDto implements Comparable<BiotypeMetadataDto>, IObject, Serializable{

	private static final long serialVersionUID = 640226475589506571L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer idx;
	private String name;
	private String parameters;
	private Boolean required;
	private BiotypeDto biotype;
	private String description;
	private DataType datatype = DataType.ALPHA;
	private Boolean hidefromdisplay = false;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Boolean getRequired() {
		return required==Boolean.TRUE;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public BiotypeDto getBiotype() {
		return biotype;
	}

	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataType getDatatype() {
		return datatype;
	}

	public void setDatatype(DataType datatype) {
		this.datatype = datatype;
	}

	public Boolean getHidefromdisplay() {
		return hidefromdisplay;
	}

	public void setHidefromdisplay(Boolean hidefromdisplay) {
		this.hidefromdisplay = hidefromdisplay;
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
	
	@Override
	public String toString() {
		return getName();
	}

	public String[] getParametersArray() {
		if(getParameters()==null) return new String[0];
		return MiscUtils.split(getParameters());
	}

	@Override
	public int compareTo(BiotypeMetadataDto o) {
		int c = CompareUtils.compare(getBiotype(), o.getBiotype());
		if(c!=0) return c;
		c = CompareUtils.compare(getIdx(), o.getIdx());
		if(c!=0) return c;
		c = (getName()==null?"":getName()).compareTo((o.getName()==null?"":o.getName()));
		return c;
	}

}
