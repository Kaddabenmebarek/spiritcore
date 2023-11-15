package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.StudyAction;

@Service
public class IndividualActionService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -4331613289751609212L;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private ExecutionService executionService;
	
	public boolean isPlanned(IndividualAction individualAction) {
        return individualAction.getSchedulePhase() !=null;
    }

    public StudyAction getAction(IndividualAction individualAction) {
        return isPlanned(individualAction) ? schedulePhaseService.getAction(individualAction.getSchedulePhase()) : individualAction.getStudyAction();
    }

    public BiosampleDto getBiosample(IndividualAction individualAction) {
        return isPlanned(individualAction) ? individualAction.getAssignment().getBiosample() : individualAction.getBiosample();
    }

    public PhaseDto getPhase(IndividualAction individualAction) {
        return isPlanned(individualAction) ? individualAction.getSchedulePhase().getPhase() : individualAction.getPhase();
    }

    public Date getDatePlanned(IndividualAction individualAction) {
        return isPlanned(individualAction) ? assignmentService.getDate(individualAction.getAssignment(), individualAction.getPhase()) : getDate(individualAction);
    }

    public Date getDate(IndividualAction individualAction) {
        return isExecuted(individualAction) ? individualAction.getExecution().getExecutionDate() : (individualAction.getBiosample() !=null && individualAction.getPhase() != null) ?
        		biosampleService.getDate(individualAction.getBiosample(), individualAction.getPhase()) : null;
    }

    public Duration getDeviation(IndividualAction individualAction) {
        return isExecuted(individualAction) ? executionService.getDeviation(individualAction.getExecution(), individualAction.getBiosample(), individualAction.getPhase()) : 
        	schedulePhaseService.getExecutionDetails(individualAction.getSchedulePhase(), individualAction.getAssignment()).getDeviation();
    }

    public boolean isExecuted(IndividualAction individualAction) {
        return individualAction.getExecution() != null;
    }
}
