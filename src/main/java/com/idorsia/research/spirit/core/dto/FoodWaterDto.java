package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;

public class FoodWaterDto implements IObject, Serializable, Comparable<FoodWaterDto>{

	private static final long serialVersionUID = -5947064010378899328L;
	private Integer id = Constants.NEWTRANSIENTID;
	private EnclosureDto enclosure;
	private Double foodTare;
	private Double foodWeight;
	private Double waterTare;
	private Double waterWeight;
	private Date fwDate;
	private Date creDate;
	private Date updDate;
	private String creUser;
	private String updUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EnclosureDto getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(EnclosureDto enclosure) {
		this.enclosure = enclosure;
	}

	public Double getFoodTare() {
		return foodTare;
	}

	public void setFoodTare(Double foodTare) {
		this.foodTare = foodTare;
	}

	public Double getFoodWeight() {
		return foodWeight;
	}

	public void setFoodWeight(Double foodWeight) {
		this.foodWeight = foodWeight;
	}

	public Double getWaterTare() {
		return waterTare;
	}

	public void setWaterTare(Double waterTare) {
		this.waterTare = waterTare;
	}

	public Double getWaterWeight() {
		return waterWeight;
	}

	public void setWaterWeight(Double waterWeight) {
		this.waterWeight = waterWeight;
	}

	public Date getFwDate() {
		return fwDate;
	}

	public void setFwDate(Date fwDate) {
		this.fwDate = fwDate;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
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

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	@Override
	public int compareTo(FoodWaterDto o) {
		int c = CompareUtils.compare(getEnclosure(), o.getEnclosure());
		if(c!=0) return c;
		c = CompareUtils.compare(getFwDate(), o.getFwDate());
		return c;
	}

}
