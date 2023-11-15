package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Biotype implements Serializable, IObject {

	private static final long serialVersionUID = -7790356096403940355L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String category;
	private String description;
	private String name;
	private String prefix;
	private String amountUnit;
	private Integer parentId;
	private Integer isAbstract;
	private String containerType;
	private String nameLabel;
	private Integer isHidden;
	private Integer nameAutoComplete;
	private Integer nameRequired;
	private String creUser;
	private Date creDate;
	private String updUser;
	private Date updDate;
	private Integer hideSampleId;
	private Integer hideContainer;
	private Integer nameUnique;

	public Biotype() {
		super();
	}

	public Biotype(Integer id, Integer isAbstract, String cat) {
		super();
		this.id = id;
		this.isAbstract = isAbstract;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getAmountUnit() {
		return amountUnit;
	}

	public void setAmountUnit(String amountUnit) {
		this.amountUnit = amountUnit;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(Integer isAbstract) {
		this.isAbstract = isAbstract;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(String nameLabel) {
		this.nameLabel = nameLabel;
	}

	public Integer getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Integer isHidden) {
		this.isHidden = isHidden;
	}

	public Integer getNameAutoComplete() {
		return nameAutoComplete;
	}

	public void setNameAutoComplete(Integer nameAutoComplete) {
		this.nameAutoComplete = nameAutoComplete;
	}

	public Integer getNameRequired() {
		return nameRequired;
	}

	public void setNameRequired(Integer nameRequired) {
		this.nameRequired = nameRequired;
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

	public Integer getHideSampleId() {
		return hideSampleId;
	}

	public void setHideSampleId(Integer hideSampleId) {
		this.hideSampleId = hideSampleId;
	}

	public Integer getHideContainer() {
		return hideContainer;
	}

	public void setHideContainer(Integer hideContainer) {
		this.hideContainer = hideContainer;
	}

	public Integer getNameUnique() {
		return nameUnique;
	}

	public void setNameUnique(Integer nameUnique) {
		this.nameUnique = nameUnique;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amountUnit == null) ? 0 : amountUnit.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((containerType == null) ? 0 : containerType.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hideContainer == null) ? 0 : hideContainer.hashCode());
		result = prime * result + ((hideSampleId == null) ? 0 : hideSampleId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isAbstract == null) ? 0 : isAbstract.hashCode());
		result = prime * result + ((isHidden == null) ? 0 : isHidden.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nameAutoComplete == null) ? 0 : nameAutoComplete.hashCode());
		result = prime * result + ((nameLabel == null) ? 0 : nameLabel.hashCode());
		result = prime * result + ((nameRequired == null) ? 0 : nameRequired.hashCode());
		result = prime * result + ((nameUnique == null) ? 0 : nameUnique.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		Biotype other = (Biotype) obj;
		if (amountUnit == null) {
			if (other.amountUnit != null)
				return false;
		} else if (!amountUnit.equals(other.amountUnit))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (containerType == null) {
			if (other.containerType != null)
				return false;
		} else if (!containerType.equals(other.containerType))
			return false;
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hideContainer == null) {
			if (other.hideContainer != null)
				return false;
		} else if (!hideContainer.equals(other.hideContainer))
			return false;
		if (hideSampleId == null) {
			if (other.hideSampleId != null)
				return false;
		} else if (!hideSampleId.equals(other.hideSampleId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isAbstract == null) {
			if (other.isAbstract != null)
				return false;
		} else if (!isAbstract.equals(other.isAbstract))
			return false;
		if (isHidden == null) {
			if (other.isHidden != null)
				return false;
		} else if (!isHidden.equals(other.isHidden))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameAutoComplete == null) {
			if (other.nameAutoComplete != null)
				return false;
		} else if (!nameAutoComplete.equals(other.nameAutoComplete))
			return false;
		if (nameLabel == null) {
			if (other.nameLabel != null)
				return false;
		} else if (!nameLabel.equals(other.nameLabel))
			return false;
		if (nameRequired == null) {
			if (other.nameRequired != null)
				return false;
		} else if (!nameRequired.equals(other.nameRequired))
			return false;
		if (nameUnique == null) {
			if (other.nameUnique != null)
				return false;
		} else if (!nameUnique.equals(other.nameUnique))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		return true;
	}


}
