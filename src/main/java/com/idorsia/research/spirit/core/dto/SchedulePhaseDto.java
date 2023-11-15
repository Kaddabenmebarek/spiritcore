package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.service.SchedulePhaseService;

@Component
public class SchedulePhaseDto implements IObject, Serializable{

	private static final long serialVersionUID = 7583846705386254828L;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	private Integer id = Constants.NEWTRANSIENTID;
	private ScheduleDto schedule;
	private PhaseDto phase;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Set<ExecutionDetailDto> executionDetails;

	public SchedulePhaseDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ScheduleDto getSchedule() {
		return schedule;
	}

	/**Do not call this method directly but call the service instead 
	 *SchedulePhaseService.setSchedule(this, schedule, false);
	 */
	@Deprecated()
	public void setSchedule(ScheduleDto schedule) {
		this.schedule = schedule;
	}

	public PhaseDto getPhase() {
		return phase;
	}

	/**Do not call this method directly but call the service instead 
	 *SchedulePhaseService.setPhase(this, phase, false);
	 */
	@Deprecated()
	public void setPhase(PhaseDto phase) {
		this.phase = phase;
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
	
	 public Set<ExecutionDetailDto> getExecutionDetails() {
		 if(executionDetails == null) {
			 getSchedulePhaseService().mapExecutionDetails(this);
		 }
        return executionDetails;
    }

	 @Deprecated
	 public Set<ExecutionDetailDto> getExecutionDetailsNoMapping() {
        return executionDetails;
	 }
	 
	/**Do not call this method directly but call the service instead 
	*SchedulePhaseService.addExecutionDetail(this, executionDetail)/removeExecutionDetail(this, executionDetail);
	*/
	 @Deprecated
	public void setExecxutionDetails(Set<ExecutionDetailDto> executionDetails) {
		this.executionDetails=executionDetails;
	}

	public SchedulePhaseService getSchedulePhaseService() {
		if(schedulePhaseService == null) {
			schedulePhaseService = (SchedulePhaseService) ContextShare.getContext().getBean("schedulePhaseService");
		}
		return schedulePhaseService;
	}
	 
	
}
