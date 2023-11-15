package com.idorsia.research.spirit.core.dto;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;

import com.actelion.research.business.ppg.formulation.PpgTreatment;
import com.actelion.research.business.spi.formulation.SpiFormulation;
import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.constants.Surgery;
import com.idorsia.research.spirit.core.dto.view.StudyAction;

public class NamedTreatmentDto implements IObject, StudyAction, Comparable<NamedTreatmentDto>, Serializable{
	private static final long serialVersionUID = -9054174139426852943L;
	private Integer id = Constants.NEWTRANSIENTID;
	private StudyDto study;
	private Boolean disease;
	private String name;
	private Color color=Color.BLACK;
	private Integer ppgtreatmentInstanceId;
	private Integer spiTreatmentId;
	private Surgery surgery;
	private String location;
	private String comments;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private int duration=60;
	private PpgTreatment ppgTreatment;
	private SpiFormulation spiTreatment;
	
	public NamedTreatmentDto(){
	}

	public NamedTreatmentDto(boolean isDisease){
		setDisease(isDisease);
	}
	
	public NamedTreatmentDto(NamedTreatmentDto namedTreatment){
		this.disease=namedTreatment.getDisease();
		this.name=namedTreatment.getName();
		this.color=namedTreatment.getColor();
		this.surgery=namedTreatment.getSurgery();
		this.location=namedTreatment.getLocation();
		this.comments=namedTreatment.getComments();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Boolean getDisease() {
		return disease;
	}
	public void setDisease(Boolean disease) {
		this.disease = disease;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Integer getPpgtreatmentInstanceId() {
		return ppgtreatmentInstanceId;
	}
	public void setPpgtreatmentInstanceId(Integer ppgtreatmentinstanceId) {
		this.ppgtreatmentInstanceId = ppgtreatmentinstanceId;
	}
	public Integer getSpiTreatmentId() {
		return spiTreatmentId;
	}
	public void setSpiTreatmentId(Integer spiTreatmentId) {
		this.spiTreatmentId = spiTreatmentId;
	}
	public Surgery getSurgery() {
		return surgery;
	}
	public void setSurgery(Surgery surgery) {
		this.surgery = surgery;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
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
	public StudyDto getStudy() {
		return study;
	}
	/**Do not call this method directly but call the service instead 
	 *NamedTreatmentService.setStudy(this, study);
	 */
	@Deprecated
	public void setStudy(StudyDto study) {
		this.study = study;
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public void setDuration(int duration) {
		this.duration=duration;
	}

	public PpgTreatment getPpgTreatment() {
		return ppgTreatment;
	}

	public void setPpgTreatment(PpgTreatment ppgTreatment) {
		this.ppgTreatment = ppgTreatment;
		ppgtreatmentInstanceId = ppgTreatment == null ? null : ppgTreatment.getTreatmentInstanceId();
	}

	public SpiFormulation getSpiTreatment() {
		return spiTreatment;
	}

	public void setSpiTreatment(SpiFormulation spiTreatment) {
		this.spiTreatment = spiTreatment;
		spiTreatmentId = spiTreatment == null ? null : spiTreatment.getId();
	}
	
	@Override
	public StudyActionType getType() {
		return getDisease() ? StudyActionType.DISEASE : StudyActionType.THERAPY;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(NamedTreatmentDto o) {
		if(this.equals(o)) 
			return 0;
		return CompareUtils.compare(getName(), o.getName());
	}
	
}
