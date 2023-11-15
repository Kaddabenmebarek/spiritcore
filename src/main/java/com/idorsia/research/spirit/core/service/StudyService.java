package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.business.Department;
import com.actelion.research.business.spi.formulation.SpiFormulation;
import com.actelion.research.hts.datacenter.restapi.ppg.DAOPpgTreatment;
import com.actelion.research.hts.datacenter.restapi.spi.DAOSpiFormulation;
import com.actelion.research.security.entity.User;
import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.Pair;
import com.idorsia.research.niobe.NiobeService;
import com.idorsia.research.spirit.core.constants.Comparators;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.DocumentType;
import com.idorsia.research.spirit.core.constants.RightLevel;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.StudyDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.DocumentDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.PropertyLinkDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDocumentDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.ElbLink;
import com.idorsia.research.spirit.core.dto.view.Event;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.Participant;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.dto.view.StudyQuery;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.model.Enclosure;
import com.idorsia.research.spirit.core.model.NamedSampling;
import com.idorsia.research.spirit.core.model.NamedTreatment;
import com.idorsia.research.spirit.core.model.PropertyLink;
import com.idorsia.research.spirit.core.model.Stage;
import com.idorsia.research.spirit.core.model.Study;
import com.idorsia.research.spirit.core.model.StudyDocument;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MappingThreadUtils;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.SpiritRights;
import com.idorsia.research.spirit.core.util.Triple;
import com.idorsia.research.spirit.core.util.UserUtil;

import biweekly.component.VEvent;

@Service
public class StudyService extends AbstractService implements Serializable{

	private static final long serialVersionUID = -6906003902946939542L;
	@Autowired
	private StudyDao studyDao;
	@Autowired
	private StageService stageService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private AssayService assayService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private BiotypeService biotypeService;
	@Autowired
	private NamedTreatmentService namedTreatmentService;
	@Autowired
	private NamedSamplingService namedSamplingService;
	@Autowired
	private EnclosureService enclosureService;
	@Autowired
	private AssayResultValueService assayResultValueService;
	@Autowired
	private PropertyLinkService propertyLinkService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StudyActionService studyActionService;
	@Autowired
	private StudyDocumentService studyDocumentService;
	@Autowired
	private SpiritRights spiritRights;
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private StudyActionResultService studyActionResultService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, StudyDto> idToStudy = (Map<Integer, StudyDto>) getCacheMap(StudyDto.class);
	@SuppressWarnings("unchecked")
	private static Map<Integer, Study> loadedStudy = (Map<Integer, Study>) getCacheMap(Study.class);
	
	public int addStudy(Study study) {
		return studyDao.addStudy(study);
	}
	
	public Integer saveOrUpdate(Study study) {
		return studyDao.saveOrUpdate(study);
	}
	
	public Study get(Integer id) {
		return studyDao.get(id);
	}

	public Study getStudyById(int id) {
		return studyDao.getStudyById(id);
	}

	public Study getStudyByStudyId(String studyId) {
		return studyDao.getStudyByStudyId(studyId);
	}
	
