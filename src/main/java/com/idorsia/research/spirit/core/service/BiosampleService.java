package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.naming.CommunicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.animal.restapi.DAOAnimalProcedure;
import com.actelion.research.animal.restapi.DAOAnimalUsage;
import com.actelion.research.business.AnimalProcedure;
import com.actelion.research.business.AnimalUsageRest;
import com.actelion.research.business.AnimalUsageRestDetail;
import com.actelion.research.osiris.util.ListHashMap;
import com.actelion.research.security.entity.User;
import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.FormatterUtils;
import com.actelion.research.util.Pair;
import com.actelion.research.util.services.Cache;
import com.idorsia.research.spirit.core.biosample.BiosampleLinker;
import com.idorsia.research.spirit.core.constants.BiosampleDuplicateMethod;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.ContainmentType;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.constants.HierarchyMode;
import com.idorsia.research.spirit.core.constants.InfoFormat;
import com.idorsia.research.spirit.core.constants.InfoSize;
import com.idorsia.research.spirit.core.constants.LocationFormat;
import com.idorsia.research.spirit.core.constants.LocationLabeling;
import com.idorsia.research.spirit.core.constants.Status;
import com.idorsia.research.spirit.core.dao.BiosampleDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AdministrationDto;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssayResultValueDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiosampleEnclosureDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataBiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.DocumentDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.LinkedBiosampleDto;
import com.idorsia.research.spirit.core.dto.LinkedDocumentDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.PlannedSampleDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Amount;
import com.idorsia.research.spirit.core.dto.view.BiosampleQuery;
import com.idorsia.research.spirit.core.dto.view.Container;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.Participant;
import com.idorsia.research.spirit.core.dto.view.ResultQuery;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.model.Administration;
import com.idorsia.research.spirit.core.model.AnimalDBPojo;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.model.Biosample;
import com.idorsia.research.spirit.core.model.BiosampleEnclosure;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.model.BiotypeMetadataBiosample;
import com.idorsia.research.spirit.core.model.LinkedBiosample;
import com.idorsia.research.spirit.core.model.LinkedDocument;
import com.idorsia.research.spirit.core.model.PlannedSample;
import com.idorsia.research.spirit.core.util.ExpressionHelper;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;
import com.idorsia.research.spirit.core.util.SetHashMap;
import com.idorsia.research.spirit.core.util.SpiritRights;
import com.idorsia.research.spirit.core.util.ThreadUtils;
import com.idorsia.research.spirit.core.util.Triple;
import com.idorsia.research.spirit.core.util.UserUtil;

import net.objecthunter.exp4j.Expression;

@Service
public class BiosampleService extends AbstractService implements Serializable{

	private static final long serialVersionUID = 7874664095547723017L;
	
	@Autowired
	private BiosampleDao biosampleDao;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private StudyActionService studyActionService;
	@Autowired
	private ParticipantService participantService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private ExecutionDetailService executionDetailService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private StageService stageService;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private SubGroupService subgroupService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private ExecutionService executionService;
	@Autowired
	private AdministrationService administrationService;
	@Autowired
	private BiosampleEnclosureService biosampleEnclosureService;
	@Autowired
	private PlannedSampleService plannedSampleService;
	@Autowired
	private BiotypeMetadataBiosampleService biotypeMetadataBiosampleService;
	@Autowired
	private LinkedDocumentService linkedDocumentService;
	@Autowired
	private BiotypeService biotypeService;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private LocationLabelingService locationLabelingService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private LinkedBiosampleService linkedBiosampleService;
	@Autowired
	private SpiritRights spiritRights;
	@Autowired
	private BarcodeService barcodeService;
	
	private List<BiosampleDto> syncList = Collections.synchronizedList(new ArrayList<BiosampleDto>());


	@SuppressWarnings("unchecked")
	private static Map<Integer, BiosampleDto> idToBiosample = (Map<Integer, BiosampleDto>) getCacheMap(
			BiosampleDto.class);

	public int addBiosample(Biosample biosample) {
		return biosampleDao.addBiosample(biosample);
	}

	public Integer saveOrUpdate(Biosample biosample) {
		return biosampleDao.saveOrUpdate(biosample);
	}

	public Biosample get(Integer id) {
		return biosampleDao.get(id);
	}

	public List<Biosample> getBiosampleByUser(String user) {
		return biosampleDao.getBiosampleByUser(user);
	}
	
	public List<Biosample> getBiosampleByInheritedPhase(Integer phaseId) {
		return biosampleDao.getBiosampleByInheritedPhase(phaseId);
	}
	
	public List<Biosample> getBiosampleByEndPhase(Integer phaseId) {
		return biosampleDao.getBiosampleByEndPhase(phaseId);
	}

	public Biosample getBiosampleBySampleId(String sampleId) {
		return biosampleDao.getBiosampleBySampleId(sampleId);
	}

	public BiosampleDto getBiosampleDtoBySampleId(String sampleId) {
		return map(biosampleDao.getBiosampleBySampleId(sampleId));
	}

	public List<BiosampleDto> getBiosampleDtoBySampleIds(List<String> sampleIds) {
		return map(biosampleDao.getBiosampleBySampleIds(sampleIds));
	}
	
	public Map<String, BiosampleDto> getBiosampleDtoBySampleIdsMap(List<String> sampleIds) {
		Map<String, BiosampleDto> map = new HashMap<>();
		for(BiosampleDto biosample : getBiosampleDtoBySampleIds(sampleIds)) {
			map.put(biosample.getSampleId(), biosample);
		}
		return map;
	}

	public List<Biosample> getBiosampleByContainerId(String containerId) {
		return biosampleDao.getBiosampleByContainerId(containerId);
	}

	public List<Biosample> getByCage(String cageClicked, Integer studyId) {
		return biosampleDao.getByCage(cageClicked, studyId);
	}

	public List<Biosample> getBiosamplesByLocationId(Integer locationId) {
		return biosampleDao.getBiosamplesByLocationId(locationId);
	}

