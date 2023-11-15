package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.awt.Color;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.Counter;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.GroupDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.GroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.GroupPatternDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.model.Group;
import com.idorsia.research.spirit.core.model.GroupBiotypeMetadataValue;
import com.idorsia.research.spirit.core.model.GroupPattern;
import com.idorsia.research.spirit.core.model.SubGroup;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.util.Frequency;

@Service
public class GroupService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 5823076479911372307L;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private StageService stageService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private SubGroupService subGroupService;
	@Autowired
	private StudyActionService studyActionService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private GroupPatternService groupPatternService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private StudyActionResultService studyActionResultService;
	@Autowired
	private GroupBiotypeMetadataValueService groupBiotypeMetadataValueService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, GroupDto> idToGroup = (Map<Integer, GroupDto>) getCacheMap(GroupDto.class);

	public Group get(Integer id) {
		return groupDao.get(id);
	}

	public List<Group> list() {
		return groupDao.list();
	}

	public List<Group> getByStage(int stageId) {
		return groupDao.getByStage(stageId);
	}

	public List<Group> getByStudy(Integer studyId) {
		return groupDao.getByStudy(studyId);
	}

	public int getCount() {
		return groupDao.getCount();
	}

	public Integer saveOrUpdate(Group group) {
		return groupDao.saveOrUpdate(group);
	}

	public int addGroup(Group group) {
		return groupDao.addGroup(group);
	}

	public GroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public PhaseDto getEndPhase(GroupDto group) {
		ActionPatternsDto terminationPattern = getTerminationPattern(group);
		if (terminationPattern != null) return scheduleService.getPhases(terminationPattern.getSchedule()).get(0);
		return null;
	}

	public ActionPatternsDto getTerminationPattern(GroupDto group) {
		for (ActionPatternsDto actionPattern : group.getActionDefinitionPattern()) {
			if (actionPattern.getAction() instanceof Disposal) 
				return actionPattern;
		}
		return null;
	}
	
	public Set<ActionPatternsDto> getWeeklyDefinition(GroupDto group) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for(ActionPatternsDto ap : group.getActionDefinitionPattern()) {
			if(ap.getSchedule().getRecurrence() != null && ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		results.addAll(stageService.getWeeklyDefinition(group.getStage()));
		return results;
	}

	public GroupDto getGroupDto(Integer id) {
		return map(get(id));
	}

	public List<GroupDto> map(List<Group> groups) {
		List<GroupDto> res = new ArrayList<GroupDto>();
		for(Group group : groups) {
			res.add(map(group));
		}
		return res;
	}
	
	public GroupDto map(Group group) {
		GroupDto groupDto = idToGroup.get(group.getId());
		if(groupDto==null) {
			groupDto = dozerMapper.map(group, GroupDto.class,"groupCustomMapping");
			if(idToGroup.get(group.getId())==null)
				idToGroup.put(group.getId(), groupDto);
			else
				groupDto = idToGroup.get(group.getId());
		}
		return groupDto;
	}
	
	@Transactional
	public void save(GroupDto group) throws Exception {
		save(group, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(GroupDto group, Boolean cross) throws Exception {
		try {
			if(group!=null && !savedItems.contains(group)) {
				if(group.getStage().getId().equals(Constants.NEWTRANSIENTID))
					stageService.save(group.getStage(), true);
				//Need to be added after stage is saved because of interaction between stage.getNextStage and subgroup.getFromGroup
				savedItems.add(group);
				if(group.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(group);
				group.setUpdDate(new Date());
				group.setUpdUser(UserUtil.getUsername());
				if(group.getId().equals(Constants.NEWTRANSIENTID)) {
					group.setCreDate(new Date());
					group.setCreUser(UserUtil.getUsername());
				}
				group.setId(saveOrUpdate(dozerMapper.map(group, Group.class, "groupCustomMapping")));
				idToGroup.put(group.getId(), group);
				if(group.getActionDefinitionNoMapping()!=null)
					for(GroupPatternDto actionPatterns : group.getActionDefinition())
						groupPatternService.save(actionPatterns, true);
				if(group.getSubgroupsNoMapping()!=null)
					for(SubGroupDto subGroup : group.getSubgroups())
					subGroupService.save(subGroup, true);
				if(group.getGroupMetadatasNoMapping()!=null)
					for(GroupBiotypeMetadataValueDto metadata : group.getGroupMetadatas())
						groupBiotypeMetadataValueService.save(metadata, true);
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
	private void deleteChildren(GroupDto group) throws Exception {
		if(group.getActionDefinitionNoMapping()!=null) {
			for(GroupPattern gp : groupPatternService.getByGroup(group.getId())) {
				Boolean found = false;
				for(GroupPatternDto child : group.getActionDefinition()){
					if(gp.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					groupPatternService.delete(groupPatternService.map(gp), true);
				}
			}
		}
		if(group.getSubgroupsNoMapping()!=null) {
			for(SubGroup sg : subGroupService.getByGroup(group.getId())) {
				Boolean found = false;
				for(SubGroupDto child : group.getSubgroups()){
					if(sg.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					subGroupService.delete(subGroupService.map(sg), true);
				}
			}
		}
		if(group.getGroupMetadatasNoMapping()!=null) {
			for(GroupBiotypeMetadataValue bmv : groupBiotypeMetadataValueService.getByGroup(group.getId())) {
				Boolean found = false;
				for(GroupBiotypeMetadataValueDto child : group.getGroupMetadatas()){
					if(bmv.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					groupBiotypeMetadataValueService.delete(groupBiotypeMetadataValueService.map(bmv), true);
				}
			}
		}
	}

	@Transactional
	public void delete(GroupDto group) throws Exception {
		delete(group, false);
	}
	
	protected void delete(GroupDto group, Boolean cross) throws Exception {
		for(SubGroupDto sg : subGroupService.map(subGroupService.getByRandoFromGroup(group.getId()))) {
			sg.setRandofromgroup(null);
		}
		groupDao.delete(group.getId());
	}

	public Set<ActionPatternsDto> getActionDefinitionWithoutWeekly(GroupDto group) {
		Set<ActionPatternsDto> results = new HashSet<ActionPatternsDto>();
		for(ActionPatternsDto ap : group.getActionDefinitionPattern()) {
			if (ap.getSchedule().getRecurrence() == null
					|| !ap.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
				results.add(ap);
			}
		}
		return results;
	}

	public boolean removeActionDefinition(GroupDto group, ActionPatternsDto actionDefinition) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(group);
			studyActionResultQuery.setAction(actionDefinition.getAction());
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION_DEFINITION, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		GroupPatternDto groupPattern = null;
    	for(GroupPatternDto pattern : group.getActionDefinition()) {
        	if(pattern.getActionpattern().equals(actionDefinition)) {
        		groupPattern=pattern;
        		break;
        	}
        }
    	if(groupPattern!=null) {
    		group.getActionDefinition().remove(groupPattern);
    	}
		for (SubGroupDto subGroup : group.getSubgroups()) {
			subGroupService.removeActionDefinition(subGroup, actionDefinition);
		}
		if (actionDefinition.getSchedule().getRecurrence() != null
				&& Frequency.WEEKLY.equals(actionDefinition.getSchedule().getRecurrence().getFrequency())) {
    		for(AssignmentDto assignment : getAssignments(group)) {
    			assignmentService.removeActionDefinitionByParent(assignment, actionDefinition);
    		}
    	}
		return true;
	}
	
	public void setStage(GroupDto group, StageDto stage) {
		setStage(group, stage, false);
	}

	@SuppressWarnings("deprecation")
	public void setStage(GroupDto group, StageDto stage, boolean cross) {
		//prevent endless loop
		if (nullSupportEqual(group.getStage(), stage))
			return;
		//remove from the old owner
		if (group.getStage() != null && !(cross && stage == null))
			stageService.removeGroup(group.getStage(), group, true);
		//set new owner
		group.setStage(stage);
		//set myself to new owner
		if (stage != null && !cross)
			stageService.addGroup(stage, group, true);
		if (stage == null) {
			for (ActionPatternsDto definitionActionPattern : new HashSet<>(group.getActionDefinitionPattern())) {
				removeActionDefinition(group, definitionActionPattern);
			}
			for (GroupBiotypeMetadataValueDto bioTypeMetadataValue : new HashSet<>(group.getGroupMetadatas())) {
				removeMetadata(group, bioTypeMetadataValue.getBiotypeMetadataValue());
			}
		}
	}
	
	public void addMetadata(GroupDto group, BiotypeMetadataValueDto metaDefinition) {
		boolean found=false;
		for(GroupBiotypeMetadataValueDto s : group.getGroupMetadatas()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata())) {
				s.getBiotypeMetadataValue().setValue(metaDefinition.getValue());
				found=true;
				break;
			}
		}
		if(!found) {
			GroupBiotypeMetadataValueDto metadata = new GroupBiotypeMetadataValueDto(group, metaDefinition);
			group.getGroupMetadatas().add(metadata);
		}
		for (SubGroupDto subGroup : group.getSubgroups()) {
			for (SubGroupBiotypeMetadataValueDto bioTypeMetadataValue : subGroup.getSubGroupMetadatas()) {
				if (bioTypeMetadataValue.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata()))
					subGroupService.removeMetadata(subGroup, bioTypeMetadataValue.getBiotypeMetadataValue());
			}
		}
	}

	public void removeMetadata(GroupDto group, BiotypeMetadataValueDto metaDefinition) {
		for(GroupBiotypeMetadataValueDto s : group.getGroupMetadatas()) {
			if(s.getBiotypeMetadataValue().getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata())) {
				group.getGroupMetadatas().remove(s);
				break;
			}
		}
		for (SubGroupDto subGroup : group.getSubgroups()) {
			Set<BiotypeMetadataValueDto> metadatas = new HashSet<BiotypeMetadataValueDto>(subGroup.getMetadatas());
			for (BiotypeMetadataValueDto bioTypeMetadataValue : metadatas) {
				if (bioTypeMetadataValue.getBiotypeMetadata().equals(metaDefinition.getBiotypeMetadata()))
					subGroupService.removeMetadata(subGroup, bioTypeMetadataValue);
			}
		}
	}
	
	public Set<BiotypeMetadataValueDto> getMetadatas(GroupDto group) {
		Set<BiotypeMetadataValueDto> results = new HashSet<>();
		for(GroupBiotypeMetadataValueDto bmv : group.getGroupMetadatas()) {
			results.add(bmv.getBiotypeMetadataValue());
		}
		return results;
	}

	public void setMetadatas(GroupDto group, Set<BiotypeMetadataValueDto> metadatas) {
		for(BiotypeMetadataValueDto bmv : metadatas) {
			addMetadata(group, bmv);
		}
	}
	
	public void addSubGroup(GroupDto group, SubGroupDto subGroup) {
		addSubGroup(group, subGroup, false);
	}

	public void addSubGroup(GroupDto group, SubGroupDto subGroup, boolean cross) {
		//prevent endless loop
		if (group.getSubgroups().contains(subGroup))
			return;
		subGroup.setNo(renumberSubGroups(group));
		//add new member
		group.getSubgroups().add(subGroup);
		//update child if request is not from it
		if (!cross) {
			subGroupService.setGroup(subGroup, group, true);
		}
	}

	public void removeSubGroup(GroupDto group, SubGroupDto subGroup) {
		removeSubGroup(group, subGroup, false);
	}

	public void removeSubGroup(GroupDto group, SubGroupDto subGroup, boolean cross) {
		//prevent endless loop
		if (!group.getSubgroups().contains(subGroup))
			return;
		//remove old member
		group.getSubgroups().remove(subGroup);
		//remove child's owner
		if (!cross) {
			subGroupService.setGroup(subGroup, null, true);
		}
		renumberSubGroups(group);
		
		if(group.getSubgroups().size()==1) {
			getSubgroup(group, 0).setName(null);
		}
	}

	private int renumberSubGroups(GroupDto group) {
		List<SubGroupDto> subGroupsSorted = group.getSubgroups();
		Collections.sort(subGroupsSorted);
		for (int i = 0; i < subGroupsSorted.size(); i++) {
			subGroupsSorted.get(i).setNo(i);
		}
		return subGroupsSorted.size();
	}

	public SubGroupDto getSubgroup(GroupDto group, int i) {
		for (SubGroupDto subGroup : group.getSubgroups()) {
			if (subGroup.getNo() == i) return subGroup;
		}
		return null;
	}

	private List<AssignmentDto> getAssignments(GroupDto group) {
		return stageService.getAssignments(group);
	}

	public String getShortName(GroupDto g) {
		String name = g.getName();
		if (name == null) return "";
		int index = name.indexOf(" ");
		if (index <= 0) index = name.length();
		if (index > 4) index = 4;
		return name.substring(0, index).trim();
	}
	
	public String getNameWithoutShortName(GroupDto group) {
		if (group.getName() == null) return "";
		String s = getShortName(group);
		return group.getName().substring(s.length()).trim();
	}

	public String getBlindedName(GroupDto group, String user) {
		StudyDto study = group.getStage().getStudy();
		if (study == null || user == null) {
			return group.getName() == null ? "" : group.getName();
		} else if (studyService.getBlindAllUsers(study).contains(user)) {
			return "Blinded";
		} else if (studyService.getBlindDetailsUsers(study).contains(user)) {
			return "Gr. " + getShortName(group);
		} else {
			return group.getName() == null ? "" : group.getName();
		}
	}

	public Color getBlindedColor(GroupDto group, String user) {
		StudyDto study = group.getStage().getStudy();
		if (study == null || user == null) {
			return group.getColor();
		} else if (studyService.getBlindAllUsers(study).contains(user)) {
			return Color.LIGHT_GRAY;
		} else if (studyService.getBlindDetailsUsers(study).contains(user)) {
			return group.getColor();
		} else {
			return group.getColor();
		}

	}

	public String extractGroup123(String s) {
		if (s == null) return null;
		if (s.indexOf(' ') >= 1) s = s.substring(0, s.indexOf(' '));

		int index = 0;
		for (; index < s.length() && !Character.isDigit(s.charAt(index)); index++) {
		}
		int index2 = index;
		for (; index2 < s.length() && Character.isDigit(s.charAt(index2)); index2++) {
		}
		return s.substring(index, index2);
	}

	public String extractGroupABC(String s) {
		if (s == null) return null;
		if (s.indexOf(' ') >= 1) s = s.substring(0, s.indexOf(' '));
		int index = 0;
		for (; index < s.length() && !Character.isLetter(s.charAt(index)); index++) {
		}

		int index2 = index;
		for (; index2 < s.length() && Character.isLetter(s.charAt(index2)); index2++) {
		}
		return s.substring(index, index2);
	}

	public ArrayList<ActionPatternsDto> getStudyActionTreatments(GroupDto group) {
		ArrayList<ActionPatternsDto> result = new ArrayList<>();
		for (SubGroupDto subGroup : group.getSubgroups()) {
			result.addAll(subGroupService.getStudyActionTreatments(subGroup));
		}
		return result;
	}

	public ArrayList<ActionPatternsDto> getStudyActionMeasurements(GroupDto group) {
		ArrayList<ActionPatternsDto> result = new ArrayList<>();
		for (SubGroupDto subGroup : group.getSubgroups()) {
			result.addAll(subGroupService.getStudyActionMeasurements(subGroup));
		}
		return result;
	}

	public ArrayList<ActionPatternsDto> getStudyActionSamplings(GroupDto group) {
		ArrayList<ActionPatternsDto> result = new ArrayList<>();
		for (SubGroupDto subGroup : group.getSubgroups()) {
			result.addAll(subGroupService.getStudyActionSamplings(subGroup));
		}
		return result;
	}
	
	public String getTreatmentDescription(GroupDto group) {
		return getTreatmentDescription(group, null, false);
	}
	
	public String getTreatmentAndDiseaseDescription(GroupDto group) {
		return getTreatmentAndDiseaseDescription(group, null, false);
	}

	public String getTreatmentDescription(GroupDto group, SubGroupDto subgroup) {
		return getTreatmentDescription(group, subgroup, false);
	}

	public Set<NamedTreatmentDto> getAllTreatments(GroupDto group, SubGroupDto subgroup) {
		Set<NamedTreatmentDto> res = new LinkedHashSet<>();
		for (SubGroupDto subGroupAct : group.getSubgroups()) {
			if (subgroup != null && !subgroup.equals(subGroupAct)) continue;
			for (ActionPatternsDto a : subGroupService.getStudyActionTreatments(subGroupAct)) {
				res.add((NamedTreatmentDto) a.getAction());
			}
		}
		return res;
	}
	
	public Set<NamedTreatmentDto> getAllTreatmentsAndDiseases(GroupDto group, SubGroupDto subgroup) {
		Set<NamedTreatmentDto> res = new LinkedHashSet<>();
		for (SubGroupDto subGroupAct : group.getSubgroups()) {
			if (subgroup != null && !subgroup.equals(subGroupAct)) continue;
			for (ActionPatternsDto a : subGroupService.getStudyActionTreatmentsAndDiseases(subGroupAct)) {
				res.add((NamedTreatmentDto) a.getAction());
			}
		}
		return res;
	}

	/**
	 * Get the description of the treatment for the given subgroup (if >=0)
	 *
	 * @return
	 */
	public String getTreatmentDescription(GroupDto group, SubGroupDto subgroup, boolean fullTreatments) {
		List<NamedTreatmentDto> namedTreatments = new ArrayList<>(getAllTreatments(group, subgroup));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < namedTreatments.size(); i++) {
			if (!fullTreatments && namedTreatments.size() > 3 && i >= 2) {
				sb.append(" +...");
				break;
			}
			if (sb.length() > 0) {
				sb.append(" +");
			}

			sb.append(studyActionService.getDescription(namedTreatments.get(i)));
		}
		return sb.toString();
	}
	
	public String getTreatmentAndDiseaseDescription(GroupDto group, SubGroupDto subgroup, boolean fullTreatments) {
		List<NamedTreatmentDto> namedTreatments = new ArrayList<>(getAllTreatmentsAndDiseases(group, subgroup));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < namedTreatments.size(); i++) {
			if (!fullTreatments && namedTreatments.size() > 3 && i >= 2) {
				sb.append(" +...");
				break;
			}
			String treatmentDescription = studyActionService.getDescription(namedTreatments.get(i));
			if(treatmentDescription!=null) {
				if (sb.length() > 0) {
					sb.append(" +");
				}
				sb.append(treatmentDescription);
			}
		}
		return sb.toString();
	}

	public PhaseDto getFirstTreatmentPhase(GroupDto group) {
		List<ActionPatternsDto> studyActionTreatments = getStudyActionTreatments(group);
		if (studyActionTreatments == null || studyActionTreatments.size() < 1) return null;
		PhaseDto firstTreatmentPhase = null;
		for (ActionPatternsDto studyActionTreatment : studyActionTreatments) {
			if (firstTreatmentPhase == null || firstTreatmentPhase.compareTo(scheduleService.getFirstPhase(studyActionTreatment.getSchedule())) > 0) {
				firstTreatmentPhase = scheduleService.getFirstPhase(studyActionTreatment.getSchedule());
			}
		}
		return firstTreatmentPhase;
	}

	public Set<BiosampleDto> getParticipants(GroupDto group) {
		Set<BiosampleDto> participants = new HashSet<>();
		for (AssignmentDto assignment : group.getStage().getAssignments()) {
			if (group.equals(assignment.getSubgroup().getGroup())) participants.add(assignment.getBiosample());
		}
		participants.remove(null);
		return participants;
	}
	
	public List<BiosampleDto> getParticipantsSorted(GroupDto group) {
		List<BiosampleDto> participantsSorted = new ArrayList<>(getParticipants(group));
		StageDto stage = group.getStage();
		StudyDto study = stage.getStudy();
		Collections.sort(participantsSorted, new Comparator<BiosampleDto>() {
			@Override
			public int compare(BiosampleDto o1, BiosampleDto o2) {
				if(o1==null && o2 ==null)
					return 0;
				if(o1==null)
					return 1;
				if(o2==null)
					return -1;
				int c=biosampleService.getSubGroup(o1, stage).compareTo(biosampleService.getSubGroup(o2, stage));
				if(c!=0)
					return c;
				return CompareUtils.compareAlphaNumeric(biosampleService.getLastAssignment(o1, study).getName(),biosampleService.getLastAssignment(o2, study).getName());
			}
		});
		return participantsSorted;
	}

	public SubGroupDto getSubGroup(GroupDto g, BiosampleDto biosample) {
		if(biosample==null) return null;
		for(SubGroupDto sg : g.getSubgroups()) {
			for(BiosampleDto b : subGroupService.getParticipants(sg)) {
				if(biosample.equals(b))
					return sg;
			}
		}
		return null;
	}

	public String getDescription(GroupDto group, SubGroupDto subgroup) {
		String[] desc = null;
		if (subgroup == null) {
			for (SubGroupDto subGroup : group.getSubgroups()) {
				if (desc == null) 
					desc = getDescriptionLines(group, subGroup);
				else {
					String[] desc2 = getDescriptionLines(group, subGroup);
					for (int j = 0; j < desc2.length; j++) {
						if (!desc[j].equals(desc2[j])) desc[j] = "";
					}
				}
			}
		} else {
			desc = getDescriptionLines(group, subgroup);
		}

		//concatenate the description
		StringBuilder sb = new StringBuilder();
		for (String s : desc) {
			if (s != null && s.length() > 0) sb.append(s + "\n");
		}
		return sb.toString();
	}
	
	public String[] getDescriptionLines(GroupDto group, SubGroupDto subgroup) {
		if (group.getStage().getStudy() == null) return new String[]{"", "", ""};
		Counter<NamedTreatmentDto> treatmentCounter = new Counter<>();
		Counter<NamedSamplingDto> samplingCounter = new Counter<>();
		Counter<Measurement> measurementCounter = new Counter<>();
		int nWeighings = 0;
		int nFoods = 0;
		int nWaters = 0;
		int nMeasures = 0;
		StringBuilder sMeasurements = new StringBuilder();
		StringBuilder sTreatments = new StringBuilder();
		StringBuilder sSamplings = new StringBuilder();
		StringBuilder sDiseases = new StringBuilder();
		for (ActionPatternsDto a : subGroupService.getStudyActionMeasurements(subgroup)) {
			Measurement measurement = (Measurement) a.getAction();
			long toAdd = a.getSchedule().getSchedulePhases()
					.stream()
					.filter(l->subGroupService.getEndPhase(subgroup)==null || l.getPhase().compareTo(getEndPhase(group))<=0)
					.count();
			if (studyActionService.isMeasureWeight(measurement)) {
				nWeighings += toAdd;
			}else if (studyActionService.isMeasureFood(measurement)) {
				nFoods += toAdd;
			}else if (studyActionService.isMeasureWater(measurement)) {
				nWaters += toAdd;
			}else {
				measurementCounter.increaseCounter(measurement, (int)toAdd);
				nMeasures += toAdd;
			}
		}
		subGroupService.getStudyActionSamplings(subgroup).forEach(a -> samplingCounter.increaseCounter((NamedSamplingDto) a.getAction()));
		subGroupService.getStudyActionTreatments(subgroup).forEach(a -> treatmentCounter.increaseCounter((NamedTreatmentDto) a.getAction()));
		subGroupService.getStudyActionDiseases(subgroup).forEach(a -> treatmentCounter.increaseCounter((NamedTreatmentDto) a.getAction()));


		if (nWeighings >= 1) sMeasurements.append((sMeasurements.length() > 0 ? ", " : "") + "+ " + nWeighings + " weigh." + " ");
		if (nFoods >= 1) sMeasurements.append((sMeasurements.length() > 0 ? ", " : "") + "+ " + nFoods + " Food" + " ");
		if (nWaters >= 1) sMeasurements.append((sMeasurements.length() > 0 ? ", " : "") + "+ " + +nWaters + " Water" + " ");

		for (Measurement t : measurementCounter.getKeys()) {
			if (measurementCounter.getCount(t) >= 1 && t!=null)
				sMeasurements.append("+ " + (nMeasures >= 1 ? measurementCounter.getCount(t) + "x " : "") + studyActionService.getDescription(t) + " ");
		}
		for (NamedTreatmentDto t : treatmentCounter.getKeySorted()) {
			if(!t.getDisease()) {
				int nTreatments = 0;
				for(ActionPatternsDto a : subGroupService.getStudyActionTreatments(subgroup)) {
					if(a.getAction() != null && t!=null && a.getAction().getName().equals(t.getName())) {
						nTreatments += a.getSchedule().getSchedulePhases()
								.stream()
								.filter(l->subGroupService.getEndPhase(subgroup)==null || l.getPhase().compareTo(getEndPhase(group))<=0)
								.count();
					}
				}
				if (treatmentCounter.getCount(t) >= 1 && t!=null) {
					sTreatments.append("+ " + (nTreatments >= 1 ? nTreatments + "x " : "") + t.getName() + " ");
				}
			}
		}
		for (NamedTreatmentDto t : treatmentCounter.getKeySorted()) {
			if(t.getDisease()) {
				int nDiseases = 0;
				for(ActionPatternsDto a : subGroupService.getStudyActionDiseases(subgroup)) {
					if(a.getAction() != null && t!=null && a.getAction().getName().equals(t.getName())) {
						nDiseases += a.getSchedule().getSchedulePhases()
								.stream()
								.filter(l->subGroupService.getEndPhase(subgroup)==null || l.getPhase().compareTo(getEndPhase(group))<=0)
								.count();
					}
				}
				if (treatmentCounter.getCount(t) >= 1 && t!=null) {
					sDiseases.append("+ " + (nDiseases >= 1 ? nDiseases + "x " : "") + t.getName() + " ");
				}
			}
		}
		for (NamedSamplingDto s : samplingCounter.getKeySorted()) {
			int nSamplings = 0;
			for(ActionPatternsDto a : subGroupService.getStudyActionSamplings(subgroup)) {
				if(a.getAction() != null && s!=null && a.getAction().getName().equals(s.getName())) {
					nSamplings += a.getSchedule().getSchedulePhases()
							.stream()
							.filter(l->subGroupService.getEndPhase(subgroup)==null || l.getPhase().compareTo(getEndPhase(group))<=0)
							.count();
				}
			}
			if (samplingCounter.getCount(s) >= 1 && s!=null) {
				sSamplings.append("+ " + (nSamplings >= 1 ? nSamplings + "x " : "") + s.getName() + " ");
			}
		}
		return new String[]{sMeasurements.toString(), sTreatments.toString(), sSamplings.toString(), sDiseases.toString()};
	}

	public NamedSamplingDto getTerminationSampling(GroupDto group) {
		ActionPatternsDto terminationPattern = getTerminationPattern(group);
		if (terminationPattern != null) return ((Disposal)terminationPattern.getAction()).getSampling();
		return null;
	}

	public void removeTermination(GroupDto group) {
		stageService.removeActionPattern(group.getStage(), getTerminationPattern(group));
		for(BiosampleDto b : getParticipants(group)) {
			if(b!=null) {
				b.setEndPhase(null);
			}
		}
	}

	public boolean setEndPhase(GroupDto group, Duration endPhase) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(group);
			studyActionResultQuery.setLastphase(endPhase);
			studyActionResultQuery.setStage(group.getStage());
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.END_PHASE, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		if (endPhase != null) {
			for (GroupDto g : studyService.getGroups(group.getStage().getStudy())) {
				for(SubGroupDto subGroup : g.getSubgroups())
					if (group.equals(subGroup.getRandofromgroup())) {
						subGroup.setRandofromgroup(null);
						subGroup.setRandofromsubgroup(null);
					}
			}
			for(SubGroupDto sg : group.getSubgroups()) {
				subGroupService.removeTermination(sg);
			}
		}
		for(BiosampleDto b : getParticipants(group)) {
			if(b!=null) {
				b.setEndPhase(stageService.getPhase(group.getStage(), endPhase));
				AssignmentDto deathAssignment = biosampleService.getAssignment(b, group.getStage());
				for(AssignmentDto a : biosampleService.getAssignments(b, group.getStage().getStudy())) {
					if(a.getStage().compareTo(deathAssignment.getStage())>0) {
						assignmentService.resetCage(a);
						biosampleService.removeAssignment(b, a);
					}
				}
			}
		}
		ActionPatternsDto terminationPattern = getTerminationPattern(group);
		if (terminationPattern == null) {
			ActionPatternsDto actionPattern = actionPatternsService.newActionPatternsDto(group.getStage(), scheduleService.newScheduleDto(endPhase), new Disposal());
			addActionDefinition(group, actionPattern);
		} else {
			scheduleService.setPhase(terminationPattern.getSchedule(), endPhase);
		}
		return true;
	}

	public boolean addActionDefinition(GroupDto group, ActionPatternsDto actionDefinition) {
		for (SubGroupDto subGroup : group.getSubgroups()) {
			subGroupService.removeActionDefinition(subGroup, actionDefinition);
		}
		if (StudyActionType.DISPOSAL.equals(actionDefinition.getAction().getType()))
			try {
				StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
				studyActionResultQuery.setSubjectSet(group);
				studyActionResultQuery.setAction(actionDefinition.getAction());
				studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION_DEFINITION, studyActionResultQuery);
			} catch (Exception e) {
				return false;
			}
		if(actionDefinition != null && actionDefinition.getSchedule().getRecurrence() != null
				&& actionDefinition.getSchedule().getRecurrence().getFrequency().equals(Frequency.WEEKLY)) {
			for(AssignmentDto a : stageService.getAssignments(group)) {
				assignmentService.addActionDefinitionWeekly(a, actionDefinition);
			}
		}
		group.getActionDefinition().add(new GroupPatternDto(group, actionDefinition));
		group.getActionDefinitionPattern().add(actionDefinition);
		for(AssignmentDto a : stageService.getAssignments(group)) {
        	assignmentService.calculateOffset(a, actionDefinition);
        }
		return true;
	}

	public void setTerminationSampling(GroupDto group, NamedSamplingDto terminationSampling) {
		ActionPatternsDto terminationPattern = getTerminationPattern(group);
		if (terminationPattern == null) return;
		((Disposal)terminationPattern.getAction()).setSampling(terminationSampling);
		for(ActionPatternsDto ap : group.getActionDefinitionPattern()) {
    		if(ap.getAction() instanceof Disposal) {
    			ap.setActionId(terminationSampling==null?0:terminationSampling.getId());
    		}
    	}
		group.getStage().getStudy().setSynchroSamples(Boolean.TRUE);
	}

	public GroupDto getFromGroup(GroupDto group) {
		GroupDto fromGroup=group.getSubgroups().get(0).getRandofromgroup();
		for(SubGroupDto subGroup : group.getSubgroups()) {
			if(fromGroup!=subGroup.getRandofromgroup()) {
				 return null;
			}
		}
		return fromGroup;
	}

	public GroupDto newGroupDto(StageDto stage) {
		return newGroupDto(stage, new HashSet<>(), new HashSet<>());
	}

	public GroupDto newGroupDto(StageDto stage, Set<ActionPatternsDto> actionPatterns, Set<BiotypeMetadataValueDto> definitionMeta) {
		GroupDto group = new GroupDto();
		setStage(group, stage);
		createName(group);
		if(actionPatterns!=null) {
	        for(ActionPatternsDto a : actionPatterns) {
	        	addActionDefinition(group, a);
	        }
        }
		group.getMetadatas().addAll(definitionMeta);
		group.setColor(group.getName() == null ? Color.BLACK : new Color(0x999999 - group.getName().hashCode()));
		addSubGroup(group, subGroupService.newSubGroupDto(group, 1));
		return group;
	}
	
	private void createName(GroupDto group) {
		String name = "";
		for (ActionPatternsDto actionPattern : group.getActionDefinitionPattern()) {
			name += actionPattern + " ";
		}
		for (BiotypeMetadataValueDto bioTypeMetadataValue : group.getMetadatas()) {
			name += bioTypeMetadataValue + " ";
		}
		group.setName(name);
	}

	public Set<ActionPatternsDto> getActionDefinitionWithWeekly(GroupDto group) {
		Set<ActionPatternsDto> stageResult = stageService.getActionDefinitionWithWeekly(group.getStage());
		Set<ActionPatternsDto> result = new HashSet<>(group.getActionDefinitionPattern());
		outerloop:
		for(ActionPatternsDto actionPattern : group.getStage().getActionPatterns()) {
			if (actionPattern.getSchedule().getRecurrence() != null
					&& Frequency.WEEKLY.equals(actionPattern.getSchedule().getRecurrence().getFrequency())
					&& actionPattern.getParent() == null && !stageResult.contains(actionPattern)) {
	    		for(AssignmentDto a : stageService.getAssignments(group)) {
					if(assignmentService.getActionPatternByParent(a, actionPattern)==null) {
						continue outerloop;
	    			}
	    		}
	    		result.add(actionPattern);
	    	}
		}
		return result;
	}

	public Color generateColor(GroupDto group) {
		String name = getShortName(group);
		if (name == null || name.length() == 0) return Color.WHITE;
		String shortGroup = extractGroup123(name);
		shortGroup = name;
		float h = 0;
		for (int i = shortGroup.length() - 1; i >= 0; i--) {
			int v = 1 + shortGroup.charAt(i);
			h = (h + v / 8f) % 1;
		}
		float s = .25f;
		float b = .85f;
		Color col = Color.getHSBColor(h, s, b);
		return col;
	}

	public GroupDto duplicateGroup(GroupDto group, StageDto stage) {
		IdentityHashMap<ActionPatternsDto, ActionPatternsDto> actionPatternMap = new IdentityHashMap<>();
		Set<ActionPatternsDto> actionPatterns = new HashSet<ActionPatternsDto>(stage.getActionPatterns());
		for (ActionPatternsDto actionPattern : actionPatterns) {
			if(actionPattern.getAction() instanceof Disposal) {
				actionPatternMap.put(actionPattern, stageService.duplicate(stage, actionPattern));
			}else {
				actionPatternMap.put(actionPattern, actionPattern);
			}
		}
		return duplicateGroup(group, stage, actionPatternMap, false);
	}

	public GroupDto duplicateGroup(GroupDto group, StageDto stage, IdentityHashMap<ActionPatternsDto, ActionPatternsDto> actionPatternClones, boolean duplicateStudy) {
		GroupDto cloneGroup = new GroupDto();
		setStage(cloneGroup, stage);
		cloneGroup.setName((!stage.equals(group.getStage()) ? "" : "Copy of ") + group.getName());
		cloneGroup.setColor(group.getColor());
		cloneGroup.setSeverity(group.getSeverity());
		
		for (ActionPatternsDto actionPattern : group.getActionDefinitionPattern()) {
			cloneGroup.addActionDefinition(actionPatternClones.get(actionPattern));
		}
		for (BiotypeMetadataValueDto bioTypeMetadataValue : group.getMetadatas()) {
			cloneGroup.addMetadata(bioTypeMetadataValue);
		}

		for (SubGroupDto subGroup : group.getSubgroups()) {
			int nbParticipants = subGroup.getAssignments().size();
			SubGroupDto cloneSubGroup = subGroupService.newSubGroupDto(cloneGroup, nbParticipants);
			cloneSubGroup.setName(subGroup.getFullName());
			GroupDto fromG = subGroup.getRandofromgroup();
			if(fromG != null) cloneSubGroup.setRandofromgroup(fromG);
			SubGroupDto fromSG = subGroup.getRandofromsubgroup();
			if(fromSG != null) cloneSubGroup.setRandofromsubgroup(fromSG); 
			for (ActionPatternsDto actionPattern : subGroup.getActionDefinitionPattern()) {
				cloneSubGroup.addActionDefinition(actionPatternClones.get(actionPattern));
			}
			Iterator<AssignmentDto> iterator = cloneSubGroup.getAssignments().iterator();
			for(AssignmentDto a : subGroup.getAssignments()) {
				AssignmentDto aclone = iterator.next();
				for (ActionPatternsDto actionPattern : a.getActionDefinitionPattern()) {
					assignmentService.addActionDefinition(aclone, actionPatternClones.get(actionPattern));
				}
			}

			for (BiotypeMetadataValueDto bioTypeMetadataValue : subGroup.getMetadatas()) {
				cloneSubGroup.addMetadata(bioTypeMetadataValue);
			}
		}
		return cloneGroup;
	}

	public void gmoveUp(GroupDto group, SubGroupDto sg) {
		int no = sg.getNo();
		no--;
		if (no < 0) return;
		getSubGroup(group, no).setNo(no + 1);
		sg.setNo(no);
	}
	
	public SubGroupDto getSubGroup(GroupDto group, int i) {
		for (SubGroupDto subGroup : group.getSubgroups()) {
			if (subGroup.getNo() == i) 
				return subGroup;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void mapSubgroups(GroupDto group) {
		List<SubGroupDto> sgs = subGroupService.map(subGroupService.getByGroup(group.getId()));
		Collections.sort(sgs);
		group.setSubgroups(sgs);
	}

	public void mapActionDefinition(GroupDto group) {
		group.setActionDefinition(groupPatternService.map(groupPatternService.getByGroup(group.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapMetadatas(GroupDto group) {
		group.setGroupMetadatas(new HashSet<>(groupBiotypeMetadataValueService.map(groupBiotypeMetadataValueService.getByGroup(group.getId()))));
	}
	
	public Set<BiosampleDto> getSubjects(GroupDto group) {
		Set<BiosampleDto> result = new HashSet<>();
		for (SubGroupDto subGroup : group.getSubgroups()) {
			for (AssignmentDto assignment : stageService.getAssignments(group.getStage(), subGroup)) {
				result.add(assignment.getBiosample());
			}
		}
		result.remove(null);
		return result;
	}
	
	public int getNAnimals(GroupDto group, PhaseDto phase) {
		int nanimals = 0;
		for (AssignmentDto assignment : group.getStage().getAssignments()) {
			if (assignment.getSubgroup() == null || assignment.getBiosample() == null) continue;
			if (group.equals(assignment.getSubgroup().getGroup()) && !biosampleService.isDeadAt(assignment.getBiosample(),phase) 
					&& !biosampleService.isRemove(assignment.getBiosample(), group.getStage().getStudy(), phase))
				nanimals++;
		}
		return nanimals;
	}

	public int getNAnimals(GroupDto group) {
		int nanimals = 0;
		for (SubGroupDto subGroup : group.getSubgroups()) {
			nanimals += subGroupService.getNanimals(subGroup);
		}
		return nanimals;
	}
}
