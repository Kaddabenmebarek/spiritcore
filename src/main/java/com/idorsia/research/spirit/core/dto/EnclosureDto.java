package com.idorsia.research.spirit.core.dto;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.EnclosureService;

@Component
public class EnclosureDto implements IObject, Comparable<EnclosureDto>, Serializable{
	
	private static final long serialVersionUID = 7468546429743041301L;
	@Autowired
	private EnclosureService enclosureService;
	private Integer id = Constants.NEWTRANSIENTID;
	private StudyDto study;
	private String name;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<BiosampleEnclosureDto> biosampleEnclosures;
	private List<FoodWaterDto> foodWaters;

	public EnclosureDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StudyDto getStudy() {
		return study;
	}

	/**Do not call this method directly but call the service instead 
	 *EnclosureService.setStudy(this, study);
	 */
	@Deprecated
	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public String getName() {
		return name;
	}

	@Deprecated
	/**Do not call this method directly but call the service instead
	 *EnclosureService.generateNewName(this, study, name);
	 */
	public void setName(String name) {
		this.name = name;
	}

	public List<BiosampleEnclosureDto> getBiosampleEnclosures() {
		if(biosampleEnclosures == null) {
			getEnclosureService().mapBiosampleEnclosures(this);
		}
		return biosampleEnclosures;
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

	@Deprecated
	public List<BiosampleEnclosureDto> getBiosampleEnclosuresNoMapping() {
		return biosampleEnclosures;
	}
	
	/**Do not call this method directly but call the service instead 
	 *EnclosureService.addBiosampleEnclosure(this, biosampleEnclosure)/removeBiosampleEnclosure(this, biosampleEnclosure);
	 */
	@Deprecated
	public void setBiosampleEnclosures(List<BiosampleEnclosureDto> biosampleEnclosures) {
		this.biosampleEnclosures = biosampleEnclosures;
	}

	public List<FoodWaterDto> getFoodWaters(){
		if(foodWaters == null) {
			getEnclosureService().mapFoodWaters(this);
		}
		return this.foodWaters;
	}
	
	@Deprecated
	public List<FoodWaterDto> getFoodWatersNoMapping(){
		return this.foodWaters;
	}
	
	public void setFoodWater(List<FoodWaterDto> foodWaters) {
		this.foodWaters=foodWaters;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(EnclosureDto o) {
		if(o==null) return 1;
        return (name==null?"":name).compareTo(o.getName());
	}

	public EnclosureService getEnclosureService() {
		if(enclosureService == null) {
			enclosureService = (EnclosureService) ContextShare.getContext().getBean("enclosureService");
		}
		return enclosureService;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		EnclosureDto other = (EnclosureDto) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