	public String getNextStudyId() {
		List<Study> studies = list();
		Collections.sort(studies, new Comparator<Study>() {
			public int compare(Study s1, Study s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		String lastStudyId = studies.get(0).getStudyId();
		int newValue = Integer.valueOf(lastStudyId.substring(3, lastStudyId.length())) + 1 ;
		return Constants.STUDYID_PREFIX + String.valueOf(newValue);
	}

	@Transactional
	public void changeOwner(Study study, String updUser, String creUser) {
		studyDao.changeOwner(study, updUser, creUser);
	}

	public Collection<StudyDto> getRecentStudiesDto(User user, RightLevel level) {
		List<StudyDto> res = map(studyDao.getRecentStudies(user));
		List<StudyDto> studies = new ArrayList<>();
		if(user!=null) {
			for (StudyDto study : res) {
				if(level==RightLevel.ADMIN) {
					if(spiritRights.canEdit(study, user)) studies.add(study);
				} else if(level==RightLevel.BLIND) {
					if(spiritRights.isBlind(study, user) || spiritRights.canWork(study, user)) studies.add(study);
				} else if(level==RightLevel.WRITE) {
					if(spiritRights.canWork(study, user)) studies.add(study);
				} else if(level==RightLevel.READ) {
					if(spiritRights.canRead(study, user)) studies.add(study);
				}
			}
		} else {
			studies.addAll(studies);
		}
		Collections.sort(studies);
		return studies;
	}

	public Collection<Study> getRecentStudies(User user, RightLevel level) {
		List<Study> res = studyDao.getRecentStudies(user);
		List<Study> studies = new ArrayList<>();
		/*if(user!=null) {
			for (Study study : res) {
				StudyDto studyDto = map(study);
				if(level==RightLevel.ADMIN) {
					if(spiritRights.canEdit(studyDto, user)) studies.add(study);
				} else if(level==RightLevel.BLIND) {
					if(spiritRights.isBlind(studyDto, user) || spiritRights.canWork(studyDto, user)) studies.add(study);
				} else if(level==RightLevel.WRITE) {
					if(spiritRights.canWork(studyDto, user)) studies.add(study);
				} else if(level==RightLevel.READ) {
					if(spiritRights.canRead(studyDto, user)) studies.add(study);
				}
			}
		} else {*/
			studies.addAll(res);
		//}
		Collections.sort(studies);
		return studies;
	}

	public List<Study> list() {
		return studyDao.list();
	}

	public int getCount() {
		return studyDao.getCount();
	}
	
	public List<Measurement> getAllMeasurementsFromActions(StudyDto study) {
		List<Measurement> res = new ArrayList<>();
		for (StageDto st :  study.getStages()) {
        	for(AssignmentDto a : st.getAssignments()) {
        		for(ActionPatternsDto sam : assignmentService.getFullActionDefinition(a))
	               	if (sam.getActionType().equals(StudyActionType.MEASUREMENT)) {
	                		res.add((Measurement)sam.getAction());
	                }
            }
		}
		return res;
	}
	
	public Collection<Measurement> getAllMeasurementsFromSamplings(StudyDto study) {
		Set<Measurement> res = new TreeSet<>();
		for (NamedSamplingDto a : new ArrayList<>(study.getNamedSamplings())) {
			for(SamplingDto s: new ArrayList<>(a.getSamplings())) {
				res.addAll(samplingService.getMeasurements(s));
			}
		}
		return res;
	}

	public List<String> getStudyIds() {
		return studyDao.getStudyIds();
	}
	
	public Map<StudyDto, Map<BiotypeDto, Triple<Integer, String, Date>>> countSamplesByStudyBiotype(
			List<StudyDto> studies) {
		return countSamplesByStudyBiotype(studies, null);
	}

	/**
	 * Return a map of study->(biotype->n.Samples)
	 * @param studies
	 * @return
	 */
	private Map<StudyDto, Map<BiotypeDto, Triple<Integer, String, Date>>> countSamplesByStudyBiotype(Collection<StudyDto> studies, Date minDate) {
		Map<StudyDto, Map<BiotypeDto, Triple<Integer, String, Date>>> res = new HashMap<>();
		Map<String, Map<String, Triple<Integer, String, Date>>> map = biosampleService.countSampleByStudyBioype(studies, minDate);
		//Convert map to the underlying type
		Map<String, StudyDto> mapStudy = mapStudyId(studies);
		for (String n1 : map.keySet()) {
			Map<BiotypeDto, Triple<Integer, String, Date>> m2 = new TreeMap<>();
			for (String n2 : map.get(n1).keySet()) {
				m2.put(biotypeService.map(biotypeService.getByName(n2)), map.get(n1).get(n2));
			}
			res.put(mapStudy.get(n1), m2);
		}
		return res;
	}
	
	public Map<BiotypeDto, Triple<Integer, String, Date>> countRecentSamplesByBiotype(Date minDate) {
		Map<StudyDto, Map<BiotypeDto, Triple<Integer, String, Date>>> res = countSamplesByStudyBiotype(null, minDate);
		if(res.size()>0) 
			return res.values().iterator().next();
		return null;
	}
	
	public Map<StudyDto, Map<BiotypeDto, Triple<Integer, String, Date>>> countSamplesByStudyBiotype(
			Collection<StudyDto> studies) {
		return countSamplesByStudyBiotype(studies, null);
	}

	public Map<StudyDto, Map<AssayDto, Triple<Integer, String, Date>>> countResultsByStudyAssay(
			Collection<StudyDto> studies) {
		assert studies!=null;
		Map<StudyDto, Map<AssayDto, Triple<Integer, String, Date>>> res = new HashMap<>();
		Map<String, Map<String, Triple<Integer, String, Date>>> map = assayService.countResultsByStudyAssay(studies);
		//Convert map to the underlying type
		Map<String, StudyDto> mapStudy = mapStudyId(studies);
		for (String n1 : map.keySet()) {
			Map<AssayDto, Triple<Integer, String, Date>> m2 = new TreeMap<AssayDto, Triple<Integer, String, Date>>();
			for (String n2 : map.get(n1).keySet()) {
				AssayDto t =  assayService.map(assayService.getAssayByName(n2));
				if(t!=null && map.get(n1).get(n2)!=null) {
					m2.put(t, map.get(n1).get(n2));
				} else {
					System.err.println("Test "+n2+" not found");
				}
			}
			res.put(mapStudy.get(n1), m2);
		}
		return res;

		
	}

	public StudyDao getStudyDao() {
		return studyDao;
	}

	public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Participant> getParticipants(StudyDto study) {
		if (study==null)
			return new HashSet<>();
		HashSet<Participant> participants = (HashSet<Participant>) Cache.getInstance().get("study_participants_"+study.getId());
		if(participants==null) {
			participants = new HashSet<>();
			for (StageDto st : study.getStages()) {
				stageService.getParticipants(st,participants);
			}
			Cache.getInstance().add("study_participants_"+study.getId(), participants);
		}
		return participants;
	}
	
	public List<BiosampleDto> getSubjectsSorted(StudyDto study) {
		HashSet<BiosampleDto> biosamples = new HashSet<>();
		getParticipants(study).stream() //
        .filter(participant -> participant.getBiosample() != null) //
        .forEach(participant -> biosamples.add(participant.getBiosample()));
		ArrayList<BiosampleDto> retval = new ArrayList<>(biosamples);
		Collections.sort(retval, Comparators.COMPARATOR_GROUP_SAMPLE_NAME);
		return retval;
	}
	
	public HashMap<Date, Set<IndividualAction>> getAllActions(StudyDto study) {
		HashMap<Date, Set<IndividualAction>> allActions = new HashMap<>();
        for (Participant participant : getParticipants(study)) {
            for (AssignmentDto assignment : participant.getAssignmentSeries()) {
                MiscUtils.putAll(allActions, participantService.getAllActions(participant,assignment));
            }
        }
        return allActions;
	}
	
	public List<Date> getAllDate(StudyDto study){
		List<Date> dates = new ArrayList<>(getAllActions(study).keySet());
		Collections.sort(dates);
		return dates;
	}

	public Participant getParticipantFor(StudyDto study, BiosampleDto sample) {
		for (Participant p : getParticipants(study)) {
			if (p.getBiosample()==null) continue;
			if (p.getBiosample().equals(sample)) return p;
		}
		return null;
	}
	
	public Participant getParticipantFor(StudyDto study, Set<Participant> participants, BiosampleDto sample) {
		for (Participant p : participants) {
			if (p.getBiosample()==null) continue;
			if (p.getBiosample().equals(sample)) return p;
		}
		return null;
	}
	
	public Participant getParticipantFor(StudyDto study, Set<Participant> participants, BiosampleDto sample,
			Date date) {
		AssignmentDto assignment = biosampleService.getAssignment(sample, study, date, participants);
		for (Participant p : participants) {
			if (p.getAssignmentSeries().contains(assignment)) return p;
		}
		return null;
	}

	public Date getFirstDate(StudyDto study) {
		if (study.getStages().size() > 0) {
			List<Date> stagesDates = new ArrayList<Date>();
			for(StageDto s : study.getStages()) {
				Date date = stageService.getFirstDate(s);
				if(date!=null)
					stagesDates.add(date);
			}
			Collections.sort(stagesDates, new Comparator<Date>() {
				@Override
				public int compare(Date d1, Date d2) {
					return d1.compareTo(d2);
				}
			});
			return stagesDates.size()>0?stagesDates.get(0):null;
		}
		return null;
	}

	public Participant getParticipantFor(StudyDto study, Set<Participant> participants, AssignmentDto assignment) {
		for (Participant p : participants) {
			if (p.getAssignmentSeries().contains(assignment)) return p;
		}
		return null;
	}

	public List<PhaseDto> getPhases(StudyDto study) {
		List<PhaseDto> phases = new ArrayList<>();
		for (StageDto stage : study.getStages()) {
			phases.addAll(stage.getPhases());
		}
		return phases;
	}
	
	public StudyDto map(Study study) {
		if(study==null || study.getId()==Constants.NEWTRANSIENTID)
			return null;
		StudyDto studyDto = idToStudy.get(study.getId());
		if(studyDto==null) {
			studyDto=dozerMapper.map(study, StudyDto.class,"studyCustomMapping");
			if(idToStudy.get(study.getId())==null) { 
				idToStudy.put(study.getId(), studyDto);
			}
			else
				studyDto=idToStudy.get(study.getId());
		}
		return studyDto;
	}
	
	public List<StudyDto> map(Collection<Study> studies) {
		List<StudyDto> result = new ArrayList<>();
		for(Study study : studies) {
			result.add(map(study));
		}
		return result;
	}
	
	public StudyDto duplicate(StudyDto study, User user) {
		StudyDto duplicateStudy = new StudyDto();
		duplicateStudy.setStatus(study.getStatus());
		duplicateStudy.setDescription(study.getDescription() + " (Duplicated)");
		duplicateStudy.setSynchroSamples(study.getSynchroSamples());
		duplicateStudy.setProperties(duplicatePropeties(duplicateStudy, duplicateStudy.getProperties()));
		Set<NamedTreatmentDto> namedTreatments = study.getNamedTreatments();
		HashMap<String, NamedTreatmentDto> namedTreatmentClones = new HashMap<>();
		Set<NamedTreatmentDto> tempNamedTreatments = new HashSet<>(namedTreatments);
		for (NamedTreatmentDto namedTreatment : tempNamedTreatments) {
			NamedTreatmentDto clone = namedTreatmentService.clone(namedTreatment, duplicateStudy);
			namedTreatmentClones.put(namedTreatment.getName(), clone);
		}
		Set<NamedSamplingDto> namedSamplings = study.getNamedSamplings();
		HashMap<String, NamedSamplingDto> namedSamplingClones = new HashMap<>();
		Set<NamedSamplingDto> tempNamedSamplings = new HashSet<>(namedSamplings);
		for (NamedSamplingDto namedSampling : tempNamedSamplings) {
			NamedSamplingDto clone = namedSamplingService.clone(namedSampling, duplicateStudy);
			for(SamplingDto sampling : clone.getSamplings()) {
        		sampling.setNamedSampling(clone);
        	}
			namedSamplingClones.put(namedSampling.getName(), clone);
			addNamedSampling(duplicateStudy, clone);
		}
		IdentityHashMap<StageDto, StageDto> stageClones = new IdentityHashMap<>();
		for (StageDto stage : study.getStages()) {
			StageDto cloneStage = stageService.duplicate(stage, duplicateStudy);
			for (ActionPatternsDto actionPattern : cloneStage.getActionPatterns()) {
				StudyAction studyAction = actionPattern.getAction();
				if(studyAction instanceof Disposal) {
					Disposal disposal = (Disposal) studyAction;
					if(disposal.getSampling()!=null)
						disposal.setSampling(namedSamplingClones.get(disposal.getSampling().getName()));
		        }else if(studyAction instanceof NamedSamplingDto) {
		        	actionPatternsService.setAction(actionPattern, namedSamplingClones.get(((NamedSamplingDto) studyAction).getName()));
		        }else if(studyAction instanceof NamedTreatmentDto) {
		        	actionPatternsService.setAction(actionPattern, namedTreatmentClones.get(((NamedTreatmentDto) studyAction).getName()));
		        }
			}
			stageClones.put(stage, cloneStage);
			addStage(duplicateStudy, cloneStage);
		}
		for (StageDto stage : study.getStages()) {
			StageDto cloneStage = stageClones.get(stage);
			stageService.setPreviousStage(cloneStage, stageClones.get(stage.getPreviousStage()));
			for (GroupDto group : stage.getGroups()) {
				GroupDto cloneGroup = stageService.getGroupByName(cloneStage, group.getName());
				for(SubGroupDto subGroup : cloneGroup.getSubgroups()) {
					if (subGroup.getRandofromgroup() != null) {
						subGroup.setRandofromgroup(stageService.getGroupByName(stageClones.get(groupService.getSubGroup(group, subGroup.getNo()).getRandofromgroup().getStage()), groupService.getSubGroup(group, subGroup.getNo()).getRandofromgroup().getName()));
					}
					if(subGroup.getRandofromsubgroup() != null) {
						List<SubGroupDto> fromSubGroups = stageService.getGroupByName(stageClones.get(groupService.getSubGroup(group, subGroup.getNo()).getRandofromsubgroup().getGroup().getStage()), groupService.getSubGroup(group, subGroup.getNo()).getRandofromgroup().getName()).getSubgroups();
						for(SubGroupDto fromSG : fromSubGroups) {
							if(fromSG.getName().equals(subGroup.getRandofromsubgroup().getName())) {
								subGroup.setRandofromsubgroup(fromSG);
							}
						}
					}
				}
			}
		}

		return duplicateStudy;
	}
	
	private List<PropertyLinkDto> duplicatePropeties(StudyDto study, List<PropertyLinkDto> properties) {
		List<PropertyLinkDto> res = new ArrayList<>();
		for(PropertyLinkDto pl : properties) {
			PropertyLinkDto pld = new PropertyLinkDto();
			pld.setProperty(pl.getProperty());
			propertyLinkService.setStudy(pl, study);
			pld.setValue(pl.getValue());
			res.add(pld);
		}
		return res;
	}
	
	@Transactional
	public void save(StudyDto study) throws Exception {
		save(study, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(StudyDto study, Boolean cross) throws Exception {
		try {
			if(study!=null && !savedItems.contains(study)) {
				savedItems.add(study);
				if(study.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(study);
				}
				if(study.getId()==Constants.NEWTRANSIENTID && "".equals(study.getStudyId()))
					study.setStudyId(getNextStudyId());
				study.setUpdDate(new Date());
				study.setUpdUser(UserUtil.getUsername());
				if(study.getId().equals(Constants.NEWTRANSIENTID)) {
					study.setCreDate(new Date());
					study.setCreUser(UserUtil.getUsername());
				}
				study.setId(saveOrUpdate(dozerMapper.map(study, Study.class, "studyCustomMapping")));
				idToStudy.put(study.getId(), study);
				if(study.getStagesNoMapping()!=null) {
					//Need to revert because of interaction between stage.getNextStage and subgroup.getFromGroup
					List<StageDto> stagesRevert = new ArrayList<>(study.getStages());
					Collections.reverse(stagesRevert);
					for(StageDto stage : stagesRevert)
						stageService.save(stage, true);
				}
				if(study.getPropertiesNoMapping()!=null)
					for(PropertyLinkDto property : study.getProperties())
						propertyLinkService.save(property, true);
				if(study.getNamedTreatmentsNoMapping()!=null)
					for(NamedTreatmentDto treatment : study.getNamedTreatments())
						namedTreatmentService.save(treatment, true);
				if(study.getNamedSamplingsNoMapping()!=null)
					for(NamedSamplingDto sampling : study.getNamedSamplings())
						namedSamplingService.save(sampling, true);
				if(study.getEnclosuresNoMapping()!=null)
					for(EnclosureDto enclosure : study.getEnclosures())
						enclosureService.save(enclosure, true);
				if(study.getDocumentsNoMapping()!=null)
					for(StudyDocumentDto studyDocument : study.getDocuments())
						studyDocumentService.save(studyDocument);
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
	private void deleteChildren(StudyDto study) throws Exception {
		if(study.getStagesNoMapping()!=null) {
			StageDto prev = null;
			List<Stage> stagesSorted = stageService.getStagesByStudyId(study.getId());
			Collections.sort(stagesSorted);
			for(Stage s : stagesSorted) {
				Boolean found = false;
				for(StageDto child : study.getStages()){
					if(s.getId().equals(child.getId())) {
						found=true;
						prev = child;
						break;
					}
				}
				if(!found) {
					if(prev!=null)
						stageService.save(prev, true);
					stageService.delete(stageService.map(s), true);
				}
			}
		}
		if(study.getPropertiesNoMapping()!=null) {
			for(PropertyLink pl : propertyLinkService.getPropertyLinksByStudy(study.getId())) {
				Boolean found = false;
				for(PropertyLinkDto child : study.getProperties()){
					if(pl.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					propertyLinkService.delete(propertyLinkService.map(pl), true);
				}
			}
		}
		if(study.getNamedTreatmentsNoMapping()!=null) {
			for(NamedTreatment nt : namedTreatmentService.getNamedTreatmentsByStudy(study.getId())) {
				Boolean found = false;
				for(NamedTreatmentDto child : study.getNamedTreatments()){
					if(nt.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					namedTreatmentService.delete(namedTreatmentService.map(nt), true);
				}
			}
		}
		if(study.getNamedSamplingsNoMapping()!=null) {
			for(NamedSampling ns : namedSamplingService.getNamedSamplingsByStudy(study.getId())) {
				Boolean found = false;
				for(NamedSamplingDto child : study.getNamedSamplings()){
					if(ns.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					namedSamplingService.delete(namedSamplingService.map(ns), true);
				}
			}
		}
		if(study.getEnclosuresNoMapping()!=null) {
			for(Enclosure e : enclosureService.getByStudy(study.getId())) {
				Boolean found = false;
				for(EnclosureDto child : study.getEnclosures()){
					if(e.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					enclosureService.delete(enclosureService.map(e), true);
				}
			}
		}
		if(study.getDocumentsNoMapping()!=null) {
			for(StudyDocument sd : studyDocumentService.getByStudyId(study.getId())) {
				Boolean found = false;
				for(StudyDocumentDto child : study.getDocuments()){
					if(sd.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					studyDocumentService.delete(studyDocumentService.map(sd), true);
				}
			}
		}
	}
	

	@Transactional
	public void delete(StudyDto study) throws Exception {
		delete(study, false);
	}
	
	protected void delete(StudyDto study, Boolean cross) throws Exception {
		studyDao.delete(study.getId());
	}
	
	public void loadAllLinkedObject(Integer studyId) {
		if(loadedStudy.containsKey(studyId))
			return;
		loadedStudy.put(studyId, null);
		System.out.println("Start load compounds");
		loadCompounds(studyId);
		System.out.println("End load compounds");
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					Long start = System.currentTimeMillis();
					System.out.println("load attributes for "+studyId);
					System.out.println("start load biosamples for "+studyId);
					biosampleService.getAllByStudy(studyId);
					System.out.println("end load biosamples in "+(System.currentTimeMillis()-start)/1000);
					Long start2 = System.currentTimeMillis();
					System.out.println("start load result for "+studyId);
					List<AssayResultDto> results = assayResultService.map(assayResultService.getAssayResultsByStudy(studyId));
					List<Enclosure> enclosures = enclosureService.getEnclosure(studyId);
					for(Enclosure enclosure : enclosures)
						enclosureService.map(enclosure);
					System.out.println("end load results in "+(System.currentTimeMillis()-start)/1000+" ----- "+(System.currentTimeMillis()-start2)/1000 +" s");
					Long start3 = System.currentTimeMillis();
					assayResultValueService.mapResultValues(results);
					System.out.println("end map results in "+(System.currentTimeMillis()-start)/1000+" ----- "+(System.currentTimeMillis()-start3)/1000 +" s");
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					MappingThreadUtils.removeThread(this);
				}
			}
		};
		MappingThreadUtils.start(t2);
		/*Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("start load biosample for "+studyId);
					Long start = System.currentTimeMillis();
					for(Biosample biosample: biosamples) {
						Thread t = new Thread() {
							@Override
							public void run() {
								try {
									BiosampleDto bDto = biosampleService.map(biosample);
									for(BiosampleDto child : bDto.getChildren()) {
										child.getResults();
										child.getMetadatas();
									}
								}catch(Exception e) {
									e.printStackTrace();
								}finally {
									MappingThreadUtils.removeThread(this);
								}							
							}
						};
						MappingThreadUtils.start(t);
					}
					MappingThreadUtils.waitProcess();
					System.out.println("end load biosamples in "+(System.currentTimeMillis()-start)/1000+" s");
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t2.start();*/
	}
	
	public void loadCompounds(Integer studyId) {
		namedSamplingService.getNamedSamplingsByStudy(studyId);
		List<NamedTreatment>nts = namedTreatmentService.getNamedTreatmentsByStudy(studyId);
		List<Integer> ids = new ArrayList<>();
		nts.forEach(nt->{
			Integer ppgTreatmentInstanceId = nt.getPpgTreatmentInstanceId();
			if(ppgTreatmentInstanceId != null && ppgTreatmentInstanceId > 0) {
				ids.add(ppgTreatmentInstanceId);
			}
		});
		if(ids.size()!=0) {
			try {
				namedTreatmentService.storeTreatments(DAOPpgTreatment.getTreatmentsByIds(ids));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		ids.clear();
		nts.forEach(nt->{
			Integer spiTreatmentId=nt.getSpiTreatmentId();
			if (spiTreatmentId != null && spiTreatmentId > 0) {
				try {
					ids.add(spiTreatmentId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		if(ids.size()>0) {
			try {
				// namedTreatmentService.storeFormulations(DAOSpiFormulation.getFormulations(ids)); // do not work
				List<SpiFormulation> formulations = new ArrayList<SpiFormulation>();
				for(int id : ids) {
					formulations.add(DAOSpiFormulation.getFormulation(id));
				}
				namedTreatmentService.storeFormulations(formulations);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public StudyDto getStudyDtoByStudyId(String studyId) {
		return map(getStudyByStudyId(studyId));
	}
	
	public StudyDto getStudyDto(Integer id ) {
		return map(get(id));
	}

	public void addEnclosure(StudyDto study, EnclosureDto enclosure) {
		addEnclosure(study, enclosure, false);
	}

	public void addEnclosure(StudyDto study, EnclosureDto enclosure, boolean cross) {
		//prevent endless loop
		if (study.getEnclosures().contains(enclosure))
			return ;
		//add new member
		study.getEnclosures().add(enclosure);
		//update child if request is not from it
		if (!cross) {
			enclosureService.setStudy(enclosure, study, true);
		}
	}

	public void removeEnclosure(StudyDto study, EnclosureDto enclosure) {
		removeEnclosure(study, enclosure, false);
	}

	public void removeEnclosure(StudyDto study, EnclosureDto enclosure, boolean cross) {
		//prevent endless loop
		if (!study.getEnclosures().contains(enclosure))
			return ;
		//remove old member
		study.getEnclosures().remove(enclosure);
		//remove child's owner
		if (!cross) {
			enclosureService.setStudy(enclosure, null, true);
		}
	}

	private void removeChildActionPatterns(StudyDto study, StudyAction action) {
		List<ActionPatternsDto> toDelete = new ArrayList<>();
		for(ActionPatternsDto a: getActionPatterns(study)) {
			if(action.equals(a.getAction()) && a.getParent()==null) {
				toDelete.add(a);
			}
		}
		try {
			for(ActionPatternsDto a: toDelete) {
					stageService.removeActionPattern(a.getStage(), a);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Set<ActionPatternsDto> getActionPatterns(StudyDto study) {
        Set<ActionPatternsDto> actionPatterns = new HashSet<>();
        for (StageDto st : study.getStages()) {
			actionPatterns.addAll(st.getActionPatterns());
        }
		return actionPatterns;
	}
	
	public  Set<ActionPatternsDto> getActionPatterns(StudyDto study, PhaseDto phase) {
		assert phase!=null;
        Set<ActionPatternsDto> res = new HashSet<>();
        getActionPatterns(study).forEach(a -> {if (scheduleService.getPhases(a.getSchedule()).contains(phase)) res.add(a);});
		return res;
	}
	
	public PropertyLinkDto getProperty(StudyDto study, String name) {
        for (PropertyLinkDto property : study.getProperties()) {
        	if(property.getProperty().getName().equalsIgnoreCase(name))
        		return property;
        }
		return null;
	}
	
	public String getPropertyValue(StudyDto study, String name) {
		PropertyLinkDto property = getProperty(study, name);
		if(property!=null)
			return property.getValue();
		return null;
	}

	public void addStage(StudyDto study, StageDto stage) {
		addStage(study, stage, false);
	}

	public void addStage(StudyDto study, StageDto stage, boolean cross) {
		//prevent endless loop
		if (study.getStages().contains(stage))
			return ;
		//add new member
		study.getStages().add(stage);
		//update child if request is not from it
		if (!cross) {
			stageService.setStudy(stage, study, true);
		}
	}

	public boolean removeStage(StudyDto study, StageDto stage) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setSubjectSet(stage);
			studyActionResultQuery.setStudy(study);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.STAGE, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeStage(study, stage, false);
		return true;
	}

	protected void removeStage(StudyDto study, StageDto stage, boolean cross) {
		//prevent endless loop
		if (!study.getStages().contains(stage))
			return;
		//remove old member
		study.getStages().remove(stage);
		//remove child's owner
		if (!cross) {
			stageService.setStudy(stage, null, true);
		}
		
		for(Participant p : getParticipants(study)) {
			BiosampleDto b = p.getBiosample();
			if(b!=null)
				b.getAssignments().remove(biosampleService.getAssignment(b, stage));
		}
	}
	
	public boolean removeNamedTreatment(StudyDto study, NamedTreatmentDto namedTreatment) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setAction(namedTreatment);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeNamedTreatment(study, namedTreatment, false);
		return true;
	}

	protected void removeNamedTreatment(StudyDto study, NamedTreatmentDto namedTreatment, boolean cross) {
		//prevent endless loop
		if (!study.getNamedTreatments().contains(namedTreatment))
			return ;

		removeChildActionPatterns(study, namedTreatment);

		//remove old member
		study.getNamedTreatments().remove(namedTreatment);

		//remove child's owner
		if (!cross) {
			namedTreatmentService.setStudy(namedTreatment, null, true);
		}
	}

	public void addNamedTreatment(StudyDto study, NamedTreatmentDto namedTreatment) {
		addNamedTreatment(study, namedTreatment, false);
	}

	public void addNamedTreatment(StudyDto study, NamedTreatmentDto namedTreatment, boolean cross) {
		//prevent endless loop
		if (study.getNamedTreatments().contains(namedTreatment))
			return ;
		//add new member
		study.getNamedTreatments().add(namedTreatment);
		//update child if request is not from it
		if (!cross) {
			try {
				namedTreatmentService.setStudy(namedTreatment, study, true);
			} catch (Exception e) {
				//at creation time there can not be any already attached entity, so we shouldn't end up here
				e.printStackTrace();
			}
		}
	}

	public void addNamedSampling(StudyDto study, NamedSamplingDto namedSampling) {
		addNamedSampling(study, namedSampling, false);
	}

	public void addNamedSampling(StudyDto study, NamedSamplingDto namedSampling, boolean cross) {
		//prevent endless loop
		if (study.getNamedSamplings().contains(namedSampling))
			return ;
		//add new member
		study.getNamedSamplings().add(namedSampling);
		//update child if request is not from it
		if (!cross) {
			try {
				namedSamplingService.setStudy(namedSampling, study, true);
			} catch (Exception e) {
				//at creation time there can not be any already attached entity, so we shouldn't end up here
				e.printStackTrace();
			}
		}
	}
	
	public boolean removeNamedSampling(StudyDto study, NamedSamplingDto namedSampling) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setAction(namedSampling);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.ACTION, studyActionResultQuery);
		} catch (Exception e) {
			return false;
		}
		removeNamedSampling(study, namedSampling, false);
		return true;
	}

	protected void removeNamedSampling(StudyDto study, NamedSamplingDto namedSampling, boolean cross) {
		//prevent endless loop
		if (!study.getNamedSamplings().contains(namedSampling))
			return ;

		removeChildActionPatterns(study, namedSampling);
		//remove old member
		study.getNamedSamplings().remove(namedSampling);
		//remove child's owner
		if (!cross) {
			namedSamplingService.setStudy(namedSampling, null, true);
		}
	}

	public List<AssignmentDto> getAssignments(StudyDto study) {
		List<AssignmentDto> results = new ArrayList<>();
		if(study!=null) {
			for(StageDto stage : study.getStages()) {
				results.addAll(stage.getAssignments());
			}
		}
		return results;
	}

	public List<BiosampleDto> getBiosamples(StudyDto study) {
		List<BiosampleDto> results = new ArrayList<>();
		for(AssignmentDto assignment: getAssignments(study)) {
			BiosampleDto b = assignment.getBiosample();
			if(b!=null && !results.contains(b))
				results.add(b);
		}
		return results;
	}

	public List<EnclosureDto> getEnclosures(StudyDto study, Date date) {
		List<EnclosureDto> enclosures = new ArrayList<>();
		for(BiosampleDto biosample : getBiosamples(study)) {
			EnclosureDto enclosure = biosampleService.getEnclosure(biosample, study, date);
			if(enclosure!=null && !enclosures.contains(enclosure))
				enclosures.add(enclosure);
		}
		Collections.sort(enclosures);
		return enclosures;
	}

	public Date getLDate(StudyDto study) {
		return getLastDate(study, null);
	}

	public Date getLastDate(StudyDto study) {
		return getLastDate(study, null);
	}
	
	public Date getLastDate(StudyDto study, Set<Participant> participants) {
		Date lastDate = null;
		if(participants==null)
			participants=getParticipants(study);
		for (StageDto stage : study.getStages()) {
			for (AssignmentDto assignment : stage.getAssignments()) {
				Participant p = getParticipantFor(study, participants,assignment);
				Duration offset = p ==null ? Duration.ZERO: participantService.getOffsetToFollow(p, assignment);
				Date lastTimeForAnimal = assignmentService.getLastDate(assignment, offset);
				if (lastDate == null || (lastTimeForAnimal != null && lastTimeForAnimal.compareTo(lastDate)>0)) 
					lastDate = lastTimeForAnimal;
			}
	
		}
		return lastDate;
	}

	public StageDto getLastStage(StudyDto study) {
		List<StageDto> stages = study.getStages();
		if(stages.size()==0)
			return null;
		return stages.get(stages.size()-1);
	}

	public Set<NamedSamplingDto> getNamedSamplings(StudyDto study, boolean necro) {
		Set<NamedSamplingDto> tempList = new HashSet<>();
		for (NamedSamplingDto sampling : study.getNamedSamplings()) {
			if (necro == sampling.getNecropsy()) tempList.add(sampling);
		}
		return tempList;
	}

	public Set<GroupDto> getGroups(StudyDto study) {
		Set<GroupDto> groups = new HashSet<>();
		for (StageDto stage : study.getStages()) {
			groups.addAll(stage.getGroups());
		}
		return groups;
	}

	public boolean isMember(StudyDto study, String user) {
		if(getAdminUsersAsSet(study).contains(user)) return true;
		if(getBlindAllUsersAsSet(study).contains(user)) return true;
		if(getExpertUsersAsSet(study).contains(user)) return true;
		if(getBlindDetailsUsers(study).contains(user)) return true;
		if(study.getCreUser().equals(user)) return true;
		return false;
	}
	
	public String getBlindDetailsUsers(StudyDto study) {
		return MiscUtils.flatten(getBlindDetailsUsersAsSet(study), ", ");
	}
	
	public Set<String> getAdminUsersAsSet(StudyDto study) {
		populateUserSets(study);
		return Collections.unmodifiableSet(study.getAdminUsersSet());
	}

	public Set<String> getExpertUsersAsSet(StudyDto study) {
		populateUserSets(study);
		return Collections.unmodifiableSet(study.getExpertUsersSet());
	}

	public Set<String> getBlindAllUsersAsSet(StudyDto study) {
		populateUserSets(study);
		return Collections.unmodifiableSet(study.getBlindAllUsersSet());
	}
	
	public String getBlindAllUsers(StudyDto study) {
		return MiscUtils.flatten(getBlindAllUsersAsSet(study), ", ");
	}
	
	public Set<String> getBlindDetailsUsersAsSet(StudyDto study) {
		populateUserSets(study);
		return Collections.unmodifiableSet(study.getBlindDetailsUsersSet());
	}
	
	private void populateUserSets(StudyDto study) {
		if(study.getAdminUsersSet()==null) {
			study.setAdminUsersSet(new TreeSet<>(Arrays.asList(MiscUtils.split(study.getWriteUsers(), MiscUtils.SPLIT_SEPARATORS_WITH_SPACE))));
			study.setExpertUsersSet(new TreeSet<>(Arrays.asList(MiscUtils.split(study.getReadUsers(), MiscUtils.SPLIT_SEPARATORS_WITH_SPACE))));

			study.setBlindAllUsersSet(new TreeSet<>());
			study.setBlindDetailsUsersSet(new TreeSet<>());
			for(String u: MiscUtils.split(study.getBlindUsers(), MiscUtils.SPLIT_SEPARATORS_WITH_SPACE)) {
				if(u.startsWith("0#")) {
					//all
					study.getBlindAllUsersSet().add(u.substring(2));
				} else if(u.startsWith("1#")) {
					//actionGroup/namedTreatments
					study.getBlindDetailsUsersSet().add(u.substring(2));
				} else {
					study.getBlindDetailsUsersSet().add(u.indexOf('#')<0? u: u.substring(u.indexOf('#')+1));
				}
			}
		}
	}

	public PhaseDto parsePhase(StudyDto study, String value) {
		String[] split = value.split(" - ");
		if (split.length < 2){
			for (StageDto stage : study.getStages()) {
				PhaseDto phase = stageService.parsePhase(stage, value);
				if (phase!=null) {
					return phase;
				}
			}
		}else{
			for (StageDto stage : study.getStages()) {
				if (stage.getName().equals(split[0].strip())) {
					return stageService.parsePhase(stage, split[1].strip());
				}
			}
		}
		return null;
	}

	public List<BiosampleDto> getSubjectsSortedByName(StudyDto study) {
		HashSet<BiosampleDto> biosamples = new HashSet<>();
		getParticipants(study).stream() //
        .filter(participant -> participant.getBiosample() != null) //
        .forEach(participant -> biosamples.add(participant.getBiosample()));
		ArrayList<BiosampleDto> retval = new ArrayList<>(biosamples);
        Collections.sort(retval, (biosample1, biosample2) -> biosampleService.compareBioSampleBySubgroupAssignmentName(biosample1, biosample2, study));
		return retval;
	}

	public Object getFirstPhase(StudyDto study) {
		if (study.getStages().size() > 0)
			return stageService.getFirstPhase(study.getStages().get(0));
		return null;
	}

	public List<Participant> getParticipantsSortedByName(StudyDto study) {
		ArrayList<Participant> participants = new ArrayList<>(getParticipants(study));
		Collections.sort(participants, new Comparator<Participant>() {
			@Override
			public int compare(Participant p1, Participant p2) {
				BiosampleDto o1=p1.getBiosample();
				BiosampleDto o2=p2.getBiosample();
				if(o1==null && o2 ==null)
					return 0;
				if(o1==null)
					return 1;
				if(o2==null)
					return -1;

				return compareBioSampleBySubgroupAssignmentName(o1, o2, study);
			}
		});
		return participants;
	}
	
	private int compareBioSampleBySubgroupAssignmentName(BiosampleDto o1, BiosampleDto o2, StudyDto study) {
		AssignmentDto a1 = biosampleService.getLastAssignment(o1, study);
		AssignmentDto a2 = biosampleService.getLastAssignment(o2, study);
		int c=a1.getSubgroup().compareTo(a2.getSubgroup());
		if(c!=0)
			return c;
		return CompareUtils.compareAlphaNumeric(a1.getName(), a2.getName());
	}

	public Date getDayOneDate(StudyDto study) {
		if (study.getStages().size() > 0)
			return stageService.getFirstDate(study.getStages().get(0));
		return null;
	}

	public boolean hasPhases(StudyDto study) {
		for (StageDto stage : study.getStages()) {
			if (stage.getPhases().size()>0) return true;
		}
		return false;
	}

	public boolean isRunning(StudyDto study) {
		if (!"ONGOING".equals(study.getStatus()) && !"IRBR".equals(study.getStatus())) return false;
		if (getFirstDate(study) == null || getFirstDate(study).after(new Date())) return false;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -2);
		Date lastDate = getLastDate(study);
		if (lastDate == null) return false;
		if (lastDate.before(c.getTime())) return false;
		return true;
	}

	public HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> getActions(StudyDto study) {
		HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> actions = new HashMap<>();
		Set<Participant> participants = getParticipants(study);
		for (StageDto stage : study.getStages()) {
			if(stageService.getFirstDate(stage)!=null)
				stageService.getActions(stage, actions, participants);
		}
		return actions;
	}
	
	public List<Event> getEvents(StudyDto study) {
		List<Event> result = new ArrayList<>();
        HashMap<Date, HashMap<StudyAction, Set<AssignmentDto>>> actions = getActions(study);
        for (Date date : actions.keySet()) {
            Event event = new Event();
            event.setDateStart(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            HashMap<StudyAction, Set<AssignmentDto>> studyActionListHashMap = actions.get(date);
            int durationSec = 0;
            String desc = "";
			String fullDesc = "";
            for (StudyAction studyAction : studyActionListHashMap.keySet()) {
				if (studyAction == null)
					continue;
                int size = studyActionListHashMap.get(studyAction).size();
                durationSec += getActionDuration(study, studyAction) * size;
                String header = size + "x ";
				switch (studyAction.getClass().getSimpleName()){
					case "NamedTreatment":
					case "NamedSampling":
						header += studyAction.getName() + "<br>";
						break;
					default:
						header+= ("null".equals(studyActionService.getDescription(studyAction)) ? "NoName" : studyActionService.getDescription(studyAction)) + "<br>";
				}
				desc += header;
				fullDesc = header;
                for (AssignmentDto assignment : studyActionListHashMap.get(studyAction)) {
                    event.addAction(new Pair<AssignmentDto, StudyAction>(assignment, studyAction));
					fullDesc += "- " + assignment.getBiosample() + "<br>";
                }
            }
            Duration duration = Duration.ofSeconds(durationSec);
            if (duration.compareTo(Duration.ofHours(7)) > 0 ) event.setColor("red");
            event.setDuration(duration);
            event.setDescription(desc);
			event.setFullDescription(fullDesc);
            result.add(event);
        }
        return result;
    }

	public List<VEvent> getEventsICS(StudyDto study) {
		List<VEvent> result = new ArrayList<>();
		for (Event event : getEvents(study)) {
			VEvent vEvent = new VEvent();
			vEvent.setSummary(study.getStudyId() + " " + event.getActions().size() + " action" + (event.getActions().size()>1 ? "s": ""));
			vEvent.setOrganizer(UserUtil.getUser().getEmail());
			vEvent.setDateStart(Timestamp.valueOf(event.getDateStart()));
			if (event.getColor() != null) vEvent.setColor(event.getColor());
			vEvent.setDuration(biweekly.util.Duration.parse(event.getDuration().toString()));
			vEvent.setDescription(event.getFullDescription().replaceAll("<br>", "\n"));
			result.add(vEvent);
		}
		return result;
	}
	
	private int getActionDuration(StudyDto study, StudyAction studyAction) {
        Integer integer = study.getActionDuration().get(studyAction);
        if (integer == null) {
            integer =  studyAction == null ? 0 : studyAction.getDuration();
        }
	    return integer.intValue();
    }

	public DocumentDto getConsentForm(StudyDto study) {
		for(StudyDocumentDto d: study.getDocuments()) {
			if(d.getDocument().getType()==DocumentType.CONSENT_FORM) {
				return d.getDocument();
			}
		}
		return null;
	}

	public Set<String> getLocalIdOrStudyIds(Set<StudyDto> studies) {
		Set<String> res = new HashSet<>();
		if(studies==null) return res;
		for (StudyDto s : studies) {
			if(s!=null) res.add(getLocalIdOrStudyId(s));
		}
		return res;
	}
	
	public String getLocalIdOrStudyId(StudyDto study) {
		return study.getIvv()!=null && study.getIvv().length()>0? study.getIvv() : study.getStudyId();
	}

	public String getStudyIdAndInternalId(StudyDto study) {
		return study.getStudyId() + (study.getIvv()!=null && study.getIvv().length()>0? " (" + study.getIvv() + ")": "") ;
	}

	public boolean isBlind(StudyDto study) {
		return getBlindAllUsersAsSet(study).size()>0 || getBlindDetailsUsersAsSet(study).size()>0;
	}

	public List<StudyDto> queryStudies(StudyQuery q, User user) throws Exception {
		List<Study> resp =  studyDao.queryStudies(q, user);
		List<StudyDto> studies = map(resp);	
		//Post Filter according to metadata
		if(q.getMetadataMap().size()>0) {
			List<StudyDto> filtered = new ArrayList<>();
			loop: for (StudyDto study : studies) {
				for (Map.Entry<String, String> e : q.getMetadataMap().entrySet()) {
					if (e.getValue().length() > 0
							&& (getProperty(study, e.getKey()) == null || !getProperty(study, e.getKey()).getValue()
									.toLowerCase().contains(e.getValue().toLowerCase()))) {
						continue loop;
					}
				}
				filtered.add(study);
			}
			studies = filtered;
		}
		return studies;
	}

	@SuppressWarnings("unchecked")
	public Collection<ContainerType> getContainerTypes(StudyDto study) {
		List<ContainerType> res = (List<ContainerType>) Cache.getInstance().get("study_containers_"+study);
		if(res==null) {
			res = new ArrayList<ContainerType>();
			List<String> containerTypes = biosampleService.getContainerTypes(study);
			for(String containerType : containerTypes) {
				if(ContainerType.get(containerType) != null) {
					res.add(ContainerType.get(containerType));
				}
			}
			Collections.sort(res);
			Cache.getInstance().add("study_containers_"+study, res);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<BiotypeDto> getBiotypes(StudyDto study) {
		List<Biotype> biotypes = (List<Biotype>) Cache.getInstance().get("study_biotypes_"+study);
		if(biotypes==null) {
			biotypes = new ArrayList<Biotype>();
			biotypes.addAll(biosampleService.getBiotypes(study));
			Cache.getInstance().add("study_biotypes_"+study, biotypes);
		}
		List<BiotypeDto> res = new ArrayList<BiotypeDto>();
		for(Biotype biotype : biotypes) {
			res.add(biotypeService.map(biotype));
		}
		return res;
	}

	public List<FoodWaterDto> getFoodWaters(StudyDto study) {
		List<FoodWaterDto> result = new ArrayList<>();
		for(EnclosureDto enclosure : study.getEnclosures()) {
			result.addAll(enclosure.getFoodWaters());
		}
		return result;
	}

	public Set<NamedTreatmentDto> getNamedTreatments(StudyDto study, boolean isDisease) {
		Set<NamedTreatmentDto> results = new HashSet<>();
		study.getNamedTreatments().stream().filter(namedTreatment -> isDisease == namedTreatment.getDisease()).forEach(results::add);
		return results;
	}

	@SuppressWarnings("deprecation")
	public void mapStages(StudyDto study) {
		study.setStages(stageService.map(stageService.getStagesByStudyId(study.getId())));
		Collections.sort(study.getStages());
	}

	@SuppressWarnings("deprecation")
	public void mapEnclosures(StudyDto study) {
		study.setEnclosures(enclosureService.map(enclosureService.getByStudy(study.getId())));
	}

	public void mapProperties(StudyDto study) {
		study.setProperties(propertyLinkService.map(propertyLinkService.getPropertyLinksByStudy(study.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapNamedTreatments(StudyDto study) {
		study.setNamedTreatments(namedTreatmentService.map(namedTreatmentService.getNamedTreatmentsByStudy(study.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapNamedSampling(StudyDto study) {
		study.setNamedSamplings(namedSamplingService.map(namedSamplingService.getNamedSamplingsByStudy(study.getId())));
	}

	public void mapDocuments(StudyDto study) {
		List<StudyDocumentDto> documents = studyDocumentService.map(studyDocumentService.getByStudyId(study.getId()));
		if(documents != null) {			
			study.setDocuments(new HashSet<StudyDocumentDto>(documents));
		}
	}

	public Set<String> getStudyIds(List<StudyDto> studies) {
		Set<String> res = new HashSet<>();
		if(studies==null) return res;
		for (StudyDto s : studies) {
			if(s!=null) res.add(s.getStudyId());
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<ElbLink> getNiobeLinksForStudy(StudyDto study) {
		List<ElbLink> res = (List<ElbLink>) Cache.getInstance().get("elb_links_"+study.getId());
		if(res==null && study.getElb() != null && !"".equals(study.getElb())) {
			Map<String, ElbLink> map = new HashMap<>();
			res = new ArrayList<>();
			com.idorsia.research.niobe.data.DocumentDto doc = NiobeService.getDocumentByElb(study.getElb());
			ElbLink elbLink = new ElbLink();
			elbLink.setElb(doc.getLabJournal());
			elbLink.setTitle(doc.getTitle());
			elbLink.setScientist(doc.getScientist());
			elbLink.setCreDate(doc.getCreaDate());
			elbLink.setPubDate(doc.getSealDate());
			if(elbLink.getPubDate()!=null) {
				try {
					elbLink.setUrl(new URL("https://ares/portal/jsp/searchNiobePdf.jsp?labJournal="+doc.getLabJournal()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			res.add(elbLink);
			map.put(doc.getLabJournal(), elbLink);
		}
		Cache.getInstance().add("elb_links_"+study.getId(), res, 60);
		return res;
	}

	public List<StudyDto> getRecentChanges(Date date) {
		Set<StudyDto> list1 = new HashSet<>();
		list1.addAll(map(studyDao.getStudyAfterDate(date)));
		list1.addAll(map(studyDao.getStudyTargetedByBiosampleAfterDate(date)));
		list1.addAll(map(studyDao.getStudyTargetedByResultAfterDate(date)));
		List<StudyDto> res = new ArrayList<>(list1);
		Collections.sort(res);
		return res;
	}
	
	public static Map<String, StudyDto> mapStudyId(Collection<StudyDto> studies){
		Map<String, StudyDto> res = new HashMap<>();
		if(studies==null) return res;
		for (StudyDto s : studies) {
			if(s!=null) res.put(s.getStudyId(), s);
		}
		return res;
	}

	public boolean isBlindAll(StudyDto study) {
		return getBlindAllUsersAsSet(study).size()>0;
	}

	public void setDepartments(StudyDto study, List<Department> departments) {
		study.setDepartment(null);
		study.setDepartment2(null);
		if(departments!=null) {
			if(departments.size()>3) 
				throw new IllegalArgumentException(departments +".length>3");
			study.setDepartment(departments.size()>0? departments.get(0): null);
			study.setDepartment2(departments.size()>1? departments.get(1): null);
		}
		
	}

	public String getDepartmentsAsString(StudyDto study) {
		List<Department> egs = study.getDepartments();
		if(egs.size()==0) return "["+study.getCreUser()+"]";
		StringBuilder sb = new StringBuilder();
		for (Department eg : egs) {
			if(sb.length()>0) sb.append(", ");
			sb.append(UserUtil.getNameShort(eg));
		}
		return "["+sb.toString()+"]";
	}

	public void changeOwnership(StudyDto study, User toUser, User updater) throws Exception {
		if(study.getId()<=0) return;
		study.setUpdUser(updater.getUsername());
		study.setCreUser(toUser.getUsername());
		save(study);
	}

	@SuppressWarnings("deprecation")
	public void addPorperty(StudyDto study, String name, String value) {
		PropertyLinkDto property = getProperty(study, name);
		if(property==null) {
			property = new PropertyLinkDto();
			property.setProperty(propertyService.getPropertyByName(name));
			property.setStudy(study);
			study.getProperties().add(property);
		}
		property.setValue(value);
	}

	public void removeDocument(StudyDto study, DocumentDto document) {
		for(StudyDocumentDto doc : study.getDocuments()) {
			if(doc.getDocument().equals(document)) {
				study.getDocuments().remove(doc);
			}
		}
	}
}
