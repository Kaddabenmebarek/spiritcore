package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.constants.OutputType;
import com.idorsia.research.spirit.core.constants.StudyActionType;
import com.idorsia.research.spirit.core.dao.AssayDao;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssayResultValueDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Assay;
import com.idorsia.research.spirit.core.model.AssayAttribute;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.Triple;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class AssayService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = -6456543698510133372L;
	@Autowired
	private AssayDao assayDao;
	@Autowired
	private AssayAttributeService assayAttributeService;
	@Autowired
	private AssayResultService assayResultService;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private AssayResultValueService assayResultValueService;
	@SuppressWarnings("unchecked")
	private Map<Integer, AssayDto> idToAssay = (Map<Integer, AssayDto>) getCacheMap(AssayDto.class);
	
	public int addAssay(Assay assay) {
		return assayDao.addAssay(assay);
	}
	
	public Integer saveOrUpdate(Assay assay) {
		return assayDao.saveOrUpdate(assay);
	}

	public Assay get(Integer id) {
		Assay res = assayDao.get(id);
		if(res!=null)
			Cache.getInstance().add("Assay_"+res.getName(), res);
		return res;
	}

	public Assay getAssayById(int id) {
		Assay res = assayDao.getAssayById(id);
		if(res!=null)
			Cache.getInstance().add("Assay_"+res.getName(), res);
		return res;
	}

	public Assay getAssayByName(String name) {
		Assay res = (Assay) Cache.getInstance().get("Assay_"+name);
		if(res!=null)
			return res;
		res = assayDao.getAssayByName(name);
		Cache.getInstance().add("Assay_"+name, res);
		return res;
	}
	
	public AssayDto getAssayDtoByName(String name) {
		return map(getAssayByName(name));
	}

	public List<Assay> getAssaysByStudy(Integer studyId) {
		return assayDao.getAssaysByStudy(studyId);
	}

	public List<Assay> list() {
		return assayDao.list();
	}

	public int getCount() {
		return assayDao.getCount();
	}
	
	public AssayDao getAssayDao() {
		return assayDao;
	}

	public void setAssayDao(AssayDao assayDao) {
		this.assayDao = assayDao;
	}
	
	public List<AssayAttributeDto> getInputAttributes(AssayDto assay) {
		return getAttributes(assay, OutputType.INPUT);
	}

	public List<AssayAttributeDto> getOutputAttributes(AssayDto assay) {
		return getAttributes(assay, OutputType.OUTPUT);
	}
	
	public AssayAttributeDto getAttribute(AssayDto assay, String attName) {
		for (AssayAttributeDto testAttribute : assay.getAttributes()) {
			if(testAttribute.getName().equals(attName)) return testAttribute;
		}
		return null;
	}
	
	public List<AssayAttributeDto> getAttributes(AssayDto assay, OutputType outputType) {
		List<AssayAttributeDto> res = new ArrayList<>();
		for (AssayAttributeDto ta : assay.getAttributes()) {
			if(ta.getOutputType()==outputType) {
				res.add(ta);
			}
		}
		Collections.sort(res);
		return Collections.unmodifiableList(res);
	}
	
	public List<AssayAttributeDto> getInfoAttributes(AssayDto assay) {
		List<AssayAttributeDto> res = new ArrayList<>();
		for (AssayAttributeDto ta : assay.getAttributes()) {
			if(ta.getOutputType()==OutputType.INFO) {
				res.add(ta);
			}
		}
		return Collections.unmodifiableList(res);
	}
	
	public AssayDto getAssayDto(Integer id) {
		return map(get(id));
	}

	public AssayDto map(Assay assay) {
		AssayDto assayDto = idToAssay.get(assay.getId());
		if(assayDto==null) {
			assayDto = dozerMapper.map(assay,AssayDto.class,"assayCustomMapping");
			if(idToAssay.get(assay.getId())==null) {
				idToAssay.put(assay.getId(), assayDto);
			}else
				assayDto=idToAssay.get(assay.getId());
		}
		return assayDto;
	}
	
	public List<AssayDto> map(List<Assay> assays) {
		List<AssayDto> result = new ArrayList<>();
		for(Assay assay : assays) {
			result.add(map(assay));
		}
		return result;
	}

	@Transactional
	public void save(AssayDto assay) throws Exception {
		save(assay, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(AssayDto assay, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(assay)) {
				savedItems.add(assay);
				preSave(assay);
				if(assay.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(assay);
				updateResults(assay);
				assay.setUpdDate(new Date());
				assay.setUpdUser(UserUtil.getUsername());
				if(assay.getId().equals(Constants.NEWTRANSIENTID)) {
					assay.setCreDate(new Date());
					assay.setCreUser(UserUtil.getUsername());
				}
				assay.setId(saveOrUpdate(dozerMapper.map(assay, Assay.class, "assayCustomMapping")));
				idToAssay.put(assay.getId(), assay);
				if(assay.getAttributesNoMapping()!=null)
					for(AssayAttributeDto assayAttribute : assay.getAttributes())
						assayAttributeService.save(assayAttribute, true);
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

	private void updateResults(AssayDto assay) throws Exception {
		for(AssayAttributeDto child : assay.getAttributes()) {
			Boolean found = false;
			for(AssayAttribute attribute : assayAttributeService.getAssayAttributesByAssay(assay.getId())) {
				if(child.getId().equals(attribute.getId())) {
					found=true;
					if(attribute.getDatatype().equals(DataType.LIST.name()) || attribute.getDatatype().equals(DataType.MULTI.name())) {
						for(String s : MiscUtils.split(attribute.getParameters())) {
							boolean foundParam =false;
							for(String s2 : child.getParametersArray()) {
								if(s.equals(s2)) {
									foundParam = true;
									break;
								}
							}
							if(!foundParam) {
								for(AssayResultValueDto val : assayResultValueService.map(assayResultValueService.getAssayResultValueByAssayAttributeAndValue(attribute.getId(), s))){
									if(val.getTextValue().contains("; ")) {	
										String[] values = val.getTextValue().split("; ");
										int i = 0;
										for(String sVal : values) {
											if(sVal.equals(s)) {
												if(i==values.length-1) {
													val.setTextValue(val.getTextValue().replace("; "+s, ""));
												}else {
													val.setTextValue(val.getTextValue().replace(s+"; ", ""));
												}
												break;
											}
											i=i+1;
										}
									}else {
										val.setTextValue(null);
									}
									assayResultValueService.save(val, true);
								}
							}
						}
					}
					break;
				}
			}
			if(!found)
				for(AssayResultDto result : assayResultService.map(assayResultService.getAssayResultsByAssay(assay.getId()))){
					AssayResultValueDto val = new AssayResultValueDto();
					val.setAssayAttribute(child);
					val.setAssayResult(result);
					result.getValues().add(val);
					assayResultService.save(result, true);
				}
		}
		
	}

	private void preSave(AssayDto assay) {
		int index = 1;
		for (AssayAttributeDto att : assay.getAttributes()) {
			att.setIdx(index);
			att.setAssay(assay);
			index++;
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteChildren(AssayDto assay) {
		if(assay.getAttributesNoMapping()!=null)
			for(AssayAttribute attribute : assayAttributeService.getAssayAttributesByAssay(assay.getId())) {
				Boolean found = false;
				for(AssayAttributeDto child : assay.getAttributes()) {
					if(child.getId().equals(attribute.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					assayAttributeService.delete(assayAttributeService.map(attribute), true);
				}
			}
	}

	@Transactional
	public void delete(AssayDto assay) {
		delete(assay, false);
	}
	
	protected void delete(AssayDto assay, Boolean cross) {
		for(ActionPatternsDto ap : actionPatternsService.map(actionPatternsService.getByAction(assay.getId(), StudyActionType.MEASUREMENT.name())))
			actionPatternsService.delete(ap, true);
		assayDao.delete(assay.getId());
	}

	public Set<AssayDto> getAssays(List<AssayResultDto> results) {
		Set<AssayDto> res = new HashSet<>();
		for (AssayResultDto r : results) {
			res.add(r.getAssay());
		}
		return res;
	}

	public String getFullName(AssayDto assay) {
		return assay.getCategory() + " - " + assay.getName();
	}

	@SuppressWarnings("unchecked")
	public Set<String[]> getSendObservations() {
		Set<String[]> cached = (Set<String[]>) Cache.getInstance().get("autocompletion_sendobs");
		if(cached==null) {
			cached = new LinkedHashSet<>(assayDao.getSendObservation());
			Cache.getInstance().add("autocompletion_sendobs", cached, Cache.FAST);
		}
		return cached;
	}
	
	public Set<String> getSendSeverity(){
		Set<String> severity =new TreeSet<>();
		severity.add("1");
		severity.add("2");
		severity.add("3");
		return severity;
	}
	
	public Set<String> getSendLocalisation() {
		@SuppressWarnings("unchecked")
		Set<String> cached = (Set<String>) Cache.getInstance().get("autocompletion_sendlocalisation");
		if(cached==null) {
			cached = new LinkedHashSet<>(assayDao.getSendLocalisation());
			Cache.getInstance().add("autocompletion_sendlocalisation", cached, Cache.FAST);
		}
		return cached;
	}

	public void mapAttributes(AssayDto assay) {
		List<AssayAttributeDto> attributes = assayAttributeService.map(assayAttributeService.getAssayAttributesByAssay(assay.getId()));
		Collections.sort(attributes);
		assay.setAttributes(attributes);
	}

	public Map<String, Map<String, Triple<Integer, String, Date>>> countResultsByStudyAssay(
			Collection<StudyDto> studies) {
		return assayDao.countResultsByStudyAssay(studies);
	}

	public List<AssayDto> getAssaysFromElbs(String elb) {
		List<AssayDto> resp = new ArrayList<AssayDto>();
		Set<Assay> assaysFromElb  = assayDao.getAssaysFromElbs(elb);
		for(Assay assay : assaysFromElb) {
			resp.add(map(assay));
		}
		return resp;
	}

	public Map<AssayDto, Integer> countResults(List<StudyDto> studies, BiotypeDto forcedBiotype) {
		Map<AssayDto, Integer> resultsMap = new TreeMap<>();
		Map<Integer, List<Integer>> assayResultMap = assayDao.getAssayResultMapByStudyAndBiotype(studies, forcedBiotype);
		for(Entry<Integer, List<Integer>> entry : assayResultMap.entrySet()) {
			Assay a = assayDao.getAssayById(entry.getKey());
			resultsMap.put(map(a), entry.getValue().size());
		}
		return resultsMap;
	}
	
	public List<AssayDto> getAssaysDto(Set<Integer> ids) {
		List<AssayDto> res = new ArrayList<>();
		if(ids.size()==0) 
			return res;
		map(list());
		for (Integer id : ids) {
			AssayDto t = idToAssay.get(id);
			if(t!=null) res.add(t);
		}
		return res;
	}
	
	public List<AssayDto> getAssaysDto() {
		map(list());
		return new ArrayList<>(idToAssay.values());
	}

	public Map<AssayAttributeDto, Collection<String>> getInputFields(Integer assayId, String studyIds) {
		Map<AssayAttributeDto, Collection<String>> res = new HashMap<>();
		AssayDto assay = getAssayDto(assayId);
		if(studyIds==null || studyIds.isBlank() || assay==null) 
			return res;

		for (AssayAttributeDto att : getInputAttributes(assay)) {
			List<String> choices = new ArrayList<>();
			for (String string : (List<String>) assayAttributeService.getInputFields(att, studyIds)) {
				if(string==null) {
					choices.add("");
				} else {
					choices.add(string);
				}
			}
			res.put(att, choices);
		}
		return res;
	}

	public List<AssayDto> getAssays(boolean showHidden) {
		List<AssayDto> tests = new ArrayList<>();
		for (AssayDto test : getAssaysDto()) {
			if(!showHidden && test.getDisabled()) continue;
			tests.add(test);
		}
		Collections.sort(tests);
		return tests;
	}

	public SortedSet<String> getAssayCategories(List<AssayDto> assays) {
		SortedSet<String> res = new TreeSet<>();
		for(AssayDto t: assays) {
			res.add(t.getCategory());
		}
		return res;
	}

	public AssayDto duplicate(AssayDto t) {
		AssayDto assay = new AssayDto();
		assay.setName(t.getName());
		assay.setCategory(t.getCategory());
		for (AssayAttributeDto attribute : t.getAttributes()) {
			AssayAttributeDto att = new AssayAttributeDto();
			att.setAssay(attribute.getAssay());
			att.setName(attribute.getName());
			att.setDataType(attribute.getDataType());
			att.setOutputType(attribute.getOutputType());
			att.setRequired(attribute.isRequired());
			assay.getAttributes().add(att);
		}
		return assay;
	}
}
