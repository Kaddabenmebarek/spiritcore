package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.dao.AssayResultValueDao;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssayResultValueDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.model.AssayResultValue;
import com.idorsia.research.spirit.core.util.MappingThreadUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class AssayResultValueService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -2635865061257078449L;
	@Autowired
	private AssayResultValueDao assayResultValueDao;
	@Autowired
	private AssayResultService assayResultService; 
	@Autowired
	private DocumentService documentService;
	@Autowired
	private AssayAttributeService assayAttributeService;
	@SuppressWarnings("unchecked")
	private static Map<Integer, AssayResultValueDto> idToValue = (Map<Integer, AssayResultValueDto>) getCacheMap(AssayResultValueDto.class);

	public int addAssayResultValue(AssayResultValue assayResultValue) {
		return assayResultValueDao.addAssayResultValue(assayResultValue);
	}

	public Integer saveOrUpdate(AssayResultValue assayResultValue) {
		return assayResultValueDao.saveOrUpdate(assayResultValue);
	}

	public void delete(Integer id) {
		assayResultValueDao.delete(id);
	}

	public AssayResultValue get(Integer id) {
		return assayResultValueDao.get(id);
	}

	public List<AssayResultValue> getAssayResultValuesByAssayResult(Integer assayResultId){
		return assayResultValueDao.getAssayResultValuesByAssayResult(assayResultId);
	}

	public List<AssayResultValue> getAssayResultValueByStudy(Integer studyId) {
		return assayResultValueDao.getAssayResultValuesByStudy(studyId);
	}
	
	public List<AssayResultValue> getAssayResultValueByAssayAttribute(Integer assay_attribute_id) {
		return assayResultValueDao.getAssayResultValuesByAssayAttribute(assay_attribute_id);
	}
	
	public List<AssayResultValue> getAssayResultValueByAssayAttributeAndValue(Integer assay_attribute_id, String value) {
		return assayResultValueDao.getAssayResultValuesByAssayAttributeAndValue(assay_attribute_id, value);
	}
	
	public List<AssayResultValue> getAssayResultValuesByDocument(Integer documentId){
		return assayResultValueDao.getAssayResultValuesByDocument(documentId);
	}

	public AssayResultValue getAssayResultsByAssayResultAndAssayAttribute(Integer assay_result_id, Integer assay_attribute_id){
		return assayResultValueDao.getAssayResultsByAssayResultAndAssayAttribute(assay_result_id, assay_attribute_id);
	}

	public AssayResultValue getAssayResultValueById(int id) {
		return assayResultValueDao.getAssayResultValueById(id);
	}

	public AssayResultValueDao getAssayResultValueDao() {
		return assayResultValueDao;
	}

	public void setAssayResultValueDao(AssayResultValueDao assayResultValueDao) {
		this.assayResultValueDao = assayResultValueDao;
	}

	public List<AssayResultValueDto> map(List<AssayResultValue> assayResultValues) {
		List<AssayResultValueDto> res = new ArrayList<AssayResultValueDto>();
		for(AssayResultValue assayResultValue : assayResultValues) {
			res.add(map(assayResultValue));
		}
		return res;
	}
	
	public AssayResultValueDto map(AssayResultValue value) {
		AssayResultValueDto valueDto = idToValue.get(value.getId());
		if(valueDto==null) {
			valueDto = dozerMapper.map(value, AssayResultValueDto.class,"assayResultValueCustomMapping");
			if(idToValue.get(value.getId())==null)
				idToValue.put(value.getId(), valueDto);
		}
		return valueDto;
	}
	
	public AssayResultValueDto getAssayResultValueDto(Integer id) {
		return map(get(id));
	}

	@Transactional
	public void save(AssayResultValueDto value) throws Exception {
		save(value, false);
	}
	
	protected void save(AssayResultValueDto value, Boolean cross) throws Exception {
		try{
			if(!savedItems.contains(value)) {
				savedItems.add(value);
				documentService.save(value.getDocument(), true);
				if(value.getAssayAttribute().getId().equals(Constants.NEWTRANSIENTID))
					assayAttributeService.save(value.getAssayAttribute(), true);
				value.setUpdDate(new Date());
				value.setUpdUser(UserUtil.getUsername());
				if(value.getId().equals(Constants.NEWTRANSIENTID)) {
					value.setCreDate(new Date());
					value.setCreUser(UserUtil.getUsername());
				}
				value.setId(saveOrUpdate(dozerMapper.map(value, AssayResultValue.class, "assayResultValueCustomMapping")));
				idToValue.put(value.getId(), value);
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

	@Transactional
	public void delete(AssayResultValueDto value) {
		delete(value, false);
	}
	
	protected void delete(AssayResultValueDto value, Boolean cross) {
		delete(value.getId());
	}

	public Double getDoubleValue(AssayResultValueDto rv) {
		if(rv.getAssayAttribute().getDataType()==DataType.NUMBER || rv.getAssayAttribute().getDataType()==DataType.FORMULA) {
			if(rv.getTextValue()==null || rv.getTextValue().length()==0) {
				return null;
			} else {
				try {
					int offset = 0;
					while(offset<rv.getTextValue().length() && "<>= ".indexOf(rv.getTextValue().charAt(offset))>=0) {
						offset++;
					}
					int index2 = rv.getTextValue().length()-1;
					while(index2>=offset && "% ".indexOf(rv.getTextValue().charAt(index2))>=0) {
						index2--;
					}

					return Double.parseDouble(rv.getTextValue().substring(offset, index2+1));
				} catch (NumberFormatException e) {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	public List<String> getValues(List<AssayResultValueDto> resultValues) {
		List<String> res = new ArrayList<String>();
		for (AssayResultValueDto rv : resultValues) {
			res.add(rv.getTextValue());
		}
		return res;
	}

	public boolean isValidDouble(String value) {
		if(value==null || value.length()==0) return true;
		return value.equals(convertToValidDouble(value));
	}
	
	public static String convertToValidDouble(String value) {
		if(value==null || value.length()==0) return null;

		for(String s: Constants.VALID_VALUES) {
			if(s.equals(value)) return value;
		}

		String[] allowedModifiers = new String[] {"<", "<=", ">", ">="};
		String doubleValue = value.trim();
		for(String mod: allowedModifiers) {
			if(value.startsWith(mod)) {
				doubleValue = value.substring(mod.length()).trim();
			}
		}

		try {
			Double.parseDouble(doubleValue);
			return value;
		} catch(Exception e) {
			if(value.length()>6) return value.replace(" ", " ");
			return "NA";
		}
	}

	@Transactional
	public int countRelations(AssayAttributeDto testAttribute) {
		if (testAttribute == null || testAttribute.getId() <= 0) return 0;
		int relations = assayResultValueDao.countRelations(testAttribute);
		return relations;
	}

	@Transactional
	public int rename(AssayAttributeDto att, String value, String newValue, User user) throws Exception {
		if(user==null || !user.isSuperAdmin()) throw new Exception("You must be an admin to rename an attribute");
		List<AssayResult> results = assayResultValueDao.getResultsByAttributeAndValue(value, att.getId());
		
		Date now = new Date();
		for (AssayResult result : results) {
			result.setUpdDate(now);
			result.setUpdUser(user.getUsername());
			assayResultService.setValue(assayResultService.map(result), att, newValue);
			assayResultService.saveOrUpdate(result);
		}

		return results.size();
	}

	public List<AssayResultValue> getOutputResultValues(List<AssayResultDto> results) {
		List<Integer> ids = getIds(results);
		List<Integer> idsTemp = new ArrayList<>();
		List<AssayResultValue> res = new ArrayList<>();
		int i=0;
		int j=0;
		while(ids.size()>i) {
			while(j%1000<999 && ids.size()>i) {
				Integer id = ids.get(i);
				if(id!=Constants.NEWTRANSIENTID) {
					idsTemp.add(id);
					j++;
				}
				i++;
			}
			j=0;
			if(idsTemp.size()>0)
				res.addAll(assayResultValueDao.getOutputResultValues(idsTemp));
			idsTemp.clear();
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public void mapResultValues(List<AssayResultDto> results) {
		List<Integer> ids = getIds(results);
		List<Integer> idsTemp = new ArrayList<>();
		List<AssayResultValue> res = new ArrayList<>();
		int i=0;
		int j=0;
		while(ids.size()>i) {
			while(j%1000<999 && ids.size()>i) {
				Integer id = ids.get(i);
				if(id!=Constants.NEWTRANSIENTID) {
					idsTemp.add(id);
					j++;
				}
				i++;
			}
			j=0;
			if(idsTemp.size()>0)
				res.addAll(assayResultValueDao.getResultValues(idsTemp));
			idsTemp.clear();
		}
		for(AssayResultValue arv : res) {
			AssayResultValueDto arvDto = map(arv);
			List<AssayResultValueDto> arvs = arvDto.getAssayResult().getValuesNoMapping();
			if(arvs==null) {
				arvs = new ArrayList<>();
				arvDto.getAssayResult().setValues(arvs);
			}
			arvs.add(arvDto);
		}
	}

	public List<BiosampleDto> getBiosamples(List<AssayResultDto> results) {
		List<BiosampleDto> biosamples = new ArrayList<>();
		for(AssayResultDto res : results) {
			biosamples.add(res.getBiosample());
		}
		return biosamples;
	}
}
