package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.ScheduleService;

import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.util.Recurrence;

@Component
public class ScheduleDto implements IObject, Serializable{

	private static final long serialVersionUID = 5685692651265233736L;
	@Autowired
	private ScheduleService scheduleService;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer startDate = 0;
	private Duration lastPhase;
	private String rRule;
	private String timePoints;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private ActionPatternsDto actionPattern;
	private Set<SchedulePhaseDto> schedulePhases;
	private ArrayList<LocalTime> timepointSet;
	private transient Recurrence recurrence;
	private transient RecurrenceRuleScribe scribe = new RecurrenceRuleScribe();
	
	public ScheduleDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStartDate() {
		return startDate;
	}

	/**Do not call this method directly but call the service instead 
	 *ScheduleService.setStartDate(this, startDate);
	 */
	@Deprecated
	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public Duration getLastPhase() {
		return lastPhase;
	}

	public void setLastPhase(Duration lastPhase) {
		this.lastPhase = lastPhase;
	}

	public String getrRule() {
		return rRule;
	}

	/**Do not call this method directly but call the service instead 
	 *ScheduleService.setRrule(this, rRule);
	 */
	@Deprecated()
	public void setrRule(String rRule) {
		this.rRule = rRule;
	}

	public String getTimePoints() {
		return timePoints;
	}

	public void setTimePoints(String timePoints) {
		this.timePoints = timePoints;
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
	
	public ActionPatternsDto getActionPattern() {
		return this.actionPattern;
	}
	
	/**Do not call this method directly but call the service instead 
	 *ScheduleService.setActionPattern(this, actionPattern);
	 */
	@Deprecated()
	public void setActionPattern(ActionPatternsDto actionPattern) {
		this.actionPattern=actionPattern;
	}

	public Set<SchedulePhaseDto> getSchedulePhases() {
		if(schedulePhases == null) {
			getScheduleService().mapSchedulePhases(this);
		}
		return schedulePhases;
	}

	@Deprecated
	public Set<SchedulePhaseDto> getSchedulePhasesNoMapping() {
		return schedulePhases;
	}
	
	/**Do not call this method directly but call the service instead 
	 *ScheduleService.addSchedulePhase(this, schedulePhase)/removeSchedulePhase(this, schedulePhase);
	 */
	@Deprecated
	public void setSchedulePhases(Set<SchedulePhaseDto> schedulePhases) {
		this.schedulePhases=schedulePhases;
	}

	public Recurrence getRecurrence() {
		return recurrence;
	}

	/**Do not call this method directly but call the service instead 
	 *ScheduleService.setRecurrence(this, recurrence);
	 */
	@Deprecated()
	public void setRecurrence(Recurrence recurrence) {
		this.recurrence = recurrence;
	}

	public ArrayList<LocalTime> getTimepointSet() {
		if(timepointSet == null) {
			getScheduleService().createRecurrence(this);
		}
		return timepointSet;
	}

	public void setTimepointSet(ArrayList<LocalTime> timepointSet) {
		this.timepointSet = timepointSet;
	}
	
	public List<LocalTime> getTimePointsSorted() {
		Collections.sort(timepointSet);
		return Collections.unmodifiableList(timepointSet);
	}

	public RecurrenceRuleScribe getScribe() {
		return scribe;
	}

	public void setScribe(RecurrenceRuleScribe scribe) {
		this.scribe = scribe;
	}

	public ScheduleService getScheduleService() {
		if(scheduleService == null) {
			scheduleService = (ScheduleService) ContextShare.getContext().getBean("scheduleService");
		}
		return scheduleService;
	}
	
	
}
