package com.idorsia.research.spirit.core.service;

import static com.idorsia.research.spirit.core.util.MiscUtils.nullSupportEqual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.RightLevel;
import com.idorsia.research.spirit.core.dao.NamedSamplingDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.SamplingParameterDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.dto.view.SubjectSet;
import com.idorsia.research.spirit.core.model.NamedSampling;
import com.idorsia.research.spirit.core.model.Sampling;
import com.idorsia.research.spirit.core.model.Study;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class NamedSamplingService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -5013379399858979947L;
	@Autowired
	private NamedSamplingDao namedSamplingDao;
	@Autowired
	private StudyService studyService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private StudyActionResultService studyActionResultService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, NamedSamplingDto> idToNamedSampling = (Map<Integer, NamedSamplingDto>) getCacheMap(NamedSamplingDto.class);
	
	public NamedSampling get(Integer id) {
		return namedSamplingDao.get(id);
	}
	
	public List<NamedSampling> getNamedSamplingsByStudy(Integer studyId) {
		return namedSamplingDao.getNamedTreatmentsByStudy(studyId);
	}
	
	public List<NamedSampling> list() {
		return namedSamplingDao.list();
	}
	
	public int getCount() {
		return namedSamplingDao.getCount();
	}

	public Integer saveOrUpdate(NamedSampling namedSampling) {
		return namedSamplingDao.saveOrUpdate(namedSampling);
	}

	public int addNamedSampling(NamedSampling namedSampling) {
		return namedSamplingDao.addNamedSampling(namedSampling);
	}

	public NamedSamplingDao getNamedSamplingDao() {
		return namedSamplingDao;
	}

	public void setNamedSamplingDao(NamedSamplingDao namedSamplingDao) {
		this.namedSamplingDao = namedSamplingDao;
	}

	public Set<StudyActionResult> getActionResults(NamedSamplingDto namedSampling, SubjectSet subjectSet,
		Study study, StageDto stage, Collection<PhaseDto> phases) {
		Set<StudyActionResult> samples = new HashSet<>();
		for (SamplingDto sampling : namedSampling.getSamplings()) {
			addSampleRec(sampling, samples);
		}
		Set<StudyActionResult> filteredSamples = new HashSet<>();
		for (StudyActionResult sample : samples) {
			filteredSamples.add(sample);
		}
		return filteredSamples;
	}
	
	private void addSampleRec(SamplingDto sampling, Set<StudyActionResult> samples) {
		samples.addAll(sampling.getSamples());
		for (SamplingDto child : sampling.getChildren()) {
			addSampleRec(child, samples);
		}
	}
	
	public String getDescription(NamedSamplingDto sampling) {
		Map<BiotypeDto, Integer> counter = new HashMap<>();
		for(SamplingDto s: sampling.getSamplings()) {
			Integer c = counter.get(s.getBiotype());
			counter.put(s.getBiotype(), c==null?1:c+1);
		}
		StringBuilder sb = new StringBuilder();
		for (BiotypeDto b : counter.keySet()) {
			if(sb.length()>0) sb.append(", ");
			sb.append(counter.get(b) + " "+b.getName());
		}
		return sb.toString();
	}
	
	public NamedSamplingDto getNamedSamplingDto(Integer id) {
		return map(get(id));
	}

	public Set<NamedSamplingDto> map(List<NamedSampling> namedSamplings) {
		Set<NamedSamplingDto> res = new HashSet<NamedSamplingDto>();
		for(NamedSampling namedSampling : namedSamplings) {
			res.add(map(namedSampling));
		}
		return res;
	}
	
	public NamedSamplingDto map(NamedSampling namedSampling) {
		NamedSamplingDto namedSamplingDto = idToNamedSampling.get(namedSampling.getId());
		if(namedSamplingDto==null) {
			namedSamplingDto = dozerMapper.map(namedSampling,NamedSamplingDto.class,"namedSamplingCustomMapping");
			if(idToNamedSampling.get(namedSampling.getId())==null)
				idToNamedSampling.put(namedSampling.getId(), namedSamplingDto);
			else
				namedSamplingDto=idToNamedSampling.get(namedSampling.getId());
		}
		return namedSamplingDto;
	}
	
	@Transactional
	public void save(NamedSamplingDto namedSampling) throws Exception {
		save(namedSampling, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(NamedSamplingDto namedSampling, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(namedSampling)) {
				savedItems.add(namedSampling);
				if(namedSampling.getId()!=Constants.NEWTRANSIENTID) {
					deleteChildren(namedSampling);
				}
				if(namedSampling.getStudy().getId()==Constants.NEWTRANSIENTID)studyService.save(namedSampling.getStudy(), true);
				namedSampling.setUpdDate(new Date());
				namedSampling.setUpdUser(UserUtil.getUsername());
				if(namedSampling.getId().equals(Constants.NEWTRANSIENTID)) {
					namedSampling.setCreDate(new Date());
					namedSampling.setCreUser(UserUtil.getUsername());
				}
				namedSampling.setId(saveOrUpdate(dozerMapper.map(namedSampling, NamedSampling.class, "namedSamplingCustomMapping")));
				idToNamedSampling.put(namedSampling.getId(), namedSampling);
				if(namedSampling.getSamplingsNoMapping()!=null)
					for(SamplingDto sampling : namedSampling.getSamplings()) {
						samplingService.save(sampling, true);
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
	private void deleteChildren(NamedSamplingDto namedSampling) throws Exception {
		if(namedSampling.getSamplingsNoMapping()!=null) {
			for(Sampling s : samplingService.getbyNamedSampling(namedSampling.getId())) {
				Boolean found = false;
				for(SamplingDto child : namedSampling.getSamplings()){
					if(s.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					samplingService.delete(samplingService.map(s), true);
				}
			}
		}
	}

	@Transactional
	public void delete(NamedSamplingDto namedSampling) throws Exception {
		delete(namedSampling, false);
	}
	
	protected void delete(NamedSamplingDto namedSampling, Boolean cross) throws Exception {
		for(ActionPatternsDto ap : actionPatternsService.map(actionPatternsService.getByAction(namedSampling.getId(), namedSampling.getType().name()))) {
			actionPatternsService.delete(ap, true);
		}
		namedSamplingDao.delete(namedSampling.getId());
	}
	
	public boolean setStudy(NamedSamplingDto namedSampling, StudyDto study) {
		try {
			StudyActionResultQuery studyActionResultQuery = new StudyActionResultQuery();
			studyActionResultQuery.setAction(namedSampling);
			studyActionResultQuery.setStudy(namedSampling.getStudy()!=null ? namedSampling.getStudy() : study);
			studyActionResultService.checkBeforeDeleteStudyActionResult(Constants.STUDY, studyActionResultQuery);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if(nullSupportEqual(namedSampling.getStudy(), study)) {
			updateActions(namedSampling);
		}else {
			setStudy(namedSampling, study, false);
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void setStudy(NamedSamplingDto namedSampling, StudyDto study, boolean cross) {
		//prevent endless loop
		if (nullSupportEqual(namedSampling.getStudy(), study))
			return;
		//remove from the old owner
		if (namedSampling.getStudy() !=null && !(cross && study == null))
			studyService.removeNamedSampling(namedSampling.getStudy(), namedSampling, true);
		//set new owner
		namedSampling.setStudy(study);
		//set myself to new owner
		if (study !=null && !cross)
			studyService.addNamedSampling(study, namedSampling, true);
	}
	
	private void updateActions(NamedSamplingDto namedSampling) {
		if(namedSampling.getNecropsy() && namedSampling.getId()==Constants.NEWTRANSIENTID)
			return;
		for(StageDto stage : namedSampling.getStudy().getStages()) {
			for(ActionPatternsDto ap : stage.getActionPatterns()) {
				if(ap.getAction() instanceof Disposal) {
					Disposal disp = (Disposal)ap.getAction();
					if(namedSampling.equals(disp.getSampling()))
						disp.setSampling(namedSampling);
				}
				if(ap.getAction() instanceof NamedSampling) {
					if(namedSampling.equals((NamedSamplingDto)ap.getAction()))
						actionPatternsService.setAction(ap, namedSampling);
				}
			}
		}
	}
	
	public List<SamplingDto> getTopSamplings(NamedSamplingDto ns) {
		return getTopSamplings(ns.getSamplings());
	}
	
	public static List<SamplingDto> getTopSamplings(Collection<SamplingDto> samplings) {
		List<SamplingDto> res = new ArrayList<>();
		for (SamplingDto s : samplings) {
			if(s.getParentSampling()==null) res.add(s);
		}
		return res;
	}

	public String getHtmlBySampling(NamedSamplingDto ns) {
		StringBuilder sb = new StringBuilder();
		for(SamplingDto s: getTopSamplings(ns)) {
			getHtmlBySamplingRec(s, 0, sb);
		}
		return sb.toString();
	}
	
	private void getHtmlBySamplingRec(SamplingDto ps, int depth, StringBuilder sb) {
		for (int i = 0; i < depth; i++) {
			sb.append("&nbsp;&nbsp;");
		}
		sb.append("-" + samplingService.getDetailsWithMeasurements(ps) + "<br>\n");
		for(SamplingDto ns: ps.getChildren()) {
			getHtmlBySamplingRec(ns, depth+1, sb);
		}
	}
	
	public List<NamedSamplingDto> getNamedSamplings(User user, StudyDto study) {
		assert user!=null;

		List<Integer> sids;
		if(study==null) {
			sids = getIds(studyService.getRecentStudiesDto(user, RightLevel.READ));
		} else {
			sids = Collections.singletonList(study.getId());
		}

		List<NamedSampling> namedSamplings = namedSamplingDao.getNamedSamplings(study, sids, user);
		List<NamedSamplingDto> res = new ArrayList<>(map(namedSamplings));
		return res;
	}
	
	private SamplingDto getSampling(NamedSamplingDto namedSampling, int id) {
		if(id<=0) return null;
		for (SamplingDto s : namedSampling.getSamplings()) {
			if(s.getId().equals(id)) return s;
		}
		return null;
	}
	
	public IdentityHashMap<SamplingDto, SamplingDto> copyFrom(NamedSamplingDto namedSampling, NamedSamplingDto input) {
		if(namedSampling==input) throw new IllegalArgumentException("ns cannot be equal to this");
		namedSampling.setName(input.getName());
		namedSampling.setNecropsy(input.getNecropsy());
		//delete outdated sampling objects (or without id)
		for(Iterator<SamplingDto> iter = namedSampling.getSamplings().iterator(); iter.hasNext(); ) {
			SamplingDto s = iter.next();
			if(getSampling(input, s.getId())==null) {
				samplingService.remove(s);
				iter.remove();
			}
		}
		//At this stage this.samplings have all an id
		//Either, there is a match between this and ns and we keep the link
		//Either, there is no match and we create a copy
		IdentityHashMap<SamplingDto, SamplingDto> input2this = new IdentityHashMap<>();
		for (SamplingDto inputSampling : input.getSamplings()) {
			SamplingDto thisSampling = getSampling(namedSampling, inputSampling.getId());
			if(thisSampling!=null) {
				//there is a match between this and ns and we keep the link
				input2this.put(inputSampling, thisSampling);
			} else {
				//work with a copy (id>0 -> copy.id=sampling.id)
				thisSampling = new SamplingDto();
				thisSampling.setId(inputSampling.getId());
				namedSampling.getSamplings().add(thisSampling);
			}
			//always copy the attributes of the sampling
			thisSampling.setBiotype(inputSampling.getBiotype());
			thisSampling.setSampleName(inputSampling.getSampleName());
			for(SamplingParameterDto sp : inputSampling.getParameters()) {
				samplingService.addProperty(thisSampling, sp.getBiotypemetadata(), sp.getValue());
			}
			thisSampling.setComments(inputSampling.getComments());
			thisSampling.setWeighingRequired(inputSampling.getWeighingRequired());
			thisSampling.setLengthRequired(inputSampling.getLengthRequired());
			thisSampling.setCommentsRequired(inputSampling.getCommentsRequired());
			thisSampling.setAmount(inputSampling.getAmount());
			thisSampling.setContainerType(inputSampling.getContainerType());
			thisSampling.setLocIndex(inputSampling.getLocIndex());
			thisSampling.setSamples(inputSampling.getSamples());
			samplingService.setMeasurementfromList(thisSampling, inputSampling.getExtraMeasurements());
			thisSampling.setRowNumber(inputSampling.getRowNumber());
			thisSampling.setNamedSampling(namedSampling);

			input2this.put(inputSampling, thisSampling);
		}
		//Recreate hierarchy
		for (SamplingDto existingSampling : new ArrayList<SamplingDto>(input.getSamplings())) {
			SamplingDto s = input2this.get(existingSampling);
			s.setParentSampling(input2this.get(existingSampling.getParentSampling()));
			s.getChildren().clear();
			for (SamplingDto child : existingSampling.getChildren()) {
				s.getChildren().add(input2this.get(child));
			}
		}
		return input2this;
	}

	public void remove(NamedSamplingDto namedSampling) {
		StudyDto study = namedSampling.getStudy();
		if(study==null || study.getNamedSamplings()==null) return;
		for (Iterator<NamedSamplingDto> iter = study.getNamedSamplings().iterator(); iter.hasNext();) {
			NamedSamplingDto ns = iter.next();
			if (ns.equals(namedSampling)) {
				iter.remove();
			}
		}
		for(SamplingDto s: namedSampling.getSamplings()) {
			for(BiosampleDto b: s.getSamples()) {
				b.setAttachedSampling(null);
			}
			s.getSamples().clear();
		}
		setStudy(namedSampling, null);
	}

	public NamedSamplingDto newNamedSamplingDto(StudyDto study) {
		NamedSamplingDto ns = new NamedSamplingDto();
		setStudy(ns, study);
		return ns;
	}

	public NamedSamplingDto clone(NamedSamplingDto namedSampling) {
		return clone(namedSampling, namedSampling.getStudy());
	}

	public NamedSamplingDto clone(NamedSamplingDto namedSampling, StudyDto study) {
		NamedSamplingDto res = newNamedSamplingDto(study);
		res.setName(namedSampling.getName() + (namedSampling.getStudy().equals(study) ? " Copy" :""));
		res.setNecropsy(namedSampling.getNecropsy());

		res.setSamplings(new ArrayList<SamplingDto>());
		Map<SamplingDto, SamplingDto> old2new = new IdentityHashMap<>();

		//Create new sampling objects
		for (SamplingDto existingSampling : namedSampling.getSamplings()) {
			SamplingDto s = new SamplingDto();
			s.setBiotype(existingSampling.getBiotype());
			s.setSampleName(existingSampling.getSampleName());
			s.setParameters(existingSampling.getParameters());
			s.setComments(existingSampling.getComments());
			s.setWeighingRequired(existingSampling.getWeighingRequired());
			s.setLengthRequired(existingSampling.getLengthRequired());
			s.setCommentsRequired(existingSampling.getCommentsRequired());
			s.setAmount(existingSampling.getAmount());
			s.setContainerType(existingSampling.getContainerType());
			s.setLocIndex(existingSampling.getLocIndex());
			s.setRowNumber(existingSampling.getRowNumber());
			s.setExtraMeasurements(existingSampling.getExtraMeasurements());
			res.getSamplings().add(s);
			old2new.put(existingSampling, s);
		}

		//Recreate hierarchy
		for (SamplingDto existingSampling : namedSampling.getSamplings()) {
			SamplingDto s = old2new.get(existingSampling);
			s.setParentSampling(old2new.get(existingSampling.getParentSampling()));

			for (SamplingDto child : existingSampling.getChildren()) {
				s.getChildren().add(old2new.get(child));
			}
		}

		return res;
	}

	public void mapSamplings(NamedSamplingDto namedSampling) {
		namedSampling.setSamplings(samplingService.map(samplingService.getbyNamedSampling(namedSampling.getId())));
		Collections.sort(namedSampling.getSamplings());
	}

}
