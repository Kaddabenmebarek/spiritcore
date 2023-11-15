package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.InvalidAttributesException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.osiris.util.ListHashMap;
import com.actelion.research.security.entity.User;
import com.actelion.research.util.FormatterUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.constants.FindDuplicateMethod;
import com.idorsia.research.spirit.core.constants.OutputType;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.dao.AssayResultDao;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssayResultValueDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.IndividualAction;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.ResultQuery;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.model.AssayResultValue;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.util.ExpressionHelper;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;
import com.idorsia.research.spirit.core.util.SetHashMap;
import com.idorsia.research.spirit.core.util.SpiritRights;
import com.idorsia.research.spirit.core.util.ThreadUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

import net.objecthunter.exp4j.Expression;
import oracle.security.crypto.cert.ValidationException;


@Service
public class AssayResultService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 4366741257761885621L;
	@Autowired
	private AssayResultDao assayResultDao;
	@Autowired
	private AssayService assayService;
	@Autowired
	private AssayResultValueService assayResultValueService;
	@Autowired
	private StudyActionService studyActionService;
	@Autowired
	private StageService stageService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private BiotypeService biotypeService;
	@Autowired
	private SubGroupService subGroupService;
	@Autowired
	private PhaseService phaseService;
	@Autowired
	private ResultAssignmentService resultAssignmentService;
	@Autowired
	private ExecutionService executionService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private SpiritRights spiritRights;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private SamplingService samplingService;

	private static Logger logger = LoggerFactory.getLogger(AssayResultService.class);
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, AssayResultDto> idToAssayResult = (Map<Integer, AssayResultDto>) getCacheMap(
			AssayResultDto.class);

	public int addAssayResult(AssayResult assayResult) {
		return assayResultDao.addAssayResult(assayResult);
	}

	public Integer saveOrUpdate(AssayResult assayResult) {
		return assayResultDao.saveOrUpdate(assayResult);
	}

	public AssayResult get(Integer id) {
		return assayResultDao.get(id);
	}
	
	public List<AssayResult> getAssayResultsByBiosample(Integer biosampleId) {
		return assayResultDao.getAssayResultsByBiosample(biosampleId);
	}
	
	public List<AssayResult> getAssayResultsByPhase(Integer phaseId) {
		return assayResultDao.getAssayResultsByPhase(phaseId);
	}
	
	public List<AssayResult> getAssayResultsByStudy(Integer studyId) {
		return assayResultDao.getAssayResultsByStudy(studyId);
	}
	
	public List<AssayResult> getByValueAndAttributeId(String value, Integer id) {
		return assayResultDao.getByValueAndAttributeId(value, id);
	}
	
	public List<AssayResult> getAssayResultsByAssay(Integer assayId) {
		return assayResultDao.getAssayResultsByAssay(assayId);
	}
	
	public List<AssayResult> getAssayResultsByComments(String comments) {
		return assayResultDao.getAssayResultsByComments(comments);
	}
	
	public List<AssayResult> getAssayResultsByIds(Collection<Integer> ids) {
		return assayResultDao.getAssayResultsByIds(ids);
	}
	
	public AssayResult getAssayResultById(int id) {
		return assayResultDao.getAssayResultById(id);
	}
	
	public List<AssayResult> list() {
		return assayResultDao.list();
	}
	
	public int getCount() {
		return assayResultDao.getCount();
	}

	public AssayResultDao getAssayResultDao() {
		return assayResultDao;
	}

	public void setAssayResultDao(AssayResultDao assayResultDao) {
		this.assayResultDao = assayResultDao;
	}

	public String[] getMeasurementParameters(AssayResultDto result) {
		List<AssayAttributeDto> atts = assayService.getInputAttributes(result.getAssay());
		List<String> res = new ArrayList<>();
		for (AssayAttributeDto att : atts) {
			AssayResultValueDto rv = getResultValue(result, att);
			res.add(rv == null ? "" : rv.getTextValue());
		}
		return res.toArray(new String[] {});
	}

	public AssayResultValueDto getResultValue(AssayResultDto result, AssayAttributeDto att) {
		if (result.getAssay() == null || att == null || !result.getAssay().equals(att.getAssay()))
			return null;
		return getValue(result, att);
	}

	private AssayResultValueDto getValue(AssayResultDto result, AssayAttributeDto att) {
		for (AssayResultValueDto value : result.getValues()) {
			if (value.getAssayAttribute().equals(att))
				return value;
		}
		return null;
	}

	public PhaseDto getInheritedPhase(AssayResultDto assayResult) {
		if(assayResult==null)
			return null;
		return assayResult.getPhase() != null ? assayResult.getPhase()
				: assayResult.getBiosample() != null ? assayResult.getBiosample().getInheritedPhase() : null;
	}

	public StudyDto getInheritedStudy(AssayResultDto result) {
		return result.getStudy()!=null ?
				result.getStudy() :
					result.getBiosample()!=null ?
							result.getBiosample().getStudy() :
						null;
	}

	public List<AssayResultDto> map(List<AssayResult> assayResults) {
		List<AssayResultDto> res = new ArrayList<AssayResultDto>();
		for(AssayResult result : assayResults) {
			res.add(map(result));
		}
		return res;
	}	

	public AssayResultDto map(AssayResult result) {
		AssayResultDto resultDto = idToAssayResult.get(result.getId());
		if (resultDto == null) {
			resultDto = dozerMapper.map(result, AssayResultDto.class, "assayResultCustomMapping");
			if (idToAssayResult.get(result.getId()) == null)
				idToAssayResult.put(result.getId(), resultDto);
			else
				resultDto = idToAssayResult.get(result.getId());
			setExecutionDate(resultDto, result.getExecutionDate());
		}
		return resultDto;
	}
	
	public AssayResultDto getAssayResultDto(Integer id) {
		return map(get(id));
	}
	
	@Transactional
	public void save(AssayResultDto result) throws Exception {
		save(result, false);
	}

	@SuppressWarnings("deprecation")
	protected void save(AssayResultDto result, Boolean cross) throws Exception {
		try{
			if (!savedItems.contains(result)) {
				savedItems.add(result);
				if (result.getPhase() != null && result.getPhase().getId() == Constants.NEWTRANSIENTID)
					phaseService.save(result.getPhase(), true);
				if (result.getResultAssignment() != null
						&& result.getResultAssignment().getId() == Constants.NEWTRANSIENTID)
					resultAssignmentService.save(result.getResultAssignment(), true);
				if (result.getBiosample().getId() == Constants.NEWTRANSIENTID)
					biosampleService.save(result.getBiosample(), true);
				if (result.getStudy()!=null && result.getStudy().getId() == Constants.NEWTRANSIENTID)
					studyService.save(result.getStudy(), true);
				result.setUpdDate(new Date());
				result.setUpdUser(UserUtil.getUsername());
				if(result.getId().equals(Constants.NEWTRANSIENTID)) {
					result.setCreDate(new Date());
					result.setCreUser(UserUtil.getUsername());
				}
				result.setId(saveOrUpdate(dozerMapper.map(result, AssayResult.class, "assayResultCustomMapping")));
				idToAssayResult.put(result.getId(), result);
				if(result.getValuesNoMapping()!=null)
					for (AssayResultValueDto value : result.getValues())
						assayResultValueService.save(value, true);
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

	@Transactional
	public void save(Collection<AssayResultDto> results) {
		for (AssayResultDto result : results) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						save(result, true);
					}catch(Exception e) {
						e.printStackTrace();
						AbstractService.clearTransient(false);
					}finally {
						ThreadUtils.removeThread(this);
					}							
				}
			};
			ThreadUtils.start(t);
		}
		ThreadUtils.waitProcess();
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	@Transactional
	public void delete(AssayResultDto result) {
		delete(result, false);
	}
	
	protected void delete(AssayResultDto result, Boolean cross) {
		assayResultDao.delete(result.getId());
	}

	@Transactional
	public void delete(List<AssayResultDto> results) {
		assayResultDao.delete(getIds(results));
	}

	public void delete(AssayResult result) {
		for (AssayResultValue value : assayResultValueService.getAssayResultValuesByAssayResult(result.getId()))
			assayResultValueService.delete(value.getId());
		assayResultDao.delete(result.getId());
	}

	public String getFirstOutputValue(AssayResultDto result) {
		for (AssayAttributeDto ta : result.getAssay().getAttributes()) {
			if (ta.getOutputType() != OutputType.OUTPUT)
				continue;
			AssayResultValueDto rv = getValue(result, ta);
			if (rv == null)
				return null;
			return rv.getTextValue();
		}
		return null;
	}

	public void setFirstOutputValue(AssayResultDto result, String val) {
		for (AssayAttributeDto ta : result.getAssay().getAttributes()) {
			if (ta.getOutputType() != OutputType.OUTPUT)
				continue;
			setValue(result, ta, val);
			return;
		}
	}

	public void setValue(AssayResultDto result, AssayAttributeDto att, String val) {
		assert att != null;
		AssayResultValueDto value = getResultValue(result, att);
		assert value != null : att + " is invalid. Valid: " + result.getAssay().getAttributes();
		value.setTextValue(val);
	}
	
	public void setValue(AssayResultDto result, String attName, String val) {
		AssayAttributeDto ta = assayService.getAttribute(result.getAssay(), attName);
		assert ta!=null;
		setValue(result, ta, val);
		
	}

	public Double getFirstAsDouble(AssayResultDto result) {
		for (AssayAttributeDto ta : result.getAssay().getAttributes()) {
			if (ta.getOutputType() != OutputType.OUTPUT)
				continue;
			AssayResultValueDto rv = getResultValue(result, ta);
			if (rv == null)
				return null;
			return assayResultValueService.getDoubleValue(rv);
		}
		return null;
	}

	public List<AssayResultValueDto> getOutputResultValues(AssayResultDto result, OutputType outputType) {
		List<AssayAttributeDto> atts = assayService.getAttributes(result.getAssay(), outputType);
		List<AssayResultValueDto> res = new ArrayList<>();
		for (AssayAttributeDto att : atts) {
			res.add(getResultValue(result, att));
		}
		return Collections.unmodifiableList(res);
	}
	
	public String getOutputResultValuesAsString(AssayResultDto result) {
		if (result.getAssay() == null)
			return "No test";
		List<AssayAttributeDto> atts = assayService.getOutputAttributes(result.getAssay());
		StringBuilder sb = new StringBuilder();
		for (AssayAttributeDto att : atts) {
			if (getResultValue(result, att) == null || getResultValue(result, att).getTextValue() == null
					|| getResultValue(result, att).getTextValue().length() == 0)
				continue;
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(att.getName() + "=" + getResultValue(result, att).getTextValue());
		}
		String comments = result.getComments();
		if (comments != null && comments.length() > 0) {
			if (sb.length() > 0)
				sb.append(" / ");
			sb.append(comments);
		}
		return sb.toString();
	}

	public String getInfoResultValuesAsString(AssayResultDto result) {
		if (result.getAssay() == null)
			return "No test";
		List<AssayAttributeDto> atts = assayService.getInfoAttributes(result.getAssay());
		StringBuilder sb = new StringBuilder();
		for (AssayAttributeDto att : atts) {
			if (getResultValue(result, att) == null || getResultValue(result, att).getTextValue() == null
					|| getResultValue(result, att).getTextValue().length() == 0)
				continue;
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(att.getName() + "=" + getResultValue(result, att).getTextValue());
		}
		return sb.toString();
	}

	public List<AssayResultValueDto> getOutputResultValues(AssayResultDto result) {
		List<AssayAttributeDto> atts = assayService.getOutputAttributes(result.getAssay());
		List<AssayResultValueDto> res = new ArrayList<>();
		for (AssayAttributeDto att : atts) {
			res.add(getResultValue(result, att));
		}
		res.remove(null);
		return Collections.unmodifiableList(res);
	}

	public AssayResultValueDto getOutputResultValue(AssayResultDto result, String attributeName) {
		List<AssayAttributeDto> atts = assayService.getOutputAttributes(result.getAssay());
		for (AssayAttributeDto att : atts) {
			if (att.getName().equals(attributeName))
				return getResultValue(result, att);
		}
		return null;
	}
	
	public List<AssayResultValueDto> getInputResultValues(AssayResultDto result) {
		List<AssayAttributeDto> atts = assayService.getInputAttributes(result.getAssay());
		List<AssayResultValueDto> res = new ArrayList<>();
		for (AssayAttributeDto att : atts) {
			res.add(getResultValue(result, att));
		}
		return Collections.unmodifiableList(res);
	}

	public boolean setBiosample(AssayResultDto result, BiosampleDto biosample) {
		setBiosample(result, biosample, false);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void setBiosample(AssayResultDto result, BiosampleDto biosample, boolean cross) {
		// prevent endless loop
		if (nullSupportEqual(result.getBiosample(), biosample))
			return;
		// remove from the old owner
		if (result.getBiosample() != null && !result.getBiosample().equals(biosample))
			biosampleService.removeResult(result.getBiosample(), result, true);
		// set new owner
		result.setBiosample(biosample);
		// delte result, a result without Biosample should not exists.
		if(biosample == null) {
			delete(result);
		} else if (!cross)
			biosampleService.addResult(biosample, result, true);
	}

	@SuppressWarnings("deprecation")
	public void setPhase(AssayResultDto result, PhaseDto phase) {
		PhaseDto remove = result.getPhase();
		result.setPhase(phase);
		if (remove != null) 
			phaseService.remove(remove);
		if(phase!=null && phase.getStage().getStudy()!=null && !phase.getStage().getStudy().equals(result.getStudy())) 
			result.setStudy(phase.getStage().getStudy());
	}
	
	public Date getExecutionDateCalculated(AssayResultDto result) {
		Date executionDate = getExecutionDate(result);
		return executionDate == null ? getExecutionDatePlanned(result) : executionDate;
	}

	public Date getExecutionDatePlanned(AssayResultDto result) {
		return executionService.getExecutionDateCalculated(result.getBiosample(), getInheritedPhase(result));
	}

	public Date getExecutionDate(AssayResultDto result) {
		return result.getExecution() == null ? null : result.getExecution().getExecutionDate();
	}

	@SuppressWarnings("deprecation")
	public void setAssay(AssayResultDto res, AssayDto assay) {
		if (nullSupportEqual(res.getAssay(), assay))
			return;
		res.setAssay(assay);
		List<AssayResultValueDto> values = new ArrayList<>();
		for (AssayAttributeDto attribute : assay.getAttributes()) {
			AssayResultValueDto value = new AssayResultValueDto();
			value.setAssayResult(res);
			value.setAssayAttribute(attribute);
			values.add(value);
		}
		res.setValues(values);
	}

	public String suggestElb() {
		return suggestElb(UserUtil.getUsername());
	}

	public String suggestElb(String username) {
		return "ELB-" + (username == null ? "" : username + "-")
				+ new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());
	}

	public boolean computeFormula(Collection<AssayResultDto> results) {
		boolean updated = false;
		Map<AssayDto, List<AssayResultDto>> map = mapAssay(results);
		for (AssayDto test : map.keySet()) {

			// Loop though attributes of type formula
			for (AssayAttributeDto ta : test.getAttributes()) {
				if (ta.getDataType() != DataType.FORMULA)
					continue;
				String formula = ta.getParameters();
				Expression e;
				try {
					e = ExpressionHelper.createExpression(formula, test);
				} catch (Exception ex) {
					continue;
				}

				// Loop through each result to calculate the formula
				for (AssayResultDto result : map.get(test)) {

					updated = true;
					try {
						double res = ExpressionHelper.evaluate(e, result);
						setValue(result, ta, "" + res);
					} catch (Exception ex) {
						setValue(result, ta, "");
					}
				}
			}
		}
		return updated;
	}

	public Map<AssayDto, List<AssayResultDto>> mapAssay(Collection<AssayResultDto> results) {
		Map<AssayDto, List<AssayResultDto>> map = new LinkedHashMap<>();
		if (results == null) {			
			return map;
		}
		for (AssayResultDto b : results) {
			List<AssayResultDto> l = map.get(b.getAssay());
			if (l == null) {
				l = new ArrayList<>();
				map.put(b.getAssay(), l);
			}
			l.add(b);
		}
		return map;
	}

	public Set<BiosampleDto> getBiosamples(List<AssayResultDto> results) {
		Set<BiosampleDto> res = new HashSet<>();
		for (AssayResultDto r : results) {
			res.add(r.getBiosample());
		}
		return res;
	}

	public Set<PhaseDto> getPhases(List<AssayResultDto> results) {
		Set<PhaseDto> res = new HashSet<>();
		for (AssayResultDto r : results) {
			res.add(r.getPhase());
		}
		return res;
	}

	public Set<String> getInputs(List<AssayResultDto> results) {
		Set<String> res = new HashSet<>();
		for (AssayResultDto r : results) {
			res.add(getInputResultValuesAsString(r));
		}
		return res;
	}

	public String getInputResultValuesAsString(AssayResultDto result) {
		AssayDto assay = result.getAssay();
		if (assay == null)
			return "No test";
		StringBuilder sb = new StringBuilder();
		for (AssayAttributeDto att : assayService.getInputAttributes(assay)) {
			AssayResultValueDto rv = getResultValue(result, att);
			if (rv.getTextValue() != null && rv.getTextValue().length() > 0) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(rv.getTextValue());
			}
		}
		return sb.toString();
	}

	public Set<AssayDto> getAssays(List<AssayResultDto> results) {
		Set<AssayDto> res = new HashSet<>();
		for (AssayResultDto r : results) {
			res.add(r.getAssay());
		}
		return res;
	}

	public boolean isEmpty(AssayResultDto result) {
		AssayDto assay = result.getAssay();
		if (assay == null)
			return true;
		if (result.getBiosample() != null)
			return false;
		for (AssayAttributeDto att : assay.getAttributes()) {
			AssayResultValueDto rv = getResultValue(result, att);
			if (rv != null && rv.getTextValue() != null && rv.getTextValue().length() > 0)
				return false;
		}
		if (result.getComments() != null && result.getComments().length() > 0)
			return false;
		return true;
	}

	public void saveExperiment(boolean isNewExperiment, String experimentElb, List<AssayResultDto> results)
			throws Exception {
		if (results.size() == 0)
			throw new Exception("The results are empty");
		if (experimentElb == null)
			throw new Exception("The elb is required");

		for (AssayResultDto result : results) {
			if (result.getId() <= 0 && isEmpty(result))
				continue;
			if (result.getElb() != null && !result.getElb().equals(experimentElb))
				throw new Exception("All the ELBs must be equal");
			result.setElb(experimentElb);
		}
		persistResults(experimentElb, isNewExperiment, true, results);
	}

	private void persistResults(String experimentElb, boolean isNewExperiment, boolean removeOlderResults,
			Collection<AssayResultDto> results) throws Exception {
		// Quick check of integrity constraints
		for (AssayResultDto r : results) {
			// Check that if a phase is present, then there is a biosample without any
			// phase. (or delete the phase, if there is a match)
			if (r.getBiosample() == null)
				throw new Exception("The result has no biosample");
			// If result is attached to a sample then it belongs to the sample and the
			// sampling phase should be referenced
			if (r.getPhase() != null) {
				if (r.getBiosample().getInheritedPhase() != null) {
					if (r.getBiosample().getInheritedPhase().equals(r.getPhase())) {
						setPhase(r, null);
					}
				}
			}
		}
		if (experimentElb != null) {
			Map<Integer, AssayResultDto> id2result = mapIds(results);
			if(id2result.size()!=0) {
				List<AssayResult> lastUpdates = getAssayResultsByIds(id2result.keySet());
				for (AssayResult lastUpdate : lastUpdates) {
					Date lastDate = lastUpdate.getUpdDate();
					AssayResultDto r = id2result.get((lastUpdate.getId()));
					if (r != null && r.getUpdDate() != null && lastDate != null) {
						int diffSeconds = (int) ((lastDate.getTime() - r.getUpdDate().getTime()) / 1000L);
						if (diffSeconds > 0)
							throw new Exception("The result (" + r + ") has just been updated by " + lastUpdate.getUpdUser()
									+ " [" + diffSeconds
									+ "seconds ago].\nYou cannot overwrite those changes unless you reopen the newest version.");
					}
				}
			}
			List<AssayResultDto> before = getResults(ResultQuery.createQueryForElb(experimentElb));
			if (removeOlderResults) {
				// Delete outdated results
				for (AssayResultDto b : before) {
					if (!id2result.containsKey(b.getId())) {
						delete(b);
					}
				}
			}
		}

		// Compute formula if needed
		computeFormula(results);
		for (AssayResultDto result : results) {
			// If result is attached to a sample then it belongs to the study, in which the
			// sampling happened
			result.setStudy(result.getStudy() == null ? result.getBiosample().getStudy() : result.getStudy());
		}
		for (AssayResultDto result : results) {
			if (result.getId() == Constants.NEWTRANSIENTID && isEmpty(result))
				continue;
			// Make sure the dual-links are there
			for (AssayResultValueDto v : result.getValues())
				v.setAssayResult(result);
			save(result);
		}
	}

	public synchronized List<AssayResultDto> getResults(ResultQuery query) throws Exception {
		StringBuilder clause = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		if (query.getBids().size() > 0) {
			clause.append(" and " + QueryTokenizer.expandForIn("b.id", query.getBids()));
		}
		if (query.getPhase() != null) {
			clause.append(" and r.phase.id = " + query.getPhase().getId());
		}
		if (query.getSid() > 0) {
			clause.append(" and r.study.id = " + query.getSid());
		}
		if (query.getStudyIds() != null && query.getStudyIds().equalsIgnoreCase("NONE")) {
			clause.append(" and r.study is null");
		} else if (query.getStudyIds() != null && query.getStudyIds().length() > 0) {
			clause.append(" and (" + QueryTokenizer.expandOrQuery("r.study.studyId = ?", query.getStudyIds()) + ")");
		}

		if (query.getSids() != null && query.getSids().size() > 0) {
			clause.append(" and " + QueryTokenizer.expandForIn("r.study.id", query.getSids()));
		}

		if (query.getGroups() != null && query.getGroups().length() > 0) {
			clause.append(
					" and (" + QueryTokenizer.expandOrQuery("as1.subGroup.group.name = ?", query.getGroups()) + ")");
		}
		if (query.getSampleIds() != null && query.getSampleIds().length() > 0) {
			clause.append(" and (" + QueryTokenizer.expandOrQuery("b.sampleId = ?", query.getSampleIds()) + ")");
		}

		if (query.getTopSampleIds() != null && query.getTopSampleIds().length() > 0) {
			clause.append(
					" and (" + QueryTokenizer.expandOrQuery("b.topParent.sampleId = ?", query.getTopSampleIds()) + ")");
		}

		if (query.getContainerIds() != null && query.getContainerIds().length() > 0) {
			clause.append(" and ("
					+ QueryTokenizer.expandOrQuery("b.container.containerId = ?", query.getContainerIds()) + ")");
		}

		if (query.getContainerIds() != null && query.getContainerIds().length() > 0) {
			clause.append(" and ("
					+ QueryTokenizer.expandOrQuery("b.container.containerId = ?", query.getContainerIds()) + ")");
		}

		if (query.getElbs() != null && query.getElbs().length() > 0) {
			clause.append(" and (" + QueryTokenizer.expandOrQuery("r.elb = ?", query.getElbs()) + ")");
		}

		if (query.getQuality() != null) {
			if (query.getQuality().getId() <= Quality.VALID.getId()) {
				clause.append(" and (r.quality is null or r.quality >= " + query.getQuality().getId() + ")");
			} else {
				clause.append(" and r.quality >= " + query.getQuality().getId());
			}
		}

		if (query.getAssayIds() != null && query.getAssayIds().size() >= 1) {
			clause.append(" and (1=0");
			for (int testId : query.getAssayIds()) {
				clause.append(" or (r.test.id = " + testId);
				for (AssayAttributeDto att : query.getAttribute2Values().keySet()) {
					if (att.getAssay().getId() != testId)
						continue;
					Set<String> attVal = query.getAttribute2Values().get(att);
					if (attVal == null || attVal.size() == 0)
						continue;

					StringBuilder sb = new StringBuilder();
					for (String s : attVal) {
						if (sb.length() > 0)
							sb.append(" or ");
						if (s == null || s.length() == 0) {
							sb.append("v.value is null");
						} else {
							sb.append("v.value = '" + (s.replace("'", "''")) + "'");
						}
					}
					clause.append(" and (r IN (SELECT v.result FROM ResultValue v WHERE v.attribute.id = " + att.getId()
							+ " and (" + sb + ")))");
				}
				clause.append(")");
			}
			clause.append(")");
		}

		if (query.getBiotype() != null && query.getBiotype().length() > 0) {
			clause.append(" and " + QueryTokenizer.expandForIn("r.biosample.biotype.name", query.getBiotype()));
		}

		if (query.getUpdUser() != null && query.getUpdUser().length() > 0) {
			clause.append(" and r.tracing.updUser = ?");
			parameters.add(query.getUpdUser());
		}
		if (query.getUpdDate() != null && query.getUpdDate().length() > 0) {
			String digits = MiscUtils.extractStartDigits(query.getUpdDate());
			if (digits.length() > 0) {
				clause.append(" and r.tracing.updDate > ?");
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
				parameters.add(cal.getTime());
			}
		}
		if (query.getCreDays() != null && query.getCreDays().length() > 0) {
			String digits = MiscUtils.extractStartDigits(query.getCreDays());
			if (digits.length() > 0) {
				clause.append(" and r.tracing.creDate > ?");
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
				parameters.add(cal.getTime());
			}
		}

		List<AssayResult> results = assayResultDao.getResults(query);
		List<AssayResultDto> res = new ArrayList<AssayResultDto>();
		for (AssayResult result : results) {
			res.add(map(result));
		}
		// Post filters
		if (query.getPhases() != null && query.getPhases().length() > 0) {

			Set<String> set = new HashSet<>(
					Arrays.asList(MiscUtils.split(query.getPhases(), MiscUtils.SPLIT_SEPARATORS_WITH_SPACE)));
			if (set.size() > 0) {
				List<AssayResultDto> filtered = new ArrayList<>();
				for (AssayResultDto r : res) {
					if (r.getBiosample() == null)
						continue;
					if (r.getBiosample().getInheritedPhase() == null)
						continue;
					if (!set.contains(phaseService.getShortName(r.getBiosample().getInheritedPhase())))
						continue;
					filtered.add(r);
				}
				res = filtered;
			}
		}
		return res;
	}

	public StageDto getStage(AssayResultDto result) {
		return getInheritedPhase(result) == null
				? result.getExecution() == null ? null 
						: executionService.getStageCalculated(result.getExecution(), result.getBiosample(), result.getStudy())
				: getInheritedPhase(result).getStage();
	}

	public SubGroupDto getSubgroup(AssayResultDto r) {
		BiosampleDto sample = r.getBiosample();
		if (sample==null) return null;
		if (sample.getStudy() != null) {
			return biosampleService.getSubGroup(sample.getTopParent(), getStage(r));
		}
		return biosampleService.getSubGroup(sample, getStage(r));
	}

	public GroupDto getGroup(AssayResultDto r) {
		BiosampleDto sample = r.getBiosample();
		if (sample==null) return null;
		if (sample.getStudy() != null) {
			return biosampleService.getGroup(sample.getTopParent(), getStage(r));
		}
		return biosampleService.getGroup(sample, getStage(r));
	}

	public Duration getDisplayPhaseCalculated(AssayResultDto assayResult) {
		return getDisplayPhaseCalculated(assayResult, null);
	}
	
	public Duration getDisplayPhaseCalculated(AssayResultDto assayResult, StageDto inStage) {
		return getInheritedPhase(assayResult) != null ?
				biosampleService.getDisplayDuration(assayResult.getBiosample(), inStage, getInheritedPhase(assayResult)) :
					assayResult.getExecution() == null ?
						null :
							executionService.getDisplayPhaseCalculated(assayResult.getExecution(), assayResult.getBiosample(), assayResult.getStudy(), inStage);
	}

	public StudyAction getAction(AssayResultDto assayResult) {
		SubGroupDto sg = biosampleService.getSubGroup(assayResult.getBiosample().getTopParent(), getInheritedPhase(assayResult));
		if(sg==null)
			return null;
		for(IndividualAction sa : subGroupService.getAllActions(sg, assayResult.getPhase())) {
			StudyAction s = sa.getStudyAction();
			if(s instanceof Measurement && ((Measurement) s).getAssay().equals(assayResult.getAssay())){
				return s;
			}
		}
		return null;
	}

	public Duration getDeviation(AssayResultDto assayResult) {
		return assayResult.getExecution() == null ? null : executionService.getDeviation(assayResult.getExecution(), assayResult.getBiosample(), getInheritedPhase(assayResult));
	}
	
	public boolean setDeviation(AssayResultDto row, Duration value) {
		if (row.getExecution() == null) {
			if (value.toMillis() == 0) 
				return false;
			row.setExecution(new Execution());
		}
		return executionService.setDeviation(row.getExecution(), value, row.getBiosample(), row.getPhase());
	}

	public AssayResultValueDto getResultValue(AssayResultDto assayResult, String attName) {
		AssayDto test = assayResult.getAssay();
		assert test!=null;
		assert attName!=null;

		for (AssayResultValueDto v : assayResult.getValues()) {
			if(v.getAssayAttribute().getName().equals(attName)) return v;
		}
		//Check that the attribute is valid
		for (AssayAttributeDto a : test.getAttributes()) {
			if(a.getName().equals(attName)) {
				AssayResultValueDto v = new AssayResultValueDto(assayResult, a, "");
				assayResult.getValues().add(v);
				return v;
			}
		}
		return null;
	}
	
	public void attachOrCreateStudyResults(StudyDto study, boolean skipEmptyPhase, Collection<BiosampleDto> biosamples,
			Collection<Measurement> measurements, PhaseDto phaseFilter, String elbForCreatingMissingOnes, Date executionDate) throws Exception {
		if(biosamples.size()==0) return;
		Set<BiosampleDto> allBiosamples = new HashSet<>(biosamples);
		//Query all results associated to those samples
		ResultQuery q = new ResultQuery();
		q.setQuality(null);
		q.setBids(getIds(allBiosamples));
		q.getAssayIds().addAll(getIds(studyActionService.getTests(measurements)));
		q.setPhase(phaseFilter);

		List<AssayResultDto> results = queryResults(q, null);

		//Map the result to the associated biosample
		Map<Integer,BiosampleDto> map = mapIds(allBiosamples);
		for (AssayResultDto result : results) {
			PhaseDto p = getInheritedPhase(result);

			if(phaseFilter==null && skipEmptyPhase && result.getBiosample().getInheritedPhase()==null) continue; //if no phase filter -> returns only samples
			if(phaseFilter!=null && !phaseFilter.equals(p)) continue; //if phase filter -> returns only results with samples and animals at this phase

			BiosampleDto b = map.get(result.getBiosample().getId());
			if(b!=null) {
				biosampleService.addResult(b, result);
			}
		}
		//Create missing results
		if(elbForCreatingMissingOnes!=null) {
			for (BiosampleDto biosample : allBiosamples) {
				PhaseDto p = phaseFilter == null ||  biosampleService.getGroup(biosample,  phaseFilter.getStage())==null? biosample.getInheritedPhase(): phaseFilter;

				if(phaseFilter==null && skipEmptyPhase && biosample.getInheritedPhase()==null) continue; //if no phase filter -> returns only samples
				if(phaseFilter!=null && !phaseFilter.equals(p)) continue; //if phase filter -> returns only results with samples and animals at this phase
				for(Measurement measurement : measurements) {
					if(biosampleService.getResultByPhaseAndAssayNameAndParameters(biosample, p, measurement.getAssay().getName(), measurement.getParameters())==null && needTest(biosample, measurement)) {
						AssayResultDto r = new AssayResultDto();
						setAssay(r, measurement.getAssay());
						r.setStudy(study);
						r.setElb(elbForCreatingMissingOnes);
						setBiosample(r, biosample);
						setExecutionDate(r, executionDate);
						setPhase(r, phaseFilter == null || biosampleService.getGroup(biosample, phaseFilter.getStage())==null? null: phaseFilter);
						String[] parameters = measurement.getParameters();
						List<AssayResultValueDto> inputResultValues = getInputResultValues(r);
						for (int i = 0; i < inputResultValues.size(); i++) {
							AssayResultValueDto intputResultValue = inputResultValues.get(i);
							if(parameters.length<=i) {
								if(intputResultValue.getAssayAttribute().isRequired()) {
									throw new InvalidAttributesException("The field "+intputResultValue.getAssayAttribute().getName() + " is mandatory");
								}else {
									intputResultValue.setTextValue("");
								}
							}else {
								intputResultValue.setTextValue(parameters[i]);
							}
						}
					}
				}
			}
		}
	}
	
	private boolean needTest(BiosampleDto biosample, Measurement measurement) {
		String name = measurement.getAssay().getName();
		if(name.equals(Constants.LENGTH_TESTNAME)||name.equals(Constants.WEIGHING_TESTNAME)||name.equals(Constants.OBSERVATION_TESTNAME))
			return true;
		SamplingDto sampling = biosample.getAttachedSampling();
		if(sampling==null)
			return true;
		for(Measurement m : samplingService.getMeasurements(sampling)) {
			if(!m.getAssay().getName().equals(name))
				continue;
			boolean isOk=true;
			int i =0;
			for(String s : Arrays.asList(m.getParameters())){
				if (!s.equals(measurement.getParameters()[i])) {
					isOk=false;
					break;
				}
				i=i+1;
			}
			if(isOk)
				return true;
		}
		return false;
	}

	public List<AssayResultDto> queryResults(ResultQuery q, User user) throws Exception  {
		//Create a new query per Test
		List<AssayResultDto> results = getResults(q);

		//Check rights
		if(user!=null) {
			for (Iterator<AssayResultDto> iterator = results.iterator(); iterator.hasNext();) {
				AssayResultDto r = iterator.next();
				if(!spiritRights.canRead(r, user)) {
					iterator.remove();
				}
			}
		}
		postLoad(results);
		return results;
	}

	private void postLoad(Collection<AssayResultDto> results) {
		ListHashMap<String, AssayResultValueDto> sampleId2rvs = new ListHashMap<>();
		Map<AssayDto, List<AssayResultDto>> map = mapAssay(results);
		for (AssayDto test : map.keySet()) {
			if (test == null)
				continue;
			for(AssayAttributeDto t: test.getAttributes()) {
				if(t.getDataType()==DataType.BIOSAMPLE) {
					for(AssayResultDto result: map.get(test)) {
						AssayResultValueDto rv  = getResultValue(result, t);
						if(rv!=null && rv.getTextValue().length()>0) {
							sampleId2rvs.add(rv.getTextValue(), rv);
						}
					}
				}
			}
		}
		if(sampleId2rvs.keySet() != null && !sampleId2rvs.keySet().isEmpty()) {	
			List<String> sampleIds = new ArrayList<String>(sampleId2rvs.keySet());
			for(BiosampleDto b: biosampleService.getBiosampleDtoBySampleIds(sampleIds)) {
				for(AssayResultValueDto rv : sampleId2rvs.get(b.getSampleId())) {
					rv.setLinkedBiosample(b);
				}
			}
		}
	}
	
	public void attachOrCreateStudyResultsToTops(StudyDto study) throws Exception {
		attachOrCreateStudyResultsToTops(study, null, null, null);
	}

	/**
	 * Load the results of Weighing, FoodWater, Observation, extraMesurements that could be performed on the top specimen
	 * @param study
	 * @param allBiosamples
	 * @param phaseFilter
	 * @param elbForCreatingMissingOnes
	 * @throws Exception
	 */
	public void attachOrCreateStudyResultsToTops(StudyDto study, Collection<BiosampleDto> allBiosamples, PhaseDto phaseFilter, String elbForCreatingMissingOnes) throws Exception  {
		if(allBiosamples==null) allBiosamples = studyService.getSubjectsSorted(study);
		AssayDto weighingTest = assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME);
		AssayDto fwTest = assayService.getAssayDtoByName(Constants.FOODWATER_TESTNAME);
		AssayDto obsTest = assayService.getAssayDtoByName(Constants.OBSERVATION_TESTNAME);
		if(weighingTest==null || fwTest==null || obsTest==null) 
			throw new Exception("You must create the tests: " + Constants.WEIGHING_TESTNAME + "(output=weight [g]), " + Constants.FOODWATER_TESTNAME + "(output=[food [g], water [ml]]) , " + Constants.OBSERVATION_TESTNAME + "(output=observation)");

		List<Measurement> measurements = new ArrayList<>();
		measurements.add(new Measurement(weighingTest));
		measurements.add(new Measurement(fwTest));
		measurements.add(new Measurement(obsTest));
		measurements.addAll(studyService.getAllMeasurementsFromActions(study));
		attachOrCreateStudyResults(study, false, allBiosamples, measurements, phaseFilter, elbForCreatingMissingOnes, null);
	}


	public void attachOrCreateStudyResultsToSamples(StudyDto study) throws Exception {
		attachOrCreateStudyResultsToSamples(study, null, null, null);
	}

	/**
	 * Load the results of Weighing, Length, Observation that could be performed on the samples
	 * @param study
	 * @param phaseFilter
	 * @throws Exception
	 */
	public void attachOrCreateStudyResultsToSamples(StudyDto study, Collection<BiosampleDto> allBiosamples, PhaseDto phaseFilter, String elbForCreatingMissingOnes) throws Exception  {
		if(allBiosamples==null) allBiosamples = studyService.getSubjectsSorted(study);
		AssayDto weighingTest = assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME);
		AssayDto lengthTest = assayService.getAssayDtoByName(Constants.LENGTH_TESTNAME);
		AssayDto obsTest = assayService.getAssayDtoByName(Constants.OBSERVATION_TESTNAME);
		if(weighingTest==null || lengthTest==null || obsTest==null) 
			throw new Exception("You must create the tests: " + Constants.WEIGHING_TESTNAME + "(output=weight [g]), " + Constants.LENGTH_TESTNAME + "(output=length) , " + Constants.OBSERVATION_TESTNAME + "(output=observation)");

		List<Measurement> measurements = new ArrayList<>();
		measurements.add(new Measurement(weighingTest));
		measurements.add(new Measurement(lengthTest));
		measurements.add(new Measurement(obsTest));
		measurements.addAll(studyService.getAllMeasurementsFromSamplings(study));
		attachOrCreateStudyResults(study, true, allBiosamples, measurements, phaseFilter, elbForCreatingMissingOnes, null);
	}
	
	/**
	 * Load the results of Weighing, Length, Observation that could be performed on the samples
	 * @param study
	 * @param phaseFilter
	 * @throws Exception
	 */
	public void attachOrCreateStudyResultsToSamplesAtDeath(StudyDto study, Collection<BiosampleDto> allBiosamples, PhaseDto phaseFilter, String elbForCreatingMissingOnes, Date executionDate) throws Exception  {
		if(allBiosamples==null) allBiosamples = studyService.getSubjectsSorted(study);
		AssayDto weighingTest = assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME);
		AssayDto lengthTest = assayService.getAssayDtoByName(Constants.LENGTH_TESTNAME);
		AssayDto obsTest = assayService.getAssayDtoByName(Constants.OBSERVATION_TESTNAME);
		if(weighingTest==null || lengthTest==null || obsTest==null)
			throw new Exception("You must create the tests: " + Constants.WEIGHING_TESTNAME + "(output=weight [g]), " + Constants.LENGTH_TESTNAME + "(output=length) , " + Constants.OBSERVATION_TESTNAME + "(output=observation)");

		List<Measurement> measurements = new ArrayList<>();
		measurements.add(new Measurement(weighingTest));
		measurements.add(new Measurement(lengthTest));
		measurements.add(new Measurement(obsTest));
		measurements.addAll(studyService.getAllMeasurementsFromSamplings(study));
		attachOrCreateStudyResults(study, false, allBiosamples, measurements, phaseFilter, elbForCreatingMissingOnes, executionDate);
	}
	
	public Boolean setExecutionDate(AssayResultDto result, Date executionDate) {
		if (result.getExecution() == null) {
			if (executionDate == null) return false;
			result.setExecution(new Execution());
		}
		result.getExecution().setExecutionDate(executionDate);
		return true;
	}
	
	public AssayResultDto getPrevious(AssayResultDto current, List<AssayResultDto> from) {
		if(current==null || (getInheritedPhase(current)==null && getExecutionDate(current)==null)) return null;
		assert from!=null;
		AssayResultDto sel = null;
		for (AssayResultDto r : from) {
			if(getFirstOutputValue(r)==null) continue;
			if(current.getBiosample()!=null && !current.getBiosample().equals(r.getBiosample())) continue;
			if(!r.getAssay().equals(current.getAssay())) continue;

			Date rDate = getExecutionDateCalculated(r);
			if(rDate==null) continue;
			if(rDate.compareTo(getExecutionDateCalculated(current))>=0) continue;

			if(sel==null) {
				sel = r;
			} else if(rDate.compareTo(getExecutionDateCalculated(sel))>0) {
				sel = r;
			}
		}
		return sel;
	}

	public AssayResultDto getFirst(AssayResultDto current, List<AssayResultDto> from) {
		assert current!=null;
		assert getInheritedPhase(current)!=null || getExecutionDate(current)!=null;
		assert from!=null;

		AssayResultDto sel = null;
		for (AssayResultDto r : from) {
			if(getFirstOutputValue(r)==null) continue;
			if(!r.getAssay().equals(current.getAssay())) continue;
			if(current.getBiosample()!=null && !current.getBiosample().equals(r.getBiosample())) continue;

			if(getExecutionDateCalculated(r)==null) continue;
			if(getExecutionDateCalculated(r).compareTo(getExecutionDateCalculated(current))>=0) continue;
			if(getFirstOutputValue(r)==null) continue;

			if(sel==null) {
				sel = r;
			} else if(getExecutionDateCalculated(r).compareTo(getExecutionDateCalculated(sel))<0) {
				sel = r;
			}
		}

		return sel;
	}
	
	public String getDetailsWithoutSampleId(AssayResultDto r) {
		return (r.getAssay()==null?"": r.getAssay().getName()+" ")
				+ (r.getPhase()==null?"": " " + phaseService.getShortName(r.getPhase()) + " ")
				+ getInputResultValuesAsString(r)
				+ ": " + getOutputResultValuesAsString(r);
	}

	public Map<BiosampleDto, List<AssayResultDto>> mapBiosample(List<AssayResultDto> results) {
		Map<BiosampleDto, List<AssayResultDto>> map = new HashMap<>();
		if(results==null) return map;
		for (AssayResultDto b : results) {
			List<AssayResultDto> l = map.get(b.getBiosample());
			if(l==null) {
				l = new ArrayList<>();
				map.put(b.getBiosample(), l);
			}
			l.add(b);
		}
		return map;
	}

	public  Map<String, String> getResultValuesAsMap(AssayResultDto row, OutputType outputType) {
		Map<String, String> res = new LinkedHashMap<>();
		for (AssayAttributeDto att : row.getAssay().getAttributes()) {
			if(att.getDataType()==DataType.FILES || att.getDataType()==DataType.D_FILE) continue;
			if(att.getOutputType()==outputType) {
				res.put(att.getName(), getResultValue(row, att).getTextValue());
			}
		}
		return Collections.unmodifiableMap(res);
	}

	public void mapValues(AssayResultDto assayResult) {
		assayResult.setValues(assayResultValueService.map(assayResultValueService.getAssayResultValuesByAssayResult(assayResult.getId())));
	}
	
	public boolean isPhaseDependant(List<AssayResultDto> results, boolean date, boolean calculate,
			StageDto referenceStage) {
		SetHashMap<String, String> key2phases = new SetHashMap<>();
		for (AssayResultDto r : results) {
			if(getInheritedPhase(r)==null) continue;
			if(r.getBiosample()==null) continue;

			String key = r.getBiosample().getTopParent().getId() + "_" + r.getAssay().getId() + "_" +getInputResultValues(r);
			String object = getXString(r, date, calculate, referenceStage);
			key2phases.add(key, object);
			if(key2phases.get(key).size()>1) return true;
		}
		return false;
	}

	public String getXString(AssayResultDto r, boolean date, boolean calculate, StageDto referenceStage) {
		if (date) {
			if (calculate) {
				return FormatterUtils.formatDateSorting(getExecutionDateCalculated(r));
			} else {
				return FormatterUtils.formatDateSorting(getExecutionDate(r));
			}
		} else {
			String stageText = referenceStage == null && getStage(r) != null ? stageService.getNo(getStage(r)) + " " : "";
			if (calculate) {
				return (stageText) + (getDisplayPhaseCalculated(r, referenceStage) == null ? "" : phaseService.getDurationStringDays(getDisplayPhaseCalculated(r, referenceStage)));
			} else {
				return (stageText) + (getInheritedPhase(r) == null ? "" : phaseService.getDurationStringDays(biosampleService.getDisplayDuration(r.getBiosample(), referenceStage, getInheritedPhase(r))));
			}
		}
	}

	public Duration getDuration(AssayResultDto result, boolean date, boolean calculate, StageDto referenceStage) {
		if (date) {
			if (calculate) {
				return Duration.between(Instant.EPOCH, getExecutionDateCalculated(result).toInstant());
			} else {
				return Duration.between(Instant.EPOCH, getExecutionDate(result).toInstant());
			}
		} else {
			if (calculate) {
				return getDisplayPhaseCalculated(result, referenceStage);
			} else {
				return biosampleService.getDisplayDuration(result.getBiosample(), referenceStage, getInheritedPhase(result));
			}
		}
	}

	public void changeOwnership(List<AssayResultDto> results, User toUser, User updater) throws ValidationException {
		for (AssayResultDto r : results) {
			if(r.getId()<=0) continue;
			if(!spiritRights.canDelete(r, updater)) 
				throw new ValidationException(updater+" is not allowed to change the ownership of "+r);
			r.setUpdUser(updater.getUsername());
			r.setCreUser(toUser.getUsername());
		}
		save(results);
	}

	public List<String> getElbsForStudy(String studyId) {
		if(studyId==null || studyId.length()==0) 
			return new ArrayList<>();
		return assayResultDao.getElbsForStudy(studyId);
	}

	public List<BiotypeDto> getBiotypes(String studyId, Set<Integer> assayIds) {
		List<BiotypeDto> res = new ArrayList<BiotypeDto>();
		if(studyId==null || studyId.trim().length()==0) 
			return res;
		List<Biotype> biotypes = biotypeService.getBiotypes(studyId, assayIds);
		if(biotypes != null) {
			res.addAll(biotypeService.map(biotypes));
		}
		return res;
	}

	public List<String> getResultsByStudyAndAttribute(Integer studyId, Integer attributeId) {
		return assayResultDao.getResultsByStudyAndAttribute(studyId, attributeId);
	}

	public AssayResultDto createWeightResult(AssignmentDto a, AssayResultDto result) {
		if (result != null) 
			return result;
		AssayDto weighingAssay = assayService.getAssayDtoByName(Constants.WEIGHING_TESTNAME);
		result = attachWeightResultFromDB(a, a.getBiosample(), a.getStage(), weighingAssay);
		if (result != null) return result;
		result = new AssayResultDto();
		assayResultService.setAssay(result, weighingAssay);
		setBiosample(result, a.getBiosample());
		setPhase(result, stageService.getFirstPhase(a.getStage()));
		result.setStudy(a.getStage().getStudy());
		assignmentService.addResultAssignment(a, resultAssignmentService.newResultAssignmentDto(a, result));
		return result;
	}

	private AssayResultDto attachWeightResultFromDB(AssignmentDto a,BiosampleDto biosample, StageDto stage, AssayDto weighingAssay) {
		AssayResultDto weight = null;
		if (biosample != null && stage != null) {
			try {
				attachOrCreateStudyResults(stage.getStudy(), false, Collections.singleton(biosample),
						Collections.singleton(new Measurement(weighingAssay)),
						stageService.getFirstPhase(stage), null, null);
				weight = biosampleService.getFirstResultByPhaseAndAssayName(biosample, stageService.getFirstPhase(stage), weighingAssay.getName());
				if (weight != null)
					assignmentService.addResultAssignment(a, resultAssignmentService.newResultAssignmentDto(a, weight));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return weight;
	}

	public boolean isDuplicate(AssayResultDto result, List<AssayResultDto> results) {
		for(AssayResultDto res : results) {
			if(res.equals(result) || !res.getAssay().equals(result.getAssay()))
				continue;
			int i =0;
			boolean missResult=false;
			for(String s : Arrays.asList(getMeasurementParameters(result))){
				if(getMeasurementParameters(res)[i]!=null && !getMeasurementParameters(res)[i].equals("")) {
					if(s==null||s.equals(""))
						missResult=true;
					else if(!s.equals(getMeasurementParameters(res)[i])) {
						missResult=false;
						break;
					}
				}else {
					if(s!=null && !s.equals("") && !s.equals(getMeasurementParameters(res)[i])) {
						missResult=false;
						break;
					}
				}
				i=i+1;
			}
			if(missResult) {
				return false;
			}
		}
		return true;
	}

	public List<AssayResultDto> findDuplicates(String elb1, String elb2, FindDuplicateMethod method, User user) throws Exception {
		
		Set<AssayResultDto> res = new HashSet<AssayResultDto>();
		//Load Experiments
		List<AssayResultDto> results1 = queryResults(ResultQuery.createQueryForElb(elb1), user);
		List<AssayResultDto> results2;
		if(elb1.equals(elb2)) {
			results2 = results1;
		} else {
			results2 = queryResults(ResultQuery.createQueryForElb(elb2), user);
		}

		if(results1.size()==0) throw new Exception("The elb "+elb1+" is invalid or you don't have any rights");
		if(results2.size()==0) throw new Exception("The elb "+elb2+" is invalid or you don't have any rights");

		Set<Integer> keepIds = new HashSet<>();
		//Find duplicates
		firstLoop: for (int i1 = 0; i1 < results1.size(); i1++) {
			AssayResultDto r1 = results1.get(i1);
			if(keepIds.contains(r1.getId())) continue;
			nextResult: for (int i2 = (elb1.equals(elb2)? i1+1: 0); i2 < results2.size(); i2++) {
				AssayResultDto r2 = results2.get(i2);
				if(keepIds.contains(r2.getId())) continue;
				if(r1==r2) continue;
				if(!r1.getAssay().equals(r2.getAssay())) continue;
				if(!r1.getBiosample().equals(r2.getBiosample())) continue;
				if(!r1.getPhase().equals(r2.getPhase())) continue;

				boolean differentOutput = false;
				List<AssayResultValueDto> resultVlues = getOutputResultValues(r1);
				for (AssayResultValueDto rv1 : resultVlues) {
					String v1 = rv1.getTextValue();
					AssayResultValueDto rv2 = getResultValue(r2, rv1.getAssayAttribute());
					if(rv2==null) continue nextResult;

					String v2 = rv2.getTextValue();
					if(rv1.getAssayAttribute().getOutputType()== OutputType.OUTPUT) { //Output
						if((v1==null && v2==null) || (v1!=null && v1.equals(v2))) {
							//Same output parameter
						} else {
							differentOutput = true;
						}
					} else if(rv1.getAssayAttribute().getOutputType()== OutputType.INPUT) { //Input
						if((v1==null && v2==null) || (v1!=null && v1.equals(v2))) {
							//Same Input Parameter
						} else {
							continue nextResult;
						}
					}

				}

				if(differentOutput) {
					//Ok: duplicate measurements with different output
				} else {
					//same results
					long diffSec = (r1.getCreDate().getTime() - r2.getCreDate().getTime()) / 1000;
					if(method==FindDuplicateMethod.RETURN_ALL) {
						res.add(r1);
						res.add(r2);
					} else if(method==FindDuplicateMethod.RETURN_FIRST_ELB) {
						res.add(r1);
						keepIds.add(r2.getId());
						continue firstLoop;
					} else if(method==FindDuplicateMethod.RETURN_EXCEPT_FIRST_ELB) {
						res.add(r2);
						keepIds.add(r1.getId());
					} else if(method==FindDuplicateMethod.RETURN_NEWEST_2MNS) {
						if(diffSec>2*60) {
							res.add(r1);
							continue firstLoop;
						} else if(diffSec<-2*60) {
							res.add(r2);
							continue firstLoop;
						}
					} else if(method==FindDuplicateMethod.RETURN_OLDEST_2MNS) {
						if(diffSec>2*60) {
							res.add(r2);
							continue firstLoop;
						} else if(diffSec<-2*60) {
							res.add(r1);
							continue firstLoop;
						}
					} else {
						throw new IllegalArgumentException("Invalid method: "+method);
					}

				}

			}
		}
		logger.debug("ELB1 has "+results1.size()+" results / ELB2 has "+results1.size()+" results / Duplicates = "+res.size());

		List<AssayResultDto> list = new ArrayList<>(res);

		return list;

	}
}
