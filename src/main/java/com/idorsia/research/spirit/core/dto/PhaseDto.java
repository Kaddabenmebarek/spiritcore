package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.PhaseService;

@Component
public class PhaseDto implements IObject, Comparable<PhaseDto>, Serializable{

	private static final long serialVersionUID = 3161678759300493883L;
	@Autowired
	private PhaseService phaseService;
	private Integer id = Constants.NEWTRANSIENTID;
	private StageDto stage;
	private Duration phase;
	private String label;
	private List<SchedulePhaseDto> schedulePhases;

	public PhaseDto() {
	}
	
	public PhaseDto(Duration phase) {
		setPhase(phase);
	}

	public PhaseDto(PhaseDto phase) {
		this.phase=phase.getPhase();
		this.label=phase.getLabel();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StageDto getStage() {
		return stage;
	}

	/**Do not call this method directly but call the service instead 
	 *PhaseService.setStage(this, stage, false);
	 */
	@Deprecated()
	public void setStage(StageDto stage) {
		this.stage = stage;
	}

	public Duration getPhase() {
		return phase;
	}

	public void setPhase(Duration phase) {
		this.phase = phase;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<SchedulePhaseDto> getSchedulePhases() {
		if(schedulePhases == null) {
			getPhaseService().mapSchedulePhases(this);
		}
		return schedulePhases;
	}

	@Deprecated
	public List<SchedulePhaseDto> getSchedulePhasesNoMapping() {
		return schedulePhases;
	}
	
	/**Do not call this method directly but call the service instead 
	 *PhaseService.addSchedulePhase(this, schedulePhase)/removeSchedulePhase(this, schedulePhase);
	 */
	@Deprecated
	public void setSchedulePhases(List<SchedulePhaseDto> schedulePhases) {
		this.schedulePhases = schedulePhases;
	}

	@Override
	public int compareTo(PhaseDto o) {
		if(this==o) return 0;
		if(o==null) return -1;
		if(getStage() == null || o.getStage() == null) {
			if(getStage()==o.getStage()) return 0;
			if(o.getStage()==null) return -1;
			if(getStage()==null) return 1;
		}
		int c = getStage().compareTo(o.getStage());
		if(c!=0) return c;
		if(getPhase()==o.getPhase()) return 0;
		if(o.getPhase()==null) return -1;
		if(getPhase()==null) return 1;
		c = getPhase().compareTo(o.getPhase());
		return c;
	}
	
	@Override
	public String toString() {
		return getPhaseService().getShortName(this) + (label == null ? "" : " " + label);
	}
	
	public PhaseService getPhaseService() {
		if(phaseService == null) {
			phaseService = (PhaseService) ContextShare.getContext().getBean("phaseService");
		}
		return phaseService;
	}
	
	
}
