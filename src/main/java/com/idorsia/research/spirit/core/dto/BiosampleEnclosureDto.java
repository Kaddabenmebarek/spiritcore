package com.idorsia.research.spirit.core.dto;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dto.view.StudyAction;

public class BiosampleEnclosureDto implements IObject, StudyAction, Comparable<BiosampleEnclosureDto>, Serializable{

	private static final long serialVersionUID = 8356090080299948117L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiosampleDto biosample;
	private EnclosureDto enclosure;
	private PhaseDto phaseIn;
	private PhaseDto phaseOut;
	private StudyDto study;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public BiosampleEnclosureDto() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BiosampleDto getBiosample() {
		return biosample;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleEnclosureService.setBiosample(this, biosample);
	 */
	@Deprecated
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}

	public EnclosureDto getEnclosure() {
		return enclosure;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleEnclosureService.setEnclosure(this, enclosure);
	 */
	@Deprecated
	public void setEnclosure(EnclosureDto enclosure) {
		this.enclosure = enclosure;
	}

	public PhaseDto getPhaseIn() {
		return phaseIn;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleEnclosureService.setPhaseIn(this, phaseIn);
	 */
	@Deprecated
	public void setPhaseIn(PhaseDto phaseIn) {
		this.phaseIn = phaseIn;
	}

	public PhaseDto getPhaseOut() {
		return phaseOut;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleEnclosureService.setPhaseOut(this, phaseOut);
	 */
	@Deprecated
	public void setPhaseOut(PhaseDto phaseOut) {
		this.phaseOut = phaseOut;
	}

	public StudyDto getStudy() {
		return study;
	}

	public void setStudy(StudyDto study) {
		this.study = study;
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
	public String getName() {
		return "Move in";
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public void setDuration(int duration) {
	}

	@Override
	public StudyActionType getType() {
		return null;
	}
	
	 @Override
     public Color getColor() {
         return null;
     }

	@Override
	public int compareTo(BiosampleEnclosureDto s) {
		if(s==null) 
			return 1;
        return getPhaseIn().compareTo(((BiosampleEnclosureDto)s).getPhaseIn());
	}

	@Override
	public int hashCode() {
		return (phaseIn == null ? 0 : phaseIn.hashCode())+(biosample==null ? 0 : biosample.hashCode())+(enclosure==null ? 0 : enclosure.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BiosampleEnclosureDto other = (BiosampleEnclosureDto) obj;
		if (enclosure == null) {
			if (other.enclosure != null)
				return false;
		} else if (!enclosure.equals(other.enclosure))
			return false;
		if (phaseIn == null) {
			if (other.phaseIn != null)
				return false;
		} else if (!phaseIn.equals(other.phaseIn))
			return false;
		if (biosample == null) {
			if (other.biosample != null)
				return false;
		} else if (!biosample.equals(other.biosample))
			return false;
		return true;
	}
	
	
}
