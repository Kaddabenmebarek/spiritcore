package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.SubGroupDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.SubGroupPatternDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.model.Assignment;
import com.idorsia.research.spirit.core.model.SubGroup;
import com.idorsia.research.spirit.core.model.SubGroupBiotypeMetadataValue;
import com.idorsia.research.spirit.core.model.SubGroupPattern;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.util.Frequency;

@Service
public class SubGroupService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -6868930090422478585L;
	@Autowired
	private SubGroupDao subGroupDao;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private StageService stageService;
	@Autowired
	BiosampleService biosampleService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private SubGroupPatternService subGroupPatternService;
	@Autowired
	private SubGroupBiotypeMetadataValueService subGroupBiotypeMetadataValueService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private StudyActionResultService studyActionResultService;

	@SuppressWarnings("unchecked")
	private static Map<Integer, SubGroupDto> idToSubGroup = (Map<Integer, SubGroupDto>) getCacheMap(SubGroupDto.class);

	public SubGroup get(Integer id) {
		return subGroupDao.get(id);
	}

	public List<SubGroup> list() {
		return subGroupDao.list();
	}

	public int getCount() {
		return subGroupDao.getCount();
	}

	public Integer saveOrUpdate(SubGroup subGroup) {
		return subGroupDao.saveOrUpdate(subGroup);
	}

	public int addSubGroup(SubGroup subGroup) {
		return subGroupDao.addSubGroup(subGroup);
	}
	
	public List<SubGroup> getByRandoFromGroup(Integer groupId) {
		return subGroupDao.getByRandoFromGroup(groupId);
	}
	
	public List<SubGroup> getByRandoFromSubGroup(Integer subGroupId) {
		return subGroupDao.getByRandoFromSubGroup(subGroupId);
	}

	public List<SubGroup> getByGroup(int groupId) {
		return subGroupDao.getByGroup(groupId);
	}

	public List<SubGroup> getByStudy(Integer studyId) {
		return subGroupDao.getByStudy(studyId);
	}

	public SubGroupDao getSubGroupDao() {
		return subGroupDao;
	}

	public void setSubGroupDao(SubGroupDao subGroupDao) {
		this.subGroupDao = subGroupDao;
	}

	public Set<ActionPatternsDto> getWeeklyDefinition(SubGroupDto subgroup) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for (ActionPatternsDto ap : subgroup.getActionDefinitionPattern()) {
			if (ap.getSchedule().getRecurrence() != null
					&& ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		results.addAll(groupService.getWeeklyDefinition(subgroup.getGroup()));
		return results;
	}

	public PhaseDto getEndPhase(SubGroupDto subgroup) {
		ActionPatternsDto terminationPattern = getTerminationPattern(subgroup);
		if (terminationPattern != null)
			return scheduleService.getPhases(terminationPattern.getSchedule()).get(0);
		return groupService.getEndPhase(subgroup.getGroup());
	}

	public ActionPatternsDto getTerminationPattern(SubGroupDto subgroup) {
		for (ActionPatternsDto actionPattern : subgroup.getActionDefinitionPattern()) {
			if (actionPattern.getAction() instanceof Disposal)
				return actionPattern;
		}
		return null;
	}

	public SubGroupDto map(SubGroup subGroup) {
		SubGroupDto subgroupdto = idToSubGroup.get(subGroup.getId());
		if (subgroupdto == null) {
			subgroupdto = dozerMapper.map(subGroup, SubGroupDto.class, "subGroupCustomMapping");
			if (idToSubGroup.get(subGroup.getId()) == null)
				idToSubGroup.put(subGroup.getId(), subgroupdto);
			else
				subgroupdto = idToSubGroup.get(subGroup.getId());
		}
		return subgroupdto;
	}

	@Transactional
	public void save(SubGroupDto subGroup) throws Exception {
		save(subGroup, false);
	}

	@SuppressWarnings("deprecation")
	protected void save(SubGroupDto subGroup, Boolean cross) throws Exception {
		try {
			if (subGroup != null && !savedItems.contains(subGroup)) {
				savedItems.add(subGroup);
				if(subGroup.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(subGroup);
				if (subGroup.getGroup().getId() == Constants.NEWTRANSIENTID)
					groupService.save(subGroup.getGroup(), true);
				if (subGroup.getRandofromgroup() != null
						&& subGroup.getRandofromgroup().getId() == Constants.NEWTRANSIENTID)
					groupService.save(subGroup.getRandofromgroup(), true);
				if (subGroup.getRandofromsubgroup() != null
						&& subGroup.getRandofromsubgroup().getId() == Constants.NEWTRANSIENTID)
					save(subGroup.getRandofromsubgroup(), true);
				subGroup.setUpdDate(new Date());
				subGroup.setUpdUser(UserUtil.getUsername());
				if(subGroup.getId().equals(Constants.NEWTRANSIENTID)) {
					subGroup.setCreDate(new Date());
					subGroup.setCreUser(UserUtil.getUsername());
				}
				subGroup.setId(saveOrUpdate(dozerMapper.map(subGroup, SubGroup.class, "subGroupCustomMapping")));
				idToSubGroup.put(subGroup.getId(), subGroup);
				if(subGroup.getActionDefinitionNoMapping()!=null)
					for (SubGroupPatternDto sp : subGroup.getActionDefinition()) {
						subGroupPatternService.save(sp, true);
				}
				if(subGroup.getAssignmentsNoMapping()!=null)
					for (AssignmentDto assignment : subGroup.getAssignments()) {
						assignmentService.save(assignment, true);
				}
				if(subGroup.getSubGroupMetadatasNoMapping()!=null)
					for (SubGroupBiotypeMetadataValueDto metadata : subGroup.getSubGroupMetadatas())
						subGroupBiotypeMetadataValueService.save(metadata, true);
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
	private void deleteChildren(SubGroupDto subGroup) {
		if(subGroup.getActionDefinitionNoMapping()!=null) {
			for(SubGroupPattern sgp : subGroupPatternService.getBySubgroup(subGroup.getId())) {
				Boolean found = false;
				for(SubGroupPatternDto child : subGroup.getActionDefinition()){
					if(sgp.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					subGroupPatternService.delete(subGroupPatternService.map(sgp), true);
				}
			}
		}
		if(subGroup.getAssignmentsNoMapping()!=null) {
			for(Assignment a : assignmentService.getAssignmentsBySubgroup(subGroup.getId())) {
				Boolean found = false;
				for(AssignmentDto child : subGroup.getAssignments()){
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
		if(subGroup.getSubGroupMetadatasNoMapping()!=null) {
			for(SubGroupBiotypeMetadataValue bmv : subGroupBiotypeMetadataValueService.getBySubGroup(subGroup.getId())) {
				Boolean found = false;
				for(SubGroupBiotypeMetadataValueDto child : subGroup.getSubGroupMetadatas()){
					if(bmv.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					subGroupBiotypeMetadataValueService.delete(subGroupBiotypeMetadataValueService.map(bmv), true);
				}
			}
		}
	}

	@Transactional
	public void delete(SubGroupDto subGroup) throws Exception {
		delete(subGroup, false);
	}
	
	protected void delete(SubGroupDto subGroup, Boolean cross) throws Exception {
		for (SubGroupDto sg : map(getByRandoFromSubGroup(subGroup.getId()))) {
			sg.setRandofromsubgroup(null);
		}
		subGroupDao.delete(subGroup.getId());
	}

	public List<SubGroupDto> map(List<SubGroup> subGroups) {
		List<SubGroupDto> result = new ArrayList<>();
		for (SubGroup subGroup : subGroups) {
			result.add(map(subGroup));
		}
		return result;
	}

	public SubGroupDto getSubGroupDto(Integer id) {
		return map(get(id));
	}

	public Set<ActionPatternsDto> getActionDefinitionWithoutWeekly(SubGroupDto subgroup) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for (ActionPatternsDto ap : subgroup.getActionDefinitionPattern()) {
			if (ap.getSchedule().getRecurrence() == null
					|| !ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		return results;
	}

	public boolean removeActionDefinition(SubGroupDto subGroup, ActionPatternsDto actionDefinition) {
		 try {
	            StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
	            studyActionResultQuery.setSubjectSet(subGroup);
	            studyActionResultQuery.setAction(actionDefinition.getAction());
	            studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION_DEFINITION, studyActionResultQuery);
	        } catch (Exception e) {
	            return false;
	        }
		 if (actionDefinition.getSchedule().getRecurrence() != null
				&& Frequency.WEEKLY.equals(actionDefinition.getSchedule().getRecurrence().getFrequency())) {
			for (AssignmentDto assignment : subGroup.getAssignments()) {
				assignmentService.removeActionDefinitionByParent(assignment, actionDefinition);
			}
		}
		SubGroupPatternDto stagePattern = null;
		for (SubGroupPatternDto pattern : subGroup.getActionDefinition()) {
			if (pattern.getActionpattern().equals(actionDefinition)) {
				stagePattern = pattern;
				break;
			}
		}
		if (stagePattern != null) {
			subGroup.getActionDefinition().remove(stagePattern);
		}
		return true;
	}

	public Boolean addActionDefinition(SubGroupDto subgroup, ActionPatternsDto actionDefinition) {
		if (StudyActionType.DISPOSAL.equals(actionDefinition.getAction().getType()))
			try {
				StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
				studyActionResultQuery.setSubjectSet(subgroup);
				studyActionResultQuery.setAction(actionDefinition.getAction());
				studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION_DEFINITION,
						studyActionResultQuery);
			} catch (Exception e) {
				return false;
			}
		if (actionDefinition != null && actionDefinition.getSchedule().getRecurrence() != null
				&& actionDefinition.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
			for (AssignmentDto a : stageService.getAssignments(subgroup.getGroup().getStage(), subgroup)) {
				assignmentService.addActionDefinitionWeekly(a, actionDefinition);
			}
		}
		subgroup.getActionDefinition().add(new SubGroupPatternDto(subgroup, actionDefinition));
		subgroup.getActionDefinitionPattern().add(actionDefinition);
		for (AssignmentDto a : stageService.getAssignments(subgroup.getGroup().getStage(), subgroup)) {
			assignmentService.calculateOffset(a, actionDefinition);
		}
		return true;
	}

	public void addActionDefinition(SubGroupDto subGroup, Set<ActionPatternsDto> actionPatterns) {
		for (ActionPatternsDto ap : actionPatterns) {
			addActionDefinition(subGroup, ap);
		}
	}

	public void removeMetadata(SubGroupDto subGroup, BiotypeMetadataValueDto bioTypeMetadataValue) {
		for(SubGroupBiotypeMetadataValueDto s : subGroup.getSubGroupMetadatas()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(bioTypeMetadataValue.getBiotypeMetadata())) {
				subGroup.getSubGroupMetadatas().remove(s);
				break;
			}
		}
	}

	public void addMetadata(SubGroupDto subGroup, BiotypeMetadataValueDto bioTypeMetadataValue) {
		boolean found=false;
		for(SubGroupBiotypeMetadataValueDto s : subGroup.getSubGroupMetadatas()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(bioTypeMetadataValue.getBiotypeMetadata())) {
				s.getBiotypeMetadataValue().setValue(bioTypeMetadataValue.getValue());
				found=true;
				break;
			}
		}
		if(!found) {
			SubGroupBiotypeMetadataValueDto metadata = new SubGroupBiotypeMetadataValueDto(subGroup, bioTypeMetadataValue);
			subGroup.getSubGroupMetadatas().add(metadata);
		}
	}
	
	public Set<BiotypeMetadataValueDto> getMetadatas(SubGroupDto subGroup) {
		Set<BiotypeMetadataValueDto> results = new HashSet<>();
		for(SubGroupBiotypeMetadataValueDto bmv : subGroup.getSubGroupMetadatas()) {
			results.add(bmv.getBiotypeMetadataValue());
		}
		return results;
	}

	public void setMetadatas(SubGroupDto subGroup, Set<BiotypeMetadataValueDto> metadatas) {
		for(BiotypeMetadataValueDto bmv : metadatas) {
			addMetadata(subGroup, bmv);
		}
	}

	public void setGroup(SubGroupDto subgroup, GroupDto group) {
		setGroup(subgroup, group, false);
	}

	@SuppressWarnings("deprecation")
	public void setGroup(SubGroupDto subgroup, GroupDto group, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(subgroup.getGroup(), group))
			return;
		// remove from the old owner
		if (subgroup.getGroup() != null && !(cross && group == null))
			groupService.removeSubGroup(subgroup.getGroup(), subgroup, true);
		// set new owner
		GroupDto previousGroup = subgroup.getGroup();
		if (group == null) {
			for (ActionPatternsDto definitionActionPattern : new HashSet<>(subgroup.getActionDefinitionPattern())) {
				removeActionDefinition(subgroup, definitionActionPattern);
			}
			for (BiotypeMetadataValueDto bioTypeMetadataValue : new HashSet<>(subgroup.getMetadatas())) {
				removeMetadata(subgroup, bioTypeMetadataValue);
			}
			Set<AssignmentDto> assignments = new HashSet<>(
					stageService.getAssignments(previousGroup.getStage(), subgroup));
			for (AssignmentDto assignment : assignments) {
				stageService.removeAssignment(previousGroup.getStage(), assignment);
			}
		}
		subgroup.setGroup(group);
		// set myself to new owner
		if (group != null && !cross)
			groupService.addSubGroup(group, subgroup, true);
	}

	public List<ActionPatternsDto> getStudyActionTreatments(SubGroupDto subGroup) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for (AssignmentDto a : subGroup.getAssignments()) {
			for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(a)) {
				if (!result.contains(actionPattern) && StudyActionType.THERAPY.equals(actionPattern.getActionType()))
					result.add(actionPattern);
			}
		}
		return result;
	}

	public List<ActionPatternsDto> getStudyActionTreatmentsAndDiseases(SubGroupDto subGroup) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for (AssignmentDto a : subGroup.getAssignments()) {
			for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(a)) {
				if (!result.contains(actionPattern) && (StudyActionType.THERAPY.equals(actionPattern.getActionType())
						|| StudyActionType.DISEASE.equals(actionPattern.getActionType())))
					result.add(actionPattern);
			}
		}
		return result;
	}

	public List<ActionPatternsDto> getStudyActionDiseases(SubGroupDto subGroup) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for (AssignmentDto a : subGroup.getAssignments()) {
			for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(a)) {
				if (!result.contains(actionPattern) && StudyActionType.DISEASE.equals(actionPattern.getActionType()))
					result.add(actionPattern);
			}
		}
		return result;
	}

	public List<ActionPatternsDto> getStudyActionMeasurements(SubGroupDto subGroup) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for (AssignmentDto a : subGroup.getAssignments()) {
			for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(a)) {
				if (!result.contains(actionPattern)
						&& StudyActionType.MEASUREMENT.equals(actionPattern.getActionType()))
					result.add(actionPattern);
			}
		}
		return result;
	}

	public List<ActionPatternsDto> getStudyActionSamplings(SubGroupDto subGroup) {
		List<ActionPatternsDto> result = new ArrayList<>();
		for (AssignmentDto a : subGroup.getAssignments()) {
			for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(a)) {
				if (!result.contains(actionPattern) && StudyActionType.SAMPLING.equals(actionPattern.getActionType()))
					result.add(actionPattern);
			}
		}
		return result;
	}

	public Set<IndividualAction> getAllActions(SubGroupDto sg, PhaseDto phase) {
		return getAllActions(sg, phase, false);
	}

	public Set<IndividualAction> getAllActions(SubGroupDto sg, PhaseDto phase, Boolean toDisplay) {
		HashSet<IndividualAction> individualActions = new HashSet<>();
		if (phase == null)
			return individualActions;
		if (toDisplay) {
			AssignmentDto assignment = null;
			for (AssignmentDto a : sg.getAssignments()) {
				if (assignment == null)
					assignment = a;
				else if (a.getStratification().compareTo(assignment.getStratification()) < 0)
					assignment = a;
			}
			assignmentService.getFullActionDefinition(assignment).forEach(actionPatternAbstract -> {
				List<AssignmentDto> assignmentList = stageService.getAssignmentsList(sg.getGroup().getStage(), sg);
				for (SchedulePhaseDto schedulePhaseLink : actionPatternAbstract.getSchedule().getSchedulePhases()) {
					if (phase.equals(schedulePhaseLink.getPhase()) && assignmentList.size() > 0
							&& (getEndPhase(sg) == null || phase.compareTo(getEndPhase(sg)) <= 0))
						individualActions.add(new IndividualAction(schedulePhaseLink, assignmentList.get(0)));
				}
			});
		} else {
			for (AssignmentDto assignment : stageService.getAssignments(sg.getGroup().getStage(), sg)) {
				assignmentService.getFullActionDefinition(assignment).forEach(actionPatternAbstract -> {
					List<AssignmentDto> assignmentList = stageService.getAssignmentsList(sg.getGroup().getStage(), sg);
					for (SchedulePhaseDto schedulePhaseLink : actionPatternAbstract.getSchedule().getSchedulePhases()) {
						if (phase.equals(schedulePhaseLink.getPhase()) && assignmentList.size() > 0
								&& (getEndPhase(sg) == null || phase.compareTo(getEndPhase(sg)) <= 0))
							individualActions.add(new IndividualAction(schedulePhaseLink, assignmentList.get(0)));
					}
				});
			}
		}
		return individualActions;
	}

	public Set<BiosampleDto> getParticipants(SubGroupDto sg) {
		Set<BiosampleDto> participants = new HashSet<>();
		for (AssignmentDto assignment : sg.getGroup().getStage().getAssignments()) {
			if (sg.equals(assignment.getSubgroup()))
				participants.add(assignment.getBiosample());
		}
		return participants;
	}

	public String getBlindedName(SubGroupDto subGroup, String user) {
		StudyDto study = subGroup.getGroup().getStage().getStudy();
		if (study == null || user == null) {
			return subGroup.getFullName() == null ? "" : subGroup.getFullName();
		} else if (studyService.getBlindAllUsers(study).contains(user)) {
			return "Blinded";
		} else if (studyService.getBlindDetailsUsers(study).contains(user)) {
			return "Gr. " + getShortName(subGroup);
		} else {
			return subGroup.getFullName() == null ? "" : subGroup.getFullName();
		}
	}

	private String getShortName(SubGroupDto subGroup) {
		String name = subGroup.getFullName();
		if (name == null)
			return "";
		int index = name.indexOf(" ");
		if (index <= 0)
			index = name.length();
		if (index > 4)
			index = 4;
		return name.substring(0, index).trim();
	}

	public NamedSamplingDto getTerminationSampling(SubGroupDto subGroup) {
		ActionPatternsDto terminationPattern = getTerminationPattern(subGroup);
		if (terminationPattern != null)
			return ((Disposal) terminationPattern.getAction()).getSampling();
		return null;
	}

	public void setTerminationSampling(SubGroupDto subGroup, NamedSamplingDto terminationSampling) {
		ActionPatternsDto terminationPattern = getTerminationPattern(subGroup);
		if (terminationPattern != null) {
			((Disposal) terminationPattern.getAction()).setSampling(terminationSampling);
			for (ActionPatternsDto ap : subGroup.getActionDefinitionPattern()) {
				if (ap.getAction() instanceof Disposal) {
					ap.setActionId(terminationSampling == null ? 0 : terminationSampling.getId());
				}
			}
		}
		subGroup.getGroup().getStage().getStudy().setSynchroSamples(Boolean.TRUE);
	}

	public void removeTermination(SubGroupDto subGroup) {
		stageService.removeActionPattern(subGroup.getGroup().getStage(), getTerminationPattern(subGroup));
		for (BiosampleDto b : getParticipants(subGroup)) {
			if (b != null && biosampleService.getTerminationExecutionDate(b) == null) {
				b.setEndPhase(null);
			}
		}
	}

	public Boolean setEndPhase(SubGroupDto subGroup, Duration endPhase) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(subGroup);
			studyActionResultQuery.setLastphase(endPhase);
			studyActionResultQuery.setStage(subGroup.getGroup().getStage());
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.END_PHASE, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		if (endPhase != null) {
			for (GroupDto group : studyService.getGroups(subGroup.getGroup().getStage().getStudy())) {
				for (SubGroupDto sg : group.getSubgroups())
					if (subGroup.equals(sg.getRandofromsubgroup())) {
						sg.setRandofromgroup(null);
						sg.setRandofromsubgroup(null);
					}
			}
			for (BiosampleDto b : getParticipants(subGroup)) {
				if (b != null) {
					b.setEndPhase(stageService.getPhase(subGroup.getGroup().getStage(), endPhase));
					AssignmentDto deathAssignment = biosampleService.getAssignment(b, subGroup.getGroup().getStage());
					for (AssignmentDto a : biosampleService.getAssignments(b,
							subGroup.getGroup().getStage().getStudy())) {
						if (a.getStage().compareTo(deathAssignment.getStage()) > 0) {
							assignmentService.resetCage(a);
							biosampleService.removeAssignment(b, a);
						}
					}
				}
			}
		}
		ActionPatternsDto terminationPattern = getTerminationPattern(subGroup);
		if (terminationPattern == null) {
			ActionPatternsDto actionPattern = actionPatternsService.newActionPatternsDto(subGroup.getGroup().getStage(),
					scheduleService.newScheduleDto(endPhase), new Disposal());
			subGroup.getActionDefinition().add(new SubGroupPatternDto(subGroup, actionPattern));
			subGroup.getActionDefinitionPattern().add(actionPattern);
		} else {
			scheduleService.setPhase(terminationPattern.getSchedule(), endPhase);
		}
		return true;

	}

	public Integer getNanimalsAssigned(SubGroupDto subgroup) {
		int i = 0;
		for (AssignmentDto assignment : stageService.getAssignments(subgroup.getGroup().getStage(), subgroup)) {
			if (assignment.getBiosample() != null)
				i++;
		}
		return i;
	}

	public Integer getNanimals(SubGroupDto subgroup) {
		return stageService.getAssignments(subgroup.getGroup().getStage(), subgroup).size();
	}

	public HashSet<StudyAction> getStudyActions(SubGroupDto subgroup, PhaseDto phase) {
		HashSet<StudyAction> actionPatterns = new HashSet<>();
		if (phase == null)
			return actionPatterns;
		for (AssignmentDto a : subgroup.getAssignments()) {
			assignmentService.getFullActionDefinition(a).forEach(actionPatternAbstract -> {
				if (scheduleService.getPhases(actionPatternAbstract.getSchedule()).contains(phase))
					actionPatterns.add(actionPatternAbstract.getAction());
			});
		}
		if (stageService.getFirstPhase(subgroup.getGroup().getStage()).equals(phase))
			actionPatterns.add(subgroup.getGroup().getStage());
		if (phase.equals(getEndPhase(subgroup)))
			actionPatterns.add(getDisposal(subgroup));
		return actionPatterns;
	}

	public StudyAction getDisposal(SubGroupDto subgroup) {
		if (getTerminationPattern(subgroup) != null)
			return (Disposal) getTerminationPattern(subgroup).getAction();
		if (groupService.getTerminationPattern(subgroup.getGroup()) != null)
			return (Disposal) groupService.getTerminationPattern(subgroup.getGroup()).getAction();
		return null;
	}

	public SubGroupDto newSubGroupDto(GroupDto group, int nanimals) {
		return newSubGroupDto(group, nanimals, new HashSet<>(), new HashSet<>());
	}

	public SubGroupDto newSubGroupDto(GroupDto group, int nanimals, Set<ActionPatternsDto> actionPatterns,
			Set<BiotypeMetadataValueDto> definitionMeta) {
		SubGroupDto subGroup = new SubGroupDto();
		setGroup(subGroup, group);
		if (actionPatterns != null) {
			for (ActionPatternsDto a : actionPatterns) {
				addActionDefinition(subGroup, a);
			}
		}
		subGroup.getMetadatas().addAll(definitionMeta);
		setNAnimals(subGroup, nanimals);
		return subGroup;
	}

	public void setNAnimals(SubGroupDto subGroup, int nAnimals) {
		stageService.setNSubjects(subGroup.getGroup().getStage(), nAnimals, subGroup);
	}

	public Set<ActionPatternsDto> getActionDefinitionWithWeekly(SubGroupDto subGroup) {
		Set<ActionPatternsDto> result = new HashSet<>(subGroup.getActionDefinitionPattern());
		Set<ActionPatternsDto> parentResult = stageService
				.getActionDefinitionWithWeekly(subGroup.getGroup().getStage());
		parentResult.addAll(groupService.getActionDefinitionWithWeekly(subGroup.getGroup()));
		outerloop: for (ActionPatternsDto actionPattern : subGroup.getGroup().getStage().getActionPatterns()) {
			if (actionPattern.getSchedule().getRecurrence() != null
					&& Frequency.WEEKLY.equals(actionPattern.getSchedule().getRecurrence().getFrequency())
					&& actionPattern.getParent() == null && !parentResult.contains(actionPattern)) {
				for (AssignmentDto a : stageService.getAssignments(subGroup.getGroup().getStage(), subGroup)) {
					if (assignmentService.getActionPatternByParent(a, actionPattern) == null) {
						continue outerloop;
					}
				}
				result.add(actionPattern);
			}
		}
		return result;
	}

	public void mapAssignments(SubGroupDto subGroup) {
		List<AssignmentDto> assignments = assignmentService
				.map(assignmentService.getAssignmentsBySubgroup(subGroup.getId()));
		if (assignments != null) {
			Collections.sort(assignments);
			subGroup.setAssignments(new HashSet<AssignmentDto>(assignments));
		}
	}

	public void mapActionDefinition(SubGroupDto subGroup) {
		subGroup.setActionDefinition(
				subGroupPatternService.map(subGroupPatternService.getBySubgroup(subGroup.getId())));
	}

	public void mapMetadatas(SubGroupDto subGroup) {
		subGroup.setSubGroupMetadatas(new HashSet<>(
				subGroupBiotypeMetadataValueService.map(subGroupBiotypeMetadataValueService.getBySubGroup(subGroup.getId()))));
	}

	public Set<BiosampleDto> getSubjects(SubGroupDto subGroup) {
		Set<BiosampleDto> result = new HashSet<>();
		for (AssignmentDto assignment : stageService.getAssignments(subGroup.getGroup().getStage(), subGroup)) {
			result.add(assignment.getBiosample());
		}
		result.remove(null);
		return result;
	}
	
	public void addAssignment(SubGroupDto subGroup, AssignmentDto assignment) {
		addAssignment(subGroup, assignment, false);
	}

	public void addAssignment(SubGroupDto subGroup, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (subGroup.getAssignments().contains(assignment))
			return;
		// add new member
		subGroup.getAssignments().add(assignment);
		// update child if request is not from it
		if (!cross) {
			assignmentService.setSubgroup(assignment, subGroup, true);
		}
		
	}

	public void removeAssignment(SubGroupDto subGroup, AssignmentDto assignment) {
		removeAssignment(subGroup, assignment, false);
	}

	public void removeAssignment(SubGroupDto subGroup, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (!subGroup.getAssignments().contains(assignment))
			return;
		// remove old member
		subGroup.getAssignments().remove(assignment);
		// remove child's owner
		if (!cross) {
			assignmentService.setSubgroup(assignment, null, true);
		}

	}
}