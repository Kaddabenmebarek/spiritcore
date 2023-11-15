package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;

public class ExecutionDetailDto implements IObject, Serializable{

	private static final long serialVersionUID = -4299006353679154295L;
	private Integer id = Constants.NEWTRANSIENTID;
	private SchedulePhaseDto phaseLink;
	private AssignmentDto assignment;
	private Duration deviation;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	
	public ExecutionDetailDto() {
	}
	public ExecutionDetailDto(ExecutionDetailDto executionDetailDto) {
		this.deviation=executionDetailDto.getDeviation();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SchedulePhaseDto getPhaseLink() {
		return phaseLink;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setSchedulePhase(this, scheulePhase);
	 */
	@Deprecated
	public void setPhaseLink(SchedulePhaseDto phaseLink) {
		this.phaseLink = phaseLink;
	}

	public AssignmentDto getAssignment() {
		return assignment;
	}

	/**Do not call this method directly but call the service instead 
	 *AssignmentService.setAssignment(this, assignment);
	 */
	@Deprecated
	public void setAssignment(AssignmentDto assignment) {
		this.assignment = assignment;
	}

	public Duration getDeviation() {
		return deviation == null ? Duration.ZERO : deviation;
	}

	public void setDeviation(Duration deviation) {
		this.deviation = deviation;
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

	public PhaseDto getPhase() {
        return getPhaseLink() != null ? getPhaseLink().getPhase() : null;
    }
	
}
