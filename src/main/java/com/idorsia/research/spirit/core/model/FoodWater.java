package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class FoodWater implements Serializable, IObject {

	private static final long serialVersionUID = 8479439962879163746L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer enclosureId;
	private Double foodTare;
	private Double foodWeight;
	private Double waterTare;
	private Double waterWeight;
	private Date fwDate;
	private Date creDate;
	private Date updDate;
	private String creUser;
	private String updUser;

	public FoodWater() {
		super();
	}

	public FoodWater(Integer id, Date creDate, Date updDate, String creUser, String updUser) {
		super();
		this.id = id;
		this.creDate = creDate;
		this.updDate = updDate;
		this.creUser = creUser;
		this.updUser = updUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEnclosureId() {
		return enclosureId;
	}

	public void setEnclosureId(Integer enclosureId) {
		this.enclosureId = enclosureId;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((enclosureId == null) ? 0 : enclosureId.hashCode());
		result = prime * result + ((foodTare == null) ? 0 : foodTare.hashCode());
		result = prime * result + ((foodWeight == null) ? 0 : foodWeight.hashCode());
		result = prime * result + ((fwDate == null) ? 0 : fwDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((waterTare == null) ? 0 : waterTare.hashCode());
		result = prime * result + ((waterWeight == null) ? 0 : waterWeight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FoodWater other = (FoodWater) obj;
		if (creDate == null) {
			if (other.creDate != null)
				return false;
		} else if (!creDate.equals(other.creDate))
			return false;
		if (creUser == null) {
			if (other.creUser != null)
				return false;
		} else if (!creUser.equals(other.creUser))
			return false;
		if (enclosureId == null) {
			if (other.enclosureId != null)
				return false;
		} else if (!enclosureId.equals(other.enclosureId))
			return false;
		if (foodTare == null) {
			if (other.foodTare != null)
				return false;
		} else if (!foodTare.equals(other.foodTare))
			return false;
		if (foodWeight == null) {
			if (other.foodWeight != null)
				return false;
		} else if (!foodWeight.equals(other.foodWeight))
			return false;
		if (fwDate == null) {
			if (other.fwDate != null)
				return false;
		} else if (!fwDate.equals(other.fwDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (waterTare == null) {
			if (other.waterTare != null)
				return false;
		} else if (!waterTare.equals(other.waterTare))
			return false;
		if (waterWeight == null) {
			if (other.waterWeight != null)
				return false;
		} else if (!waterWeight.equals(other.waterWeight))
			return false;
		return true;
	}

}