	public List<Biosample> getBiosamplesByParentId(Integer parentId) {
		if(parentId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return biosampleDao.getBiosamplesByParentId(parentId);
	}

	public Biosample getBiosampleById(int id) {
		return biosampleDao.getById(id);
	}

	public List<Biosample> getByStudy(Integer studyId) {
		return biosampleDao.getByStudy(studyId);
	}
	
	public List<Biosample> getAllByStudy(Integer studyId) {
		return  biosampleDao.getAllByStudy(studyId);
	}


	public List<Biosample> getByInheritedStudy(Integer studyId) {
		return biosampleDao.getByInheritedStudy(studyId);
	}

	public List<Biosample> getBySampling(Integer samplingId) {
		return biosampleDao.getBySampling(samplingId);
	}

	public List<Biosample> list() {
		return biosampleDao.list();
	}

	public int getCount() {
		return biosampleDao.getCount();
	}

	public Set<Biosample> getBiosamples(Set<Integer> samplesIds) {
		return biosampleDao.getBiosamples(samplesIds);
	}

	public List<String> getBiosampleNames(Set<Integer> biosamples) {
		return biosampleDao.getSampleIds(new ArrayList<Integer>(biosamples));
	}

	public BiosampleDao getBiosampleDao() {
		return biosampleDao;
	}

	public void setBiosampleDao(BiosampleDao biosampleDao) {
		this.biosampleDao = biosampleDao;
	}

	public Set<IndividualAction> getActionsPerformed(BiosampleDto biosample) {
		Set<IndividualAction> result = new HashSet<>();
		if (biosample.getCreationExecution() != null && biosample.getAttachedSampling() != null)
			result.add(new IndividualAction(biosample.getAttachedSampling().getNamedSampling(), biosample,
					biosample.getCreationExecution(), biosample.getInheritedPhase(), biosample.getStudy()));
		for (AssignmentDto assignment : biosample.getAssignments()) {
			if (assignment.getStage() == null)
				continue; // assignment without stage can only exist as temporary assignment in the group
							// assignment no group table...
			result.add(
					new IndividualAction(assignment.getStage(), biosample, assignmentService.getExecution(assignment),
							stageService.getFirstPhase(assignment.getStage()), assignment.getStage().getStudy()));
		}
		for (AssayResultDto auxResult : biosample.getResults()) {
			result.add(new IndividualAction(
					new Measurement(auxResult.getAssay(), assayResultService.getMeasurementParameters(auxResult)),
					biosample, auxResult.getExecution(), auxResult.getPhase(), auxResult.getStudy()));
		}
		for (AdministrationDto administration : administrationService.map(administrationService
				.getByBiosamplesAndPhase(Collections.singletonList(biosample.getId()), (Integer) null))) {
			if(administration.getNamedTreatment()==null)
			result.add(
					new IndividualAction(administration.getNamedTreatment(), biosample, administration.getExecution(),
							administration.getPhase(), administration.getNamedTreatment().getStudy()));
		}
		if (getTerminationExecutionDate(biosample) != null)
			result.add(new IndividualAction(new Disposal(), biosample, biosample.getTerminationExecution(),
					biosample.getEndPhase(),
					biosample.getEndPhase() == null ? null : biosample.getEndPhase().getStage().getStudy()));
		return result;
	}

	public boolean isDeadAt(BiosampleDto biosample, PhaseDto phase) {
		Date time = getDate(biosample, phase, true);
		if (time == null)
			return false;
		return isDeadAt(biosample, time);
	}
	
	public boolean isDeadAt(BiosampleDto biosample, Date date) {
		Date parseDateTime = getDateOfDeath(biosample);
		if (date == null)
			date = new Date();
		return parseDateTime == null ? false : date.compareTo(parseDateTime) > 0;
	}

	public Date getDateOfDeath(BiosampleDto biosample) {
		if (getTerminationExecutionDate(biosample) != null) {
			return getTerminationExecutionDate(biosample);
		} else if (getTerminationExecutionDatePlanned(biosample) != null) {
			return getTerminationExecutionDatePlanned(biosample);
		} else if (getMetadataValue(biosample, Constants.DATEOFDEATH) != null) {
			return FormatterUtils.parseDateTime(getMetadataValue(biosample, Constants.DATEOFDEATH));
		}
		return null;
	}

	public Date getTerminationExecutionDatePlanned(BiosampleDto biosample) {
		PhaseDto phase = getTerminationPhasePlanned(biosample);
		if (phase == null)
			return null;
		ExecutionDetailDto ed = assignmentService.getExecutionDetail(getAssignment(biosample, phase.getStage()), phase,
				getTerminationAction(biosample));
		if (ed != null) {
			return executionDetailService.getExecutionDate(ed, true);
		}
		return executionService.getExecutionDateCalculated(biosample, phase);
	}

	private StudyAction getTerminationAction(BiosampleDto biosample) {
		BiosampleDto animal = biosample.getTopParent();
		for (AssignmentDto assignment : animal.getAssignments()) {
			if (assignment.getSubgroup() != null) {
				ActionPatternsDto actionPattern = subgroupService.getTerminationPattern(assignment.getSubgroup());
				if (actionPattern != null)
					return actionPattern.getAction();
				actionPattern = groupService.getTerminationPattern(assignment.getSubgroup().getGroup());
				if (actionPattern != null)
					return actionPattern.getAction();
			}
		}
		return null;
	}

	public PhaseDto getTerminationPhasePlanned(BiosampleDto biosample) {
		BiosampleDto animal = biosample.getTopParent();
		if (animal.getEndPhase() != null && animal.getState() != null && !animal.getState().isAvailable()) {
			return animal.getEndPhase();
		} else {
			for (AssignmentDto assignment : animal.getAssignments()) {
				if (assignment.getSubgroup() != null) {
					PhaseDto endPhase = subgroupService.getEndPhase(assignment.getSubgroup());
					if (endPhase != null)
						return endPhase;
					endPhase = groupService.getEndPhase(assignment.getSubgroup().getGroup());
					if (endPhase != null)
						return endPhase;
				}
			}
			return null;
		}
	}

	public Date getTerminationExecutionDateCalculated(BiosampleDto biosample) {
		return getTerminationExecutionDate(biosample) == null ? getTerminationExecutionDatePlanned(biosample)
				: getTerminationExecutionDate(biosample);
	}

	public Date getTerminationExecutionDate(BiosampleDto biosample) {
		Execution terminationExecution = biosample.getTerminationExecution();
		return terminationExecution == null ? null : terminationExecution.getExecutionDate();
	}

	public boolean setTerminationExecutionDate(BiosampleDto biosample, Date value) {
		if (biosample.getTerminationExecution() == null) {
			if (value == null)
				return false;
			biosample.setTerminationExecution(new Execution());
		}
		biosample.getTerminationExecution().setExecutionDate(value);
		biosample.setEndDate(value);
		return true;
	}

	public Date getCreationExecutionDate(BiosampleDto biosample) {
		return biosample.getCreationExecution() == null ? null : biosample.getCreationExecution().getExecutionDate();
	}
	
	public Date getCreationExecutionDateCalculated(BiosampleDto biosample) {
		return getCreationExecutionDate(biosample) == null ? getCreationExecutionDatePlanned(biosample) : getCreationExecutionDate(biosample);
	}
	
	public Date getCreationExecutionDatePlanned(BiosampleDto biosample) {
		return executionService.getExecutionDateCalculated(biosample, biosample.getInheritedPhase());
	}

	public Date getDate(BiosampleDto biosample, PhaseDto phase, boolean daysAfterDeath) {
		if (phase == null || phase.getStage() == null)
			return null;
		AssignmentDto assignment = getAssignment(biosample, phase.getStage());
		if (assignment != null)
			return assignmentService.getDate(assignment,
					phase.getPhase().plus(assignmentService.getOffsetToFollow(assignment)), daysAfterDeath);
		assignment = getAssignment(biosample.getTopParent(), phase.getStage());
		if (phase.getStage().getStudy().equals(biosample.getStudy()) && assignment != null)
			return assignmentService.getDate(assignment,
					phase.getPhase().plus(assignmentService.getOffsetToFollow(assignment)), true);
		return null;
	}

	public AssignmentDto getAssignment(BiosampleDto biosample, StageDto stage) {
		for (AssignmentDto s : biosample.getAssignments()) {
			if (s.getStage() != null && s.getStage().equals(stage))
				return s;
		}
		return null;
	}
	
	public AssignmentDto getAssignment(BiosampleDto biosample, Date date) {
		for (AssignmentDto s : biosample.getAssignments()) {
			Duration offset = assignmentService.getOffsetToFollow(s);
			Date firstDate = assignmentService.getFirstDate(s, offset);
			Date lastDate = assignmentService.getLastDate(s, offset);
			if(firstDate==null || lastDate == null)
				continue;
			if ((firstDate.before(date) || firstDate.equals(date))
					&& (lastDate.after(date) || lastDate.equals(date))) return s;
		}
		return null;
	}

	public AssignmentDto getAssignment(BiosampleDto biosample, StudyDto study, Date date) {
		return getAssignment(biosample, study, date, null);
	}

	public AssignmentDto getAssignment(BiosampleDto biosample, StudyDto study, Date date,
			Set<Participant> participants) {
		if (biosample == null || study == null)
			return null;
		for (AssignmentDto assignment : assignmentService.getAssignments(biosample, study)) {
			Participant p = studyService.getParticipantFor(study,
					participants != null ? participants : studyService.getParticipants(study), assignment);
			StageDto stage = assignment.getStage();
			StageDto nextStage = participantService.getNextStage(p, assignment);
			for (ExecutionDetailDto ed : assignment.getExecutionDetails()) {
				Date edDate = executionDetailService.getExecutionDate(ed);
				if (edDate != null && edDate.equals(date)) {
					date = executionDetailService.getPlannedDate(ed);
				}
			}
			Duration duration = assignmentService.getDuration(assignment, date,
					participantService.getOffsetToFollow(p, assignment));
			if (stageService.getFirstDate(stage) == null || duration == null || duration.toMillis() < 0)
				continue;
			Date nextFirstDate = assignmentService.getNextStageFirstDate(assignment,
					participantService.getOffsetToFollow(p, assignment));
			if (nextStage != null && stageService.getFirstDate(nextStage) != null && date != null
					&& nextFirstDate != null && date.compareTo(nextFirstDate) >= 0)
				continue;
			return assignment;
		}
		return null;
	}

	/**
	 * Not map metadatas and results for children.
	 * 
	 * @param Child to map.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public BiosampleDto map(Biosample biosample) {
		if (biosample == null)
			return null;
		BiosampleDto biosampleDto = idToBiosample.get(biosample.getId());
		if (biosampleDto == null) {
			biosampleDto = dozerMapper.map(biosample, BiosampleDto.class, "biosampleCustomMapping");
			if (idToBiosample.get(biosample.getId()) == null)
				idToBiosample.put(biosample.getId(), biosampleDto);
			else
				biosampleDto = idToBiosample.get(biosample.getId());
			//Do not map with dozer -> infinite loop
			if(biosample.getTopParentId()!=null)
				biosampleDto.setTopParent(getBiosampleDto(biosample.getTopParentId()));
			if(biosample.getContainerId()!=null) {
				biosampleDto.setContainerId(null);
				setContainerId(biosampleDto, biosample.getContainerId());
			}if(biosample.getContainerType()!=null) {
				biosampleDto.setContainerType(null);
				setContainerType(biosampleDto, ContainerType.valueOf(biosample.getContainerType()));
			}

		}
		return biosampleDto;
	}

	public List<BiosampleDto> map(List<Biosample> biosamples) {
		List<BiosampleDto> result = new ArrayList<>();
		for (Biosample biosample : biosamples) {
			result.add(map(biosample));
		}
		return result;
	}

	@Transactional
	public void save(BiosampleDto biosample) throws Exception {
		save(biosample, false);
	}

	public void save(Collection<BiosampleDto> biosamples) {
		for(BiosampleDto biosample : biosamples) {
			try {
				save(biosample, true);
			}catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
				break;
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	@SuppressWarnings("deprecation")
	protected void save(BiosampleDto biosample, Boolean cross) throws Exception {
		try {
			if (biosample != null && !savedItems.contains(biosample)) {
				savedItems.add(biosample);
				if(biosample.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(biosample);
				}
				if (biosample.getAttachedSampling() != null
						&& biosample.getAttachedSampling().getId() == Constants.NEWTRANSIENTID)
					samplingService.save(biosample.getAttachedSampling(), true);
				if (biosample.getEndPhase() != null && biosample.getEndPhase().getId() == Constants.NEWTRANSIENTID)
					phaseService.save(biosample.getEndPhase(), true);
				if (biosample.getInheritedPhase() != null
						&& biosample.getInheritedPhase().getId() == Constants.NEWTRANSIENTID)
					phaseService.save(biosample.getInheritedPhase(), true);
				if (biosample.getStudy() != null && biosample.getStudy().getId() == Constants.NEWTRANSIENTID)
					studyService.save(biosample.getStudy(), true);
				if (biosample.getParent() != null && biosample.getParent().getId() == Constants.NEWTRANSIENTID)
					save(biosample.getParent(), true);
				if (biosample.getSampleId() == null || biosample.getSampleId().length() == 0)
					try {
						biosample.setSampleId(barcodeService.getNextId(biosample));
					} catch (Exception e) {
						e.printStackTrace();
					}
				biosample.setTopParent(biosample.getParent() == null ? biosample : biosample.getParent().getTopParent());
				biosample.setUpdDate(new Date());
				biosample.setUpdUser(UserUtil.getUsername());
				if(biosample.getId().equals(Constants.NEWTRANSIENTID)) {
					biosample.setCreDate(new Date());
					biosample.setCreUser(UserUtil.getUsername());
				}
				Biosample biosamplePojo = dozerMapper.map(biosample, Biosample.class, "biosampleCustomMapping");
				biosamplePojo.setTopParentId(biosample.getTopParent().getId());
				biosample.setId(saveOrUpdate(biosamplePojo));
				idToBiosample.put(biosample.getId(), biosample);
				if(biosample.getAssignmentsNoMapping()!=null) {
					for (AssignmentDto assignment : biosample.getAssignments())
						assignmentService.save(assignment, true);
				}
				if(!Boolean.FALSE.equals((Boolean)Cache.getInstance().get("saveResults")) && biosample.getResultsNoMapping()!=null) {
					for (AssayResultDto result : biosample.getResults())
						assayResultService.save(result, true);
				}
				if(biosample.getAdministrationsNoMapping()!=null) {
					for (AdministrationDto administration : biosample.getAdministrations())
						administrationService.save(administration, true);
				}
				if(biosample.getBiosampleEnclosuresNoMapping()!=null) {
					for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures())
						biosampleEnclosureService.save(biosampleEnclosure, true);
				}
				if(biosample.getPlannedSamplesNoMapping()!=null) {
					for (PlannedSampleDto plannedSample : biosample.getPlannedSamples())
						plannedSampleService.save(plannedSample, true);
				}
				if(biosample.getMetadatasNoMapping()!=null) {
					for (BiotypeMetadataBiosampleDto metadata : biosample.getMetadatas())
						biotypeMetadataBiosampleService.save(metadata, true);
				}
				if(biosample.getDocumentsNoMapping()!=null) {
					for(LinkedDocumentDto linkedDocument : biosample.getDocuments())
						linkedDocumentService.save(linkedDocument, true);
				}
				if(biosample.getLinkedBiosamplesNoMapping()!=null) {
					for(LinkedBiosampleDto linkedBiosample : biosample.getLinkedBiosamples())
						linkedBiosampleService.save(linkedBiosample, true);
				}
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
	private void deleteChildren(BiosampleDto biosample) throws Exception {
		if(biosample.getResultsNoMapping()!=null) {
			for(AssayResult result : assayResultService.getAssayResultsByBiosample(biosample.getId())) {
				Boolean found = false;
				for(AssayResultDto child : biosample.getResults()) {
					if(result.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					assayResultService.delete(assayResultService.map(result), true);
				}
			}
		}
		if(biosample.getAdministrationsNoMapping()!=null) {
			for(Administration administration : administrationService.getByBiosample(biosample.getId())) {
				Boolean found = false;
				for(AssignmentDto child : biosample.getAssignments()) {
					if(administration.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					administrationService.delete(administrationService.map(administration), true);
				}
			}
		}
		if(biosample.getBiosampleEnclosuresNoMapping()!=null) {
			for(BiosampleEnclosure enclosure : biosampleEnclosureService.getByBiosample(biosample.getId())) {
				Boolean found = false;
				for(BiosampleEnclosureDto child : biosample.getBiosampleEnclosures()) {
					if(enclosure.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					biosampleEnclosureService.delete(biosampleEnclosureService.map(enclosure), true);
				}
			}
		}
		if(biosample.getPlannedSamplesNoMapping()!=null) {
			for(PlannedSample ps : plannedSampleService.getPlannedSamplesByBiosample(biosample.getId())) {
				Boolean found = false;
				for(AssignmentDto child : biosample.getAssignments()) {
					if(ps.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					plannedSampleService.delete(plannedSampleService.map(ps), true);
				}
			}
		}
		if(biosample.getMetadatasNoMapping()!=null) {
			for(BiotypeMetadataBiosample metadata : biotypeMetadataBiosampleService.getBiotypeMetadataBiosamplesByBiosample(biosample.getId())) {
				Boolean found = false;
				for(BiotypeMetadataBiosampleDto child : biosample.getMetadatas()) {
					if(metadata.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					biotypeMetadataBiosampleService.delete(biotypeMetadataBiosampleService.map(metadata), true);
				}
			}
		}
		if(biosample.getDocumentsNoMapping()!=null) {
			for(LinkedDocument document : linkedDocumentService.getLinkedDocumentsByBiosample(biosample.getId())) {
				Boolean found = false;
				for(LinkedDocumentDto child : biosample.getDocuments()) {
					if(document.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					linkedDocumentService.delete(linkedDocumentService.map(document), true);
				}
			}
		}
		if(biosample.getLinkedBiosamplesNoMapping()!=null) {
			for(LinkedBiosample lb : linkedBiosampleService.getLinkedBiosamplesByBiosample(biosample.getId())) {
				Boolean found = false;
				for(LinkedBiosampleDto child : biosample.getLinkedBiosamples()) {
					if(lb.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					linkedBiosampleService.delete(linkedBiosampleService.map(lb), true);
				}
			}
		}
	}

	@Transactional
	public void delete(Collection<BiosampleDto> biosamples) throws Exception {
		for(BiosampleDto biosample : biosamples) {
			Set<AssignmentDto> assignments = new HashSet<>(biosample.getAssignments());
			for (AssignmentDto assignment : assignments) {
				assignmentService.setBiosample(assignment, null);
			}
		}
		biosampleDao.delete(getIds(biosamples));
	}

	@Transactional
	public void delete(BiosampleDto biosample) throws Exception {
		delete(biosample, false);
	}
	
	protected void delete(BiosampleDto biosample, Boolean cross) throws Exception {
		Set<AssignmentDto> assignments = new HashSet<>(biosample.getAssignments());
		for (AssignmentDto assignment : assignments) {
			assignmentService.setBiosample(assignment, null);
		}
		biosampleDao.delete(biosample.getId());
	}

	public BiosampleDto getBiosampleDto(Integer id) {
		return map(get(id));
	}
	
	@SuppressWarnings("deprecation")
	public BiosampleDto clone(BiosampleDto biosample) {
		BiosampleDto res = new BiosampleDto();
		res.setBiotype(biosample.getBiotype());
		res.setComments(biosample.getComments());
		res.setCreDate(biosample.getCreDate());
		res.setCreUser(biosample.getCreUser());
		res.setDepartment(biosample.getDepartment());
		res.setElb(biosample.getElb());
		res.setId(biosample.getId());
		res.setAttachedSampling(biosample.getAttachedSampling());
		res.setInheritedPhase(biosample.getInheritedPhase());
		res.setLocalId(biosample.getLocalId());
		setContainer(res, biosample.getContainer());
		res.setContainerIndex(biosample.getContainerIndex());

		res.setQuality(biosample.getQuality());
		res.setSampleId(biosample.getSampleId());
		res.setExpiryDate(biosample.getExpiryDate());
		res.setAmount(biosample.getAmount());
		res.setContainerIndex(biosample.getContainerIndex());

		setParent(res, biosample.getParent());

		setMetadatas(res, biosample.getMetadatas());
		res.setLinkedBiosamples(biosample.getLinkedBiosamples());
		res.setDocuments(biosample.getDocuments());
		return res;
	}

	public Integer getStrainId(BiosampleDto biosample) {
		try {
			return Integer.parseInt(getMetadataValue(biosample, "TypeID"));
		} catch (Exception ex) {
			String type = getMetadataValue(biosample, "Type");
			if (type == null)
				return null;
			return biosampleDao.getStrainID(type.substring(type.indexOf("/") + 1));
		}
	}

	public String getMetadataValue(BiosampleDto biosample, String metadataName) {
		for (BiotypeMetadataBiosampleDto metadata : biosample.getMetadatas()) {
			if (metadata.getMetadata().getName().equalsIgnoreCase(metadataName)) {
				return metadata.getValue();
			}
		}
		return null;
	}

	public String getMetadataValue(BiosampleDto biosample, BiotypeMetadataDto metadata) {
		for (BiotypeMetadataBiosampleDto metadataBiosample : biosample.getMetadatas()) {
			if (metadataBiosample.getMetadata().equals(metadata)) {
				return metadataBiosample.getValue();
			}
		}
		return null;
	}

	public BiosampleDto getMetadataBiosample(BiosampleDto biosample, BiotypeMetadataDto metadata) {
		LinkedBiosampleDto lb = getLinkedBiosamples(biosample, metadata);
		if(lb != null) {			
			BiosampleDto b = lb.getLinkedBiosample();
			if (b == null) {
				if (getMetadataValue(biosample, metadata) != null && getMetadataValue(biosample, metadata).length() > 0) {
					b = new BiosampleDto(getMetadataValue(biosample, metadata));
				}
			}
			return b;
		}
		return null;
	}

	public LinkedBiosampleDto getLinkedBiosamples(BiosampleDto biosample, BiotypeMetadataDto metadata) {
		for (LinkedBiosampleDto linkedBiosample : biosample.getLinkedBiosamples()) {
			if (linkedBiosample.getMetadata().equals(metadata))
				return linkedBiosample;
		}
		return null;
	}
	
	public void setMetadataBiosample(BiosampleDto row, BiotypeMetadataDto bType, BiosampleDto value) {
		assert bType!=null;
		assert row.getBiotype()!=null;
		assert row.getBiotype().equals(bType.getBiotype());
		if(bType.getDatatype()!=DataType.BIOSAMPLE) throw new IllegalArgumentException(bType + "'s type is not biosample");
		LinkedBiosampleDto metadata = null; 
		for(LinkedBiosampleDto m : row.getLinkedBiosamples()) {
			if(m.getMetadata().equals(bType)) {
				metadata=m;
				break;
			}
		}
		if(value!=null) {
			if(metadata==null) {
				metadata = new LinkedBiosampleDto();
				metadata.setBiosample(row);
				metadata.setMetadata(bType);
				row.getLinkedBiosamples().add(metadata);
			}
			metadata.setLinkedBiosample(value);
			addMetadata(row, new BiotypeMetadataBiosampleDto(row, bType, value==null? "": value.getSampleId()));
		}else {
			if(metadata!=null) {
				row.getLinkedBiosamples().remove(metadata);
				removeMetadata(row, bType);
			}
		}
	}
	
	public DocumentDto getMetadataDocument(BiosampleDto biosample, String bType) {
		if(biosample.getBiotype()==null) return null;
		return getMetadataDocument(biosample, biotypeService.getMetadataByName(biosample.getBiotype(), bType));
	}

	/**
	 * Returns the linked document corresponding to the linked bType. It is assumed that the datatype of bType is File
	 * @param bType
	 * @return
	 */
	public DocumentDto getMetadataDocument(BiosampleDto biosample, BiotypeMetadataDto bType) {
		assert bType!=null;
		assert bType.getDatatype().isDocument();
		assert biosample.getBiotype()!=null;
		LinkedDocumentDto linkedDocument = getLinkedDocumentDto(biosample, bType);
		return linkedDocument !=null ? linkedDocument.getLinkedDocument() : null;
	}
	
	/**
	 * Sets the document for a metadata where dataType = FILES
	 * 
	 * @param metadataName
	 * @param doc
	 */
	public void setMetadataDocument(BiosampleDto biosample, String metadataName, DocumentDto doc) {
		setMetadataDocument(biosample, biotypeService.getMetadataByName(biosample.getBiotype(), metadataName), doc);
	}

	/**
	 * Sets the document for a metadata where dataType = FILES
	 * 
	 * @param bType
	 * @param doc
	 */
	public void setMetadataDocument(BiosampleDto biosample, BiotypeMetadataDto bType, DocumentDto doc) {
		BiotypeDto biotype = biosample.getBiotype();
		assert bType != null;
		assert biotype != null;
		assert biotype.equals(bType.getBiotype());
		if (bType.getDatatype() != DataType.D_FILE && bType.getDatatype() != DataType.FILES)
			throw new IllegalArgumentException(bType + " is not a document's type");
		removeDocument(biosample, bType);
		 if(doc!=null){
			LinkedDocumentDto document = new LinkedDocumentDto(biosample, doc, bType);
			linkedDocumentService.addLinkedDocument(document);
			biosample.getDocuments().add(document);
		}
		setMetadata(biosample, bType, doc == null ? "" : doc.getFilename());
	}

	public void removeDocument(BiosampleDto biosample, BiotypeMetadataDto bType) {
		LinkedDocumentDto document = getLinkedDocumentDto(biosample, bType);
		if(document!=null){
			biosample.getDocuments().remove(document);
		}
	}
	
	public LinkedDocumentDto getLinkedDocumentDto(BiosampleDto biosample, BiotypeMetadataDto metadata) {
		for(LinkedDocumentDto linkedDocument : biosample.getDocuments()) {
			if(linkedDocument.getMetadata().equals(metadata)) {
				return linkedDocument;
			}
		}
		return null;
	}
	
	public Set<String> getMetadata(String metadataName, Collection<BiosampleDto> biosamples) {
		if(biosamples==null) return null;
		Set<String> res = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			if(b.getBiotype()==null) continue;
			String s = getMetadataValue(b, metadataName);
			if(s!=null && s.length()>0) {
				res.add(s);
			}
		}
		return res;
	}

