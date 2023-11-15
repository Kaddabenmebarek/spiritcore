package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.Participant;

@Service
public class ParticipantService extends AbstractService implements Serializable{
	
	private static final long serialVersionUID = -2468831522008318707L;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StageService stageService;
	@Autowired
	private ExecutionService executionService;
	@Autowired
	private ExecutionDetailService executionDetailService;
	@Autowired
	private PhaseService phaseService;
	
	public HashMap<Date, Set<IndividualAction>> getAllActions(Participant participant, AssignmentDto assignment) {
        HashMap<Date, Set<IndividualAction>> events  = new HashMap<>();
        if(assignment==null)
        	return events;
        StageDto stage = assignment.getStage();
        if (stageService.getFirstDate(stage) != null) {
        	StageDto nextStage = getNextStage(participant, assignment);
            Date startDateNextStage = null;
            if (nextStage != null) {
                if (nextStage.isDynamic() && getAssignment(participant, nextStage)!=null) startDateNextStage = phaseService.add(stageService.getFirstDate(nextStage), assignmentService.getOffset(getAssignment(participant, nextStage)));
                else startDateNextStage = phaseService.add(assignmentService.getLastDate(assignment,getOffsetToFollow(participant, assignment)), nextStage.getOffsetFromPreviousStage());
            }
            events = assignmentService.getAllActions(assignment, startDateNextStage, getOffsetToFollow(participant, assignment));
        }
        return events;
    }
	
	public StageDto getNextStage(Participant participant, AssignmentDto assignment) {
    	boolean getNext=false;
		for(AssignmentDto a : participant.getAssignmentSeries()) {
			if(getNext) {
				return a.getStage();
			}
			if(a.equals(assignment))
				getNext=true;
		}
		return null;
    }
	
	public AssignmentDto getAssignment(Participant participant, StageDto stage) {
		if(participant==null)
			System.out.println();
        for (AssignmentDto assignment : participant.getAssignmentSeries()) {
            if (assignment.getStage().equals(stage)) return assignment;
        }
        return null;
    }

	public Duration getOffset(Participant participant, AssignmentDto assignment) {
        if (assignment.getStage().isDynamic()) {
            return assignmentService.getOffset(assignment);
        } else {
            if (getAssignment(participant, assignment.getStage().getPreviousStage()) == null) {
                return Duration.ZERO;
            } else {
                return getOffset(participant, getAssignment(participant, assignment.getStage().getPreviousStage()));
            }
        }
    }
	
	public Duration getOffsetToFollow(Participant participant, AssignmentDto assignment) {
        if (assignment.getStage().isDynamic()) {
            // dynamic does not follow previous offsets
            return Duration.ZERO;
        } else {
            if (getAssignment(participant, assignment.getStage().getPreviousStage()) == null) {
                // probably we should not end up here
                return Duration.ZERO;
            } else {
                return getOffset(participant, getAssignment(participant, assignment.getStage().getPreviousStage()));
            }
        }
    }

	public AssignmentDto getNextAssignment(Participant participant, AssignmentDto assignment) {
		boolean getNext=false;
		for(AssignmentDto a : participant.getAssignmentSeries()) {
			if(getNext) {
				return a;
			}
			if(a.equals(assignment))
				getNext=true;
		}
		return null;
	}
	
	public PhaseDto getPhase(Participant participant, Date date) {
		List<AssignmentDto> assignmentSeriesReverse = new ArrayList<>(participant.getAssignmentSeries());
    	Collections.reverse(assignmentSeriesReverse);
    	for (AssignmentDto assignment : assignmentSeriesReverse) {
    		if(stageService.getFirstDate(assignment.getStage())!=null) {
	    		if(date.after(assignmentService.getLastDate(assignment, getOffsetToFollow(participant, assignment))))
					return null;
	    		PhaseDto phase = assignmentService.getPhase(assignment, date, getOffsetToFollow(participant, assignment));
	            if (phase != null) return phase;
    		}
        }
        return null;
	}

	public Date getFirstDate(Participant participant, AssignmentDto assignment) {
		return assignmentService.getFirstDate(assignment, getOffsetToFollow(participant, assignment));
	}

	public AssignmentDto getAssignment(Participant participant, StudyDto study, Date date) {
		for (AssignmentDto assignment : participant.getAssignmentSeries()) {
			StageDto stage = assignment.getStage();
			StageDto nextStage = getNextStage(participant, assignment);
			for(ExecutionDetailDto ed : assignment.getExecutionDetails()) {
				Date edDate = executionService.getExecutionDate(ed);
				if(edDate!=null && edDate.equals(date)) {
					date = executionService.getPlannedDate(ed);
				}
			}
			Duration duration=assignmentService.getDuration(assignment, date, getOffsetToFollow(participant, assignment));
			if (stageService.getFirstDate(stage) == null || duration==null || duration.toMillis() < 0)
				continue;
			Date nextFirstDate = assignmentService.getNextStageFirstDate(assignment, getOffsetToFollow(participant, assignment));
			if (nextStage != null && stageService.getFirstDate(nextStage) != null
					&& date !=null && nextFirstDate != null && date.compareTo(nextFirstDate) >= 0)
				continue;
			return assignment;
		}
		return null;
	}

	public Date getLastDate(Participant participant, AssignmentDto assignment) {
		List<AssignmentDto> assignmentRevers = new ArrayList<>(participant.getAssignmentSeries());
		Collections.reverse(assignmentRevers);
		AssignmentDto lastAssignment = null;
		for(AssignmentDto a : assignmentRevers) {
			if(stageService.getFirstDate(a.getStage())!=null) {
				lastAssignment = a;
				break;
			}
		}
		return assignmentService.getLastDate(lastAssignment, getOffsetToFollow(participant, lastAssignment));
	}

	public ExecutionDetailDto getExecutionDetail(Participant participant, IndividualAction individualAction) {
		 AssignmentDto assignment = getAssignment(participant, individualAction.getPhase().getStage());
	        HashMap<Date, Set<IndividualAction>> allActions = getAllActions(participant, assignment);
	        for (Date date : allActions.keySet()) {
	            for (IndividualAction action : allActions.get(date)) {
	                if (individualAction.getSchedulePhase()!=null && individualAction.getPhase()!=null
	                        && individualAction.getSchedulePhase().equals(action.getSchedulePhase())
	                        && individualAction.getPhase().equals(action.getPhase())
	                ) {
	                    for (ExecutionDetailDto executionDetail : assignment.getExecutionDetails()) {
	                        if (individualAction.getSchedulePhase().equals(executionDetail.getPhaseLink())
	                                && individualAction.getPhase().equals(executionDetail.getPhase()))
	                            return executionDetail;
	                    }
	                    ExecutionDetailDto ed = new ExecutionDetailDto();
	                    executionDetailService.setAssignment(ed, assignment);
	                    executionDetailService.setSchedulePhase(ed, individualAction.getSchedulePhase());
	                    return ed;
	                }
	            }
	        }
	        return null;
	}
}
