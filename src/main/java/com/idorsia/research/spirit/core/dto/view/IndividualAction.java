package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Objects;

import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.StudyDto;

public class IndividualAction implements Serializable {
	private static final long serialVersionUID = -3428192677888890356L;
	private SchedulePhaseDto schedule;
	private AssignmentDto assignment;
	private StudyAction action;
	private BiosampleDto biosample;
	private PhaseDto phase;
	private StudyDto study;
	private Execution execution;

	public IndividualAction(SchedulePhaseDto scheduleDto, AssignmentDto assignment) {
		this.schedule = scheduleDto;
		this.assignment = assignment;
		this.biosample = assignment.getBiosample();
		this.action = schedule.getSchedule().getActionPattern().getAction();
		this.phase = schedule.getPhase();
	}

	public IndividualAction(StudyAction action, BiosampleDto biosample, Execution execution, PhaseDto phase,
			StudyDto study) {
		this.action = action;
		this.biosample = biosample;
		this.execution = execution;
		this.phase = phase;
		this.study = study;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		IndividualAction that = (IndividualAction) o;
		BiosampleDto biosample = getBiosample();
		if (biosample == null && getAssignment() != null)
			biosample = getAssignment().getBiosample();
		BiosampleDto biosampleThat = that.getBiosample();
		if (biosampleThat == null && that.getAssignment() != null)
			biosampleThat = that.getAssignment().getBiosample();
		return Objects.equals(getStudyAction(), that.getStudyAction()) && Objects.equals(biosample, biosampleThat)
				&& Objects.equals(getPhase(), that.getPhase());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStudyAction(), getPhase());
	}

	public PhaseDto getPhase() {
		return phase;
	}

	public Execution getExecution() {
		return execution;
	}

	public SchedulePhaseDto getSchedulePhase() {
		return schedule;
	}

	public AssignmentDto getAssignment() {
		return assignment;
	}

	public StudyAction getStudyAction() {
		return action;
	}

	public StudyDto getStudy() {
		return study;
	}

	public BiosampleDto getBiosample() {
		return biosample;
	}

	public boolean isExecuted() {
		return getExecution() != null;
	}
}
