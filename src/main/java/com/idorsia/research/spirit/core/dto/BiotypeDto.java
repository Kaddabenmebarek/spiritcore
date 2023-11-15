package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.AmountUnit;
import com.idorsia.research.spirit.core.constants.BiotypeCategory;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.service.BiotypeService;

@Component
public class BiotypeDto implements IObject, Comparable<BiotypeDto>, Serializable{
	
	private static final long serialVersionUID = -1165231738776084496L;
	@Autowired
	private transient BiotypeService biotypeService;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiotypeCategory category;
	private String description;
	private String name;
	private String prefix;
	private AmountUnit amountUnit;
	private transient BiotypeDto parent;
	private Boolean isAbstract;
	private transient ContainerType containerType;
	private String nameLabel;
	private Boolean isHidden;
	private Boolean nameAutoComplete;
	private Boolean nameRequired;
	private String creUser;
	private Date creDate;
	private String updUser;
	private Date updDate;
	private Boolean hideSampleId;
	private Boolean hideContainer;
	private Boolean nameUnique;
	private transient List<BiotypeMetadataDto> metadatas;
	private transient List<BiotypeDto> children;
	/**Attributes used for displaying the hierarchy*/
	private int depth;
	
	
	public BiotypeDto() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BiotypeCategory getCategory() {
		return category;
	}
	public void setCategory(BiotypeCategory category) {
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
	public AmountUnit getAmountUnit() {
		return amountUnit;
	}
	public void setAmountUnit(AmountUnit amountUnit) {
		this.amountUnit = amountUnit;
	}
	public BiotypeDto getParent() {
		return parent;
	}
	public void setParent(BiotypeDto parent) {
		this.parent = parent;
	}
	public Boolean getIsAbstract() {
		return isAbstract==Boolean.TRUE;
	}
	public void setIsAbstract(Boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public ContainerType getContainerType() {
		return containerType;
	}
	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}
	public String getNameLabel() {
		return nameLabel==null || nameLabel.length()==0? null: nameLabel;
	}
	public void setNameLabel(String nameLabel) {
		this.nameLabel = nameLabel;
	}
	public Boolean getIsHidden() {
		return isHidden==Boolean.TRUE;
	}
	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}
	public Boolean getNameAutoComplete() {
		return nameAutoComplete==Boolean.TRUE;
	}
	public void setNameAutoComplete(Boolean nameAutoComplete) {
		this.nameAutoComplete = nameAutoComplete;
	}
	public Boolean getNameRequired() {
		return nameRequired==Boolean.TRUE;
	}
	public void setNameRequired(Boolean nameRequired) {
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
	public Boolean getHideSampleId() {
		return hideSampleId==Boolean.TRUE;
	}
	public void setHideSampleId(Boolean hideSampleId) {
		this.hideSampleId = hideSampleId;
	}
	public Boolean getHideContainer() {
		return hideContainer==Boolean.TRUE;
	}
	public void setHideContainer(Boolean hideContainer) {
		this.hideContainer = hideContainer;
	}
	public Boolean getNameUnique() {
		return nameUnique==Boolean.TRUE;
	}
	public void setNameUnique(Boolean nameUnique) {
		this.nameUnique = nameUnique;
	}
	
	public void setMetadatas(List<BiotypeMetadataDto> metadatas) {
		this.metadatas=metadatas;
	}

	public List<BiotypeMetadataDto> getMetadatas() {
		if(metadatas == null) {
			getBiotypeService().mapMetadatas(this);
		}
		return metadatas;
	}
	
	@Deprecated
	public List<BiotypeMetadataDto> getMetadatasNoMapping() {
		return metadatas;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	@Override
	public int compareTo(BiotypeDto o) {
		if(this.equals(o)) return 0;
		if(o==null) return 1;

		LinkedList<BiotypeDto> l1 = new LinkedList<>();
		BiotypeDto b = this;
		while(b!=null) {
			l1.addFirst(b);
			b = b.getParent();
		}
		LinkedList<BiotypeDto> l2 = new LinkedList<>();
		b = o;
		while(b!=null) {
			l2.addFirst(b);
			b = b.getParent();
		}

		for (int i = 0; i<l1.size() || i<l2.size(); i++) {
			if(i>=l1.size()) return -1;
			if(i>=l2.size()) return 1;

			BiotypeDto b1 = l1.get(i);
			BiotypeDto b2 = l2.get(i);

			int c = b1.getIsHidden()? (b2.getIsHidden()?0: 1): (b2.getIsHidden()?-1: 0);
			if(c!=0) return c;

			c = b1.getCategory()==null? (b2.getCategory()==null?0: 1): b1.getCategory().compareTo(b2.getCategory());
			if(c!=0) return c;

			c = b1.getName().compareToIgnoreCase(b2.getName());
			if(c!=0) return c;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BiotypeDto == false)
			return false;
		if (this == obj)
			return true;
		BiotypeDto t2 = (BiotypeDto) obj;

		if (getId() > 0 && t2.getId() > 0) {
			return (int) getId() == (int) t2.getId();
		} else {
			return (getName()==null && t2.getName()==null)
					|| (getName()!=null && getName().equals(t2.getName()));
		}
	}

	public List<BiotypeDto> getChildren() {
		if(children == null) {
			biotypeService.mapChildren(this);
		}
		return children;
	}
	
	@Deprecated
	public List<BiotypeDto> getChildrenNoMapping() {
		return children;
	}

	public void setChildren(List<BiotypeDto> children) {
		this.children = children;
	}

	public BiotypeService getBiotypeService() {
		if(biotypeService == null) {
			biotypeService = (BiotypeService) ContextShare.getContext().getBean("biotypeService");
		}
		return biotypeService;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
