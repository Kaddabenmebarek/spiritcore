package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AssayAttributeDao;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.model.AssayAttribute;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class AssayAttributeService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -1701849492109819228L;
	@Autowired
	private AssayAttributeDao assayAttributeDao;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, AssayAttributeDto> idToAssayAttribute = (Map<Integer, AssayAttributeDto>) getCacheMap(AssayAttributeDto.class);

	@Autowired
	private AssayService assayService;
	@Autowired
	private AssayResultService assayResultService;

	public int addAssayAttribute(AssayAttribute assayResult) {
		return assayAttributeDao.addAssayAttribute(assayResult);
	}

	public Integer saveOrUpdate(AssayAttribute assayResult) {
		return assayAttributeDao.saveOrUpdate(assayResult);
	}

	public AssayAttribute get(Integer id) {
		return assayAttributeDao.get(id);
	}
	
	public AssayAttribute getAssayAttributesByAssayAndName(Integer assayId, String name){
		return assayAttributeDao.getAssayAttributeByAssayAndName(assayId, name);
	}
	
	public List<AssayAttribute> getAssayAttributesByName(String name){
		return assayAttributeDao.getAssayAttributesByName(name);
	}
	
	public List<AssayAttribute> getAssayAttributesByAssay(Integer assayId){
		return assayAttributeDao.getAssayAttributesByAssay(assayId);
	}
	
	public List<AssayAttribute> getAssayAttributeByStudy(Integer studyId) {
		return assayAttributeDao.getAssayAttributesByStudy(studyId);
	}
	
	public AssayAttribute getAssayAttributeById(int id) {
		return assayAttributeDao.getAssayAttributeById(id);
	}
	
	public List<AssayAttribute> list() {
		return assayAttributeDao.list();
	}
	
	public int getCount() {
		return assayAttributeDao.getCount();
	}

	public AssayAttributeDao getAssayAttributeDao() {
		return assayAttributeDao;
	}

	public void setAssayAttributeDao(AssayAttributeDao assayResultDao) {
		this.assayAttributeDao = assayResultDao;
	}

	public AssayAttributeDto getAssayAttributeDto(Integer id) {
		return map(get(id));
	}

	public List<AssayAttributeDto> map(List<AssayAttribute> assayAttributes) {
		List<AssayAttributeDto> res = new ArrayList<AssayAttributeDto>();
		for(AssayAttribute assayAttribute : assayAttributes) {
			res.add(map(assayAttribute));
		}
		return res;
	}
	
	public synchronized AssayAttributeDto map(AssayAttribute assayAttribute) {
		AssayAttributeDto assayAttributeDto = idToAssayAttribute.get(assayAttribute.getId());
		if(assayAttributeDto==null) {
			assayAttributeDto = dozerMapper.map(assayAttribute, AssayAttributeDto.class,"assayAttributeCustomMapping");
			if(idToAssayAttribute.get(assayAttribute.getId())==null)
				idToAssayAttribute.put(assayAttribute.getId(), assayAttributeDto);
		}
		return assayAttributeDto;
	}
	
	@Transactional
	public void save(AssayAttributeDto assayAttribute) throws Exception {
		save(assayAttribute, false);
	}
	
	protected void save(AssayAttributeDto assayAttribute, Boolean cross) throws Exception {
		try {
			if (!savedItems.contains(assayAttribute)) {
				savedItems.add(assayAttribute);
				if (assayAttribute.getAssay().getId() == Constants.NEWTRANSIENTID)
					assayService.save(assayAttribute.getAssay(), true);
				assayAttribute.setUpdDate(new Date());
				assayAttribute.setUpdUser(UserUtil.getUsername());
				if(assayAttribute.getId().equals(Constants.NEWTRANSIENTID)) {
					assayAttribute.setCreDate(new Date());
					assayAttribute.setCreUser(UserUtil.getUsername());
				}
				assayAttribute.setId(saveOrUpdate(
						dozerMapper.map(assayAttribute, AssayAttribute.class, "assayAttributeCustomMapping")));
			}
		} catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally {
			if (!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}
	
	@Transactional
	public void delete(AssayAttributeDto assayAttribute) {
		delete(assayAttribute, false);
	}
	
	protected void delete(AssayAttributeDto assayAttribute, Boolean cross) {
		assayAttributeDao.delete(assayAttribute.getId());
	}

	public String[] getParametersArray(AssayAttributeDto attribute) {
		if(attribute.getParameters()==null) 
			return new String[0];
		return MiscUtils.split(attribute.getParameters());
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getAutoCompletionFields(AssayAttributeDto attribute) {
		Set<String> cached = (Set<String>) Cache.getInstance().get("autocompletion_ta_" + attribute.getId());
		if(cached==null) {
			List<String> items = assayAttributeDao.getRegisteredValues(attribute.getId());
			cached = new TreeSet<String>(items);
			Cache.getInstance().add("autocompletion_ta_" + attribute.getId(), cached, Cache.FAST);
		}
		return cached;
	}

	public String extractNameWithoutUnit(String name) {
		if(name==null) return null;
		int index1 = name.lastIndexOf('[');
		int index2 = name.lastIndexOf(']');
		if(index1>0 && index1<index2) {
			return (name.substring(0, index1) + name.substring(index2+1).trim()).trim();
		}

		index1 = name.lastIndexOf('(');
		index2 = name.lastIndexOf(')');
		if(index1>0 && index1<index2) {
			return (name.substring(0, index1) + name.substring(index2+1).trim()).trim();
		}

		return name;
	}

	public String extractUnit(String name) {
		if(name==null) return null;
		int index1 = name.lastIndexOf('[');
		int index2 = name.lastIndexOf(']');
		if(index1>0 && index1<index2) {
			return name.substring(index1+1, index2).trim();
		}

		index1 = name.lastIndexOf('(');
		index2 = name.lastIndexOf(')');
		if(index1>0 && index1<index2) {
			return name.substring(index1+1, index2).trim();
		}

		return null;
	}

	@Transactional
	public int rename(AssayAttributeDto att, String value, String newValue, User user) throws Exception {
		if(user==null || !user.isSuperAdmin()) 
			throw new Exception("You must be an admin to rename an attribute");
		List<AssayResultDto> results = assayResultService.map(assayResultService.getByValueAndAttributeId(value, att.getId()));
		for (AssayResultDto result : results) {
			assayResultService.setValue(result, att, newValue);
			assayResultService.save(result);
		}
		return results.size();
	}

	public List<String> getInputFields(AssayAttributeDto att, String studyIds) {
		return assayAttributeDao.getInputFields(att.getId(), studyIds);
	}
}