	public PhaseDto getPhase(BiosampleDto biosample, StudyDto study, Date date) {
		return getPhase(biosample, study, date, null, null);
	}

	public PhaseDto getPhase(BiosampleDto biosample, StudyDto study, Date date, Participant p) {
		return getPhase(biosample, study, date, p, null);
	}

	public PhaseDto getPhase(BiosampleDto biosample, StudyDto study, Date date, Participant p,
			Set<Participant> participants) {
		if (biosample == null || study == null || date == null)
			return null;
		if (p != null)
			return participantService.getPhase(p, date);
		AssignmentDto assignment = getAssignment(biosample, study, date, participants);
		if (assignment == null)
			return null;
		p = studyService.getParticipantFor(study,
				participants != null ? participants : studyService.getParticipants(study), assignment);
		return participantService.getPhase(p, date);
	}

	public Duration getDuration(BiosampleDto biosample, StudyDto study, Date date) {
		AssignmentDto assignment = getAssignment(biosample, study, date);
		if (assignment == null)
			return null;
		Participant p = studyService.getParticipantFor(study, studyService.getParticipants(study), assignment);
		return assignmentService.getDuration(assignment, date, participantService.getOffsetToFollow(p, assignment));
	}

	public Duration getDisplayDuration(BiosampleDto biosample, StageDto stage, PhaseDto phase) {
		if (stage == null || stage.equals(phase.getStage()))
			return phaseService.getDisplayPhase(phase);
		return phase.getPhase()
				.minus(Duration.between(stageService.getFirstDate(phase.getStage()).toInstant(),
						stageService.getFirstDate(stage).toInstant()))
				.plus(getAssignment(biosample, phase.getStage()) == null ? Duration.ZERO
						: getAssignment(biosample, phase.getStage()).getStratification())
				.minus(getAssignment(biosample, stage) == null ? Duration.ZERO
						: getAssignment(biosample, stage).getStratification())
				.minus(stageService.getOffsetOfD0(stage));
	}

	public Duration getDisplayDuration(BiosampleDto biosample, StageDto stage, Date date) {
		AssignmentDto assignment = getAssignment(biosample, stage);
		return assignment != null ? assignmentService.getDisplayDuration(assignment, date) : null;
	}

	public Duration getDisplayDuration(BiosampleDto biosample, StudyDto study, Date date) {
		AssignmentDto assignment = getAssignment(biosample, study, date);
		return assignment != null ? assignmentService.getDisplayDuration(assignment, date) : null;
	}

	public Date getDate(BiosampleDto biosample, PhaseDto phase) {
		if (biosample == null || phase == null || phase.getStage() == null)
			return null;
		AssignmentDto assignment = getAssignment(biosample, phase.getStage());
		if (assignment != null)
			return assignmentService.getDate(assignment, phase);
		if (phase.getStage().getStudy().equals(biosample.getStudy())
				&& getAssignment(biosample.getTopParent(), phase.getStage()) != null)
			return assignmentService.getDate(getAssignment(biosample.getTopParent(), phase.getStage()), phase);
		return null;
	}

	public BiosampleDto getTopParent(Set<BiosampleDto> biosamples) {
		BiosampleDto topParent = null;
		for (BiosampleDto biosample : biosamples) {
			if (topParent == null) 
				topParent = biosample.getTopParent();
			else if (!topParent.equals(biosample.getTopParent())) return null;
		}
		return topParent;
	}

	public BiosampleDto getBiosampleInCache(Integer id) {
		return idToBiosample.get(id);
	}

	public void removeAssignment(BiosampleDto biosample, AssignmentDto assignment) {
		removeAssignment(biosample, assignment, false);
	}

	public void removeAssignment(BiosampleDto biosample, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (!biosample.getAssignments().contains(assignment))
			return;
		// remove old member
		biosample.getAssignments().remove(assignment);
		// remove child's owner
		if (!cross) {
			assignmentService.setBiosample(assignment, null, true);
		}

	}

	public void addAssignment(BiosampleDto biosample, AssignmentDto assignment) {
		addAssignment(biosample, assignment, false);
	}

	public void addAssignment(BiosampleDto biosample, AssignmentDto assignment, boolean cross) {
		// prevent endless loop
		if (biosample.getAssignments().contains(assignment))
			return;
		// add new member
		biosample.getAssignments().add(assignment);
		// update child if request is not from it
		if (!cross) {
			assignmentService.setBiosample(assignment, biosample, true);
		}
	}

	public void addBiosampleEnclosure(BiosampleDto biosample, BiosampleEnclosureDto biosampleEnclosure) {
		addBiosampleEnclosure(biosample, biosampleEnclosure, false);
	}

	public void addBiosampleEnclosure(BiosampleDto biosample, BiosampleEnclosureDto biosampleEnclosure, boolean cross) {
		// prevent endless loop
		if (biosample.getBiosampleEnclosures().contains(biosampleEnclosure))
			return;
		// add new member
		biosample.getBiosampleEnclosures().add(biosampleEnclosure);
		// update child if request is not from it
		if (!cross) {
			biosampleEnclosureService.setBiosample(biosampleEnclosure, biosample, true);
		}
	}

	public void removeBiosampleEnclosure(BiosampleDto biosample, BiosampleEnclosureDto biosampleEnclosure) {
		removeBiosampleEnclosure(biosample, biosampleEnclosure, false);
	}

	public void removeBiosampleEnclosure(BiosampleDto biosample, BiosampleEnclosureDto biosampleEnclosure,
			boolean cross) {
		// prevent endless loop
		if (!biosample.getBiosampleEnclosures().contains(biosampleEnclosure))
			return;
		// remove old member
		biosample.getBiosampleEnclosures().remove(biosampleEnclosure);
		// remove child's owner
		if (!cross) {
			biosampleEnclosureService.setBiosample(biosampleEnclosure, null, true);
		}
	}

	public void addPlannedSample(BiosampleDto biosample, PlannedSampleDto plannedSample) {
		addPlannedSample(biosample, plannedSample, false);
	}

	public void addPlannedSample(BiosampleDto biosample, PlannedSampleDto plannedSample, boolean cross) {
		// prevent endless loop
		if (biosample.getPlannedSamples().contains(plannedSample))
			return;
		// add new member
		biosample.getPlannedSamples().add(plannedSample);
		// update child if request is not from it
		if (!cross) {
			plannedSampleService.setBiosample(plannedSample, biosample, true);
		}
	}

	public void removePlannedSample(BiosampleDto biosample, PlannedSampleDto plannedSample) {
		removePlannedSample(biosample, plannedSample, false);
	}

	public void removePlannedSample(BiosampleDto biosample, PlannedSampleDto plannedSample, boolean cross) {
		// prevent endless loop
		if (!biosample.getPlannedSamples().contains(plannedSample))
			return;
		// remove old member
		biosample.getPlannedSamples().remove(plannedSample);
		// remove child's owner
		if (!cross) {
			plannedSampleService.setBiosample(plannedSample, null, true);
		}
	}

