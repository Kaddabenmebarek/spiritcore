package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;

public class StudyActionResultQuery implements Serializable {

	private static final long serialVersionUID = 8185676485076998093L;
	private StudyAction action;
	private SubjectSet subjectSet;
	private StudyDto study;
	private StageDto stage;
	private Duration lastphase;
	private Set<PhaseDto> phases;
	private ScheduleDto schedule;
	private boolean batch = true;

	public StudyActionResultQuery() {}

	public StudyAction getAction() {
		return action;
	}

	public void setAction(StudyAction action) {
		this.action = action;
	}

	public SubjectSet getSubjectSet() {
		return subjectSet;
	}

	public void setSubjectSet(SubjectSet subjectSet) {
		this.subjectSet = subjectSet;
	}

	public StudyDto getStudy() {
		return study;
	}

	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public StageDto getStage() {
		return stage;
	}

	public void setStage(StageDto stage) {
		this.stage = stage;
	}

	public Duration getLastphase() {
		return lastphase;
	}

	public void setLastphase(Duration lastphase) {
		this.lastphase = lastphase;
	}

	public Set<PhaseDto> getPhases() {
		return phases;
	}

	public void setPhases(Set<PhaseDto> phases) {
		this.phases = phases;
	}

	public boolean isBatch() {
		return batch;
	}

	public void setBatch(boolean batch) {
		this.batch = batch;
	}

	public ScheduleDto getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduleDto studySchedule) {
		this.schedule = studySchedule;
	}

	@Override
	public boolean equals(Object o) {
		if (o==null || ! (o instanceof StudyActionResultQuery)) return false;
		StudyActionResultQuery o1 = (StudyActionResultQuery) o;
		if ((action == null && o1.getAction()==null)
				|| (action != null && o1.getAction() != null && action.equals(o1.getAction()))) {
			if ((subjectSet == null && o1.getSubjectSet()==null)
					|| (subjectSet != null && o1.getSubjectSet() != null && subjectSet.equals(o1.getSubjectSet()))) {
				if ((study == null && o1.getStudy() == null)
						|| (study != null && o1.getStudy() != null && study.equals(o1.getStudy()))) {
					if ((stage == null && o1.getStage() == null)
							|| (stage != null && o1.getStage() != null && stage.equals(o1.getStage()))) {
						if ((lastphase == null && o1.getLastphase() == null)
								|| (lastphase != null && o1.getLastphase() != null && lastphase.equals(o1.getLastphase()))) {
							if ((phases == null && o1.getPhases() == null)
									|| (phases != null && o1.getPhases() != null && phases.equals(o1.getPhases()))) {
								if ((schedule == null && o1.getSchedule() == null)
										|| (schedule != null && o1.getSchedule() != null && schedule.equals(o1.getSchedule()))) {
									if (batch == o1.isBatch()) return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(action, subjectSet, study, stage, lastphase, phases, schedule, batch);
	}

}
