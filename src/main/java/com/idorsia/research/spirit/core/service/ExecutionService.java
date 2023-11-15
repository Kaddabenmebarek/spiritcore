package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Execution;

@Service
public class ExecutionService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = -5386253494013345621L;
	@Autowired
	BiosampleService biosampleService;
	@Autowired
	AssignmentService assignmentService;

	public Date getExecutionDateCalculated(BiosampleDto biosample, PhaseDto phase) {
        return biosampleService.getDate(biosample, phase, true);
    }

    public Duration getPhaseCalculated(Execution execution, BiosampleDto biosample, StudyDto study) {
        PhaseDto phase = biosampleService.getPhase(biosample, study, execution.getExecutionDate(), null);
        if (phase != null) return phase.getPhase();
        return biosampleService.getDuration(biosample, study, execution.getExecutionDate());
    }

    public Duration getDisplayPhaseCalculated(Execution execution, BiosampleDto biosample, StudyDto study, StageDto stage) {
        PhaseDto phase = biosampleService.getPhase(biosample, study, execution.getExecutionDate(), null);
        if (phase != null) return biosampleService.getDisplayDuration(biosample, stage, phase);
        return stage == null ? biosampleService.getDisplayDuration(biosample, study, execution.getExecutionDate()) : biosampleService.getDisplayDuration(biosample, stage, execution.getExecutionDate());
    }

    public StageDto getStageCalculated(Execution execution, BiosampleDto biosample, StudyDto study) {
        AssignmentDto assignment = biosampleService.getAssignment(biosample.getTopParent(), study, execution.getExecutionDate());
        return assignment == null ? null : assignment.getStage();
    }
    
    public Duration getDeviation(Execution execution, BiosampleDto biosample, PhaseDto phase) {
        if (execution.getExecutionDate() == null || phase == null) return Duration.ZERO;
        Date executionDate = getExecutionDateCalculated(biosample, phase);
        if(executionDate==null) return Duration.ZERO;
        return Duration.between(executionDate.toInstant(), execution.getExecutionDate().toInstant());
    }

    public boolean setDeviation(Execution execution, Duration value, BiosampleDto biosample, PhaseDto phase) {
        if (phase == null || biosample == null || value == null) return false;
        execution.setExecutionDate(Date.from(getExecutionDateCalculated(biosample, phase).toInstant().plus(value)));
        return true;
    }

    public PhaseDto getPhase(ExecutionDetailDto ed) {
        return ed.getPhaseLink() != null ? ed.getPhaseLink().getPhase() : null;
    }
    
    public Date getPlannedDate(ExecutionDetailDto ed) {
        return assignmentService.getDate(ed.getAssignment(), getPhase(ed).getPhase(), true);
    }
    
	public Date getExecutionDate(ExecutionDetailDto ed) {
    	return getExecutionDate(ed, false);
    }
    
    public Date getExecutionDate(ExecutionDetailDto ed, boolean dateAfterDeath) {
    	Date date = getPlannedDate(ed);
    	if(date==null)
    		return null;
    	Date executionDate = Date.from(date.toInstant().plus(ed.getDeviation()));
    	if(!dateAfterDeath && ed.getAssignment().getBiosample() != null && biosampleService.isDeadAt(ed.getAssignment().getBiosample(), executionDate))
    		return null;
    	return executionDate;
    }
}
