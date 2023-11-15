package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class SamplingParameterDto implements IObject, Serializable, Comparable<SamplingParameterDto> {

	private static final long serialVersionUID = 5710135094494183924L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiotypeMetadataDto biotypemetadata;
	private SamplingDto sampling;
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

	public BiotypeMetadataDto getBiotypemetadata() {
		return biotypemetadata;
	}

	public void setBiotypemetadata(BiotypeMetadataDto biotypemetadata) {
		this.biotypemetadata = biotypemetadata;
	}

	public SamplingDto getSampling() {
		return sampling;
	}

	public void setSampling(SamplingDto sampling) {
		this.sampling = sampling;
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
	public int compareTo(SamplingParameterDto o) {
		if(o==null) return -1;
		if (o.equals(this)) return 0;
		int c = o.getSampling().compareTo(getSampling());
		if(c!=0)
			return c;
		return o.getBiotypemetadata().compareTo(getBiotypemetadata());
	}

}
