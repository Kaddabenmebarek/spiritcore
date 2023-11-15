package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.business.AnimalUsageRest;
import com.actelion.research.business.AnimalUsageRestDetail;
import com.actelion.research.security.entity.User;
import com.actelion.research.util.Pair;
import com.actelion.research.util.ui.JExceptionDialog;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.StageDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.GroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.dto.StageBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StagePatternDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Participant;
import com.idorsia.research.spirit.core.dto.view.Pool;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.model.ActionPatterns;
import com.idorsia.research.spirit.core.model.Assignment;
import com.idorsia.research.spirit.core.model.Group;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.Stage;
import com.idorsia.research.spirit.core.model.StageBiotypeMetadataValue;
import com.idorsia.research.spirit.core.model.StagePattern;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.StatUtils;
import com.idorsia.research.spirit.core.util.ThreadUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.util.Frequency;

@Service
public class StageService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -4726638837225462457L;
	@SuppressWarnings("unchecked")
	private static Map<Integer, StageDto> idToStage = (Map<Integer, StageDto>) getCacheMap(StageDto.class);

	@Autowired
	private StageDao stageDao;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private SubGroupService subGroupService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private StagePatternService stagePatternService;
	@Autowired
	private StageBiotypeMetadataValueService stageBiotypeMetadataValueService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private BiotypeService biotypeService;
	@Autowired
	private StudyActionResultService studyActionResultService;

	public Stage get(Integer id) {
		return stageDao.get(id);
	}

	public Stage getStageById(int id) {
		return stageDao.getStageById(id);
	}

	public Stage getBySchedule(int scheduleId) {
		return stageDao.getBySchedule(scheduleId);
	}

	public List<Stage> getStagesByStudyId(Integer studyId) {
		return stageDao.getStagesByStudyId(studyId);
	}

	public List<Stage> getStagesByStudyIdentifier(String studyIdentifier) {
		return stageDao.getStagesByStudyIdentifier(studyIdentifier);
	}

	@Transactional
	public void changeOwner(Stage stage, String updUser, String creUser) {
		stageDao.changeOwner(stage, updUser, creUser);
	}

	public List<Stage> list() {
		return stageDao.list();
	}

	public int getCount() {
		return stageDao.getCount();
	}

	public Integer saveOrUpdate(Stage stage) {
		return stageDao.saveOrUpdate(stage);
	}

	public int addStage(Stage stage) {
		return stageDao.addStage(stage);
	}

	public void getParticipants(StageDto stage, HashSet<Participant> participants) {
		List<AssignmentDto> assignments = new ArrayList<>(stage.getAssignments());
		Collections.sort(assignments);
		// Over all assignments having a biosample
		outerloop: //
		for (AssignmentDto assignment : assignments) {
			BiosampleDto biosample = assignment.getBiosample();
			if (biosample != null) {

				// Add assignment to the first participants with the same biosample
				for (Participant participant : participants) {
					if (biosample.equals(participant.getBiosample())) {
						participant.getAssignmentSeries().add(assignment);
						continue outerloop;
					}
				}
				// No matching participant found for this biosample
				// Create a new participant with this biosample and add the assignment
				Participant participant = new Participant();
				participant.setBiosample(biosample);
				participant.getAssignmentSeries().add(assignment);
				participants.add(participant);
			} else {
				Participant participant = new Participant();
				participant.getAssignmentSeries().add(assignment);
				participants.add(participant);
			}
		}
	}

	public Date getFirstDate(StageDto stage) {
		if (stage.isDynamic()) {
			ZonedDateTime zonedDate = getZonedDate(stage);
			if (zonedDate == null) {
				return null;
			}
			return Date.from(zonedDate.toInstant());
		} else {
			if (stage.getPreviousStage() == null || getLastDate(stage.getPreviousStage()) == null) {
				return null;
			}
			return new Date(
					getLastDate(stage.getPreviousStage()).getTime() + stage.getOffsetFromPreviousStage().toMillis());
		}
	}

	public Date getLastDate(StageDto stage) {
		Date firstDate = getFirstDate(stage);
		return firstDate == null ? null
				: Date.from(Instant.ofEpochMilli(
						firstDate.toInstant().toEpochMilli() + getLastPhase(stage).getPhase().toMillis()));
	}

	public PhaseDto getLastPhase(StageDto stage) {
		List<PhaseDto> phaseList = stage.getPhases();
		if (phaseList.size() == 0)
			return null;
		return phaseList.get(phaseList.size() - 1);
	}

	@SuppressWarnings("deprecation")
	public ZonedDateTime getZonedDate(StageDto stage) {
		if (stage.isDynamic()) {
			return stage.getStartDate();
		} else {
			if (stage.getPreviousStage() != null && getZonedDate(stage.getPreviousStage()) != null) {
				return getZonedDate(stage.getPreviousStage()).plus(stage.getOffsetFromPreviousStage());
			}
			return null;
		}
	}

	public PhaseDto getFirstPhase(StageDto stage) {
		List<PhaseDto> phaseList = stage.getPhases();
		if (phaseList.size() == 0)
			return null;
		return phaseList.get(0);
	}

	public PhaseDto getPhase(StageDto stage, Duration duration) {
		List<PhaseDto> phases = stage.getPhases();
		for (PhaseDto phase : phases) {
			if (phase.getPhase().equals(duration))
				return phase;
		}
		return null;
	}

	public PhaseDto getPhase(StageDto stage, String name) {
		for (PhaseDto phase : stage.getPhases()) {
			if (phaseService.getShortName(phase).equals(name) || phase.toString().equals(name))
				return phase;
		}
		return null;
	}

	public List<StageDto> map(List<Stage> stages) {
		List<StageDto> res = new ArrayList<StageDto>();
		for(Stage stage : stages) {
			res.add(map(stage));
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public StageDto map(Stage stage) {
		if (stage == null)
			return null;
		StageDto stageDto = idToStage.get(stage.getId());
		if (stageDto == null) {
			stageDto = dozerMapper.map(stage, StageDto.class, "stageCustomMapping");
			if (idToStage.get(stage.getId()) == null)
				idToStage.put(stage.getId(), stageDto);
			else
				stageDto = idToStage.get(stage.getId());
			if (stageDto.getNextStage() != null)
				stageDto.getNextStage().setPreviousStage(stageDto);
		}
		return stageDto;
	}

	@Transactional
	public void save(StageDto stage) throws Exception {
		save(stage, false);
	}

	@SuppressWarnings("deprecation")
	protected void save(StageDto stage, Boolean cross) throws Exception {
		try {
			if (stage != null && !savedItems.contains(stage)) {
				savedItems.add(stage);
				if(stage.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(stage);
				}
				if (stage.getNextStage()!=null && stage.getNextStage().getId() == Constants.NEWTRANSIENTID)
					save(stage.getNextStage(), true);
				if (stage.getStudy().getId() == Constants.NEWTRANSIENTID)
					studyService.save(stage.getStudy(), true);
				stage.setUpdDate(new Date());
				stage.setUpdUser(UserUtil.getUsername());
				if(stage.getId().equals(Constants.NEWTRANSIENTID)) {
					stage.setCreDate(new Date());
					stage.setCreUser(UserUtil.getUsername());
				}
				stage.setId(saveOrUpdate(dozerMapper.map(stage, Stage.class, "stageCustomMapping")));
				idToStage.put(stage.getId(), stage);
				if(stage.getPhasesNoMapping()!=null)
					for (PhaseDto phase : stage.getPhases())
						phaseService.save(phase, true);
				if(stage.getAssignmentsNoMapping()!=null)
					for (AssignmentDto assignment : stage.getAssignments())
						assignmentService.save(assignment, true);
				if(stage.getActionPatternsNoMapping()!=null)
					for (ActionPatternsDto ap : stage.getActionPatterns())
						actionPatternsService.save(ap, true);
				if(stage.getActionDefinitionNoMapping()!=null)
					for (StagePatternDto stagePattern : stage.getActionDefinition())
						stagePatternService.save(stagePattern, true);
				if(stage.getGroupsNoMapping()!=null)
					for (GroupDto group : stage.getGroups())
						groupService.save(group, true);
				if(stage.getStageMetadataValuesNoMapping()!=null)
					for (StageBiotypeMetadataValueDto value : stage.getStageMetadataValues())
						stageBiotypeMetadataValueService.save(value, true);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if (!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteChildren(StageDto stage) throws Exception {
		if(stage.getPhasesNoMapping()!=null) {
			for(Phase p : phaseService.getPhasesByStage(stage.getId())) {
				Boolean found = false;
				for(PhaseDto child : stage.getPhases()){
					if(p.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					phaseService.delete(phaseService.map(p), true);
				}
			}
		}
		if(stage.getAssignmentsNoMapping()!=null) {
			for(Assignment a : assignmentService.getAssignmentsByStage(stage.getId())) {
				Boolean found = false;
				for(AssignmentDto child : stage.getAssignments()){
					if(a.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					assignmentService.delete(assignmentService.map(a), true);
				}
			}
		}
		if(stage.getActionPatternsNoMapping()!=null) {
			for(ActionPatterns ap : actionPatternsService.getActionPatternsByStage(stage.getId())) {
				Boolean found = false;
				for(ActionPatternsDto child : stage.getActionPatterns()){
					if(ap.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					actionPatternsService.delete(actionPatternsService.map(ap), true);
				}
			}
		}
		if(stage.getActionDefinitionNoMapping()!=null) {
			for(StagePattern sp : stagePatternService.getByStage(stage.getId())) {
				Boolean found = false;
				for(StagePatternDto child : stage.getActionDefinition()){
					if(sp.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					stagePatternService.delete(stagePatternService.map(sp), true);
				}
			}
		}
		if(stage.getGroupsNoMapping()!=null) {
			for(Group g : groupService.getByStage(stage.getId())) {
				Boolean found = false;
				for(GroupDto child : stage.getGroups()){
					if(g.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					groupService.delete(groupService.map(g), true);
				}
			}
		}
		if(stage.getStageMetadataValuesNoMapping()!=null) {
			for(StageBiotypeMetadataValue bmv : stageBiotypeMetadataValueService.getByStage(stage.getId())) {
				Boolean found = false;
				for(StageBiotypeMetadataValueDto child : stage.getStageMetadataValues()){
					if(bmv.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					stageBiotypeMetadataValueService.delete(stageBiotypeMetadataValueService.map(bmv), true);
				}
			}
		}
	}

	@Transactional
	public void delete(StageDto stage) throws Exception {
		delete(stage, false);
	}
	
	protected void delete(StageDto stage, Boolean cross) throws Exception {
		stageDao.delete(stage.getId());
	}

	public List<Integer> getStageDtoIds(List<StageDto> stages) {
		List<Integer> stageIds = new ArrayList<Integer>();
		for (StageDto st : stages) {
			stageIds.add(st.getId());
		}
		return stageIds;
	}

	public List<Integer> getStageIds(List<Stage> stages) {
		List<Integer> stageIds = new ArrayList<Integer>();
		for (Stage st : stages) {
			stageIds.add(st.getId());
		}
		return stageIds;
	}

	public StageDto getStageDto(Integer id) {
		return map(get(id));
	}

	public Set<ActionPatternsDto> getActionDefinitionWithoutWeekly(StageDto stage) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for (ActionPatternsDto ap : stage.getActionDefinitionPattern()) {
			if (ap.getSchedule().getRecurrence() == null
					|| !ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		return results;
	}

	public Set<ActionPatternsDto> getWeeklyDefinition(StageDto stage) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for (ActionPatternsDto ap : stage.getActionDefinitionPattern()) {
			if (ap.getSchedule().getRecurrence() != null
					&& ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		return results;
	}

	public void setPreviousStage(StageDto stage, StageDto previousStage) {
		setPreviousStage(stage, previousStage, false);
	}

	@SuppressWarnings("deprecation")
	public void setPreviousStage(StageDto stage, StageDto previousStage, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(stage.getPreviousStage(), previousStage))
			return;
		// remove from the old owner
		if (stage.getPreviousStage() != null && !(cross && previousStage == null))
			setNextStage(stage.getPreviousStage(), null, true);
		// set new owner
		stage.setPreviousStage(previousStage);
		// set myself to new owner
		if (previousStage != null && !cross)
			setNextStage(previousStage, stage, true);
	}

	public void setNextStage(StageDto stage, StageDto nextStage) {
		setNextStage(stage, nextStage, false);
	}

	@SuppressWarnings("deprecation")
	public void setNextStage(StageDto stage, StageDto nextStage, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(stage.getNextStage(), nextStage))
			return;
		// remove from the old owner
		if (stage.getNextStage() != null && !(cross && nextStage == null))
			setPreviousStage(stage.getNextStage(), null, true);
		// set new owner
		stage.setNextStage(nextStage);
		// set myself to new owner
		if (nextStage != null && !cross)
			setPreviousStage(nextStage, stage, true);
	}

	public void setStudy(StageDto stage, StudyDto study) {
		setStudy(stage, study, false);
	}

	@SuppressWarnings("deprecation")
	public void setStudy(StageDto stage, StudyDto study, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(stage.getStudy(), study))
			return;
		if (stage.getNextStage() != null) {
			setPreviousStage(stage.getNextStage(), stage.getPreviousStage());
		} else if (stage.getPreviousStage() != null) {
			setNextStage(stage.getPreviousStage(), null);
		}
		// remove from the old owner
		if (stage.getStudy() != null && !(cross && study == null))
			studyService.removeStage(stage.getStudy(), stage, true);
		// set new owner
		stage.setStudy(study);
		// set myself to new owner
		if (study != null && !cross)
			studyService.addStage(study, stage, true);
	}

	public void addPhase(StageDto stage, PhaseDto phase) {
		addPhase(stage, phase, false);
	}

	public void addPhase(StageDto stage, PhaseDto phase, boolean cross) {
		// prevent endless loop
		if (stage.getPhases().contains(phase))
			return;
		// add new member
		stage.getPhases().add(phase);
		Collections.sort(stage.getPhases());
		// update child if request is not from it
		if (!cross) {
			phaseService.setStage(phase, stage, true);
		}
	}

	public void removePhase(StageDto stage, PhaseDto phase) {
		removePhase(stage, phase, false);
	}

	protected void removePhase(StageDto stage, PhaseDto phase, boolean cross) {
		// prevent endless loop
		if (!stage.getPhases().contains(phase))
			return;
		// remove old member
		stage.getPhases().remove(phase);
		// remove child's owner
		if (!cross) {
			phaseService.setStage(phase, null, true);
		}
	}

	public ActionPatternsDto duplicate(StageDto stage, ActionPatternsDto actionPattern) {
		ActionPatternsDto cloneActionPattern = null;
		ScheduleDto schedule = actionPattern.getSchedule();
		ScheduleDto cloneSchedule = new ScheduleDto();
		scheduleService.setRrule(cloneSchedule, schedule.getrRule());
		try {
			cloneSchedule.setLastPhase(schedule.getLastPhase());
			scheduleService.setStartDate(cloneSchedule, schedule.getStartDate());
			scheduleService.removeTimepoint(cloneSchedule, LocalTime.MIDNIGHT);
		} catch (Exception e) {
			// at creation time there can not be any already attached entity, so we
			// shouldn't end up here
			e.printStackTrace();
		}
		schedule.getTimePointsSorted().forEach(e -> scheduleService.addTimePoint(cloneSchedule, e));
		cloneActionPattern = new ActionPatternsDto();
		try {
			actionPatternsService.setStage(cloneActionPattern, stage);
			actionPatternsService.setAction(cloneActionPattern, actionPattern.getAction());
			actionPatternsService.setSchedule(cloneActionPattern, cloneSchedule);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cloneActionPattern;
	}

	public boolean removeActionPattern(StageDto stage, ActionPatternsDto actionPatterns) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setAction(actionPatterns.getAction());
			studyActionResultQuery.setSubjectSet(stage);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeActionPattern(stage, actionPatterns, false);
		return true;
	}

	public void removeActionPattern(StageDto stage, ActionPatternsDto actionPattern, boolean cross) {
		// prevent endless loop
		if (!stage.getActionPatterns().contains(actionPattern))
			return;
		// remove phases
		outerloop: for (PhaseDto phase : scheduleService.getPhases(actionPattern.getSchedule())) {
			for (ActionPatternsDto ap : stage.getActionPatterns()) {
				if (ap.equals(actionPattern))
					continue;
				for (PhaseDto apPhase : scheduleService.getPhases(ap.getSchedule())) {
					if (apPhase.equals(phase)) {
						continue outerloop;
					}
				}
			}
			removePhase(stage, phase);
		}
		removeActionDefinition(stage, actionPattern);
		// remove old member
		stage.getActionPatterns().remove(actionPattern);
		// remove child's owner
		if (!cross) {
			try {
				actionPatternsService.setStage(actionPattern, null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean removeActionDefinition(StageDto stage, ActionPatternsDto actionDefinition) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setStage(stage);
			studyActionResultQuery.setAction(actionDefinition.getAction());
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION_DEFINITION, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		StagePatternDto stagePattern = null;
		for (StagePatternDto pattern : stage.getActionDefinition()) {
			if (pattern.getActionpattern().equals(actionDefinition)) {
				stagePattern = pattern;
				break;
			}
		}
		if (stagePattern != null) {
			stage.getActionDefinition().remove(stagePattern);
		}
		for (GroupDto group : stage.getGroups()) {
			groupService.removeActionDefinition(group, actionDefinition);
		}
		if (actionDefinition.getSchedule().getRecurrence() != null
				&& Frequency.WEEKLY.equals(actionDefinition.getSchedule().getRecurrence().getFrequency())) {
			for (AssignmentDto assignment : stage.getAssignments()) {
				assignmentService.removeActionDefinitionByParent(assignment, actionDefinition);
			}
			if (actionDefinition.getChildren() != null && !actionDefinition.getChildren().isEmpty()) {
				actionDefinition.getChildren().clear();
			}
		}
		return true;
	}

	public boolean addActionDefinition(StageDto stage, ActionPatternsDto actionDefinition) {
		if (StudyActionType.DISPOSAL.equals(actionDefinition.getAction().getType()))
			return false;
		stage.getActionDefinition().add(new StagePatternDto(stage, actionDefinition));
		for (GroupDto group : stage.getGroups()) {
			groupService.removeActionDefinition(group, actionDefinition);
		}
		return true;
	}

	public void addActionPattern(StageDto stage, ActionPatternsDto actionPattern) {
		addActionPattern(stage, actionPattern, false);
	}

	public void addActionPattern(StageDto stage, ActionPatternsDto actionPattern, boolean cross) {
		// prevent endless loop
		if (stage.getActionPatterns().contains(actionPattern))
			return;
		// add new member
		stage.getActionPatterns().add(actionPattern);
		// update child if request is not from it
		if (!cross) {
			actionPatternsService.setStage(actionPattern, stage, true);
		}
	}

	public List<AssignmentDto> getAssignments(GroupDto group) {
		StageDto stage = group.getStage();
		List<AssignmentDto> result = new ArrayList<>();
		if (group != null)
			for (AssignmentDto assignment : stage.getAssignments()) {
				if (group.equals(assignment.getSubgroup().getGroup()))
					result.add(assignment);
			}
		return result;
	}
	
	public void addAssignment(StageDto stage, AssignmentDto assignment) {
		addAssignment(stage, assignment, false);
	}

	public void addAssignment(StageDto stage, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (stage.getAssignments().contains(assignment))
			return;
		// add new member
		stage.getAssignments().add(assignment);
		// update child if request is not from it
		if (!cross) {
			assignmentService.setStage(assignment, stage, true);
		}
	}

	public boolean removeAssignment(StageDto stage, AssignmentDto assignment) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setStage(stage);
			studyActionResultQuery.setSubjectSet(new Pool(Collections.singleton(assignment.getBiosample())));
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ASSIGNMENT, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeAssignment(stage, assignment, false);
		return true;
	}

	protected void removeAssignment(StageDto stage, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (!stage.getAssignments().contains(assignment))
			return;
		// remove old member
		stage.getAssignments().remove(assignment);
		// remove child's owner
		if (!cross) {
			assignmentService.setStage(assignment, null, true);
		}
	}

	public void addGroup(StageDto stage, GroupDto group) {
		addGroup(stage, group, false);
	}

	public void addGroup(StageDto stage, GroupDto group, boolean cross) {
		// prevent endless loop
		if (stage.getGroups().contains(group))
			return;
		// add new member
		stage.getGroups().add(group);
		// update child if request is not from it
		if (!cross) {
			groupService.setStage(group, stage, true);
		}
	}

	public boolean removeGroup(StageDto stage, GroupDto group) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(group);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.GROUP, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeGroup(stage, group, false);
		return true;
	}

	protected void removeGroup(StageDto stage, GroupDto group, boolean cross) {
		// prevent endless loop
		if (!stage.getGroups().contains(group))
			return;
		// remove old member
		stage.getGroups().remove(group);
		// remove child's owner
		if (!cross) {
			groupService.setStage(group, null, true);
		}
		Set<AssignmentDto> assignments = new HashSet<>(stage.getAssignments());
		for (AssignmentDto assignment : assignments) {
			if (group.equals(assignment.getSubgroup().getGroup()))
				removeAssignment(stage, assignment);
		}

	}

	public Set<AssignmentDto> getAssignments(StageDto stage, SubGroupDto subgroup) {
		Set<AssignmentDto> result = new HashSet<>();
		if (subgroup != null)
			for (AssignmentDto assignment : stage.getAssignments()) {
				if (subgroup.equals(assignment.getSubgroup()))
					result.add(assignment);
			}
		return result;
	}
	
	public List<AssignmentDto> getAssignmentsList(StageDto stage, SubGroupDto subGroup) {
		List<AssignmentDto> result = new ArrayList<>();
		if (subGroup != null)
			for (AssignmentDto assignment : stage.getAssignments()) {
				if (subGroup.equals(assignment.getSubgroup())) result.add(assignment);
			}
		return result;
	}

	public Set<StudyAction> getActions(StageDto stage, BiosampleDto biosample, PhaseDto phase) {
		Set<StudyAction> result = new HashSet<>();
		if (stage.getPhases().contains(phase)) {
			if (biosample != null) {
				AssignmentDto assignment = biosampleService.getAssignment(biosample, phase.getStage());
				if (assignment != null) {
					for (ActionPatternsDto actionPatternAbstract : assignmentService
							.getFullActionDefinition(assignment)) {
						if (scheduleService.getPhases(actionPatternAbstract.getSchedule()).contains(phase))
							result.add(actionPatternAbstract.getAction());
					}
				}
			}
		}
		return result;
	}

	public PhaseDto getClosestPhase(StageDto stage, Duration duration) {
		List<PhaseDto> phases = stage.getPhases();
		PhaseDto last = null;
		for (PhaseDto phase : phases) {
			if (phase.getPhase().equals(duration))
				return phase;
			if (phase.getPhase().compareTo(duration) < 0)
				last = phase;
			else if (phase.getPhase().compareTo(duration) > 0 && last != null)
				return last;
		}
		return last;
	}

	public SubGroupDto getSubGroup(StageDto stage, BiosampleDto biosample) {
		for (AssignmentDto assignment : stage.getAssignments()) {
			if (biosample.equals(assignment.getBiosample()))
				return assignment.getSubgroup();
		}
		return null;
	}

	public GroupDto getGroup(StageDto stage, BiosampleDto biosample) {
		SubGroupDto subGroup = getSubGroup(stage, biosample);
		return subGroup == null ? null : subGroup.getGroup();
	}

	@SuppressWarnings("unchecked")
	public List<PhaseDto> getPhasesSorted(StageDto stage) {
		List<PhaseDto> phaseList = (List<PhaseDto>) Cache.getInstance()
				.get("phasesSorted" + stage.getStudy().getStudyId() + "_" + stage.getName());
		if (phaseList == null) {
			phaseList = new ArrayList<>(stage.getPhases());
			Collections.sort(phaseList);
			Cache.getInstance().add("phasesSorted" + stage.getStudy().getStudyId() + "_" + stage.getName(), phaseList);
		}
		return phaseList;
	}

	public PhaseDto parsePhase(StageDto stage, String value) {
		for (PhaseDto phase : stage.getPhases()) {
			if (phaseService.getDisplayPhase(phase).equals(phaseService.getDurationFromString(value))) return phase;
		}
		return null;
	}

	public Integer getNo(StageDto stage) {
    	int i = 1;
		for (StageDto s : stage.getStudy().getStages()) {
			if (stage.equals(s)) return i;
			i++;
		}
		return i;
	}

	public PhaseDto getNextPhase(PhaseDto phase) {
		boolean match = false;
		StageDto stage = phase.getStage();
		for (PhaseDto phase1 : stage.getPhases()) {
			if (match) return phase1;
			if (phase.equals(phase1)) match = true;
		}
		return null;
	}

	public PhaseDto getPreviousPhase(PhaseDto phase) {
		PhaseDto previous = null;
		StageDto stage = phase.getStage();
		for (PhaseDto phase1 : stage.getPhases()) {
			if (phase.equals(phase1)) return previous;
			previous = phase1;
		}
		return null;
	}
	
	public PhaseDto getNextPhaseAnyStage(PhaseDto phase) {
		boolean match = false;
		StageDto stage = phase.getStage();
		for (PhaseDto phase1 : stage.getPhases()) {
			if (match) return phase1;
			if (phase.equals(phase1)) match = true;
		}
		if(match && stage.getNextStage()!=null)
			return getFirstPhase(stage.getNextStage());
		return null;
	}

	public PhaseDto getPreviousPhaseAnyStage(PhaseDto phase) {
		PhaseDto previous = null;
		StageDto stage = phase.getStage();
		for (PhaseDto phase1 : stage.getPhases()) {
			if (phase.equals(phase1)) 
				return previous!=null?previous:stage.getPreviousStage()!=null?getLastPhase(stage.getPreviousStage()):null;
			previous = phase1;
		}
		return null;
	}

	public ActionPatternsDto getSinglePhaseActionPattern(StageDto stage, StudyAction action, PhaseDto phase) {
		ScheduleDto schedule = new ScheduleDto();
		scheduleService.addTimePoint(schedule, LocalTime.of(phaseService.getHours(phase, false), phaseService.getMinutes(phase, false)));
		scheduleService.setStartDate(schedule, phaseService.getDays(phase, false));
		scheduleService.setRrule(schedule, null);
		ActionPatternsDto ap = getActionPatterns(stage, action.getType())
				.stream()
				.filter(a -> a.getSchedule().getStartDate() == schedule.getStartDate()
						&& a.getSchedule().getTimePoints().equals(schedule.getTimePoints())
						&& a.getSchedule().getrRule() == null
						&& a.getAction().equals(action))
				.findFirst()
				.orElse(null);
		if (ap == null) {
			ap = new ActionPatternsDto();
			try {
				actionPatternsService.setStage(ap, stage);
				actionPatternsService.setAction(ap, action);
				actionPatternsService.setSchedule(ap, schedule);
			} catch (Exception e) {
				e.printStackTrace();
			}
			addActionPattern(stage, ap);
		}
		return ap;
	}

	public Set<ActionPatternsDto> getActionPatterns(StageDto stage, StudyActionType studyActionType) {
		return getActionPatterns(stage, studyActionType, false);
	}
	
	public Set<ActionPatternsDto> getActionPatterns(StageDto stage, StudyActionType studyActionType, boolean getVisibleOnly) {
		Set<ActionPatternsDto> result = new HashSet<>();
		stage.getActionPatterns().forEach(a -> {if (a.getActionType().equals(studyActionType) && 
				(!getVisibleOnly || actionPatternsService.isVisible(a))) result.add(a);});
		return result;
	}

	public int getNData(StageDto stage) {
		int n = 0;
		for (AssignmentDto rndSample : stage.getAssignments()) {
			n = Math.max(n, rndSample.getDataListList().size());
		}
		return n;
	}

	public void getActions(StageDto stage, HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> events,
			Set<Participant> participants) {
		if (getFirstDate(stage) != null) {
			for (AssignmentDto assignment : stage.getAssignments()) {
				Thread t = new Thread() {
					@Override
					public void run() {
						try {
							Participant p = studyService.getParticipantFor(stage.getStudy(), participants, assignment);
							assignmentService.getActions(assignment, events, participantService.getOffsetToFollow(p, assignment));
						}catch (Exception e) {
							e.printStackTrace();
						}finally {
							ThreadUtils.removeThread(this);
						}
					}
				};	
				ThreadUtils.start(t);
			}
		}
		ThreadUtils.waitProcess();
	}

	@SuppressWarnings("deprecation")
	public void setStartDate(StageDto stage, ZonedDateTime date) {
		stage.setStartDate(date);
		try {
			updateSchedules(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateSchedules(StageDto stage) throws Exception {
		for (ActionPatternsDto actionPattern : stage.getActionPatterns()) {
			scheduleService.setPhases(actionPattern.getSchedule());
		}
		for(AssignmentDto a : stage.getAssignments()) {
			for (ActionPatternsDto actionPattern : stage.getActionPatterns()) {
				assignmentService.calculateOffset(a, actionPattern);
			}
        }
	}

	public boolean addAction(StageDto stage, ActionPatternsDto actionPattern) {
		if(actionPattern!=null && actionPattern.getSchedule()!=null && actionPattern.getSchedule().getRecurrence()!=null && 
				Frequency.WEEKLY.equals(actionPattern.getSchedule().getRecurrence().getFrequency())) {
			for (GroupDto group : stage.getGroups()) {
				groupService.removeActionDefinition(group, actionPattern);
			}
			for(AssignmentDto a : stage.getAssignments()) {
				assignmentService.addActionDefinitionWeekly(a, actionPattern);
			}
		}else
			addActionDefinition(stage, actionPattern);
		return true;
	}

	public void updatePhases(StageDto stage, ActionPatternsDto actionPattern) {
		for(AssignmentDto a : stage.getAssignments()) {
			for(ActionPatternsDto ap : a.getActionDefinitionPattern()) {
				if(actionPattern.equals(ap.getParent())) {
					try {
						ScheduleDto old = actionPattern.getSchedule();
						scheduleService.setRecurrence(ap.getSchedule(), null);
						ScheduleDto ss = new ScheduleDto();
						scheduleService.setRrule(ss, actionPattern.getSchedule().getrRule());						
						ss.setLastPhase(old.getLastPhase());
						scheduleService.setStartDate(ss, old.getStartDate());
						scheduleService.removeTimepoint(ss, LocalTime.MIDNIGHT);
						for(LocalTime lt : old.getTimepointSet())
							scheduleService.addTimePoint(ss, lt);
						actionPatternsService.setSchedule(ap, ss);
						scheduleService.setPhases(ss, a.getStratification());
						actionPatternsService.setAction(ap, actionPattern.getAction());
						actionPatternsService.setParent(ap, actionPattern);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	public Integer setNSubjects(StageDto stage, int nAnimals, SubGroupDto subGroup) {
		Set<AssignmentDto> assignmentsSubGroup = getAssignments(stage, subGroup);
		if (assignmentsSubGroup.size() > nAnimals) {
			int diff = assignmentsSubGroup.size() - nAnimals;
			Set<AssignmentDto> toRemove = new HashSet<>();
			Pool toCheck = new Pool();
			loop:
			for (AssignmentDto next : assignmentsSubGroup) {
				if (subGroup.equals(next.getSubgroup()) && next.getBiosample() == null) {
					toRemove.add(next);
					toCheck.addBiosample(next.getBiosample());
					diff--;
					if (diff == 0) break loop;
				}
			}
			try {
				StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
				studyActionResultQuery.setSubjectSet(toCheck);
				studyActionResultQuery.setStage(stage);
				studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.GROUP_SIZE, studyActionResultQuery);
			} catch (Exception e) {
				return getAssignments(stage, subGroup).size();
			}
			for(AssignmentDto next : toRemove) {
				removeAssignment(stage, next);
			}
		} else {
			int nextNum = 0;
			for (AssignmentDto assignment : stage.getAssignments()) {
				if (assignment.getNo() > nextNum) nextNum = assignment.getNo();
			}
			for (int i = 0; i < nAnimals-assignmentsSubGroup.size(); i++){
				nextNum++;
				assignmentService.newAssignment(stage, null, nextNum, Duration.ZERO, subGroup);
			}
		}
		return subGroup.getAssignments().size();
	}

	public String getNextName(StageDto stage) {
		int next = 0;
		for (AssignmentDto assignment : stage.getAssignments()) {
			int n = 0;
			try {
				n = Integer.parseInt(assignment.getName());
			} catch (Exception e) {}
			next = Math.max(next, n);
		}
		int cur = next+1;
		return String.valueOf(cur);
	}

	public Set<ActionPatternsDto> getActionDefinitionWithWeekly(StageDto stage) {
		Set<ActionPatternsDto> result = new HashSet<>(stage.getActionDefinitionPattern());
		Set<ActionPatternsDto> toRemove = new HashSet<>();
		for(ActionPatternsDto ap : result){
			if(ap.getParent()!=null)
				toRemove.add(ap);
		}
		result.removeAll(toRemove);
		outerloop:
		for(ActionPatternsDto actionPattern : stage.getActionPatterns()) {
			if (actionPattern.getSchedule() != null
					&& actionPattern.getSchedule().getRecurrence() != null
					&& Frequency.WEEKLY.equals(actionPattern.getSchedule().getRecurrence().getFrequency()) 
	    			&& actionPattern.getParent()==null) {
	    		for(AssignmentDto a : stage.getAssignments()) {
    				if(assignmentService.getActionPatternByParent(a, actionPattern)==null) {
    					continue outerloop;
	    			}
	    		}
	    		result.add(actionPattern);
	    	}
		}
		return result;
	}

	public boolean isMoveChangingStage(StageDto first, StageDto second) {
		if (first.isDynamic()&&!second.isDynamic()) return true;
		return false;
	}

	public void moveBackward(StageDto stage) {
		StageDto nextStage = stage.getNextStage();
		StageDto previousStage = stage.getPreviousStage();
		if (nextStage == null) return;
		StageDto tempStage = nextStage;
		setPreviousStage(nextStage, previousStage);
		setNextStage(stage, tempStage.getNextStage());
		setPreviousStage(stage, tempStage);
		if (stage.isDynamic()&&!tempStage.isDynamic()){
			tempStage.setDynamic(true);
		}
	}

	public void moveForward(StageDto stage) {
		StageDto nextStage = stage.getNextStage();
		StageDto previousStage = stage.getPreviousStage();
		if (previousStage == null) return;
		StageDto tempStage = previousStage;
		setNextStage(previousStage, nextStage);
		setPreviousStage(stage, tempStage.getPreviousStage());
		setNextStage(stage, tempStage);
		if ((tempStage.isDynamic()&&!stage.isDynamic())||stage.getPreviousStage()==null){
			stage.setDynamic(true);
		}
	}
	
	public StageDto newStageDto(StudyDto study, StageDto previousStage, StageDto nextStage, BiotypeDto biotype, String name) {
		StageDto stage = new StageDto();
		setStudy(stage,study);
		stage.setBiotype(biotype);
		setPreviousStage(stage, previousStage);
		setNextStage(stage, nextStage);
		if (previousStage != null) 
			stage.setDynamic(false);
		stage.setName(name);
		addActionDefinition(stage, actionPatternsService.newActionPatternsDto(stage, scheduleService.newScheduleDto(Duration.ZERO), stage));
		return stage;
	}

	public StageDto duplicate(StageDto stage, StudyDto study) {
		BiotypeDto biotype = stage.getBiotype();
		StageDto cloneStage = newStageDto(study, null, null, biotype==null?null:biotypeService.getBiotype(biotype.getName()), (!study.equals(stage.getStudy()) ? "" : "Copy of ") + stage.getName());
		IdentityHashMap<ActionPatternsDto, ActionPatternsDto> actionPatternClones = new IdentityHashMap<>();
		cloneStage.setOffsetFromPreviousStage(stage.getOffsetFromPreviousStage());
		for (ActionPatternsDto actionPattern : stage.getActionPatterns()) {
			if (actionPattern.getAction().getType().equals(StudyActionType.GROUPASSIGN)) continue;// already there
			ActionPatternsDto cloneActionPattern = duplicate(cloneStage, actionPattern);
			actionPatternClones.put(actionPattern, cloneActionPattern);
		}
		for (ActionPatternsDto actionPattern : actionPatternClones.keySet()) {
			actionPatternsService.setParent(actionPatternClones.get(actionPattern), actionPatternClones.get(actionPattern.getParent()));
		}

		for (GroupDto group : stage.getGroups()) {
			groupService.duplicateGroup(group, cloneStage, actionPatternClones, true);
		}

		for (ActionPatternsDto actionPatternAbstract : stage.getActionDefinitionPattern()) {
			if (actionPatternAbstract.getAction().getType().equals(StudyActionType.GROUPASSIGN)) continue;
			cloneStage.addActionDefinition(actionPatternClones.get(actionPatternAbstract));
		}

		for (BiotypeMetadataValueDto bioTypeMetadataValue : stage.getMetadatas()) {
			cloneStage.addMetadata(bioTypeMetadataValue);
		}
		cloneStage.setDynamic(stage.isDynamic());
		if(stage.isDynamic() && studyService.getLastDate(study)!=null)
			try {
				setStartDate(cloneStage, ZonedDateTime.ofInstant(studyService.getLastDate(study).toInstant(),
				        ZoneId.systemDefault()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		cloneStage.setOffsetOfD0(getOffsetOfD0(stage));
		return cloneStage;
	}

	@SuppressWarnings("deprecation")
	public void mapAssignments(StageDto stage) {		
		stage.setAssignments(assignmentService.map(assignmentService.getAssignmentsByStage(stage.getId())));
		Collections.sort(stage.getAssignments());
	}

	@SuppressWarnings("deprecation")
	public void mapGroups(StageDto stage) {
		stage.setGroups(groupService.map(groupService.getByStage(stage.getId())));
		Collections.sort(stage.getGroups());
	}

	@SuppressWarnings("deprecation")
	public void mapPhases(StageDto stage) {
		List<PhaseDto> phases = phaseService.map(phaseService.getPhasesByStage(stage.getId()));
		Collections.sort(phases);
		stage.setPhases(phases);
	}

	@SuppressWarnings("deprecation")
	public void mapActionDefinition(StageDto stage) {
		stage.setActionDefinition(stagePatternService.map(stagePatternService.getByStage(stage.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapActionpatterns(StageDto stage) {
		stage.setActionPatterns(
				actionPatternsService.map(actionPatternsService.getActionPatternsByStage(stage.getId())));
	}

	public void mapMetadatas(StageDto stage) {
		stage.setStageMetadataValues(new HashSet<>(stageBiotypeMetadataValueService.map(stageBiotypeMetadataValueService.getByStage(stage.getId()))));
	}

	public void setNData(StageDto stage, int val) throws Exception{
		for (AssignmentDto sample : stage.getAssignments()) {
			while(sample.getDataListList().size()>val) {
				sample.getDataListList().remove(val);
			}
			while(sample.getDataListList().size()<val) {
				sample.getDataListList().add(null);
			}
		}
	}


	public void addMetadata(StageDto stage, BiotypeMetadataValueDto metaDefinition) {
		boolean found=false;
		for(StageBiotypeMetadataValueDto s : stage.getStageMetadataValues()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata())) {
				s.getBiotypeMetadataValue().setValue(metaDefinition.getValue());
				found=true;
				break;
			}
		}
		if(!found) {
			StageBiotypeMetadataValueDto metadata = new StageBiotypeMetadataValueDto(stage, metaDefinition);
			stage.getStageMetadataValues().add(metadata);
		}
		for (GroupDto group : stage.getGroups()) {
			for (GroupBiotypeMetadataValueDto bioTypeMetadataValue : group.getGroupMetadatas()) {
				if (bioTypeMetadataValue.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata()))
					groupService.removeMetadata(group, bioTypeMetadataValue.getBiotypeMetadataValue());
			}
		}
	}

	public void assignParticipantsFromPrevious(StageDto stage) {
		//TODO update according to Participants
		if (stage.getPreviousStage()!=null){
			HashSet<BiosampleDto> used = new HashSet<>();
			List<AssignmentDto>filteredAssignment = new ArrayList<>();
			List<AssignmentDto>groupAssignment = new ArrayList<>();
			List<AssignmentDto>subGroupAssignment = new ArrayList<>();
			
			for (AssignmentDto assignment : stage.getAssignments()) {
				if (assignment.getBiosample() != null) {
					used.add(assignment.getBiosample());
				}
				if(assignment.getSubgroup().getRandofromgroup()!=null) {
					if(assignment.getSubgroup().getRandofromsubgroup()!=null)
						subGroupAssignment.add(assignment);
					else
						groupAssignment.add(assignment);
				}else {
					filteredAssignment.add(assignment);
				}
			}
			
			filteredAssignment.addAll(0, groupAssignment);
			filteredAssignment.addAll(0, subGroupAssignment);
			
			assignmentsLoop:
			for (AssignmentDto assignment : filteredAssignment) {
				if (assignment.getBiosample() != null) continue;
				GroupDto fromGroup = assignment.getSubgroup().getRandofromgroup();
				SubGroupDto fromSubGroup = assignment.getSubgroup().getRandofromsubgroup();
				if (fromGroup != null) {
					Set<BiosampleDto> participants = fromSubGroup==null ? groupService.getParticipants(fromGroup) : subGroupService.getParticipants(fromSubGroup);
					for (BiosampleDto participant : participants) {
						if (!used.contains(participant) && canAssign(stage, assignment, participant, false)) {
							assignmentService.setBiosample(assignment, participant);
							used.add(participant);
							continue assignmentsLoop;
						}
					}
				} else {
					for(BiosampleDto participant : stage.getPreviousStage().getSubjects()) {
						if (!used.contains(participant) && canAssign(stage, assignment, participant, false)){
							assignmentService.setBiosample(assignment, participant);
							used.add(participant);
							continue assignmentsLoop;
						}
					}
				}
			}
			System.out.println("done");
		}
	}
	
	private boolean canAssign(StageDto stage, AssignmentDto r, SubGroupDto g) {
		if(r==null || r.getBiosample()==null) return true; //Should not happen, but...
		BiosampleDto biosample = r.getBiosample();
		return canAssign(stage, g,biosample,  false, r);
	}
	
	public boolean canAssign(StageDto stage, AssignmentDto r, BiosampleDto b, boolean displayWarning) {
		if(r==null) return true; //Should not happen, but...
		if (b==null) return true;
		return canAssign(stage, r.getSubgroup(), b, displayWarning, r);
	}

	private boolean canAssign(StageDto stage, SubGroupDto sg, BiosampleDto b, boolean displayWarning, AssignmentDto a) {
		if(sg==null) return true;
		if (b==null) return true;
		
		if(b.getBiotype()==null) {
			if(displayWarning) throw new InvalidParameterException("This sample has no biotype.\nPlease set a biotype first.");
			return false;
		}
		//if the animal is killed at the previous stage return false
		if(biosampleService.getTerminationPhasePlanned(b)!=null && stage.compareTo(biosampleService.getTerminationPhasePlanned(b).getStage())>0) {
			if(displayWarning) throw new InvalidParameterException("This animal should be dead at this stage");
			return false;
		}
		if(biosampleService.isDeadAt(b, assignmentService.getFirstDate(a, assignmentService.getOffsetToFollow(a)))) {
			if(displayWarning) throw new InvalidParameterException("This animal is dead at this stage");
			return false; //Cannot move dead animals to subsequent groups
		}
		if(biosampleService.isRemove(b, stage.getStudy(), getFirstDate(stage))) {
			if(displayWarning) throw new InvalidParameterException("This animal is removed from study");
			return false; //Cannot move dead animals removed from study
		}
		if(stage.getBiotype()!=null && !b.getBiotype().equals(stage.getBiotype())) { 
			if(displayWarning) throw new InvalidParameterException("This biosample should be : "+stage.getBiotype());
			return false; //Cannot assign a biosample with an other biotype
		}
		if("Animal".equals(b.getBiotype().getName())) {
			boolean isSexSubGroup=false;
			for(BiotypeMetadataValueDto metadata : sg.getMetadatas()) {
				if(metadata.getBiotypeMetadata().getName().equals("Sex")) {
					 if(!metadata.getValue().equals(biosampleService.getMetadataValue(b,"Sex"))){
						if(displayWarning) throw new InvalidParameterException("This animal must be "+(metadata.getValue().equals("F")?"female":"male"));
						return false;
					}
					isSexSubGroup=true;
					break;
				}
			}
			if(!isSexSubGroup) {
				for(BiotypeMetadataValueDto metadata : sg.getGroup().getMetadatas()) {
					if(metadata.getBiotypeMetadata().getName().equals("Sex")) {
						 if(!metadata.getValue().equals(biosampleService.getMetadataValue(b,"Sex"))){
							if(displayWarning) throw new InvalidParameterException("This animal must be "+(metadata.getValue().equals("F")?"female":"male"));
							return false;
						}
						break;
					}
				}
			}
			SubGroupDto subGroup = sg;
			if(subGroup.getRandofromgroup()!=null) {
				StageDto prevStage = stage.getPreviousStage();
				while(prevStage!=null) {
					SubGroupDto prevSubGroup = getSubGroup(prevStage, b);
					//previous group equals from group is ok and compare with subgroup if needed
					if (prevSubGroup != null && subGroup.getRandofromgroup().equals(prevSubGroup.getGroup())
							&& (subGroup.getRandofromsubgroup() == null
									|| subGroup.getRandofromsubgroup().equals(getSubGroup(prevStage, b)))) {						
						return true;
					}
					prevStage=prevStage.getPreviousStage();
				}
				if(displayWarning) throw new InvalidParameterException("This animal must come from "+(subGroup.getRandofromsubgroup()!=null?"subgroup "+subGroup.getRandofromsubgroup().getFullName():"group "+subGroup.getRandofromgroup().getName()));
				return false;
			}
		}
		return true;
	}

	public boolean hasSeverityToGroups(StageDto stage) {
		boolean has = false;
		for (GroupDto group : stage.getGroups()) {
			if (group.getSeverity() != null) {
				has = true;
			} else return false;
		}
		return has;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean persistAssignments(StageDto stage, User user) throws Exception {
		List<AssayResultDto> r = new ArrayList();
		boolean animalDB = false;
		if (biotypeService.isAnimal(stage.getBiotype())) {
			HashSet<String> elbs = new HashSet<>();
			for (AssignmentDto assignment : stage.getAssignments()) {
				elbs.add(assignment.getElb());
			}
			elbs.remove(null);
			ArrayList<AnimalUsageRest> usages = new ArrayList<>();
			String username = user.getUsername();
			String studyLicenseNo = studyService.getPropertyValue(stage.getStudy(),"LICENSENO");
			for (String elb : elbs) {
				AnimalUsageRest animalUsageRest = new AnimalUsageRest();
				animalUsageRest.setUserId(username);
				animalUsageRest.setElb(elb);
				animalUsageRest.setStartDate(studyService.getFirstDate(stage.getStudy()));
				animalUsageRest.setDetails(new ArrayList<>());
				for (AssignmentDto assignment : stage.getAssignments()) {
					if (!elb.equals(assignment.getElb())) continue;
					BiosampleDto biosample = assignment.getBiosample();
					String licenseNo = biosampleService.getMetadataValue(biosample, "LicenseNo");
					if (biosample == null || !"ANIMALDB".equals(biosampleService.getMetadataValue(biosample, "DataSource"))) continue;
					AnimalUsageRestDetail animalUsageRestDetail = new AnimalUsageRestDetail();
					animalUsageRestDetail.setAnimalId(Integer.parseInt(biosample.getSampleId()));
					if (licenseNo != null && !licenseNo.isEmpty())
						animalUsageRestDetail.setLicenseNo(licenseNo);
					else
						animalUsageRestDetail.setLicenseNo(studyLicenseNo);
					if (assignment.getSubgroup().getGroup().getSeverity() == null) continue;
					animalUsageRestDetail.setSeverityDegree(assignment.getSubgroup().getGroup().getSeverity());
					animalUsageRest.getDetails().add(animalUsageRestDetail);
				}
				if (animalUsageRest.getDetails().size()>0) usages.add(animalUsageRest);
			}
			if (usages.size()>0) {
				biosampleService.updateAnimalDB(usages, username);
				animalDB = true;
			}
		}

		for(AssignmentDto assignment : stage.getAssignments()) {
			assignmentService.serializeDataList(assignment);
			if(assignmentService.getWeightResult(assignment)!= null) 
				r.add(assignmentService.getWeightResult(assignment));
		}
		
		studyService.save(stage.getStudy());
		return animalDB;
	}

	public void reset(StageDto stage) {
		for (AssignmentDto assignment : stage.getAssignments()) {
			assignmentService.reset(assignment);
		}
	}
	
	public void removeMetadata(StageDto stage, BiotypeMetadataValueDto metaDefinition) {
		for(StageBiotypeMetadataValueDto s : stage.getStageMetadataValues()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata())) {
				stage.getStageMetadataValues().remove(s);
				break;
			}
		}
		for (GroupDto group : stage.getGroups()) {
			for (BiotypeMetadataValueDto bioTypeMetadataValue : group.getMetadatas()) {
				if (bioTypeMetadataValue.getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata()))
					groupService.removeMetadata(group, bioTypeMetadataValue);
			}
		}
	}
	
	public Set<BiosampleDto> getSubjects(StageDto stage) {
		Set<BiosampleDto> result = new HashSet<>();
		for (AssignmentDto assignment : stage.getAssignments()) {
			result.add(assignment.getBiosample());
		}
		result.remove(null);
		return result;
	}

	public void resetCages(StageDto stage, boolean forNewEnclosure) {
		for (AssignmentDto assignment : stage.getAssignments()) {
			assignmentService.resetCage(assignment, forNewEnclosure);
		}
	}

	@SuppressWarnings("deprecation")
	public void randomize(StageDto stage, int factorvalue) throws Exception {
		
		if (stage.getAssignments().size()<1) throw new InvalidParameterException("empty table");
		List<AssignmentDto> assignmentSet = new ArrayList<AssignmentDto>(stage.getAssignments());
		boolean dataAvailable = false;
		dataCheckLooop:
		for (AssignmentDto assignment : assignmentSet) {
			if (assignmentService.getWeight(assignment) != null) {
				dataAvailable = true;
				break dataCheckLooop;
			}
			if (assignment.getDataListList() != null) {
				for (Double aDouble : assignment.getDataListList()) {
					if (aDouble != null && !aDouble.isNaN() && aDouble.intValue() != 0) {
						dataAvailable = true;
						break dataCheckLooop;
					}
				}
			}
		}
		if (!dataAvailable) throw new InvalidParameterException("no data");

		List<Double> weights = assignmentService.getData(assignmentSet, -1);

		final Double wMean = StatUtils.getMean(weights);
		Collections.sort(assignmentSet, new Comparator<AssignmentDto>() {
			@Override
			public int compare(AssignmentDto o1, AssignmentDto o2) {
				Double w1 = assignmentService.getWeight(o1);
				Double w2 = assignmentService.getWeight(o2);
				if(w1==null)
					if(w2==null)
						return 0;
					else
						return -1;
				if(w2==null) 
					return 1;
				double d1 = Math.abs(wMean - w1);
				double d2 = Math.abs(wMean - w2);
				return d1-d2==0?0:d1-d2<0?-1:1;
			}
		});

		List<Pair<SubGroupDto, Duration>> bestGroups = store(stage, assignmentSet);
		double bestScore = score(stage, assignmentSet, factorvalue);
		System.out.println("Start with score: "+bestScore);

		///////////////////////////////////
		//Simulated Annealing to optimize
		for(int step=0; step<30000; step++) {
			double T = 400;
			int i1 = (int) (Math.random()*assignmentSet.size());
			int i2 = (int) (Math.random()*assignmentSet.size());
			if(assignmentSet.get(i1).getSkipRando()) continue;
			if(assignmentSet.get(i2).getSkipRando()) continue;

			SubGroupDto g1 = assignmentSet.get(i1).getSubgroup();
			SubGroupDto g2 = assignmentSet.get(i2).getSubgroup();
			Duration o1 = assignmentSet.get(i1).getStratification();
			Duration o2 = assignmentSet.get(i2).getStratification();
			if(g1==g2) continue;

			//In case of subgroups, make sure that the we mix compatible assignments
			if(!canAssign(stage, assignmentSet.get(i1), g2)) continue;
			if(!canAssign(stage, assignmentSet.get(i2), g1)) continue;

			assignmentService.setSubgroup(assignmentSet.get(i1), g2, false, false);
			assignmentService.setSubgroup(assignmentSet.get(i2), g1, false, false);
			//We don't control date of death including stratifictaion during global randomization, take long time !
			assignmentService.setStratificationWithoutControl(assignmentSet.get(i1), o2);
			assignmentService.setStratificationWithoutControl(assignmentSet.get(i2), o1);

			double score = score(stage, assignmentSet, factorvalue);
			if(score<bestScore) {
				bestScore = score;
				bestGroups = store(stage, assignmentSet);
				System.out.println(step + ". Accept better score: "+score);
			} else {
				double p = Math.exp(-(score-bestScore)/T);
				if(p<Math.random()) {
					restore(assignmentSet, bestGroups);
					System.out.println(step+". restore best "+bestScore + " p = "+Math.exp(-(score-bestScore)/T));
				}
			}
		}
		restore(assignmentSet, bestGroups);
		for(AssignmentDto assignment : stage.getAssignments()) {
			assignmentService.getWeeklyDefinition(assignment);
		}

		System.out.println("Finish with score: "+score(stage, assignmentSet, factorvalue));
		System.out.println("Finished");
		
	}

	public double score(StageDto stage, List<AssignmentDto> list, int factorvalue) {
		return  (1-factorvalue/10.0) * score(stage, list, true) +  (factorvalue/10.0) * score(stage, list, false);
	}

	public void restore(List<AssignmentDto> samples, List<Pair<SubGroupDto, Duration>> memo) {
		if(memo.size()!=samples.size()) throw new IllegalArgumentException("Invalid size in samples or memo");
		for (int i = 0; i < samples.size(); i++) {
			assignmentService.setSubgroup(samples.get(i), memo.get(i).getFirst(), false, false);
			try {
				assignmentService.setStratification(samples.get(i), memo.get(i).getSecond());
			} catch (Exception e) {
				JExceptionDialog.showError(e);
				e.printStackTrace();
			}
		}
		
	}

	public List<Pair<SubGroupDto, Duration>> store(StageDto stage, List<AssignmentDto> samples) {
		List<Pair<SubGroupDto, Duration>> res = new ArrayList<>();
		for (AssignmentDto s : samples) {
			res.add(new Pair<>(s.getSubgroup(), s.getStratification()));
		}
		return res;
	}
	
	public double score(StageDto stage, List<AssignmentDto> list, boolean forWeights) {
		int minIndex = forWeights? -1: 0;
		int maxIndex = forWeights? -1: getNData(stage)-1;

		double res = 0;
		for(int index = minIndex; index<=maxIndex; index++) {
			List<Double> doubles2 = assignmentService.getData(list, index);
			Double mRef = StatUtils.getMean(doubles2);
			Double sRef = StatUtils.getStandardDeviation(doubles2, mRef);

			List<Integer> groupIds = new ArrayList<Integer>();
			for (GroupDto g : stage.getGroups()) groupIds.add((int)g.getId());

			Map<Integer, List<AssignmentDto>> splits = assignmentService.splitByGroup(new HashSet<>(list));
			double tot = 0;
			for(Integer groupId: groupIds) {
				List<AssignmentDto> l = splits.get(groupId);
				if(l==null || l.get(0).getSubgroup().getGroup()==null) continue;
				List<Double> doubles = assignmentService.getData(l, index);
				Double m = StatUtils.getMean(doubles);
				if(m==null) continue;
				tot += ((m-mRef) * (m-mRef)) / (mRef>0? mRef*mRef: 1);

				if(doubles.size()>=1) {
					Double s = StatUtils.getStandardDeviation(doubles, m);
					if(s!=null && sRef!=null && sRef>0 && s>0) tot += (m/s-mRef/sRef) * (m/s-mRef/sRef);
				}
			}
			res += tot / (maxIndex - minIndex + 1);
		}
		return res;
	}

	public int getNSubjects(StageDto stage) {
		return stage.getAssignments()==null ? 0 : stage.getAssignments().size();
	}

	public GroupDto getGroupByName(StageDto stage, String name) {
		for (GroupDto g : stage.getGroups()) {
			if (name.equals(g.getName())) return g;
		}
		return null;
	}

	public Set<BiotypeMetadataValueDto> getMetadatas(StageDto stage) {
		Set<BiotypeMetadataValueDto> results = new HashSet<>();
		for(StageBiotypeMetadataValueDto bmv : stage.getStageMetadataValues()) {
			results.add(bmv.getBiotypeMetadataValue());
		}
		return results;
	}

	public void setMetadatas(StageDto stage, Set<BiotypeMetadataValueDto> metadatas) {
		for(BiotypeMetadataValueDto bmv : metadatas) {
			addMetadata(stage, bmv);
		}
	}	
	
	@SuppressWarnings("deprecation")
	public Duration getOffsetOfD0(StageDto stage) {
		if(stage.isDynamic()) {
			if(stage.getOffsetOfD0() == null) {
				return Duration.ZERO;
			}
			return stage.getOffsetOfD0();
		}else {
			if(stage.getOffsetFromPreviousStage() == null || stage.getPreviousStage() == null) {
				return Duration.ZERO;
			}
			return stage.getOffsetFromPreviousStage().plus(phaseService.getDisplayPhase(getLastPhase(stage.getPreviousStage()))).negated();
		}
	}
}
