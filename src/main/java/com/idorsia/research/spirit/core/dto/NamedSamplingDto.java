package com.idorsia.research.spirit.core.dto;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.service.NamedSamplingService;

@Component
public class NamedSamplingDto implements IObject, StudyAction, Comparable<NamedSamplingDto>, Serializable {

	private static final long serialVersionUID = -7173150857706352810L;
	@Autowired
	private NamedSamplingService namedSamplingService;
	private Integer id = Constants.NEWTRANSIENTID;
	private String name;
	private StudyDto study;
	private Color color=Color.BLACK;
	private Boolean necropsy;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<SamplingDto> samplings;
	private int duration = 60;
	
	public NamedSamplingDto() {
	}

	public NamedSamplingDto(String name) {
		setName(name);
	}

	public NamedSamplingDto(NamedSamplingDto namedSampling) {
		this.name=namedSampling.getName();
		this.color=namedSampling.getColor();
		this.necropsy=namedSampling.getNecropsy();
	}

	public Integer getId() {
		return id;
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

	public StudyDto getStudy() {
		return study;
	}

	/**Do not call this method directly but call the service instead 
	 *NamedSamplingService.setStudy(this, study);
	 */
	@Deprecated
	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Boolean getNecropsy() {
		return necropsy==Boolean.TRUE;
	}

	public void setNecropsy(Boolean necropsy) {
		this.necropsy = necropsy;
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

	public List<SamplingDto> getSamplings() {
		if(samplings == null) {
			getNamedSamplingService().mapSamplings(this);
		}
		return samplings;
	}

	@Deprecated
	public List<SamplingDto> getSamplingsNoMapping() {
		return samplings;
	}
	
	public void setSamplings(List<SamplingDto> samplings) {
		this.samplings=samplings;
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public StudyActionType getType() {
		return StudyActionType.SAMPLING;
	}

	@Override
	public int compareTo(NamedSamplingDto o) {
		if(this==o) return 0;
		int c = -CompareUtils.compare(getStudy(), o.getStudy());
		if(c!=0) return c;

		c = (getNecropsy()?1:0) - (o.getNecropsy()?1:0);
		if(c!=0) return c;

		c = CompareUtils.compare(getName(), o.getName());
		if(c!=0) return c;
		return -(getId()-o.getId());
	}

	public NamedSamplingService getNamedSamplingService() {
		if(namedSamplingService == null) {
			namedSamplingService = (NamedSamplingService) ContextShare.getContext().getBean("namedSamplingService");
		}
		return namedSamplingService;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