	public boolean populateFromExternalDB(BiosampleDto sample) {
		if (sample == null || sample.getSampleId() == null || sample.getSampleId().length() == 0)
			return false;
		// Make sure the id is integer
		try {
			Integer.parseInt(sample.getSampleId());
		} catch (Exception e) {
			return false;
		}
		if (sample.getBiotype() == null || sample.getBiotype().getName().equals(Constants.ANIMAL)) {
			// Load the sampleId form the animal DB
			Map<String, String> values = getAnimalDbInfo(sample.getSampleId());
			if (values != null) {
				if (sample.getBiotype() == null)
					setBiotype(sample, biotypeService.map(biotypeService.getByName(Constants.ANIMAL)));
				List<BiotypeMetadataBiosampleDto> metadatas = new ArrayList<>();
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "PO Number"), values.get("PO Number")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Delivery Date"),
						values.get("Delivery Date")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Sex"), values.get("Sex")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Type"), values.get("Type")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Birthday"), values.get("Birthday")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "TypeID"), values.get("TypeID")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "LicenseNo"), values.get("LicenseNo")));
				setMetadatas(sample, metadatas);
				return true;
			}
		}
		if (sample.getBiotype() == null || sample.getBiotype().getName().equals(Constants.HUMAN)) {
			Map<String, String> values = getHumanDbInfo(sample.getSampleId());
			if (values != null) {
				List<BiotypeMetadataBiosampleDto> metadatas = new ArrayList<>();
				int year = Calendar.getInstance().get(Calendar.YEAR);
				if (sample.getBiotype() == null)
					setBiotype(sample, biotypeService.map(biotypeService.getByName(Constants.HUMAN)));
				String yob = values.get("year_of_birth");
				String gender = values.get("gender");
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Age"),
						yob == null || yob.length() == 0 ? ""
								: "" + (Integer.parseInt(yob) > 1900 ? year - Integer.parseInt(yob) : "")));
				metadatas.add(new BiotypeMetadataBiosampleDto(sample,
						biotypeService.getMetadataByName(sample.getBiotype(), "Gender"),
						gender.equals("male") ? "M" : gender.equals("female") ? "F" : ""));
				setMetadatas(sample, metadatas);
				return true;
			}
		}
		return false;
	}

	public void setMetadata(BiosampleDto sample, BiotypeMetadataDto biotypeMetadata, String value) {
		BiotypeMetadataBiosampleDto biotypeMetadataBiosample = new BiotypeMetadataBiosampleDto();
		biotypeMetadataBiosample.setBiosample(sample);
		biotypeMetadataBiosample.setMetadata(biotypeMetadata);
		biotypeMetadataBiosample.setValue(value);
		setMetadata(sample, biotypeMetadataBiosample);
	}

	public void setMetadatas(BiosampleDto sample, List<BiotypeMetadataBiosampleDto> metadatas) {
		for (BiotypeMetadataBiosampleDto metadata : metadatas) {
			setMetadata(sample, metadata);
		}
	}

	public void setMetadata(BiosampleDto sample, BiotypeMetadataBiosampleDto biotypeMetadataBiosample) {
		BiotypeMetadataDto metadata = biotypeMetadataBiosample.getMetadata();
		String value = biotypeMetadataBiosample.getValue();
		if (value == null)
			value="";
		biotypeMetadataBiosample.setValue(value.trim());
		if (metadata.getDatatype() == DataType.LIST) {
			for (String s : MiscUtils.split(metadata.getParameters())) {
				if (s.trim().equalsIgnoreCase(value) || "".equals(value) || value == null) {
					addMetadata(sample, biotypeMetadataBiosample);
					return;
				}
			}
			throw new InvalidParameterException(
					"This value is not acceptable, please enter one of these: " + metadata.getParameters());
		} else if (metadata.getDatatype() == DataType.MULTI) {
			// Sort Multi values
			String[] v = MiscUtils.split(value, ";");
			Arrays.sort(v);
			biotypeMetadataBiosample.setValue(MiscUtils.unsplit(v, ";"));
			addMetadata(sample, biotypeMetadataBiosample);
		} else if (metadata.getDatatype() == DataType.DATE) {
			addMetadata(sample, biotypeMetadataBiosample);
		} else {
			addMetadata(sample, biotypeMetadataBiosample);
		}
	}

	private void addMetadata(BiosampleDto sample, BiotypeMetadataBiosampleDto biotypeMetadataBiosample) {
		for (BiotypeMetadataBiosampleDto metadata : sample.getMetadatas()) {
			if (metadata.getMetadata().equals(biotypeMetadataBiosample.getMetadata())) {
				metadata.setValue(biotypeMetadataBiosample.getValue());
				return;
			}
		}
		sample.getMetadatas().add(biotypeMetadataBiosample);
	}
	
	private void removeMetadata(BiosampleDto sample, BiotypeMetadataDto biotypeMetadata) {
		for (BiotypeMetadataBiosampleDto metadata : sample.getMetadatas()) {
			if (metadata.getMetadata().equals(biotypeMetadata)) {
				sample.getMetadatas().remove(metadata);
				return;
			}
		}
	}

	private Map<String, String> getHumanDbInfo(String sampleId) {
		return biosampleDao.getHumanDBInfo(sampleId);
	}

	private Map<String, String> getAnimalDbInfo(String sampleId) {
		return biosampleDao.getAnimalDBInfo(sampleId);
	}

	public List<BiosampleDto> getByStudy(StudyDto study) {
		List<BiosampleDto> samples = new ArrayList<>();
		if (study != null) {
			for (StageDto stage : study.getStages()) {
				for (AssignmentDto assignment : stage.getAssignments()) {
					BiosampleDto biosample = assignment.getBiosample();
					if (biosample != null)
						samples.add(biosample);
				}
			}
		}
		return samples;
	}

	public boolean isRemove(BiosampleDto animal, StudyDto study, Date date) {
		AssignmentDto assignment = getAssignment(animal, study, date);
		if (assignment == null)
			return false;
		return assignmentService.isRemove(assignment, date);
	}

	public boolean isRemove(BiosampleDto biosample, StudyDto study, PhaseDto phase) {
		Date date = getDate(biosample, phase);
		if (date != null)
			return isRemove(biosample, study, date);
		for (AssignmentDto a : getAssignments(biosample, phase.getStage().getStudy())) {
			if (a.getStage().compareTo(phase.getStage()) <= 0 && a.getRemoveDate() != null)
				return true;
		}
		return false;
	}

	public Set<StudyAction> getStudyActions(BiosampleDto biosample, PhaseDto phase) {
		if (phase == null || phase.getStage() == null)
			return new HashSet<StudyAction>();
		return stageService.getActions(phase.getStage(), biosample, phase);
	}

	public EnclosureDto getEnclosure(BiosampleDto biosample, StudyDto study, Date date) {
		return getEnclosure(biosample, getPhase(biosample, study, date, null));
	}

	public EnclosureDto getEnclosure(BiosampleDto biosample, PhaseDto phase) {
		for (BiosampleEnclosureDto biosampleEnclosureLink : biosample.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsPhase(biosampleEnclosureLink, phase, ContainmentType.FW))
				return biosampleEnclosureLink.getEnclosure();
		}
		return null;
	}

	/**
	 * 
	 * @param biosample
	 * @param phase
	 * @param enclosure could be null
	 * @return
	 */
	public BiosampleEnclosureDto getBiosampleEnclosure(BiosampleDto biosample, PhaseDto phase, EnclosureDto enclosure) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if ((enclosure == null || enclosure.equals(biosampleEnclosure.getEnclosure()))
					&& biosampleEnclosureService.containsPhase(biosampleEnclosure, phase, ContainmentType.FW))
				return biosampleEnclosure;
		}
		return null;
	}

	public Date getDate(BiosampleDto biosample, PhaseDto phase, StudyAction action) {
		if (phase == null || phase.getStage() == null)
			return null;
		AssignmentDto assignment = getAssignment(biosample, phase.getStage());
		return assignment == null ? null : assignmentService.getDate(assignment, phase, action);
	}

	public EnclosureDto getInsertEnclosure(BiosampleDto biosample, Date date) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.INSERT))
				return biosampleEnclosure.getEnclosure();
		}
		return null;
	}
	
	public EnclosureDto getInsertEnclosure(BiosampleDto biosample, PhaseDto phase) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsPhase(biosampleEnclosure, phase, ContainmentType.INSERT)) 
				return biosampleEnclosure.getEnclosure();
		}
		return null;
	}

	public List<AssayResultDto> getResultByPhaseAndAssayName(BiosampleDto biosample, PhaseDto phase, String assayName) {
		List<AssayResultDto> results = new ArrayList<>();
		List<AssayResultDto> resultsToRemove = new ArrayList<>();
		if (biosample == null)
			return null;
		for (AssayResultDto res : biosample.getResults()) {
			if ((phase==null || phase.equals(res.getPhase())) && (assayName==null || assayName.equals(res.getAssay().getName())))
					results.add(res);
		}
		if(results.size()>1 && Constants.PKBIOANALYSISTESTNAME.equals(assayName)) {
			for (AssayResultDto result : results) {
				if(!assayResultService.isDuplicate(result, results))
					resultsToRemove.add(result);
			}
		}
		results.removeAll(resultsToRemove);
		return results;
	}
	
	public AssayResultDto getFirstResultByPhaseAndAssayName(BiosampleDto animal, PhaseDto phase,
			String assayName) {
		List<AssayResultDto> results = getResultByPhaseAndAssayName(animal, phase, assayName);
		if(results.size()>0)
			return results.get(0);
		return null;
	}
	
	public AssayResultDto getFirstResultByAssayName(BiosampleDto sample, String assayName) {
		List<AssayResultDto> results = getResultByAssayName(sample, assayName);
		if(results.size()>0)
			return results.get(0);
		return null;
	}
	
	public AssayResultDto getResultByPhaseAndAssayNameAndParameters(BiosampleDto biosample, PhaseDto p, String assay,
			String[] parameters) {
		assert assay!=null;
		List<AssayResultDto> results = (List<AssayResultDto>) biosample.getResults();
		if(results==null) return null;
		AssayResultDto sel = null;
		loop: for (int i = 0; i < results.size(); i++) {
			AssayResultDto result = results.get(i);
			if(!assay.equals(result.getAssay().getName())) continue;
			if(p!=null && assayResultService.getInheritedPhase(result) != null && !assayResultService.getInheritedPhase(result).equals(p)) continue;
			if(parameters!=null) {
				List<AssayResultValueDto> rvs = assayResultService.getInputResultValues(result);
				for (int j = 0; j < parameters.length && j<rvs.size(); j++) {
					AssayResultValueDto resultValue = rvs.get(j);
					String v = null;
					if (resultValue != null) v = resultValue.getTextValue();
					if(CompareUtils.compare(v==null? "": v, parameters[j])!=0) continue loop;
				}
			}
			if(sel!=null && sel.getUpdDate()!=null && result.getUpdDate()!=null && sel.getUpdDate().after(result.getUpdDate())) continue;
			sel = result;
		}
		return sel;
	}

	public List<AssayResultDto> getResultByAssayName(BiosampleDto biosample, String assayName) {
		List<AssayResultDto> results = new ArrayList<>();
		if (biosample != null && assayName != null) {
			for (AssayResultDto res : biosample.getResults()) {
				if (assayName.equals(res.getAssay().getName()))
					results.add(res);
			}
		}
		return results;
	}
	
	public List<AssayResultDto> getResultByAssayNameAndStudy(BiosampleDto biosample, String assayName, StudyDto study) {
		List<AssayResultDto> results = new ArrayList<>();
		if (biosample != null && assayName != null) {
			for (AssayResultDto res : biosample.getResults()) {
				if (assayName.equals(res.getAssay().getName()) && (study==null || study.equals(res.getStudy())))
					results.add(res);
			}
		}
		return results;
	}

	public AssayResultDto getPreviousResult(BiosampleDto biosample, PhaseDto phase, String assayName) {
		if (biosample == null || phase == null || assayName == null)
			return null;

		AssayResultDto sel = null;

		for (AssayResultDto result : biosample.getResults()) {
			if (assayResultService.getFirstOutputValue(result) == null)
				continue;
			if (!assayName.equals(result.getAssay().getName()))
				continue;

			Date rDate = assayResultService.getExecutionDateCalculated(result);
			if (rDate == null)
				continue;
			if (rDate.compareTo(getDate(biosample, phase)) >= 0)
				continue;

			if (sel == null) {
				sel = result;
			} else if (rDate.compareTo(assayResultService.getExecutionDateCalculated(sel)) > 0) {
				sel = result;
			}
		}

		return sel;
	}

	public AssignmentDto getLastAssignment(BiosampleDto biosample, StudyDto study) {
		ArrayList<AssignmentDto> assignments = new ArrayList<>(getAssignments(biosample, study));
		if (assignments.size() == 0)
			return null;
		Collections.sort(assignments, new Comparator<AssignmentDto>() {
			@Override
			public int compare(AssignmentDto o1, AssignmentDto o2) {
				return o1.getStage().compareTo(o2.getStage());
			}
		});
		return assignments.get(assignments.size() - 1);
	}
	
	public AssignmentDto getFirstAssignment(BiosampleDto row, StudyDto study) {
		ArrayList<AssignmentDto> assignments = new ArrayList<>(getAssignments(row, study));
		if(assignments.size()==0)
			return null;
		Collections.sort(assignments, new Comparator<AssignmentDto>() {
			@Override
			public int compare(AssignmentDto o1, AssignmentDto o2) {
				return o1.getStage().compareTo(o2.getStage());
			}
		});
		return assignments.get(0);
	}

	public List<AssignmentDto> getAssignments(BiosampleDto biosample, StudyDto study) {
		List<AssignmentDto> result = new ArrayList<>();
		biosample.getAssignments().forEach(s -> {
			if (s.getStage() != null && s.getStage().getStudy().equals(study) && !result.contains(s))
				result.add(s);
		});
		return result;
	}

	public AdministrationDto getAdministration(BiosampleDto animal, NamedTreatmentDto namedTreatment, PhaseDto phase) {
		for (AdministrationDto adm : animal.getAdministrations()) {
			if (namedTreatment.equals(adm.getNamedTreatment()) && phase.equals(adm.getPhase())) {
				return adm;
			}
		}
		return null;
	}

	public void removeResult(BiosampleDto biosample, AssayResultDto result) {
		removeResult(biosample, result, false);
	}
	
	public void removeResults(BiosampleDto biosample, List<AssayResultDto> toRemove) {
		for(AssayResultDto result : toRemove) {
			removeResult(biosample, result);
		}
	}

	public void removeResult(BiosampleDto biosample, AssayResultDto result, boolean cross) {
		// prevent endless loop
		if (!biosample.getResults().contains(result))
			return;
		// remove old member
		biosample.getResults().remove(result);
		// delte result, a result without Biosample should not exists.
		assayResultService.delete(result);
	}

	public void addResult(BiosampleDto biosample, AssayResultDto result) {
		addResult(biosample, result, false);
	}

	public void addResult(BiosampleDto biosample, AssayResultDto result, boolean cross) {
		// prevent endless loop
		if (biosample.getResults().contains(result))
			return;
		// add new member
		biosample.getResults().add(result);
		// update child if request is not from it
		if (!cross) {
			assayResultService.setBiosample(result, biosample, true);
		}
	}

	public void removeAdministration(BiosampleDto biosample, AdministrationDto administration) {
		removeAdministration(biosample, administration, false);
	}

	public void removeAdministration(BiosampleDto biosample, AdministrationDto administration, boolean cross) {
		// prevent endless loop
		if (!biosample.getAdministrations().contains(administration))
			return;
		// remove old member
		biosample.getAdministrations().remove(administration);
		// remove child's owner
		if (!cross) {
			administrationService.setBiosample(administration, null, true);
		}

	}

	public void addAdministration(BiosampleDto biosample, AdministrationDto administration) {
		addAdministration(biosample, administration, false);
	}

	public void addAdministration(BiosampleDto biosample, AdministrationDto administration, boolean cross) {
		// prevent endless loop
		if (biosample.getAdministrations().contains(administration))
			return;
		// add new member
		biosample.getAdministrations().add(administration);
		// update child if request is not from it
		if (!cross) {
			administrationService.setBiosample(administration, biosample, true);
		}
	}

	public List<AssayResultValueDto> getResultByAssayAttribute(BiosampleDto biosample, AssayAttributeDto attribute) {
		List<AssayResultValueDto> res = new ArrayList<>();
		for (AssayResultDto result : getResultByAssayName(biosample, attribute.getAssay().getName())) {
			AssayResultValueDto val = assayResultService.getResultValue(result, attribute);
			if (val != null)
				res.add(val);
		}
		return res;
	}

	public void setLocation(BiosampleDto biosample, LocationDto location) {
		setLocPos(biosample, location, biosample.getLocationPos() == null ? -1 : biosample.getLocationPos(), false);
	}

	public void setLocation(BiosampleDto biosample, LocationDto location, boolean cross) {
		setLocPos(biosample, location, biosample.getLocationPos() == null ? -1 : biosample.getLocationPos(), cross);
	}
	
	public void setLocPos(BiosampleDto biosample, LocationDto loc, int pos) {
		setLocPos(biosample, loc, pos, false);
	}

	@SuppressWarnings("deprecation")
	public void setLocPos(BiosampleDto biosample, LocationDto loc, int pos, boolean cross) {
		// Return if nothing is changed
		if (loc == biosample.getLocation() && (loc == null || biosample.getLocationPos() == pos)) {
			return;
		}

		// remove from the old owner
		if (biosample.getLocation() != null && loc != biosample.getLocation() && !cross)
			locationService.removeBiosample(biosample.getLocation(), biosample, true);
		// set new owner
		biosample.setLocation(loc);
		biosample.setLocationPos(loc == null ? -1 : pos);

		// set myself to new owner
		if (biosample != null && !cross)
			locationService.addBiosample(loc, biosample, true);
	}
	
	public void setParent(BiosampleDto biosample, BiosampleDto parent) {
		setParent(biosample, parent, true);
	}

	@SuppressWarnings("deprecation")
	public void setParent(BiosampleDto biosample, BiosampleDto parent, boolean updateDoubleRelationship) {
		if(parent==biosample.getParent()) return;

		if(biosample==parent) {
			System.err.println("Cannot set the parent to itself");
			return;
		}

		if(updateDoubleRelationship && biosample.getParent()!=null) {
			biosample.getParent().getChildren().remove(biosample);
		}

		//Set the parent
		biosample.setParent(parent);


		if(updateDoubleRelationship) {
			if(biosample.getParent()!=null) {
				if(parent.getInheritedPhase()!=null) {
					biosample.setInheritedPhase(parent.getInheritedPhase());
				}
				biosample.getParent().getChildren().add(biosample);
				biosample.setTopParent(biosample.getParent().getTopParent());
			} else {
				biosample.setTopParent(biosample);
			}
		}
	}

	public GroupDto getGroup(List<BiosampleDto> biosamples, StudyDto study, Date date) {
		GroupDto group = null;
		Boolean first = true;
		for (BiosampleDto biosample : biosamples) {
			AssignmentDto assignment = getAssignment(biosample, study, date);
			if (assignment == null)
				return null;
			GroupDto g = assignment.getSubgroup().getGroup();
			if (group == null && first) {
				group = g;
				first = false;
			} else {
				if (!g.equals(group)) {
					return null;
				}
			}
		}
		return group;
	}
	
	public GroupDto getGroup(BiosampleDto biosample, StudyDto study, Date date) {
		AssignmentDto assignment = getAssignment(biosample, study, date);
		if (assignment == null)
			return null;
		return assignment.getSubgroup().getGroup();
	}
	
	public GroupDto getGroup(BiosampleDto animal, PhaseDto phase) {
		if (phase == null || phase.getStage() == null) return null;
		return stageService.getGroup(phase.getStage(), animal);
	}

	public boolean addEnclosure(BiosampleDto biosample, EnclosureDto enclosure, Date dateIn) {
		return addEnclosure(biosample, enclosure, getPhase(biosample, enclosure.getStudy(), dateIn));
	}

	public boolean addEnclosure(BiosampleDto biosample, EnclosureDto enclosure, PhaseDto phaseIn) {
		PhaseDto phaseOut = null;
		BiosampleEnclosureDto previous = null;

		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (enclosure.equals(biosampleEnclosure.getEnclosure())
					&& biosampleEnclosureService.containsPhase(biosampleEnclosure, phaseIn, ContainmentType.FW)) {
				if (phaseIn.compareTo(biosampleEnclosure.getPhaseOut()) == 0) {
					for (BiosampleEnclosureDto biosampleEnclosureOut : biosample.getBiosampleEnclosures()) {
						if (biosampleEnclosureOut.getPhaseIn().compareTo(phaseIn) == 0) {
							biosampleEnclosureService.setPhaseOut(biosampleEnclosure,
									biosampleEnclosureOut.getPhaseOut());
							biosampleEnclosureService.remove(biosampleEnclosureOut);
							break;
						}
					}
				}
				return true;
			}
		}

		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (phaseIn.compareTo(biosampleEnclosure.getPhaseIn()) == 0) {
				biosampleEnclosureService.setEnclosure(biosampleEnclosure, enclosure);
				try {					
					biosampleEnclosureService.save(biosampleEnclosure);
				} catch(Exception e) {
					e.printStackTrace();
					AbstractService.clearTransient(false);
				}finally {
					AbstractService.clearSavedItem();
					AbstractService.clearTransient(true);
				}
				return true;
			}
			if (phaseIn.compareTo(biosampleEnclosure.getPhaseIn()) > 0
					&& phaseIn.compareTo(biosampleEnclosure.getPhaseOut()) <= 0
					&& phaseIn.getStage().getStudy().equals(biosampleEnclosure.getPhaseIn().getStage().getStudy())) {
				previous = biosampleEnclosure;
			}
			if ((phaseOut == null && biosampleEnclosure.getPhaseIn().compareTo(phaseIn) > 0)
					|| (phaseOut != null && phaseOut.compareTo(biosampleEnclosure.getPhaseIn()) > 0))
				phaseOut = biosampleEnclosure.getPhaseIn();
		}

		if (previous != null) {
			biosampleEnclosureService.setPhaseOut(previous, phaseIn);
			AbstractService.clearSavedItem();
		}
		BiosampleEnclosureDto biosampleEnclosure = new BiosampleEnclosureDto();
		biosampleEnclosureService.setBiosample(biosampleEnclosure, biosample);
		biosampleEnclosureService.setEnclosure(biosampleEnclosure, enclosure);
		biosampleEnclosureService.setPhaseIn(biosampleEnclosure, phaseIn);
		if (phaseOut != null)
			biosampleEnclosureService.setPhaseOut(biosampleEnclosure, phaseOut);
		AbstractService.clearSavedItem();
		return true;
	}

	public boolean removeEnclosure(BiosampleDto biosample, EnclosureDto enclosure, PhaseDto phaseOut) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (biosampleEnclosure.getPhaseOut() == null && biosampleEnclosure.getEnclosure().equals(enclosure)) {
				biosampleEnclosureService.setPhaseOut(biosampleEnclosure, phaseOut);
				return true;
			}
		}
		return false;
	}

	public boolean removeEnclosure(BiosampleDto biosample, PhaseDto phaseOut) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if ((biosampleEnclosure.getPhaseOut() == null || biosampleEnclosure.getPhaseOut().compareTo(phaseOut) > 0)
					&& biosampleEnclosure.getPhaseIn().compareTo(phaseOut) <= 0
					&& biosampleEnclosure.getPhaseIn().getStage().getStudy().equals(phaseOut.getStage().getStudy())) {
				if (phaseOut.equals(biosampleEnclosure.getPhaseIn())) {
					try {
						biosample.getBiosampleEnclosures().remove(biosampleEnclosure);
						biosampleEnclosure.getEnclosure().getBiosampleEnclosures().remove(biosampleEnclosure);
					} catch (Exception e) {
						return false;
					}
				} else {
					biosampleEnclosureService.setPhaseOut(biosampleEnclosure, phaseOut);
				}
				return true;
			}
		}
		return false;
	}

	public List<EnclosureDto> getEnclosures(BiosampleDto biosample, StudyDto study) {
		ArrayList<EnclosureDto> res = new ArrayList<>();
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (study == null || study.equals(biosampleEnclosure.getStudy()))
				res.add(biosampleEnclosure.getEnclosure());
		}
		return res;
	}

	public EnclosureDto getEnclosure(BiosampleDto biosample, Date date) {
		return getEnclosure(biosample, date, null);
	}

	public EnclosureDto getEnclosure(BiosampleDto biosample, Date date, Set<Participant> participants) {
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.FW, participants))
				return biosampleEnclosure.getEnclosure();
		}
		return null;
	}

	public void setState(BiosampleDto biosample, Status status) throws Exception {
		setState(biosample, status, null, null);
	}

	@SuppressWarnings("deprecation")
	public void setState(BiosampleDto biosample, Status status, PhaseDto phase, Date dod) throws Exception {
		for(AssignmentDto a : biosample.getAssignments()) {
			if(dod != null && assignmentService.getFirstDate(a, assignmentService.getOffsetToFollow(a)).after(dod))
				throw new Exception("Animal "+biosample.getSampleId()+" is assigned to the study "+a.getStage().getStudy().getStudyId()+" after the date you would like to set up."
						+ "\nPlease remove this assignment first.");
		}
		if (status == biosample.getState())
			return;
		biosample.setState(status);
		if (status == null || status.isAvailable()) {
			setTerminationExecutionDate(biosample, null);
			biosample.setEndPhase(null);
		} else {
			setTerminationExecutionDate(biosample, dod);
			biosample.setEndPhase(phase);
		}
	}

	public List<BiosampleDto> getParentHierarchy(BiosampleDto biosample) {
		LinkedList<BiosampleDto> res = new LinkedList<>();
		BiosampleDto current = biosample;
		do {
			res.addFirst(current);
			current = current.getParent();
		} while(current!=null && current!=current.getParent());
		return res;
	}
	
	public Set<BiosampleDto> getHierarchy(BiosampleDto biosample, HierarchyMode attachedSamples) {
		return getHierarchy(biosample, attachedSamples, null);
	}

	public Set<BiosampleDto> getHierarchy(BiosampleDto biosample, HierarchyMode mode, StudyDto study) {
		if (HierarchyMode.AS_STUDY_DESIGN.equals(mode) && study == null)
			return null;
		Set<BiosampleDto> res = new LinkedHashSet<>();
		if (mode == HierarchyMode.ALL || mode == HierarchyMode.ALL_MAX2) {
			BiosampleDto b = biosample.getParent();
			while (b != null) {
				res.add(b);
				b = b.getParent();
			}
			getHierarchyRec(biosample, HierarchyMode.CHILDREN, res, biosample, mode == HierarchyMode.ALL ? 99 : 2,
					null);
			return res;

		} else if (mode == HierarchyMode.SIEBLINGS) {
			if (biosample.getParent() != null) {
				res.addAll(biosample.getParent().getChildren());
			}
			res.remove(biosample);
		} else if (mode == HierarchyMode.PARENTS) {
			BiosampleDto b = biosample.getParent();
			while (b != null) {
				res.add(b);
				b = b.getParent();
			}
		} else {
			getHierarchyRec(biosample, mode, res, biosample, 99, study);
			if (mode != HierarchyMode.AS_STUDY_DESIGN && mode != HierarchyMode.ATTACHED_SAMPLES)
				res.remove(biosample);
		}
		return res;
	}

	private void getHierarchyRec(BiosampleDto biosample, HierarchyMode mode, Set<BiosampleDto> res,
			BiosampleDto actualSample, int maxDepth, StudyDto study) {
		if (maxDepth < 0)
			return;
		if (res.contains(actualSample))
			return; // avoid loops, but this should never happen

		if (mode == HierarchyMode.TERMINAL) {
			if (actualSample.getChildren().size() == 0) {
				res.add(actualSample);
			}
		} else if (mode == HierarchyMode.AS_STUDY_DESIGN) {
			if (actualSample.equals(biosample) || actualSample.getAttachedSampling() != null
					|| assignedToStudy(actualSample, study)) {
				res.add(actualSample);
			}
		} else if (mode == HierarchyMode.ATTACHED_SAMPLES) {
			if (actualSample.equals(biosample) || actualSample.getAttachedSampling() != null) {
				res.add(actualSample);
			} else if (assignedToStudy(actualSample, study)) {
				return;
			}
		} else if (mode == HierarchyMode.CHILDREN || mode == HierarchyMode.CHILDREN_NOT_ATTACHED) {
			res.add(actualSample);
		}

		if (actualSample.getChildren() != null && actualSample.getChildren().size() > 0) {
			List<BiosampleDto> children = new ArrayList<>(actualSample.getChildren());
			Collections.sort(children);
			for (BiosampleDto child : children) {
				if (mode == HierarchyMode.CHILDREN_NOT_ATTACHED && assignedToStudy(child, study))
					continue;
				getHierarchyRec(biosample, mode, res, child, maxDepth - 1, study);
			}
		}
	}

	public boolean assignedToStudy(BiosampleDto biosample, StudyDto study) {
		if (study == null)
			return false;
		if(biosample.getParent()==null)
			for (AssignmentDto assignment : biosample.getAssignments()) {
				if (study.equals(assignment.getStage().getStudy()))
					return true;
			}
		return false;
	}

	public Boolean setCreationExecutionDate(BiosampleDto biosample, Date date) {
		if (biosample.getCreationExecution() == null) {
			if (date == null)
				return false;
			biosample.setCreationExecution(new Execution());
		}
		biosample.setInheritedDate(date);
		return true;
	}

	public Duration getCreationDeviation(BiosampleDto biosample) {
		return biosample.getCreationExecution() == null ? null
				: executionService.getDeviation(biosample.getCreationExecution(), biosample,
						biosample.getInheritedPhase());
	}

	@SuppressWarnings("deprecation")
	public void setBiotype(BiosampleDto biosample, BiotypeDto biotype) {
		biosample.setBiotype(biotype);
		if (biotype != null && biotype.getContainerType() != null && biosample.getContainerType() == null) {
			if (biotype.getIsAbstract()) {
				setContainerType(biosample, null);
			} else {
				setContainerType(biosample, biotype.getContainerType());
			}
		}

	}

	public Set<String> getTypes(Set<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<String> res = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			String s = b.getBiotype() == null ? "" : b.getBiotype().getName();
			res.add(s);
		}
		return res;
	}

	public StudyDto getStudy(Collection<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<StudyDto> res = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b == null)
				continue;
			if (b.getStudy() != null)
				res.add(b.getStudy());
			if (b.getInheritedPhase() != null && b.getInheritedPhase().getStage() != null)
				res.add(b.getInheritedPhase().getStage().getStudy());
		}
		if (res.size() == 1)
			return res.iterator().next();
		return null;
	}

	public BiotypeDto getBiotype(Collection<BiosampleDto> biosamples) {
		Set<BiotypeDto> res = getBiotypes(biosamples);
		if(res.size()==1) return res.iterator().next();
		return null;
	}
	
	public Set<GroupDto> getCreationGroups(Set<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		SortedSet<GroupDto> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			if (getCreationGroup(b) != null)
				res.add(getCreationGroup(b));
		}
		return res;
	}

	public Set<GroupDto> getGroups(Collection<BiosampleDto> biosamples) {
		if(biosamples==null) return null;
		SortedSet<GroupDto> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			for (AssignmentDto assignment : b.getAssignments()) {
				res.add(getGroup(b, assignment.getStage()));
			}
		}
		return res;
	}
	
	public SortedSet<GroupDto> getGroups(Collection<BiosampleDto> biosamples, Date date) {
		if(biosamples==null) return null;
		SortedSet<GroupDto> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			AssignmentDto assignment = getAssignment(b, date);
			if(assignment != null)
				res.add(assignment.getSubgroup().getGroup());
		}
		return res;
	}
	
	public GroupDto getGroup(Collection<BiosampleDto> biosamples) {
		Set<GroupDto> res = getGroups(biosamples);
		if(res.size()==1) return res.iterator().next();
		return null;
	} 
	
	public GroupDto getGroup(BiosampleDto biosample, StageDto stage) {
		if (stage == null)
			return null;
		return stageService.getGroup(stage, biosample);
	}

	public GroupDto getCreationGroup(BiosampleDto biosample) {
		return getCreationSubGroup(biosample) == null ? null : getCreationSubGroup(biosample).getGroup();
	}

	public GroupDto getCreationGroup(Set<BiosampleDto> biosamples) {
		Set<GroupDto> res = getCreationGroups(biosamples);
		if (res.size() == 1)
			return res.iterator().next();
		return null;
	}

	public SubGroupDto getCreationSubGroup(BiosampleDto biosample) {
		PhaseDto p = biosample.getInheritedPhase() == null
				? getClosestPhase(biosample.getTopParent(), biosample.getStudy(), getCreationExecutionDate(biosample))
				: biosample.getInheritedPhase();
		return p == null ? null : getSubGroup(getTopParentInSameStudy(biosample, p.getStage().getStudy()), p);
	}

	public SubGroupDto getSubGroup(BiosampleDto biosample, PhaseDto phase) {
		return phase != null && phase.getStage() != null ? stageService.getSubGroup(phase.getStage(), biosample) : null;
	}

	public SubGroupDto getSubGroup(BiosampleDto biosample, StageDto stage) {
		return stage != null ? stageService.getSubGroup(stage, biosample) : null;
	}

	public BiosampleDto getTopParentInSameStudy(BiosampleDto biosample, StudyDto study) {
		BiosampleDto top = biosample.getTopParent();
		// Skip going to the hierarchy if there is not study, or if the study matches
		if (study == null || assignedToStudy(top, study))
			return top;
		// If not, go up until we find a parent within the same study
		top = biosample;
		while (top.getParent() != null && assignedToStudy(top, study)) {
			top = top.getParent();
		}
		return top;
	}

	public PhaseDto getClosestPhase(BiosampleDto biosample, StudyDto study, Date date) {
		AssignmentDto assignment = getAssignment(biosample, study, date);
		if (assignment == null)
			return null;
		return assignmentService.getClosestPhase(assignment, date, getOffsetToFollow(biosample, assignment));
	}

	public Duration getOffsetToFollow(BiosampleDto biosample, AssignmentDto assignment) {
		if (assignment.getStage().isDynamic()) {
			// dynamic does not follow previous offsets
			return Duration.ZERO;
		} else {
			if (getAssignment(biosample, assignment.getStage().getPreviousStage()) == null) {
				// probably we should not end up here
				return Duration.ZERO;
			} else {
				return getOffset(biosample, getAssignment(biosample, assignment.getStage().getPreviousStage()));
			}
		}
	}

	public Duration getOffset(BiosampleDto biosample, AssignmentDto assignment) {
		if (assignment.getStage().isDynamic()) {
			return assignment.getStratification();
		} else {
			if (getAssignment(biosample, assignment.getStage().getPreviousStage()) == null) {
				return Duration.ZERO;
			} else {
				return getOffset(biosample, getAssignment(biosample, assignment.getStage().getPreviousStage()));
			}
		}
	}

	public PhaseDto getPhase(Collection<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<PhaseDto> res = getPhases(biosamples);
		if (res.size() == 1)
			return res.iterator().next();
		return null;
	}

	public Set<PhaseDto> getPhases(Collection<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<PhaseDto> res = new LinkedHashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b == null)
				continue;
			res.add(b.getInheritedPhase());
		}
		return res;
	}

	public Set<StudyDto> getStudies(Collection<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<StudyDto> res = new LinkedHashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b == null)
				continue;
			res.addAll(getStudies(b));
		}
		return res;
	}

	public Set<StudyDto> getStudies(BiosampleDto biosample) {
		if (biosample == null)
			return null;
		Set<StudyDto> res = new LinkedHashSet<>();
		
		for (AssignmentDto a : biosample.getAssignments()) {
			if (a == null)
				continue;
			res.add(a.getStage().getStudy());
		}
		return res;
	}

	public Status getStatus(Set<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		Set<Status> res = new LinkedHashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b != null)
				res.add(b.getState());
		}
		return res.size() == 1 ? res.iterator().next() : null;
	}

	public Set<BiosampleDto> getTopParents(Collection<BiosampleDto> biosamples) {
		Set<BiosampleDto> res = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			res.add(b.getTopParent());
		}
		return res;
	}

	public Set<BiosampleDto> getParents(Collection<BiosampleDto> biosamples) {
		Set<BiosampleDto> res = new LinkedHashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b.getParent() != null)
				res.add(b.getParent());
		}
		return res;
	}

	public SortedSet<LocationDto> getLocations(Collection<BiosampleDto> biosamples) {
		SortedSet<LocationDto> locations = new TreeSet<>();
		for (BiosampleDto animal : biosamples) {
			LocationDto loc = animal.getLocation();
			if (loc != null) {
				if (!locations.contains(loc)) {
					locations.add(loc);
				}
			}
		}
		return locations;
	}

	public SortedSet<BiotypeDto> getBiotypes(Collection<BiosampleDto> biosamples) {
		if (biosamples == null)
			return null;
		SortedSet<BiotypeDto> biotypes = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			if (b != null && b.getBiotype() != null)
				biotypes.add(b.getBiotype());
		}
		return biotypes;
	}

	public String getInfos(BiosampleDto biosample, EnumSet<InfoFormat> dataFormat, InfoSize infoSize) {
		return getInfos(Collections.singletonList(biosample), dataFormat, infoSize);
	}

	public String getInfos(Collection<BiosampleDto> biosamples, EnumSet<InfoFormat> dataFormat, InfoSize infoSize) {
		return getInfos(biosamples, dataFormat, infoSize, null);
	}
	
	public String getInfos(BiosampleDto biosample, EnumSet<InfoFormat> dataFormat) {
		return getInfos(Collections.singletonList(biosample), dataFormat, InfoSize.ONELINE);
	}

	/**
	 * Find shared metadata
	 * 
	 * @param biosamples
	 * @return
	 */
	public String getInfos(Collection<BiosampleDto> biosamples, EnumSet<InfoFormat> dataFormat, InfoSize infoSize,
			List<String> names) {
		if (biosamples == null)
			return null;
		StringBuilder sb = new StringBuilder();

		Set<BiotypeDto> types = getBiotypes(biosamples);
		String separator1 = infoSize == InfoSize.ONELINE ? " " : "\n"; // Separator between different items (ex: groups
																		// and metadata)
		String separator2 = infoSize == InfoSize.EXPANDED ? "\n" : " "; // Separator between similar items (ex:
																		// metadata)

		// STUDY Display
		if (dataFormat.contains(InfoFormat.STUDY)) {

			// Add the study
			StudyDto study = getStudy(biosamples);
			if (study != null) {
				sb.append(study.getStudyId()
						+ (study.getIvv() != null && study.getIvv().length() > 0 ? " (" + study.getIvv() + ")" : "")
						+ separator1);
			}

			// Add the phase
			PhaseDto phase = getPhase(biosamples);
			if (phase != null) {
				sb.append(phaseService.getShortName(phase) + separator1);
			}
		}

		// TOPIDNAME Display
		if (dataFormat.contains(InfoFormat.TOPIDNAMES)) {
			boolean first = true;
			Set<BiosampleDto> tops = getTopParents(biosamples);
			tops.removeAll(biosamples);

			if (tops.size() == 0 || tops.size() > 4) {
				// skip
			} else {
				StudyDto study = getStudy(tops);
				if (study != null) {
					for (BiosampleDto b : tops) {
						AssignmentDto lastAssignment = getLastAssignment(b, study);
						if (first)
							first = false;
						else
							sb.append(separator2);
						sb.append(b.getSampleId());
						if (infoSize == InfoSize.CUSTOM && lastAssignment != null) {
							String nameInStudy = lastAssignment.getName();
							if (nameInStudy != null && !nameInStudy.isEmpty())
								sb.append(separator2).append("[").append(nameInStudy).append("]");
						}
					}
				}
				sb.append(separator1);
			}
		}

		// SAMPLEID Display
		if (dataFormat.contains(InfoFormat.SAMPLEID) && biosamples.size() == 1) {
			BiosampleDto b = biosamples.iterator().next();
			sb.append(b.getSampleId() + separator1);
		}

		// TYPE Display
		if (dataFormat.contains(InfoFormat.BIOTYPE)) {
			if (types.size() == 1) {
				String s = types.iterator().next().getName();
				sb.append(s + separator2);
			}
		}

		// SAMPLENAME Display
		if (dataFormat.contains(InfoFormat.SAMPLENAME)) {
			HashSet<String> map = new HashSet<String>();
			for (BiosampleDto b : biosamples) {
				if (b.getBiotype() != null && b.getBiotype().getNameLabel() != null && b.getSampleId() != null) {
					map.add(b.getSampleId());
				} else {
					map.add("");
				}
			}
			if (map.size() == 1 && map.iterator().next().length() > 0) {
				sb.append(map.iterator().next() + separator2);
			}
		}

		// LOCALID Display
		if (dataFormat.contains(InfoFormat.LOCALID) && biosamples.size() == 1) {
			BiosampleDto b = biosamples.iterator().next();
			sb.append(b.getLocalId() + " ");
		}

		// Find shared metadata
		StringBuilder sb2 = new StringBuilder();
		if (dataFormat.contains(InfoFormat.METATADATA)) {

			SetHashMap<String, String> map = new SetHashMap<>();
			for (BiosampleDto b : biosamples) {
				if (b.getBiotype() == null)
					continue;
				for (BiotypeMetadataDto bm : b.getBiotype().getMetadatas()) {
					if (bm.getHidefromdisplay() || bm.getDatatype() == DataType.D_FILE
							|| bm.getDatatype() == DataType.FILES || bm.getDatatype() == DataType.LARGE)
						continue;
					String s = getMetadataValue(b, bm);
					if (s == null || s.length() == 0 || (names != null && !names.contains(bm.getName())))
						continue;
					map.add(bm.getName(), s + biotypeMetadataService.extractUnit(bm));
				}
			}
			int count = 0;
			for (String mt : map.keySet()) {
				Set<String> l = map.get(mt);
				String val = l.size() == 1 ? l.iterator().next() : null;
				if (val != null && val.length() > 0) {
					sb2.append(val + separator2);
					if (++count > 6)
						break;
				}
			}
		}
		// Amount
		if (dataFormat.contains(InfoFormat.AMOUNT)) {
			if (biosamples.size() == 1) {
				for (BiosampleDto b : biosamples) {
					Amount amount = getAmountAndUnit(b);
					if (amount != null) {
						sb.append(amount.toString() + separator2);
						break;
					}
				}
			}
		}

		boolean printParentSampleName = dataFormat.contains(InfoFormat.PARENT_SAMPLENAME);
		boolean printParentLocalId = dataFormat.contains(InfoFormat.PARENT_LOCALID);
		if (printParentSampleName || printParentLocalId) {
			// Inherited Parents??
			{
				SetHashMap<String, String> map = new SetHashMap<>();
				for (BiosampleDto b : biosamples) {
					if (b.getBiotype() != null && b.getBiotype().getParent() != null && b.getParent() != null
							&& b.getBiotype().getParent().equals(b.getParent().getBiotype())) {
						if (printParentSampleName) {
							if (b.getParent().getBiotype().getNameLabel() != null && b.getParent().getSampleId() != null) {
								map.add("_name", b.getParent().getSampleId());
							}
						} else if (printParentLocalId) {
							if (b.getParent().getLocalId() != null) {
								map.add("_localdId", b.getParent().getLocalId());
							}
						}
					}
				}
				for (String mt : map.keySet()) {
					Set<String> l = map.get(mt);
					String val = l.size() == 1 ? l.iterator().next() : null;
					if (val != null && val.length() > 0)
						sb2.append(l.iterator().next() + separator2);

				}
			}
		}
		if (sb2.length() > 0) {
			sb.append(sb2.substring(0, sb2.length() - 1) + separator1);
		}

		// Find shared metadata
		sb2 = new StringBuilder();
		if (dataFormat.contains(InfoFormat.PARENT_METATADATA)) {
			SetHashMap<String, String> map = new SetHashMap<>();
			for (BiosampleDto b : getParents(biosamples)) {
				if (b.getBiotype() != null) {
					for (BiotypeMetadataDto m : b.getBiotype().getMetadatas()) {
						if (m.getDatatype() == DataType.D_FILE)
							continue;
						else if (m.getDatatype() == DataType.FILES)
							continue;
						else if (m.getDatatype() == DataType.LARGE)
							continue;
						else if (m.getHidefromdisplay())
							continue;
						map.add(m.getName(), getMetadataValue(b, m) + biotypeMetadataService.extractUnit(m));
					}
				}
			}
			int count = 0;
			for (String mt : map.keySet()) {
				Set<String> l = map.get(mt);
				String val = l.size() == 1 ? l.iterator().next() : null;
				if (val != null && val.length() > 0) {
					sb2.append(val + separator2);
					if (++count > 6)
						break;
				}
			}
		}

		// Comments
		if (dataFormat.contains(InfoFormat.COMMENTS)) {
			String val = null;
			for (BiosampleDto b : biosamples) {
				String v = b.getComments();
				if (val != null && !val.equals(v)) {
					val = null;
					break;
				}
				val = v;
			}
			if (val != null) {
				sb.append(val + separator1);
			}
		}

		if (dataFormat.contains(InfoFormat.LOCATION)) {
			// Location
			Set<LocationDto> locations = getLocations(biosamples);
			if (locations.size() == 1) {
				sb.append("[" + locations.iterator().next() + "]" + separator1);
			} else if (locations.size() > 1) {
				sb.append("[" + locations.size() + " Locs]" + separator1);
			} else {
				if (types.size() != 1) {
					sb.append(separator1);
				}
			}
		}

		String res = sb.toString();
		while (res.startsWith(separator1))
			res = res.substring(separator1.length());
		while (res.endsWith(separator1))
			res = res.substring(0, res.length() - separator1.length());

		return res;
	}

	public Amount getAmountAndUnit(BiosampleDto b) {
		return b.getBiotype() == null || b.getBiotype().getAmountUnit() == null ? null
				: new Amount(b.getAmount(), b.getBiotype().getAmountUnit());
	}

	public String getSpecies(BiosampleDto biosample) {
		String type = getMetadataValue(biosample, Constants.TYPE_METADATA);
		if (type == null)
			return null;
		return type.substring(0, type.indexOf("/"));
	}

	public String getLocationString(BiosampleDto biosample, LocationFormat format, User user) {
		StringBuilder sb = new StringBuilder();
		LocationDto location = biosample.getLocation();
		if (location != null) {
			switch (format) {
			case FULL_POS:
				sb.append(locationService.getHierarchyFull(location));
				break;
			case MEDIUM_POS:
				sb.append(locationService.getHierarchyMedium(location));
				break;
			case NAME_POS:
				sb.append(location.getName());
				break;
			}

			if (biosample.getLocation().getLabeling() != LocationLabeling.NONE) {
				sb.append(":" + locationLabelingService.formatPosition(location.getLabeling(), location,
						biosample.getLocationPos()));
			}
		}
		return sb.toString();
	}
	
	public List<Container> getContainers(List<String> containerIds) {
		try {
			return getContainers(queryBiosamples(BiosampleQuery.createQueryForContainerIds(containerIds), null), true);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Container> getContainers(Collection<BiosampleDto> biosamples) {
		return getContainers(biosamples, false);
	}

	public List<Container> getContainers(Collection<BiosampleDto> biosamples, boolean createFakeContainerForEmptyOnes) {
		if (biosamples == null)
			return null;
		List<Container> res = new ArrayList<>();
		HashSet<Container> seen = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			if (b == null)
				continue;
			Container c = b.getContainer();
			if (c == null) {
				if (createFakeContainerForEmptyOnes) {
					c = new Container();
					setContainer(b, c);
					res.add(c);
				}
			} else if (!seen.contains(c)) {
				res.add(c);
				seen.add(c);
			}
		}
		return res;
	}

	public Integer getBlocNo(BiosampleDto biosample) {
		return containerService.getBlocNo(biosample.getContainerType(), biosample.getContainerId());
	}

	public int getRow(BiosampleDto biosample) {
		if (biosample.getLocation() == null)
			return 0;
		return locationService.getRow(biosample.getLocation(), biosample.getLocationPos());
	}

	public int getCol(BiosampleDto biosample) {
		if (biosample.getLocation() == null)
			return 0;
		return locationService.getCol(biosample.getLocation(), biosample.getLocationPos());
	}

	public boolean isAbstract(BiosampleDto biosample) {
		return biosample.getBiotype() != null && biosample.getBiotype().getIsAbstract();
	}

	public BiosampleDto getBiosampleByStudyAndSampleId(Integer studyId, String sampleId) {
		List<BiosampleDto> biosamples = getBiosamplesByStudyAndSampleId(studyId, Collections.singletonList(sampleId));
		return biosamples.size() == 1 ? biosamples.get(0) : null;
	}

	public List<BiosampleDto> getBiosamplesByStudyAndSampleId(Integer studyId, List<String> animalIdOrNo) {
		return map(biosampleDao.getBiosampleByStudyAndSampleId(studyId, animalIdOrNo));
	}


	public boolean computeFormula(Set<BiosampleDto> biosamples) {
		Map<BiotypeDto, List<BiosampleDto>> map = mapBiotype(biosamples);
		boolean updated = false;
		for (BiotypeDto biotype : map.keySet()) {
			// Loop though attributes of type formula
			for (BiotypeMetadataDto bm : biotype.getMetadatas()) {
				if (bm.getDatatype() != DataType.FORMULA)
					continue;
				String formula = bm.getParameters();
				Expression e;
				try {
					e = ExpressionHelper.createExpression(formula, biotype);
					;
				} catch (Exception ex) {
					continue;
				}
				// Loop through each result to calculate the formula
				for (BiosampleDto biosample : map.get(biotype)) {
					updated = true;
					try {
						double res = ExpressionHelper.evaluate(e, biosample);
						setMetadata(biosample, bm, "" + res);
					} catch (Exception ex) {
						setMetadata(biosample, bm, "");
					}
				}
			}
		}
		return updated;
	}

	public static Map<BiotypeDto, List<BiosampleDto>> mapBiotype(Collection<BiosampleDto> col) {
		Map<BiotypeDto, List<BiosampleDto>> map = new HashMap<>();
		if (col == null)
			return map;
		for (BiosampleDto b : col) {
			if (b.getBiotype() == null)
				continue;
			List<BiosampleDto> l = map.get(b.getBiotype());
			if (l == null) {
				l = new ArrayList<>();
				map.put(b.getBiotype(), l);
			}
			l.add(b);
		}
		return map;
	}

	public String getMetadataAsString(BiosampleDto biosample) {
		return getInfos(biosample, EnumSet.of(InfoFormat.METATADATA), InfoSize.ONELINE);
	}

	public String getAnimalAge(BiosampleDto biosample) {
		String birtday = getMetadataValue(biosample, Constants.BIRTHDAY_METADATA_NAME);
		Date from = FormatterUtils.parseDateTime((birtday == null || birtday.equals(""))
				? getMetadataValue(biosample, Constants.DELIVERY_DATE_METADATA_NAME)
				: birtday);
		if (from == null)
			return "Unknown";
		String deathDay = getMetadataValue(biosample, Constants.DATEOFDEATH);
		Date to = (deathDay == null || deathDay.equals("")) ? new Date()
				: FormatterUtils.parseDateTime(deathDay);
		if (to == null)
			return "Unknown";
		int diffDays = (int) ((to.getTime() - from.getTime()) / (24 * 60 * 60 * 1000));
		return diffDays + " days old";
	}

	public int compareBioSampleBySubgroupAssignmentName(BiosampleDto o1, BiosampleDto o2, StudyDto study) {
		AssignmentDto a1 = getLastAssignment(o1, study);
		AssignmentDto a2 = getLastAssignment(o2, study);
		int c=a1.getSubgroup().compareTo(a2.getSubgroup());
		if(c!=0)
			return c;
		return CompareUtils.compareAlphaNumeric(a1.getName(), a2.getName());
	}

	public List<BiosampleDto> getSamplesFromStudyDesign(BiosampleDto biosample, PhaseDto phase, boolean onlyWithRequiredAction) {
		List<BiosampleDto> res = new ArrayList<>();
		for (BiosampleDto 	sample : getHierarchy(biosample, HierarchyMode.AS_STUDY_DESIGN, phase.getStage().getStudy())) {
			if(sample.getInheritedPhase()==null) {
				if(!onlyWithRequiredAction) {
					res.add(sample);
				}
			} else if(phase==null || sample.getInheritedPhase().equals(phase)) {
				if(!onlyWithRequiredAction) {
					res.add(sample);
				} else if(sample.getAttachedSampling()!=null && samplingService.hasMeasurements(sample.getAttachedSampling())) {
					res.add(sample);
				}
			}
		}

		return res;
	}

	public List<BiosampleDto> getSamplesFromStudyDesign(BiosampleDto biosample, StudyDto study, boolean onlyWithRequiredAction) {
		List<BiosampleDto> res = new ArrayList<>();
		for (BiosampleDto 	sample : getHierarchy(biosample, HierarchyMode.AS_STUDY_DESIGN, study)) {
			if(!onlyWithRequiredAction) {
				res.add(sample);
			} else if(sample.getAttachedSampling()!=null && samplingService.hasMeasurements(sample.getAttachedSampling())) {
				res.add(sample);
			}
		}
		return res;
	}

	public BiosampleDto getSample(BiosampleDto biosample, SamplingDto sampling, PhaseDto phase) {
		assert sampling!=null;
		for(BiosampleDto b: getHierarchy(biosample, HierarchyMode.ATTACHED_SAMPLES)) {
			if((phase==null || phase.equals(b.getInheritedPhase())) && sampling.equals(b.getAttachedSampling()))
				return b;
		}
		return null;
	}

	public String getInheritedGroupString(BiosampleDto b, String username) {
		SubGroupDto subGroup = getCreationSubGroup(b);
		if(subGroup==null || username==null) {
			return "";
		} else if(studyService.getBlindAllUsers(subGroup.getGroup().getStage().getStudy()).contains(username) || studyService.getBlindDetailsUsers(subGroup.getGroup().getStage().getStudy()).contains(username)) {
			return "Blinded";
		} else {
			return groupService.getShortName(subGroup.getGroup()) + (subGroup.getGroup().getSubgroups().size()>1? " '"+(subGroup.getFullName()):"");
		}
	}

	public String getCreationPhaseString(BiosampleDto b) {
		return b.getInheritedPhase()==null? "": phaseService.getShortName(b.getInheritedPhase());
	}

	public List<BiosampleDto> queryBiosamples(BiosampleQuery query, User user) throws Exception {
		syncList.clear();
		List<Biosample> biosamples = biosampleDao.queryBiosample(query, user);
		for(Biosample biosample : biosamples) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						syncList.add(map(biosample));
					}catch(Exception e) {
						e.printStackTrace();
					}finally {
						ThreadUtils.removeThread(this);
					}							
				}
			};
			ThreadUtils.start(t);
		}
		ThreadUtils.waitProcess();
		List<BiosampleDto> res = new ArrayList<>(syncList);
		syncList.clear();
		// Verify the metadata, as the search didn't check the exact fields
		loop: for (Iterator<BiosampleDto> iterator = res.iterator(); iterator.hasNext();) {
			BiosampleDto biosample = iterator.next();
			for (Entry<BiosampleLinker, String> entry : query.getLinker2values().entrySet()) {
				if (!QueryTokenizer.matchQuery(entry.getKey().getValue(biosample), entry.getValue())) {
					iterator.remove();
					continue loop;
				}
			}
		}

		// Filter samples that should not be searchable (location = private )
		if (user != null) {
			for (Iterator<BiosampleDto> iterator = res.iterator(); iterator.hasNext();) {
				BiosampleDto biosample = iterator.next();
				if (!spiritRights.canRead(biosample, user)) {
					iterator.remove();
					continue;
				}
				if (query.isSearchMySamples() && !spiritRights.canEdit(biosample, user)) {
					iterator.remove();
					continue;
				}
			}
		}
		// Filter for groups instead of using query so group search can find creation
		// groups and assignment groups
		if (query.getGroup() != null && query.getGroup().length() > 0) {
			outer: for (Iterator<BiosampleDto> iterator = res.iterator(); iterator.hasNext();) {
				BiosampleDto biosample = iterator.next();
				if (getCreationGroup(biosample) != null
						&& getCreationGroup(biosample).getName().equals(query.getGroup())) {
					continue;
				}
				for (AssignmentDto a : biosample.getAssignments()) {
					if (a.getSubgroup().getGroup().getName().equals(query.getGroup()))
						continue outer;
				}
				iterator.remove();
			}
		}

		if (query.getPhases() != null && query.getPhases().length() > 0) {
			Set<String> set = new HashSet<>(
					Arrays.asList(MiscUtils.split(query.getPhases(), MiscUtils.SPLIT_SEPARATORS_WITH_SPACE)));
			if (set.size() > 0) {
				for (Iterator<BiosampleDto> iterator = res.iterator(); iterator.hasNext();) {
					BiosampleDto b = iterator.next();
					if (b.getInheritedPhase() == null || !set.contains(phaseService.getShortName(b.getInheritedPhase()))) {
						iterator.remove();
					}
				}
			}
		}

		// Apply the Select-One query
		if (query.getSelectOneMode() == BiosampleQuery.SELECT_MOST_RIGHT) {
			Map<BiosampleDto, BiosampleDto> top2Sel = new HashMap<>();
			for (BiosampleDto b : res) {
				Container c = b.getContainer();
				if (c == null || containerService.getPos(c) < 0)
					continue;
				BiosampleDto top = b.getTopParent();
				BiosampleDto sel = top2Sel.get(top);

				if (sel == null || (getCol(b) > getCol(sel))) {
					top2Sel.put(top, b);
				}
			}
			res = new ArrayList<>(top2Sel.values());
		} else if (query.getSelectOneMode() == BiosampleQuery.SELECT_MOST_LEFT) {
			Map<BiosampleDto, BiosampleDto> top2Sel = new HashMap<>();
			for (BiosampleDto b : res) {
				Container c = b.getContainer();
				if (c == null || containerService.getPos(c) < 0)
					continue;
				BiosampleDto top = b.getTopParent();
				BiosampleDto sel = top2Sel.get(top);

				if (sel == null || (getCol(b) < getCol(sel))) {
					top2Sel.put(top, b);
				}
			}
			res = new ArrayList<>(top2Sel.values());
		}
		return res;
	}

	public Pair<Status, PhaseDto> getLastActionStatus(BiosampleDto biosample) {
		Status status = biosample.getState();
		return new Pair<Status, PhaseDto>(status, status == null ? null : status.isAvailable() ? null : getTerminationPhasePlanned(biosample));
	}

	public void setMetadataValue(BiosampleDto biosample, BiotypeMetadataDto biotypeMetadata, String value) {
		BiotypeMetadataBiosampleDto metadata = new BiotypeMetadataBiosampleDto();
		metadata.setBiosample(biosample);
		metadata.setMetadata(biotypeMetadata);
		metadata.setValue(value);
		setMetadata(biosample, metadata);
	}
	
	public void setMetadataValue(BiosampleDto biosample, String metadataName, String value) {
		BiotypeMetadataDto bType = biotypeService.getMetadataByName(biosample.getBiotype(), metadataName);
		if(bType==null) 
			throw new IllegalArgumentException("Invalid metadatatype: " + metadataName+" not in " + biosample.getBiotype().getMetadatas());
		setMetadataValue(biosample, bType, value);
	}

	public String getSampleIdName(BiosampleDto biosample) {
		if(biosample.getBiotype()==null) {
			return biosample.getSampleId();
		} else if(biosample.getBiotype().getHideSampleId()) {
			return (biosample.getBiotype()!=null && biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null && biosample.getLocalId().length()>0? biosample.getLocalId(): biosample.getSampleId());
		} else {
			return biosample.getSampleId() + (biosample.getBiotype()!=null && biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null && biosample.getLocalId().length()>0?" ["+biosample.getLocalId()+"]":"");
		}
	}

	public SubGroupDto getFirstSubGroup(BiosampleDto biosample, StudyDto study) {
		AssignmentDto firstAssignment = null;
		for (AssignmentDto assignment : biosample.getAssignments()) {
			if ((firstAssignment == null || firstAssignment.getStage().compareTo(assignment.getStage()) > 0)
					&& assignment.getStage().getStudy().equals(study)) {
				firstAssignment = assignment;
			}
		}
		return firstAssignment.getSubgroup();
	}

	public String getBiotypeString(Set<BiosampleDto> biosamples) {
		Collection<BiotypeDto> biotypes = getBiotypes(biosamples);
		String s = biotypes.size()==1? biotypes.iterator().next().getName(): "";
		return s;
	}

	public Map<String, List<BiosampleDto>> mapContainerId(List<BiosampleDto> biosamples) {
		Map<String, List<BiosampleDto>> res = new HashMap<>();
		for(BiosampleDto b: biosamples) {
			if(b.getContainerId()!=null) {
				List<BiosampleDto> l = res.get(b.getContainerId());
				if(l==null) {
					res.put(b.getContainerId(), l = new ArrayList<>());
				}
				l.add(b);
			}
		}
		return res;
	}

	public boolean isEmpty(List<BiosampleDto> biosamples) {
		for (BiosampleDto biosample : biosamples) {
			if(!isEmpty(biosample)) return false;
		}
		return true;
	}
	
	public boolean isEmpty(BiosampleDto biosample) {
		if(biosample.getSampleId()!=null && biosample.getSampleId().length()>0) return false;
		if(biosample.getBiotype()!=null &&  biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null && biosample.getLocalId().length()>0) return false;
		if(biosample.getAmount()!=null) return false;
		if(biosample.getContainerId()!=null && biosample.getContainerId().length()>0) return false;
		if(getMetadataAsString(biosample).trim().length()>0) return false;
		if(biosample.getComments()!=null && biosample.getComments().length()>0) return false;
		return true;
	}

	public List<BiosampleDto> filter(List<BiosampleDto> biosamples, BiotypeDto biotype) {
		if(biotype==null) return null;
		List<BiosampleDto> res = new ArrayList<>();
		for (BiosampleDto biosample : biosamples) {
			if(biotype.equals(biosample.getBiotype())) {
				res.add(biosample);
			}
		}
		return res;
	}

	public String getSampleIdNameAtEnd(BiosampleDto biosample, StudyDto study) {
		PhaseDto phase = getTerminationPhasePlanned(biosample);
		if(phase==null) {
			phase = getClosestPhase(biosample, study, getTerminationExecutionDate(biosample));
		}
		if(biosample.getBiotype()==null) {
			return biosample.getSampleId();
		} else if(biosample.getBiotype().getHideSampleId()) {
			return (biosample.getBiotype()!=null && biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null && biosample.getLocalId().length()>0? biosample.getLocalId(): biosample.getSampleId());
		} else {
			String name = biosample.getBiotype() == null ||biosample. getBiotype().getNameLabel() ==null ? "" :
					getNameAt(biosample, phase)!=null && getNameAt(biosample, phase).length()>0 ?
					" ["+getNameAt(biosample, phase)+"]": "";
			return biosample.getSampleId() + name;
		}
	}

	public String getNameAt(BiosampleDto biosample, PhaseDto phase) {
		if(phase==null)
			return null;
		AssignmentDto assignment = getAssignment(biosample, phase.getStage());
		if (assignment!=null) return assignment.getName();
		return null;
	}

	public GroupDto getGroupByStudy(Set<BiosampleDto> biosamples, StudyDto study) {
		Set<GroupDto> res = getGroupsByStudy(biosamples, study);
		if(res.size()==1) return res.iterator().next();
		return null;
	}

	public Set<GroupDto> getGroupsByStudy(Set<BiosampleDto> biosamples, StudyDto study) {
		if(biosamples==null) return null;
		SortedSet<GroupDto> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			if (b.getAssignments()!=null && b.getAssignments().size() > 0) {
				for (AssignmentDto assignment : b.getAssignments()) {
					if (assignment.getStage().getStudy().equals(study)) res.add(getGroup(b, assignment.getStage()));
				}
			} else {
				// if sample has no group itself, lets try to get the group of the parent sample in the phase the sample was created
				GroupDto group = null;
				PhaseDto p = getPhase(Collections.singleton(b));
				Set<BiosampleDto> parents = getParents(Collections.singleton(b));
				while (parents != null) {
					for (BiosampleDto bio : parents) {
						group = getGroup(bio, p.getStage());
						if (group != null) break;
					}
					if (group != null) break;
					parents = getParents(parents);
				}
				if (group != null) res.add(group);
			}
		}
		return res;
	}

	public String getSampleNameOrId(BiosampleDto biosample) {
		return biosample.getBiotype()!=null && biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null && biosample.getLocalId().length()>0? biosample.getLocalId() : biosample.getSampleId();
	}

	public List<BiosampleDto> getChildSamples(BiosampleDto biosample, SamplingDto sampling) {
		List<BiosampleDto> samples = new ArrayList<>();
		for(BiosampleDto b: getHierarchy(biosample, HierarchyMode.ATTACHED_SAMPLES)) {
			if(sampling.equals(b.getAttachedSampling()))
				samples.add(b);
		}
		return samples;
	}

	@SuppressWarnings("deprecation")
	public void mapResults(BiosampleDto biosample) {
		biosample.setResults(assayResultService.map(assayResultService.getAssayResultsByBiosample(biosample.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapAdministrations(BiosampleDto biosample) {
		biosample.setAdministrations(administrationService.map(administrationService.getByBiosample(biosample.getId())));
	}

	@SuppressWarnings("deprecation")
	public void mapAssignments(BiosampleDto biosample) {
		if(biosample.getParent()==null) {
			biosample.setAssignments(new HashSet<AssignmentDto>(assignmentService.map(assignmentService.getAssignmentsByBiosample(biosample.getId()))));
		}else {
			biosample.setAssignments(new HashSet<>());
		}
	}

	@SuppressWarnings({ "deprecation" })
	public void mapPlannedSamples(BiosampleDto biosample) {
		if(biosample.getParent()==null) {
			biosample.setPlannedSamples(plannedSampleService.map(plannedSampleService.getPlannedSamplesByBiosample(biosample.getId())));
		}else {
			biosample.setPlannedSamples(new ArrayList<>());
		}
	}

	@SuppressWarnings({ "deprecation" })
	public void mapBiosampleEnclosures(BiosampleDto biosample) {
		if(biosample.getParent()==null) {
			biosample.setBiosampleEnclosures(biosampleEnclosureService.map(biosampleEnclosureService.getByBiosample(biosample.getId())));
			Collections.sort(biosample.getBiosampleEnclosures());
		}else {
			biosample.setBiosampleEnclosures(new ArrayList<>());
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	public void mapMetadatas(BiosampleDto biosample) {
		biosample.setMetadatas(biotypeMetadataBiosampleService
				.map(biotypeMetadataBiosampleService.getBiotypeMetadataBiosamplesByBiosample(biosample.getId())));
	}

	public void mapChildren(BiosampleDto biosampleDto) {
		Set<BiosampleDto> children = new HashSet<>();
		for (Biosample child : getBiosamplesByParentId(biosampleDto.getId()))
			children.add(map(child));
		biosampleDto.setChildren(children);
	}

	public void mapLinkedBiosamples(BiosampleDto biosample) {
		biosample.setLinkedBiosamples(linkedBiosampleService.map(linkedBiosampleService.getLinkedBiosamplesByBiosample(biosample.getId())));
	}

	public void mapDocuments(BiosampleDto biosample) {
		biosample.setDocuments(linkedDocumentService.map(linkedDocumentService.getLinkedDocumentsByBiosample(biosample.getId())));
	}

	public boolean isInDifferentContainers(Collection<BiosampleDto> biosamples) {
		if(biosamples==null || biosamples.size()<=1) return false;
		//Count number of Containers
		List<String> l = new ArrayList<>();
		for (BiosampleDto b : biosamples) {
			if(b.getContainerId()!=null && b.getContainerId().length()>0) {
				if(l.contains(b.getContainerId())) {
					//ok
				} else if(l.size()>=1) {
					return true;
				} else {
					l.add(b.getContainerId());
				}
			} else {
				if(l.size()>=1) return true;
				l.add(b.getContainerId());
			}
		}
		return false;
	}

	public Set<String> getContainerIds(List<BiosampleDto> biosamples) {
		SortedSet<String> res = new TreeSet<>();
		if(biosamples==null) return null;
		for (BiosampleDto b : biosamples) {
			if(b.getContainerId()!=null && b.getContainerId().length()>0) res.add(b.getContainerId());
		}
		return res;
	}

	public PhaseDto getFPhaseatDay(BiosampleDto biosample, StudyDto study, Date toDate) {
		for (AssignmentDto assignment : biosample.getAssignments()) {
			if (assignment.getStage().getStudy().equals(study)) {
				for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(assignment)) {
					if (!(actionPattern.getAction() instanceof Measurement)) continue;
					Measurement measurement = (Measurement) actionPattern.getAction();
					if (!studyActionService.isMeasureFood(measurement)) continue;
					for (PhaseDto phase : scheduleService.getPhases(actionPattern.getSchedule())) {
						Date time = getDate(biosample, phase, measurement);
						if (time != null && toDate != null && Constants.DAYFORMAT.format(toDate).equals(Constants.DAYFORMAT.format(time))) {
							return phase;
						}
					}
				}
				return getPhase(biosample, study, toDate);
			}
		}
		return null;
	}

	public PhaseDto getWPhaseatDay(BiosampleDto biosample, StudyDto study, Date toDate) {
		for (AssignmentDto assignment : biosample.getAssignments()) {
			if (assignment.getStage().getStudy().equals(study)) {
				for (ActionPatternsDto actionPattern : assignmentService.getFullActionDefinition(assignment)) {
					if (!(actionPattern.getAction() instanceof Measurement)) continue;
					Measurement measurement = (Measurement) actionPattern.getAction();
					if (!studyActionService.isMeasureWater(measurement)) continue;
					for (PhaseDto phase : scheduleService.getPhases(actionPattern.getSchedule())) {
						Date time = getDate(biosample, phase, measurement);
						if (time != null && toDate != null && Constants.DAYFORMAT.format(toDate).equals(Constants.DAYFORMAT.format(time))) {
							return phase;
						}
					}
				}
			}
		}
		return null;
	}

	public Set<StudyAction> getStudyActions(BiosampleDto biosample, StudyDto study, Date date, Participant participant, Set<Participant> participants) {
		return getStudyActions(biosample, getPhase(biosample, study,date,participant,participants));
	}

	public EnclosureDto getInsertEnclosure(BiosampleDto animal, Date date, Set<Participant> participants) {
		for (BiosampleEnclosureDto biosampleEnclosure: animal.getBiosampleEnclosures()) {
			if (biosampleEnclosureService.containsDate(biosampleEnclosure, date, ContainmentType.INSERT, participants)) 
				return biosampleEnclosure.getEnclosure();
		}
		return null;
	}

	public void refreshResults(BiosampleDto biosample, AssayDto assay, PhaseDto phase) throws Exception{
		List<AssayResultDto> res = getResultByPhaseAndAssayName(biosample, phase, assay.getName());
		biosample.getResults().removeAll(res);
		getCacheMap(AssayResultDto.class).clear();
		getCacheMap(AssayResultValueDto.class).clear();
		assayResultService.attachOrCreateStudyResultsToTops(phase.getStage().getStudy(), Collections.singleton(biosample), null, null);
	}

	public Collection<Integer> getScannedPoses(List<BiosampleDto> biosamples, LocationDto loc) {
		SortedSet<Integer> res = new TreeSet<>();
		if(biosamples==null) return null;
		for (BiosampleDto b : biosamples) {
			int pos = b.getLocationPos();
			try {
				if(b.getScannedPosition()!=null && loc!=null) pos = locationService.parsePosition(loc, b.getScannedPosition());
			} catch (Exception e) {
				e.printStackTrace();
			}
			res.add(pos);
		}
		return res;
	}

	public Map<String, BiosampleDto> getBiosamplesBySampleIds(Set<String> sampleIds) {
		Map<String, BiosampleDto> res = new HashMap<>();
		if(sampleIds.size()==0) return res;
		List<Biosample> biosamples = biosampleDao.getBiosamplesBySampleIds(sampleIds);
		for(Biosample biosample : biosamples) {
			res.put(biosample.getSampleId(), map(biosample));
		}
		return res;
	}

	public List<String> getContainerTypes(StudyDto study) {
		return biosampleDao.getContainerTypes(study);
	}

	public List<Biotype> getBiotypes(StudyDto study) {
		return biosampleDao.getBiotype(study);
	}

	public Map<String, Map<String, Triple<Integer, String, Date>>> countSampleByStudyBioype(
			Collection<StudyDto> studies, Date minDate) {
		return biosampleDao.countSampleByStudyBiotype(studies, minDate);
	}

	public String getInfosStudy(List<BiosampleDto> biosamples) {
		return getInfos(biosamples, EnumSet.of(InfoFormat.STUDY, InfoFormat.TOPIDNAMES), InfoSize.COMPACT);
	}

	public String getInfosMetadata(List<BiosampleDto> biosamples) {
		return getInfos(biosamples, EnumSet.of(InfoFormat.BIOTYPE, InfoFormat.SAMPLENAME, InfoFormat.METATADATA, InfoFormat.COMMENTS, InfoFormat.AMOUNT), InfoSize.ONELINE);
	}

	public List<BiosampleDto> getBiosampleByBiotypeName(String biotypeName) {
		return map(biosampleDao.getBiosampleByBiotypeName(biotypeName));
	}

	public List<BiosampleDto> getBiosampleByMetadataValue(String value) {
		return map(biosampleDao.getBiosampleByMetadataValue(value));
	}

	public void deletePlannedSamples(List<PlannedSampleDto> samples) {
		for (PlannedSampleDto b : samples) {
			plannedSampleService.delete(b);
		}
	}

	public void updateAnimalDB(ArrayList<AnimalUsageRest> usages, String username) throws Exception{
    	ResponseEntity<String> stringResponseEntity = DAOAnimalUsage.addAnimalUsage(usages, username, "spirit-core");
        if (stringResponseEntity.getStatusCode().value() != 201) {
            throw new CommunicationException(stringResponseEntity.getBody());
        }
    }

	public boolean isFromExternalDB(BiosampleDto biosample) {
		if(biosample.getIsFromAnOtherDB()==null)
			try {
				biosample.setIsFromAnOtherDB(isFromExternalDB(biosample));
			} catch (Exception e) {
				e.printStackTrace();
			}
		return biosample.getIsFromAnOtherDB();
	}

	public void dischargeAnimalDB(String elb, Date startDate, Collection<AnimalDBPojo> subjects, String username) throws Exception {
    	AnimalUsageRest animalUsageRest = new AnimalUsageRest();
        animalUsageRest.setUserId(username);
        animalUsageRest.setElb(elb);
        animalUsageRest.setStartDate(startDate);
        animalUsageRest.setDetails(new ArrayList<>());
        for (AnimalDBPojo subject : subjects) {
            AnimalUsageRestDetail animalUsageRestDetail = new AnimalUsageRestDetail();
            animalUsageRestDetail.setAnimalId(subject.getAnimalId());
            animalUsageRestDetail.setEndDate(subject.getEndDate());
            animalUsageRestDetail.setLicenseNo(subject.getLicense().toString());
            animalUsageRestDetail.setSeverityDegree(subject.getSeverity());
            animalUsageRestDetail.setComment(subject.getComment());
            animalUsageRest.getDetails().add(animalUsageRestDetail);
        }
        ResponseEntity<String> stringResponseEntity = DAOAnimalUsage.dischargeAnimal(Collections.singletonList(animalUsageRest), username, "spirit-core");
        if (stringResponseEntity.getStatusCode().value() != 201) {
            throw new Exception(stringResponseEntity.getBody());
        }
    }

    public ArrayList<String> getLicenses() {
        HashSet<String> licenses = new HashSet<>();
        try {
            List<AnimalProcedure> allLicenses = DAOAnimalProcedure.getAllLicenses();
            for (AnimalProcedure animalProcedure : allLicenses) {
                licenses.add(animalProcedure.getLicenseNo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> result = new ArrayList<>(licenses);
        Collections.sort(result);
        return result;
    }

	public void changeOwnership(List<BiosampleDto> biosamples, User toUser, User user) throws Exception {
		if(!spiritRights.canEditBiosamples(biosamples, user)) 
			throw new Exception("You don't have sufficient rights to change the ownership");
		for (BiosampleDto b : biosamples) {
			if (b.getId() <= 0) continue;
			b.setCreUser(toUser.getUsername());
			b.setDepartment(UserUtil.getMainDepartment(toUser));
			save(b);
		}
	}

	public boolean setEnclosure(BiosampleDto biosample, EnclosureDto enclosure, PhaseDto phaseIn) {
		PhaseDto phaseOut = null;
		BiosampleEnclosureDto previous = null;
		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (enclosure.equals(biosampleEnclosure.getEnclosure()) && biosampleEnclosureService.containsPhase(biosampleEnclosure, phaseIn, ContainmentType.FW)) {
				biosampleEnclosureService.setPhaseOut(biosampleEnclosure, null);
				return true;
			}
		}

		for (BiosampleEnclosureDto biosampleEnclosure : biosample.getBiosampleEnclosures()) {
			if (phaseIn.compareTo(biosampleEnclosure.getPhaseIn()) == 0) {
				biosampleEnclosureService.setEnclosure(biosampleEnclosure, enclosure);
				return true;
			}
			if (phaseIn.compareTo(biosampleEnclosure.getPhaseIn()) > 0 && phaseIn.compareTo(biosampleEnclosure.getPhaseOut()) <= 0
					&& phaseIn.getStage().getStudy().equals(biosampleEnclosure.getPhaseIn().getStage().getStudy())) {
						previous=biosampleEnclosure;
			}
			if ((phaseOut == null && biosampleEnclosure.getPhaseIn().compareTo(phaseIn) > 0)
					|| (phaseOut != null && phaseOut.compareTo(biosampleEnclosure.getPhaseIn()) > 0)) phaseOut = biosampleEnclosure.getPhaseIn();
		}
		
		if(previous!=null)
			biosampleEnclosureService.setPhaseOut(previous, phaseIn);
		BiosampleEnclosureDto biosampleEnclosure = biosampleEnclosureService.newBiosampleEnclosure(biosample, enclosure, phaseIn);
		if (phaseOut != null) biosampleEnclosureService.setPhaseOut(biosampleEnclosure, phaseOut);
		enclosure.getBiosampleEnclosures().add(biosampleEnclosure);
		return true;
		
	}

	public SortedSet<ContainerType>  getContainerTypes(Set<BiosampleDto> biosamples) {
		SortedSet<ContainerType> res = new TreeSet<>();
		if(biosamples==null) return null;
		for (BiosampleDto b : biosamples) {
			if(b.getContainerType()!=null) res.add(b.getContainerType());
		}
		return res;
	}

	public SortedSet<NamedSamplingDto> getNamedSamplings(Set<BiosampleDto> biosamples) {
		if(biosamples==null) return null;
		SortedSet<NamedSamplingDto> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			if(b.getAttachedSampling()!=null) res.add(b.getAttachedSampling().getNamedSampling());
		}
		return res;
	}

	public boolean isCompatible(BiosampleDto biosample, String metadata, String groupPrefixOrSuffix) {
		if(metadata!=null && metadata.length()>0) {
			StringTokenizer st = new StringTokenizer(metadata, " ,;/");
			String m = getInfos(biosample, EnumSet.allOf(InfoFormat.class), InfoSize.ONELINE).toLowerCase();
			while(st.hasMoreTokens()) {
				String token = st.nextToken().toLowerCase();
				if(!m.contains(token)) return false;
			}
		}
/*		if(groupPrefixOrSuffix!=null && groupPrefixOrSuffix.length()>0) {
		if(getInheritedGroup()==null || !getInheritedGroup().getName().equalsIgnoreCase(groupPrefixOrSuffix)) {
			//Ok
		} else if(getInheritedGroup()==null || !getInheritedGroup().getName().startsWith(groupPrefixOrSuffix)) {
			return false;
		} else if(getInheritedGroup()==null || !getInheritedGroup().getName().endsWith(groupPrefixOrSuffix)) {
			return false;
		}
	}*/		
		return true;
	}

	public void setContainerType(BiosampleDto biosample, ContainerType containerType) {
		setContainer(biosample, new Container(containerType, biosample.getContainerId()));
	}
	
	public void setContainerId(BiosampleDto biosample, String containerId) {
		setContainer(biosample, new Container(biosample.getContainerType(), containerId));
	}

	@SuppressWarnings("deprecation")
	public void setContainer(BiosampleDto biosample, Container container) {
		String oldCid = biosample.getContainerId();
		ContainerType oldCType = biosample.getContainerType();

		String newCid = container==null? null: container.getContainerId();
		ContainerType newCType = container==null? null: container.getContainerType();

		//Return if there are no change
		if(container!=null) {
			container.setCreatedFor(biosample);
		}
		if(oldCid!=null && oldCid.equals(newCid) && oldCType==newCType) {
			return;
		}

		//Remove this sample from the older container
		if(biosample.getContainer()!=null) {
			containerService.removeBiosample(biosample.getContainer(), biosample);
		}

		//Update the container
		biosample.setContainer(container);

		//And add it to the new one
		if(container!=null) {
			containerService.addBiosample(container, biosample);
		}
		
	}

	public Set<String>  getSampleIds(List<BiosampleDto> biosamples) {
		if(biosamples==null) return null;
		Set<String> res = new HashSet<>();
		for (BiosampleDto b : biosamples) {
			if(b==null) continue;
			res.add(b.getSampleId());
		}
		return res;
	}

	public void setCreationPhase(BiosampleDto destinationBiosample, BiosampleDto sourceBiosample) {
		//Return immediately if there are no changes
		PhaseDto sourceInheritedPhase = sourceBiosample.getInheritedPhase();
		if(sourceInheritedPhase== destinationBiosample.getInheritedPhase()) {
			return;
		}
		//Make sure the study matched the phase
		if(sourceBiosample.getStudy()==null && sourceInheritedPhase!=null) {
			destinationBiosample.setStudy(sourceInheritedPhase.getStage().getStudy());
		}
		//Update the biosample
		destinationBiosample.setInheritedPhase(sourceInheritedPhase);
	}

	public void clearAuxInfos(List<BiosampleDto> col) {
		for (BiosampleDto b : col) {
			b.getAuxiliaryInfos().clear();
		}
	}

	public List<BiosampleDto> findDuplicates(StudyDto study, BiotypeDto biotype, BiosampleDuplicateMethod duplicateMethod,
			User user) throws Exception {
		if (study == null && biotype == null)
			throw new Exception("You need to specifiy a filter");
		BiosampleQuery q = new BiosampleQuery();
		q.setStudyIds(study == null ? "" : study.getStudyId());
		q.setBiotype(biotype);
		List<BiosampleDto> all = queryBiosamples(q, user);
		List<BiosampleDto> res = new ArrayList<>();
		ListHashMap<String, BiosampleDto> key2Sample = new ListHashMap<>();

		for (BiosampleDto b : all) {
			if (b.getTopParent() == b)
				continue;
			String key = nvl(b.getBiotype()) + "_" + nvl(b.getTopParent()) + "_" + nvl(b.getSampleId()) + "_" + nvl(getMetadataAsString(b)) + "_" + nvl(b.getContainerType()) + "_"
					+ (b.getAttachedSampling() == null ? "" : b.getAttachedSampling().getId());
			key2Sample.add(key, b);
		}

		if (duplicateMethod == BiosampleDuplicateMethod.RETURN_OLDEST_WITHOUT_RESULT) {
			List<AssayResultDto> results;
			results = assayResultService.queryResults(ResultQuery.createQueryForBiosampleIds(getIds(all)), null);
			for (AssayResultDto r : results) {
				r.getBiosample().getAuxiliaryInfos().put("result", "true");
			}
		}

		for (List<BiosampleDto> list : key2Sample.values()) {
			if (list.size() <= 1)
				continue;

			BiosampleDto sel = null;
			if (duplicateMethod == BiosampleDuplicateMethod.RETURN_NEWEST) {
				for (BiosampleDto b : list) {
					if (sel == null || b.getCreDate().after(sel.getCreDate()))
						sel = b;
				}
			} else if (duplicateMethod == BiosampleDuplicateMethod.RETURN_OLDEST) {
				for (BiosampleDto b : list) {
					if (sel == null || b.getCreDate().before(sel.getCreDate()))
						sel = b;
				}
			} else if (duplicateMethod == BiosampleDuplicateMethod.RETURN_OLDEST_WITHOUT_RESULT) {
				for (BiosampleDto b : list) {
					if (b.getAuxiliaryInfos().get("result") != null)
						continue;
					if (sel == null || b.getCreDate().before(sel.getCreDate()))
						sel = b;
				}
			} else if (duplicateMethod == BiosampleDuplicateMethod.RETURN_ALL) {
				// OK
			} else {
				throw new IllegalArgumentException("Invalid method: " + duplicateMethod);
			}

			if (duplicateMethod == BiosampleDuplicateMethod.RETURN_ALL) {
				res.addAll(list);
			} else {
				if (sel == null) {
					System.err.println("Could not decide between " + list);
				} else {
					list.remove(sel);
					res.addAll(list);
				}
			}
		}
		return res;
	}
	
	private static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}
}