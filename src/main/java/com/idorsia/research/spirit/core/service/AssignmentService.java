package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.ui.JExceptionDialog;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AssignmentDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.AssignmentPatternDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiosampleEnclosureDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.ResultAssignmentDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.Pool;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.dto.view.StudyRemoval;
import com.idorsia.research.spirit.core.model.Assignment;
import com.idorsia.research.spirit.core.model.AssignmentPattern;
import com.idorsia.research.spirit.core.model.Biosample;
import com.idorsia.research.spirit.core.model.ExecutionDetail;
import com.idorsia.research.spirit.core.model.ResultAssignment;
import com.idorsia.research.spirit.core.model.Stage;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.util.Frequency;

@Service
public class AssignmentService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = 3797571752158809615L;
	@Autowired
	private AssignmentDao assignmentDao;
	@Autowired
	private StudyService studyService;
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private BiosampleEnclosureService biosampleEnclosureService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private SubGroupService subgroupService;
	@Autowired
	private StageService stageService;
	@Autowired
	private ExecutionDetailService executionDetailService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private SchedulePhaseService schedulePhaseService;
	@Autowired
	private IndividualActionService individualActionService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private ResultAssignmentService resultAssignmentService;
	@Autowired
	private AssignmentPatternService assignmentPatternService;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private AssayService assayService;
	@Autowired
	private StudyActionResultService studyActionResultService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, AssignmentDto> idToAssignment = (Map<Integer, AssignmentDto>) getCacheMap(AssignmentDto.class);
	
	public int addAssignment(Assignment assignment) {
		return assignmentDao.addAssignment(assignment);
	}
	
	public Integer saveOrUpdate(Assignment assignment) {
		return assignmentDao.saveOrUpdate(assignment);
	}

	public Assignment get(Integer id) {
		return assignmentDao.get(id);
	}

	public Assignment getAssignmentById(int id) {
		return assignmentDao.getAssignmentById(id);
	}

	public Assignment getAssignmentByBiosampleAndStage(Integer biosampleId, Integer stageId) {
		return assignmentDao.getAssignmentByBiosampleAndStage(biosampleId, stageId);
	}
	
	public Assignment getAssignmentByBiosampleAndSubgroup(Integer biosampleId, Integer subgroupId) {
		return assignmentDao.getAssignmentByBiosampleAndSubgroup(biosampleId, subgroupId);
	}

	public List<Assignment> getAssignmentsByBiosample(Integer biosampleId) {
		return assignmentDao.getAssignmentsByBiosample(biosampleId);
	}

	public List<Assignment> getAssignmentsByStage(Integer stageId) {
		return assignmentDao.getAssignmentsByStage(stageId);
	}

	public List<Assignment> getAssignmentsByStudy(Integer studyId) {
		return assignmentDao.getAssignmentsByStudy(studyId);
	}

	public List<Assignment> getAssignmentsBySubgroup(Integer subgroupId) {
		return assignmentDao.getAssignmentsBySubgroup(subgroupId);
	}

	public int getCount() {
		return assignmentDao.getCount();
	}

	public List<Assignment> getAssignmentsByStages(List<Stage> stages) {
		return assignmentDao.getAssignmentsByStages(stageService.getStageIds(stages));
	}

	public Map<Integer, Integer> getBiosampleSubgroupMap(List<StageDto> stages) {
		return assignmentDao.getBiosampleSubgroupMap(stageService.getStageDtoIds(stages));
	}

	public List<Biosample> getBiosamplesByStage(List<Integer> stages) {
		Set<Integer> biosampleIds = new HashSet<Integer>();
		List<Assignment> assignments = assignmentDao.getAssignmentsByStages(stages);
		for(Assignment as : assignments) {
			biosampleIds.add(as.getBiosampleId());
		}
		return new ArrayList<Biosample>(biosampleService.getBiosamples(biosampleIds));
	}
	
	public AssignmentDao getAssignmentDao() {
		return assignmentDao;
	}

	public void setAssignmentDao(AssignmentDao assignmentDao) {
		this.assignmentDao = assignmentDao;
	}

	public Date getLastDate(AssignmentDto assignment, Duration offsetToFollow) {
		 List<Date> dates = getDatesSorted(assignment, offsetToFollow);
		 if (dates.size() == 0) 
			 return null;
		 return dates.get(dates.size()-1);
	}

	@SuppressWarnings("unchecked")
	public List<Date> getDatesSorted(AssignmentDto assignment, Duration stratification) {
		List<Date> datesSorted = (List<Date>) Cache.getInstance().get("dates_sorted" + assignment.getStage().getStudy() + "_" + stratification + "_" + assignment.getStage().getName() + "_" + assignment.getNo());
		if(datesSorted == null) {
			datesSorted = new ArrayList<>(getAllActions(assignment, getNextStageFirstDate(assignment, stratification), stratification).keySet());
			Collections.sort(datesSorted);
			Cache.getInstance().add("dates_sorted" + assignment.getStage().getStudy() + "_"  + stratification + "_" + assignment.getStage().getName() + "_" + assignment.getNo(), datesSorted, Cache.MEDIUM);
		}
		return datesSorted;
	}
	
	public Date getNextStageFirstDate(AssignmentDto assignment, Duration offsetToFollow) {
		StageDto nextStage = null;
		AssignmentDto nextAssignment = null;
		if(assignment.getBiosample()!=null) {
			nextAssignment = participantService.getNextAssignment(studyService.getParticipantFor(assignment.getStage().getStudy(), assignment.getBiosample()),assignment);
			if(nextAssignment!=null)
				nextStage = nextAssignment.getStage();
		}
		return nextStage==null?null: nextStage.isDynamic() ? getFirstDate(nextAssignment, Duration.ZERO) : phaseService.add(stageService.getFirstDate(nextStage), offsetToFollow.plus(getOffset(assignment)));
	}

	public Duration getOffset(AssignmentDto assignment) {
		return assignment.getStage().isDynamic() ? assignment.getStratification() : Duration.ZERO;
	}
	
	public Date getFirstDate(AssignmentDto assignment, Duration offset) {
		List<Date> dates = getDatesSorted(assignment, offset);
		if (dates.size() == 0) return null;
		return dates.get(0);
	}
	
	public HashMap<Date, Set<IndividualAction>> getAllActions(AssignmentDto assignment, Date startDateNextStage, Duration offset) {
		@SuppressWarnings("unchecked")
		HashMap<Date, Set<IndividualAction>> events  = (HashMap<Date, Set<IndividualAction>>) Cache.getInstance().get("all_actions_assignment_" + assignment.getStage().getStudy() + "_" + startDateNextStage + "_" + offset + "_" + assignment.getStage().getName() + "_" + assignment.getNo());
		if (events == null) {
			events = new HashMap<>();
			if (stageService.getFirstDate(assignment.getStage()) != null) {
				Set<IndividualAction> actionsPerformed = new HashSet<>();
				if (assignment.getBiosample() != null) {
					for (IndividualAction individualAction : biosampleService.getActionsPerformed(assignment.getBiosample())) {
						PhaseDto phase = individualAction.getPhase();
						if (phase == null
								|| phase.getStage() == null
								|| !phase.getStage().equals(assignment.getStage())
								|| !assignment.equals(individualAction.getAssignment()))
							continue;
						Date date = individualActionService.getDate(individualAction);
						if (date != null
								&& !date.before(stageService.getFirstDate(assignment.getStage()))
								&& !(startDateNextStage != null && date.compareTo(startDateNextStage) >= 0))
							actionsPerformed.add(individualAction);
					}
				}
				for (ActionPatternsDto actionPatternAbstract : getFullActionDefinition(assignment)) {
					outerloop:
					for (SchedulePhaseDto schedulePhaseLink : actionPatternAbstract.getSchedule().getSchedulePhases()) {
						if (schedulePhaseLink.getPhase() != null && schedulePhaseLink.getPhase().compareTo(subgroupService.getEndPhase(assignment.getSubgroup())) > 0)
							continue;
						for (IndividualAction individualAction : actionsPerformed) {
							if (schedulePhaseLink.equals(individualAction.getSchedulePhase())) {
								MiscUtils.put(events, individualActionService.getDate(individualAction), individualAction);
								continue outerloop;
							}
						}
						for (ExecutionDetailDto executionDetail : assignment.getExecutionDetails()) {
							if (schedulePhaseLink.equals(executionDetail.getPhaseLink())) {
								Date executionDate = executionDetailService.getExecutionDate(executionDetail);
								if(executionDate!=null && !isRemove(assignment, executionDate))
									MiscUtils.put(events, executionDate, new IndividualAction(schedulePhaseLink, assignment));
								continue outerloop;
							}
						}
						Date date = getDate(assignment, schedulePhaseLink.getPhase(), schedulePhaseService.getAction(schedulePhaseLink));
						if (date == null) continue;
						if (!schedulePhaseLink.getPhase().getStage().equals(assignment.getStage())) continue;
						if (assignment.getStage().getPreviousStage() != null) date = phaseService.add(date, offset);
						if (!(startDateNextStage != null && date.compareTo(startDateNextStage) >= 0))
							MiscUtils.put(events, date, new IndividualAction(schedulePhaseLink, assignment));
					}
				}
				if(assignment.getRemoveDate()!=null) {
					MiscUtils.put(events, assignment.getRemoveDate(), new IndividualAction(new StudyRemoval(assignment),assignment.getBiosample(),null, null, assignment.getStage().getStudy()));
				}
			}
			Cache.getInstance().add("all_actions_assignment_" + assignment.getStage().getStudy() + "_"  + startDateNextStage + "_" + offset + "_" + assignment.getStage().getName() + "_" + assignment.getNo(), events, Cache.MEDIUM);
		}
		return events;
	}

	public Date getDate(AssignmentDto assignment, PhaseDto phase, StudyAction action) {
		if (phase == null)
			return null;
		if (action != null) {
			ExecutionDetailDto ed = getExecutionDetail(assignment, phase, action);
			if(ed!=null)
				return executionDetailService.getExecutionDate(ed);
		}
		return getDate(assignment, phase);
	}
	
	public Date getDate(AssignmentDto assignment, Duration phase, StudyAction action) {
		if (phase == null)
			return null;
		if (action != null) {
			ExecutionDetailDto ed = getExecutionDetail(assignment, phase, action);
			if(ed!=null)
				return executionDetailService.getExecutionDate(ed);
		}
		return getDate(assignment, phase);
	}
	
	public Date getRemovalDate(AssignmentDto a) {
		return a.getRemoveDate()==null ? null : Date.from(a.getRemoveDate().toInstant());
	}
	
	public ExecutionDetailDto getExecutionDetail(AssignmentDto assignment, Duration duration, StudyAction action) {
		for (ExecutionDetailDto executionDetails : assignment.getExecutionDetails()) {
			if (executionDetails == null) {
				continue;
			}
			PhaseDto phase = executionDetailService.getPhase(executionDetails);
			if (phase!=null && action.equals(executionDetails.getPhaseLink().getSchedule().getActionPattern().getAction())
					&& duration.equals(phase.getPhase())) {
				return executionDetails;
			}
		}
		return null;
	}
	
	public Date getDate(AssignmentDto assignment, PhaseDto phase) {
		if (phase == null) return null;
		return getDate(assignment, phase.getPhase());
	}
	
	public Date getDate(AssignmentDto assignment, Duration duration) {
	       return getDate(assignment, duration, false);
		}
	
	public Date getDate(AssignmentDto assignment, Duration duration, boolean datesAfterDeath) {
		if (duration == null || stageService.getFirstDate(assignment.getStage()) == null) return null;
		Date x = (Date)Cache.getInstance().get("assignment_getDateByDur" + duration.toString() + "_" + datesAfterDeath + "_" + assignment.getStage().getStudy() + "_" + assignment.getStage().getName() + "_" + assignment.getNo());
		if (x != null) return x;
		Duration offset = assignment.getStratification();
		Date from = Date.from(Instant.ofEpochMilli(stageService.getFirstDate(assignment.getStage()).toInstant().toEpochMilli() + (offset == null ? 0 : offset.toMillis()) + duration.toMillis()));
		if (isRemove(assignment, from) || (assignment.getBiosample() != null && !datesAfterDeath && biosampleService.isDeadAt(assignment.getBiosample(), from)))  return null;
		Cache.getInstance().add("assignment_getDateByDur" + duration.toString() + "_" + datesAfterDeath + "_" + assignment.getStage().getStudy() + "_" + assignment.getStage().getName() + "_" + assignment.getNo(), from);
		return from;
	}

	public ExecutionDetailDto getExecutionDetail(AssignmentDto assignment, PhaseDto phase, StudyAction action) {
		for (ExecutionDetailDto executionDetails : assignment.getExecutionDetails()) {
			if (executionDetails == null) {
				continue;
			}
			if (action.equals(executionDetailService.getAction(executionDetails))
					&& phase.equals(executionDetails.getPhaseLink()==null?null:executionDetails.getPhaseLink().getPhase())) {
				return executionDetails;
			}
		}
		return null;
	}

	public boolean isRemove(AssignmentDto assignment, Date date) {
		if(assignment.getRemoveDate()==null || assignment.getRemoveDate().compareTo(date)>0)
			return false;
		return true;
	}

	public GroupDto getGroup(AssignmentDto assignment) {
		if (assignment.getSubgroup() == null) return null;
		return assignment.getSubgroup().getGroup();
	}

	public Duration getOffsetToFollow(AssignmentDto assignment) {
		return participantService.getOffsetToFollow(studyService.getParticipantFor(assignment.getStage().getStudy(),studyService.getParticipants(assignment.getStage().getStudy()),assignment),assignment);
	}

	public Duration getDuration(AssignmentDto assignment, Date date, Duration offsetToFollow) {
		StageDto stage = assignment.getStage();
		if (date == null || stageService.getFirstDate(stage) == null) return null;
		return Duration.ofMillis(date.toInstant().toEpochMilli() - stageService.getFirstDate(stage).toInstant().toEpochMilli() - (stage.isDynamic()?getOffset(assignment).toMillis():assignment.getStratification().toMillis()));
	}
	
	public Execution getExecution(AssignmentDto assignment) {
		return new Execution(getDate(assignment, stageService.getFirstPhase(assignment.getStage())));
	}
	
	public Set<AssignmentDto> getAssignments(BiosampleDto biosample, StudyDto study) {
		if(biosample==null)
			return null;
		Set<AssignmentDto> result = new HashSet<>();
		biosample.getAssignments().forEach(s -> {
			if (s.getStage() != null && s.getStage().getStudy().equals(study)) 
				result.add(s);
			});
		return result;
	}
	
	public PhaseDto getPhase(AssignmentDto assignment, Date date, Duration offset) {
		if (date == null) return null;
		for(ExecutionDetailDto ed : assignment.getExecutionDetails()) {
			Date edDate = executionDetailService.getExecutionDate(ed);
			if(edDate!=null && edDate.equals(date)) {
				return ed.getPhaseLink()==null?null:ed.getPhaseLink().getPhase();
			}
		}
		return stageService.getPhase(assignment.getStage(), getDuration(assignment, date, offset));
	}
	
	public List<PhaseDto> getPhases(AssignmentDto assignment) {
		HashSet<PhaseDto> phases = new HashSet<>();
		phases.add(stageService.getFirstPhase(assignment.getStage()));
		PhaseDto endPhase = subgroupService.getEndPhase(assignment.getSubgroup());
		if (endPhase != null) {
			phases.add(endPhase);
		}
		for (ActionPatternsDto actionPatternAbstract : getFullActionDefinition(assignment)) {
			for (PhaseDto phase : scheduleService.getPhases(actionPatternAbstract.getSchedule())) {
				phases.add(phase);
			}
		}
		ArrayList<PhaseDto> phasesSorted = new ArrayList<>(phases);
		Collections.sort(phasesSorted);
		return phasesSorted;
	}

	public Duration getDisplayDuration(AssignmentDto assignment, Date date) {
		StageDto stage = assignment.getStage();
		if (date == null || stageService.getFirstDate(stage) == null) return null;
		return Duration.ofMillis(date.toInstant().toEpochMilli() - stageService.getFirstDate(stage).toInstant().toEpochMilli() - 
				stageService.getOffsetOfD0(stage).toMillis() - assignment.getStratification().toMillis());
	}
	
	public void removeDuplicateActionPattern(AssignmentDto assignment, ActionPatternsDto actionPattern) {
		if(actionPattern==null)
			return;
		for(ActionPatternsDto ap : assignment.getActionDefinitionPattern()) {
			if(actionPattern.getStage().equals(ap.getStage()) &&
                    actionPattern.getSchedule().getRecurrence().equals(ap.getSchedule().getRecurrence()) &&
                    actionPattern.getAction().equals(ap.getAction()) &&
                    actionPattern.getActionType().equals(ap.getActionType())) {
				removeActionPattern(assignment, ap, false);
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setRemoveDate(AssignmentDto assignment, Date date) {
		BiosampleDto biosample = assignment.getBiosample();
		boolean isNext = false;
		for(AssignmentDto a : studyService.getParticipantFor(assignment.getStage().getStudy(), studyService.getParticipants(assignment.getStage().getStudy()), assignment).getAssignmentSeries()) {
			if(isNext)
				setBiosample(a, null);
			if(a.equals(assignment))
				isNext=true;
		}
		if(biosample!=null) {
			PhaseDto phase = biosampleService.getPhase(biosample, assignment.getStage().getStudy(), date);
			biosampleEnclosureService.setPhaseOut(biosampleService.getBiosampleEnclosure(biosample, phase, null), phase);
		}
		assignment.setRemoveDate(date);
	}
	
	protected void removeActionPattern(AssignmentDto assignment, ActionPatternsDto actionPattern, boolean cross) {
		//prevent endless loop
		if (!assignment.getActionDefinitionPattern().contains(actionPattern))
			return ;
        //remove old member
        removeActionDefinition(assignment, actionPattern);
		//remove child's owner
		if (!cross) {
			try {
				actionPattern.getChildren().remove(actionPattern);
				actionPatternsService.setStage(actionPattern, null, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
    public boolean addActionDefinition(AssignmentDto assignment, ActionPatternsDto actionDefinition) {
        return assignment.getActionDefinition().add(new AssignmentPatternDto(assignment, actionDefinition));
    }

    public boolean removeActionDefinition(AssignmentDto assignment, ActionPatternsDto actionDefinition) {
        AssignmentPatternDto assignmentPattern = null;
    	for(AssignmentPatternDto pattern : assignment.getActionDefinition()) {
        	if(pattern.getActionpattern().equals(actionDefinition)) {
        		assignmentPattern=pattern;
        		break;
        	}
        }
    	if(assignmentPattern!=null) {
    		assignment.getActionDefinition().remove(assignmentPattern);
    		return true;
    	}
    	return false;
    }
    
    public void removeActionDefinitionByParent(AssignmentDto assignment, ActionPatternsDto actionDefinition) {
        ActionPatternsDto child = getActionPatternByParent(assignment, actionDefinition);
    	removeActionPattern(assignment, child, false);
        actionDefinition.getChildren().remove(child);
    }

    @SuppressWarnings("unchecked")
	public Set<ActionPatternsDto> getFullActionDefinition(AssignmentDto assignment) {
		Set<ActionPatternsDto> results = (Set<ActionPatternsDto>)Cache.getInstance().get("fullactiondef_assignment_"+assignment.getStage().getStudy().getStudyId()+"_"+assignment.getSubgroup().getGroup().getStage().getName()+"_"+assignment.getSubgroup().getFullName()+"_"+assignment.getNo());
        if (results == null) {
            results = new HashSet<>();
            results.addAll(stageService.getActionDefinitionWithoutWeekly(assignment.getSubgroup().getGroup().getStage()));
            results.addAll(groupService.getActionDefinitionWithoutWeekly(assignment.getSubgroup().getGroup()));
            results.addAll(subgroupService.getActionDefinitionWithoutWeekly(assignment.getSubgroup()));
            results.addAll(assignment.getActionDefinitionPattern());
            Cache.getInstance().add("fullactiondef_assignment_"+assignment.getStage().getStudy().getStudyId()+"_"+assignment.getSubgroup().getGroup().getStage().getName()+"_"+assignment.getSubgroup().getFullName()+"_"+assignment.getNo(),results, Cache.MEDIUM);
        }
        return results;
    }
	
	public void getWeeklyDefinition(AssignmentDto assignment) {
		if(assignment.getSubgroup()!=null)
			for(ActionPatternsDto ac : subgroupService.getWeeklyDefinition(assignment.getSubgroup())) {
				addActionDefinitionWeekly(assignment, ac);
			}
	}
	
	@SuppressWarnings("deprecation")
	public void setStratification(AssignmentDto assignment, Duration offset) throws Exception {
		if (assignment.getStage().isDynamic()) {
			Duration prevOffset = assignment.getStratification();
			assignment.setStratification(offset);
			if(prevOffset.equals(offset))
				return;
			BiosampleDto biosample = assignment.getBiosample();
			if(biosample!=null && prevOffset.compareTo(offset)<0) {
				AssignmentDto nextAssignment = assignment;
				Cache.getInstance().clear();
				while(nextAssignment!=null) {
					if(biosampleService.isDeadAt(biosample, getFirstDate(assignment, getOffsetToFollow(assignment)))) {
						assignment.setStratification(prevOffset);
						throw new Exception("Animal "+biosample.getSampleId()+" will be dead before stage starts.");
					}
					if(assignment.getStage().getNextStage()==null || assignment.getStage().getNextStage().isDynamic())
						nextAssignment = null;
					else
						nextAssignment = biosampleService.getAssignment(biosample, assignment.getStage().getNextStage());
				}
			}
			for(ActionPatternsDto ap : assignment.getActionDefinitionPattern()) {
				calculateOffset(assignment, ap);
			}
		}
	}
	
	/**
	 * You should use setStratification(AssignmentDto assignment, Duration offset) to control date of death including new offset.
	 * This method is faster but could include dead animals in next stages
	 * @param assignment
	 * @param offset
	 * @throws Exception
	 */
	@Deprecated 
	public void setStratificationWithoutControl(AssignmentDto assignment, Duration offset) throws Exception {
		if (assignment.getStage().isDynamic()) {
			assignment.setStratification(offset);
		}
	}
	
	public void setSubgroup(AssignmentDto assignment, SubGroupDto subGroup) {
		setSubgroup(assignment, subGroup, false);
	}
	public void setSubgroup(AssignmentDto assignment, SubGroupDto subGroup, Boolean cross) {
		setSubgroup(assignment, subGroup, cross, true);
	}
	
	@SuppressWarnings("deprecation")
	public void setSubgroup(AssignmentDto assignment, SubGroupDto subGroup, Boolean cross, Boolean computeOffset) {
		if (nullSupportEqual(assignment.getSubgroup(), subGroup))
			return;
		//remove from the old owner
		if (assignment.getSubgroup()!=null && !(cross && subGroup == null))
			subgroupService.removeAssignment(assignment.getSubgroup(), assignment, true);
		//set new owner
		assignment.setSubgroup(subGroup);
		setPreviousName(assignment);
		getWeightResult(assignment);
		//set myself to new owner
		if (subGroup!=null && !cross)
			subgroupService.addAssignment(subGroup, assignment, true);
		if(computeOffset)
			getWeeklyDefinition(assignment);
	}
	
	public ActionPatternsDto getActionPatternByParent(AssignmentDto assignment, ActionPatternsDto actionPattern) {
		for(ActionPatternsDto a : assignment.getActionDefinitionPattern()) {
			if(actionPattern.equals(a.getParent())) {
				return a;
			}
		}
		return null;
	}


	public void addActionDefinitionWeekly(AssignmentDto assignment, ActionPatternsDto actionPattern) {
		ActionPatternsDto ap = getActionPatternByParent(assignment, actionPattern);
		if(ap!=null) {
			actionPatternsService.setAction(ap, actionPattern.getAction());
			try {
				ScheduleDto schedule = actionPattern.getSchedule();
				ScheduleDto cloneSchedule = new ScheduleDto();
				scheduleService.setRrule(cloneSchedule, schedule.getrRule());
				try {
					cloneSchedule.setLastPhase(schedule.getLastPhase());
					scheduleService.setStartDate(cloneSchedule, schedule.getStartDate());
					scheduleService.removeTimepoint(cloneSchedule, LocalTime.MIDNIGHT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				schedule.getTimePointsSorted().forEach(e -> scheduleService.addTimePoint(cloneSchedule, e));
				
				actionPatternsService.setSchedule(ap, cloneSchedule);
				scheduleService.setPhases(ap.getSchedule(), getOffset(assignment));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try{
				ActionPatternsDto patternDuplicate = stageService.duplicate(assignment.getStage(), actionPattern);
				scheduleService.setPhases(patternDuplicate.getSchedule(), getOffset(assignment));
				actionPatternsService.setParent(patternDuplicate, actionPattern);
				actionPattern.getChildren().add(patternDuplicate);
				addActionDefinition(assignment, patternDuplicate);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void calculateOffset(AssignmentDto assignment, ActionPatternsDto ap) {
		if (ap.getSchedule() != null && ap.getSchedule().getRecurrence() != null
				&& Frequency.WEEKLY.equals(ap.getSchedule().getRecurrence().getFrequency())) {
			try {
				scheduleService.setPhases(ap.getSchedule(), assignment.getStage().isDynamic() ? assignment.getStratification() : getOffsetToFollow(assignment));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void calculateDatePattern(AssignmentDto assignment) {
		for(ActionPatternsDto ap : assignment.getActionDefinitionPattern()) {
			calculateOffset(assignment, ap);
		}
	}
	
	public boolean hasAction(AssignmentDto assignment, ActionPatternsDto actionPattern) {
		if(actionPattern==null)
			return false;
		for(ActionPatternsDto ap : assignment.getActionDefinitionPattern()) {
			if(actionPattern.getStage().equals(ap.getStage()) &&
                    actionPattern.getSchedule().getRecurrence().equals(ap.getSchedule().getRecurrence()) &&
                    actionPattern.getAction().equals(ap.getAction()) &&
                    actionPattern.getActionType().equals(ap.getActionType()))
				return true;
		}
		return false;
	}

	public AssignmentDto map(Assignment assignment) {
		AssignmentDto assignmentDto = idToAssignment.get(assignment.getId());
		if(assignmentDto==null) {
			assignmentDto = dozerMapper.map(assignment, AssignmentDto.class,"assignmentCustomMapping");
			if(idToAssignment.get(assignment.getId())==null)
				idToAssignment.put(assignment.getId(), assignmentDto);
			else
				assignmentDto = idToAssignment.get(assignment.getId());
			
			setBiosample(assignmentDto, assignment.getBiosampleId()==null ? null : biosampleService.getBiosampleDto(assignment.getBiosampleId()));
		}
		return assignmentDto;
	}
	
	@Transactional
	public void save(AssignmentDto assignment) throws Exception {
		save(assignment, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(AssignmentDto assignment, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(assignment)) {
				savedItems.add(assignment);
				if(assignment.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(assignment);
				if(assignment.getBiosample()!=null && assignment.getBiosample().getId()==Constants.NEWTRANSIENTID)biosampleService.save(assignment.getBiosample(), true);
				if(assignment.getStage().getId()==Constants.NEWTRANSIENTID)stageService.save(assignment.getStage(), true);
				if(assignment.getSubgroup().getId()==Constants.NEWTRANSIENTID)subgroupService.save(assignment.getSubgroup(), true);
				assignment.setUpdDate(new Date());
				assignment.setUpdUser(UserUtil.getUsername());
				if(assignment.getId().equals(Constants.NEWTRANSIENTID)) {
					assignment.setCreDate(new Date());
					assignment.setCreUser(UserUtil.getUsername());
				}
				assignment.setId(saveOrUpdate(dozerMapper.map(assignment, Assignment.class, "assignmentCustomMapping")));
				idToAssignment.put(assignment.getId(), assignment);
				if(assignment.getExecutionDetailsNoMapping()!=null) {
					for(ExecutionDetailDto executionDetail : assignment.getExecutionDetails()) {
						executionDetailService.save(executionDetail, true);
					}
				}
				if(assignment.getActionDefinitionNoMapping()!=null) {
					for(AssignmentPatternDto ap : assignment.getActionDefinition()) {
						assignmentPatternService.save(ap, true);
					}
				}
				if(assignment.getResultAssignmentsNoMapping()!=null) {
					for(ResultAssignmentDto ra : assignment.getResultAssignments()) {
						resultAssignmentService.save(ra, true);
					}
				}
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteChildren(AssignmentDto assignment) {
		if(assignment.getExecutionDetailsNoMapping()!=null) {
			for(ExecutionDetail ed : executionDetailService.getByAssignment(assignment.getId())) {
				Boolean found=false;
				for(ExecutionDetailDto child : assignment.getExecutionDetails()) {
					if(ed.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found)
					executionDetailService.delete(executionDetailService.map(ed), true);
			}
		}
		if(assignment.getActionDefinitionNoMapping()!=null) {
			for(AssignmentPattern ap : assignmentPatternService.getByAssignment(assignment.getId())) {
				Boolean found=false;
				for(AssignmentPatternDto child : assignment.getActionDefinition()) {
					if(ap.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found)
					assignmentPatternService.delete(assignmentPatternService.map(ap), true);
			}
		}
		if(assignment.getResultAssignmentsNoMapping()!=null) {
			for(ResultAssignment ra : resultAssignmentService.getResultAssignmentByAssignment(assignment.getId())) {
				Boolean found=false;
				for(ExecutionDetailDto child : assignment.getExecutionDetails()) {
					if(ra.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found)
					resultAssignmentService.delete(resultAssignmentService.map(ra), true);
			}
		}
	}

	@Transactional
	public void delete(AssignmentDto assignment) {
		delete(assignment, false);
	}
	
	protected void delete(AssignmentDto assignment, Boolean cross) {
		assignmentDao.delete(assignment.getId());
	}

	public AssignmentDto getAssignmentDto(Integer id) {
		return map(get(id));
	}

	public List<AssignmentDto> map(List<Assignment> assignments) {
		List<AssignmentDto> result = new ArrayList<>();
		for(Assignment assignment : assignments) {
			result.add(map(assignment));
		}
		return result;
	}
	
	public boolean setBiosample(AssignmentDto assignment, BiosampleDto biosample) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(new Pool(Collections.singleton(assignment.getBiosample())));
			studyActionResultQuery.setStage(assignment.getStage());
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ASSIGNED_SUBJECT,studyActionResultQuery);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		setBiosample(assignment, biosample, false);
		calculateDatePattern(assignment);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void setBiosample(AssignmentDto assignment, BiosampleDto biosample, boolean cross) {
		//prevent endless loop
		if (nullSupportEqual(assignment.getBiosample(), biosample))
			return;
		// check if we set a Biosample from NULL to some actual Sample
		for (ResultAssignmentDto resultAssignment : new HashSet<>(assignment.getResultAssignments())) {
			removeResultAssignment(assignment, resultAssignment);
		}
		//remove from the old owner
		if (assignment.getBiosample()!=null && !(cross && biosample == null))
			biosampleService.removeAssignment(assignment.getBiosample(), assignment, true);
		//set new owner
		assignment.setBiosample(biosample);
		setPreviousName(assignment);
		getWeightResult(assignment);
		//set myself to new owner
		if (biosample!=null && !cross)
			biosampleService.addAssignment(biosample, assignment, true);
	}
	
	
	public void setStage(AssignmentDto assignment, StageDto stage) {
		setStage(assignment, stage, false);
	}

	@SuppressWarnings("deprecation")
	public void setStage(AssignmentDto assignment, StageDto stage, boolean cross) {
		//prevent endless loop
		if (nullSupportEqual(assignment.getStage(), stage))
			return;
		//remove from the old owner
		if (assignment.getStage()!=null && !(cross && stage == null))
			stageService.removeAssignment(assignment.getStage(), assignment, true);
		//set new owner
		assignment.setStage(stage);
		//set myself to new owner
		if (stage !=null && !cross)
			stageService.addAssignment(stage, assignment, true);
	}
	
	public void setPreviousName(AssignmentDto assignment) {
		if (assignment.getStage() != null && assignment.getStage().getPreviousStage()!=null) {
			StageDto stage = assignment.getStage().getPreviousStage();
			loop: while (stage!=null) {
				for (AssignmentDto a : stage.getAssignments()) {
					if (a.getBiosample()!=null && a.getBiosample().equals(assignment.getBiosample())){
						if (a.getName()!=null) {
							assignment.setName(a.getName());
							break loop;
						}
					}
				}
				stage = stage.getPreviousStage();
			}
		}
	}
	
	public AssayResultDto getWeightResult(AssignmentDto assignment) {
		for (ResultAssignmentDto resultAssignment : assignment.getResultAssignments()) {
			AssayResultDto result = resultAssignment.getAssayResult();
			if (Constants.WEIGHING_TESTNAME.equals(result.getAssay().getName())) 
				return result;
		}
		return null;
	}
	
	public void removeResultAssignment(AssignmentDto assignment, ResultAssignmentDto resultAssignment) {
		removeResultAssignment(assignment, resultAssignment, false);
	}

	public void removeResultAssignment(AssignmentDto assignment, ResultAssignmentDto resultAssignment, boolean cross) {
		//prevent endless loop
		if (!assignment.getResultAssignments().contains(resultAssignment))
			return ;
		//remove old member
		assignment.getResultAssignments().remove(resultAssignment);
		//remove child's owner
		if (!cross) {
			resultAssignmentService.setAssignment(resultAssignment, null, true);
		}
	}

	public void addResultAssignment(AssignmentDto assignment, ResultAssignmentDto resultAssignment) {
		addResultAssignment(assignment, resultAssignment, false);
	}
	
	public void addResultAssignment(AssignmentDto assignment, ResultAssignmentDto resultAssignment, boolean cross) {
		//prevent endless loop
		if (assignment.getResultAssignments().contains(resultAssignment))
			return ;
		//add new member
		assignment.getResultAssignments().add(resultAssignment);
		//update child if request is not from it
		if (!cross) {
			resultAssignmentService.setAssignment(resultAssignment, assignment, true);
		}
	}
	
	public void addExecutionDetails(AssignmentDto assignment, ExecutionDetailDto executionDetails) {
		addExecutionDetails(assignment, executionDetails, false);
	}

	public void addExecutionDetails(AssignmentDto assignment, ExecutionDetailDto executionDetails, boolean cross) {
		//prevent endless loop
		if (assignment.getExecutionDetails().contains(executionDetails))
			return ;
		//add new member
		assignment.getExecutionDetails().add(executionDetails);
		//update child if request is not from it
		if (!cross) {
			executionDetailService.setAssignment(executionDetails, assignment, true);
		}
	}

	public void removeExecutionDetails(AssignmentDto assignment, ExecutionDetailDto executionDetails) {
		removeExecutionDetails(assignment, executionDetails, false);
	}

	public void removeExecutionDetails(AssignmentDto assignment, ExecutionDetailDto executionDetails, boolean cross) {
		//prevent endless loop
		if (!assignment.getExecutionDetails().contains(executionDetails))
			return ;
		//remove old member
		assignment.getExecutionDetails().remove(executionDetails);
		//remove child's owner
		if (!cross) {
			executionDetailService.setAssignment(executionDetails, null, true);
		}
	}

	public PhaseDto getClosestPhase(AssignmentDto assignment, Date date, Duration offsetToFollow) {
		if (date == null) return null;
		for(ExecutionDetailDto ed : assignment.getExecutionDetails()) {
			Date edDate = executionDetailService.getExecutionDate(ed);
			if(edDate!=null && edDate.equals(date)) {
				return executionDetailService.getPhase(ed);
			}
		}
		return stageService.getClosestPhase(assignment.getStage(), getDuration(assignment, date, assignment.getStratification()));
	}

	public EnclosureDto getEnclosure(AssignmentDto assignment) {
		BiosampleDto biosample = assignment.getBiosample();
		if(biosample==null) return null;
		return biosampleService.getInsertEnclosure(biosample, stageService.getFirstPhase(assignment.getStage()));
	}

	public void resetCage(AssignmentDto assignment) {
		resetCage(assignment, false);
	}
	public void resetCage(AssignmentDto assignment, boolean forNewEnclosure) {
		StageDto stage = assignment.getStage();
		if (assignment.getBiosample() == null || stage == null) return;
		HashSet<BiosampleEnclosureDto> toDelete = new HashSet<>();
		for (BiosampleEnclosureDto biosampleEnclosure : assignment.getBiosample().getBiosampleEnclosures()) {
			if (stageService.getFirstPhase(stage).equals(biosampleEnclosure.getPhaseIn())) 
				toDelete.add(biosampleEnclosure);
			if (!forNewEnclosure && stageService.getFirstPhase(stage).equals(biosampleEnclosure.getPhaseOut())) 
				biosampleEnclosureService.setPhaseOut(biosampleEnclosure, null);
			if (forNewEnclosure && biosampleEnclosure.getPhaseIn().getStage().compareTo(stage) < 0) {
				if (biosampleEnclosure.getPhaseOut() == null
						|| biosampleEnclosure.getPhaseOut().compareTo(stageService.getFirstPhase(stage))>=0) {
					biosampleEnclosureService.setPhaseOut(biosampleEnclosure, stageService.getFirstPhase(stage));
				}
			}
		}
		for (BiosampleEnclosureDto biosampleEnclosure : toDelete) {
			biosampleEnclosureService.remove(biosampleEnclosure);
		}
	}

	public void getActions(AssignmentDto assignment, HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> events,
			Duration offsetToFollow) {
		HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> dateMap = getActions(assignment, getOffsetToFollow(assignment));
		for (Date date: dateMap.keySet()) {
			Map<StudyAction, Set<AssignmentDto>> actionMap = dateMap.get(date);
			for (StudyAction action : actionMap.keySet()) {
				putAction(assignment, events, action, date);
			}
		}
	}
	
	public HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> getActions(AssignmentDto assignment, Duration offset) {
		HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> events  = new HashMap<>();
		HashMap<Date, Set<IndividualAction>> actions = getAllActions(assignment, getNextStageFirstDate(assignment, offset), offset);
		for (Date date : actions.keySet()) {
			for (IndividualAction action : actions.get(date)) {
				putAction(assignment, events, action.getStudyAction(), date);
			}
		}
		return events;
	}
	
	private synchronized void putAction(AssignmentDto assignment, HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> events, StudyAction action, Date date) {
		HashMap<StudyAction, Set<AssignmentDto>> studyActionListHashMap = events.get(date);
		if (studyActionListHashMap == null) {
			studyActionListHashMap = new HashMap<>();
		}
		MiscUtils.put(studyActionListHashMap, action, assignment);
		events.put(date, studyActionListHashMap);
	}

	public Date getGroupAssignmentDate(AssignmentDto assignment) {
		for (ExecutionDetailDto executionDetail : assignment.getExecutionDetails()) {
			if (assignment.getStage().equals(schedulePhaseService.getAction(executionDetail.getPhaseLink())))
				return executionDetailService.getPlannedDate(executionDetail);
		}
		return phaseService.add(stageService.getFirstDate(assignment.getStage()), assignment.getStratification());
	}

	@SuppressWarnings("deprecation")
	public AssignmentDto newAssignment(StageDto stage, BiosampleDto biosample, int no, Duration offset, SubGroupDto subGroup) {
		AssignmentDto assignmentDto = new AssignmentDto();
		assignmentDto.setNo(no);
		assignmentDto.setStratification(offset);
		setBiosample(assignmentDto, biosample);
		if (stage!=null) {
			setStage(assignmentDto, stage);
			assignmentDto.setName(stageService.getNextName(stage));
		}
		setSubgroup(assignmentDto, subGroup);
		
		return assignmentDto;
	}

	@SuppressWarnings("deprecation")
	public void mapExecutionDetails(AssignmentDto assignment) {
		assignment.setExecutionDetails(executionDetailService.map(executionDetailService.getByAssignment(assignment.getId())));
	}

	public void mapActionDefinition(AssignmentDto assignment) {
		assignment.setActionDefinition(assignmentPatternService.map(assignmentPatternService.getByAssignment(assignment.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapResultAssignments(AssignmentDto assignment) {
		assignment.setResultAssignments(resultAssignmentService.map(resultAssignmentService.getResultAssignmentByAssignment(assignment.getId())));
	}

	public ExecutionDetailDto getExecutionDetail(AssignmentDto assignment, IndividualAction individualAction) {
		for (ExecutionDetailDto executionDetail : assignment.getExecutionDetails()) {
			if (assignment.equals(individualAction.getAssignment())
				&& individualAction.getSchedulePhase().equals(executionDetail.getPhaseLink()))
				return executionDetail;
		}
		return executionDetailService.newExecutionDetails(assignment, individualAction.getSchedulePhase());
	}

	public boolean isActionRequired(AssignmentDto assignment, Date date, StudyAction action, Duration offset) {
		Set<IndividualAction> individualActionSet = getAllActions(assignment, getNextStageFirstDate(assignment, offset),offset).get(date);
		return individualActionSet == null ? false : individualActionSet.stream()
				.anyMatch(ia-> ia.getStudyAction().equals(action) && ia.getSchedulePhase()!=null);
	}

	public void setWeight(AssignmentDto a, Double weight) {
		if (a.getStage() == null) {
			a.setTmpWeight(weight);
			return;
		}
		AssayResultDto result = getWeightResult(a);
		if (result == null && weight != null)
			result = assayResultService.createWeightResult(a, result);
		else if (result == null && weight == null)
			return;
		assayResultService.setValue(result, "Weight [g]", String.valueOf(weight));
	}

	public AssayResultDto attachWeightResultFromDB(AssignmentDto assignment) {
		AssayResultDto weight = null;
		if (assignment.getBiosample() != null && assignment.getStage() != null) {
			try {
				Measurement weighing = new Measurement(assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME));
				assayResultService.attachOrCreateStudyResults(assignment.getStage().getStudy(), false,
						Collections.singleton(assignment.getBiosample()), Collections.singleton(weighing),
						stageService.getFirstPhase(assignment.getStage()), null, null);
				weight = biosampleService.getFirstResultByPhaseAndAssayName(assignment.getBiosample(), stageService.getFirstPhase(assignment.getStage()), assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME).getName());
				if (weight != null)
					addResultAssignmentLink(assignment, new ResultAssignmentDto(weight), false);
			} catch (Exception e) {
				JExceptionDialog.showError(e);
			}
		}
		return weight;
	}


	public void addResultltAssignmentLink(AssignmentDto assignment,ResultAssignmentDto resultAssignmentLinks) {
		addResultAssignmentLink(assignment, resultAssignmentLinks, false);
	}

	private void addResultAssignmentLink(AssignmentDto assignment, ResultAssignmentDto resultAssignmentLinks, boolean cross) {
		//prevent endless loop
		if (assignment.getResultAssignments().contains(resultAssignmentLinks))
			return ;
		//add new member
		assignment.getResultAssignments().add(resultAssignmentLinks);
		//update child if request is not from it
		if (!cross) {
			resultAssignmentService.setAssignment(resultAssignmentLinks, assignment);
		}
	}

	public void serializeDataList(AssignmentDto assignment) {
		StringBuilder sb = new StringBuilder();
		if (assignment.getDataListList() == null || assignment.getDataListList().isEmpty()) {
			assignment.setDataListList(null);
			return;
		}
		for(Double d : assignment.getDataListList()) {
			if (d == null) sb.append(" ");
			if (d != null) sb.append(d.toString());
			sb.append(",");
		}
		assignment.setDatalist(sb.toString());
	}

	public void reset(AssignmentDto assignment) {
		resetCage(assignment);
		setBiosample(assignment,null);
		assignment.getResultAssignments().clear();
		assignment.setDatalist(null);
	}

	public Map<Integer, List<AssignmentDto>> splitByGroup(Collection<AssignmentDto> list) {
		Map<Integer, List<AssignmentDto>> id2list = new HashMap<>();
		for (AssignmentDto rndSample : list) {
			Integer key = rndSample.getSubgroup().getGroup()==null? -1: (int) rndSample.getSubgroup().getGroup().getId();
			List<AssignmentDto> l = id2list.get(key);
			if(l==null) {
				l = new ArrayList<>();
				id2list.put(key, l);
			}
			l.add(rndSample);
		}
		return id2list;
	}

	public List<Double> getData(List<AssignmentDto> rows, int index) {
		List<Double> res = new ArrayList<Double>();
		for (AssignmentDto s : rows) {
			if(index<0) {
				Double data = getWeight(s) != null ? Double.valueOf(getWeight(s)) : null;
				res.add(data);
			}else {
				if(index<s.getDataListList().size()) {
					res.add(s.getDataListList().get(index));
				}
			}
		}
		return res;
	}

	public Double getWeight(AssignmentDto assignment) {
		if (assignment.getStage()==null) 
			return assignment.getTmpWeight();
		return getWeightResult(assignment) == null ? null : assayResultService.getFirstAsDouble(getWeightResult(assignment));
	}

	public EnclosureDto getPreviousEnclosure(AssignmentDto assignment) {
		BiosampleDto biosample = assignment.getBiosample();
		if(biosample==null) return null;
		PhaseDto firstPhase = assignment.getStage().getPreviousStage() == null ? stageService.getFirstPhase(assignment.getStage()) : stageService.getFirstPhase(assignment.getStage().getPreviousStage());
		PhaseDto lastPhase = assignment.getStage().getPreviousStage() == null ? stageService.getLastPhase(assignment.getStage()) : stageService.getLastPhase(assignment.getStage().getPreviousStage());
		return biosampleService.getInsertEnclosure(biosample, assignment.getStage().getPreviousStage() != null ? lastPhase : firstPhase);
	}
}
