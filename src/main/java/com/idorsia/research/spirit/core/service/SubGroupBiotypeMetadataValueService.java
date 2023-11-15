package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SubGroupBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.dto.SubGroupBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.model.SubGroupBiotypeMetadataValue;

@Service
public class SubGroupBiotypeMetadataValueService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 7199638858402820485L;
	@Autowired
	private SubGroupBiotypeMetadataValueDao subGroupBiotypeMetadataValueDao;
	@Autowired
	private BiotypeMetadataValueService biotypeMetadataValueService;
	@Autowired
	private SubGroupService subGroupService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, SubGroupBiotypeMetadataValueDto> idToSubGroupBiotypeMetadataValue = (Map<Integer, SubGroupBiotypeMetadataValueDto>) getCacheMap(SubGroupBiotypeMetadataValueDto.class);

	public SubGroupBiotypeMetadataValue get(Integer id) {
		return subGroupBiotypeMetadataValueDao.get(id);
	}
	
	public List<SubGroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		return subGroupBiotypeMetadataValueDao.getByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	public List<SubGroupBiotypeMetadataValue> getBySubGroup(int subGroupId) {
		return subGroupBiotypeMetadataValueDao.getBySubGroup(subGroupId);
	}

	public List<SubGroupBiotypeMetadataValue> list() {
		return subGroupBiotypeMetadataValueDao.list();
	}

	public int getCount() {
		return subGroupBiotypeMetadataValueDao.getCount();
	}

	public void addSubGroupBiotypeMetadataValue(SubGroupBiotypeMetadataValue subGroupBiotypeMetadataValue) {
		subGroupBiotypeMetadataValueDao.addSubGroupBiotypeMetadataValue(subGroupBiotypeMetadataValue);
	}

	public void delete(Integer subGroupBiotypeMetadataValueId) {
		subGroupBiotypeMetadataValueDao.delete(subGroupBiotypeMetadataValueId);
	}
	
	protected void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		subGroupBiotypeMetadataValueDao.deleteByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	protected void deleteBySubGroup(int subGroupId) {
		subGroupBiotypeMetadataValueDao.deleteBySubGroup(subGroupId);
	}
	
	public SubGroupBiotypeMetadataValueDao getSubGroupBiotypeMetadataValueDao() {
		return subGroupBiotypeMetadataValueDao;
	}

	public void setSubGroupBiotypeMetadataValueDao(SubGroupBiotypeMetadataValueDao subGroupBiotypeMetadataValueDao) {
		this.subGroupBiotypeMetadataValueDao = subGroupBiotypeMetadataValueDao;
	}
	
	public List<SubGroupBiotypeMetadataValueDto> map(List<SubGroupBiotypeMetadataValue> metadatas){
		List<SubGroupBiotypeMetadataValueDto> results = new ArrayList<>();
		for(SubGroupBiotypeMetadataValue metadata : metadatas) {
			results.add(map(metadata));
		}
		return results;
	}

	public SubGroupBiotypeMetadataValueDto map(SubGroupBiotypeMetadataValue subGroupBiotypeMetadataValue) {
		SubGroupBiotypeMetadataValueDto subGroupBiotypeMetadataValueDto = idToSubGroupBiotypeMetadataValue
				.get(subGroupBiotypeMetadataValue.getId());
		if (subGroupBiotypeMetadataValueDto == null) {
			subGroupBiotypeMetadataValueDto = dozerMapper.map(subGroupBiotypeMetadataValue,
					SubGroupBiotypeMetadataValueDto.class, "subGroupBiotypeMetadataValueCustomMapping");
			if (idToSubGroupBiotypeMetadataValue.get(subGroupBiotypeMetadataValue.getId()) == null)
				idToSubGroupBiotypeMetadataValue.put(subGroupBiotypeMetadataValue.getId(),
						subGroupBiotypeMetadataValueDto);
			else
				subGroupBiotypeMetadataValueDto = idToSubGroupBiotypeMetadataValue
						.get(subGroupBiotypeMetadataValue.getId());
		}
		return subGroupBiotypeMetadataValueDto;
	}

	@Transactional
	public void save(SubGroupBiotypeMetadataValueDto subGroupBiotypeMetadataValue) throws Exception {
		save(subGroupBiotypeMetadataValue, false);
	}
	
	protected void save(SubGroupBiotypeMetadataValueDto subGroupBiotypeMetadataValue, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(subGroupBiotypeMetadataValue)) {
				savedItems.add(subGroupBiotypeMetadataValue);
				biotypeMetadataValueService.save(subGroupBiotypeMetadataValue.getBiotypeMetadataValue(), true);
				if(subGroupBiotypeMetadataValue.getSubGroup().getId().equals(Constants.NEWTRANSIENTID)) {
					subGroupService.save(subGroupBiotypeMetadataValue.getSubGroup(), true);
				}
				subGroupBiotypeMetadataValueDao.saveOrUpdate(dozerMapper.map(subGroupBiotypeMetadataValue, SubGroupBiotypeMetadataValue.class, "subGroupBiotypeMetadataValueCustomMapping"));
				idToSubGroupBiotypeMetadataValue.put(subGroupBiotypeMetadataValue.getId(), subGroupBiotypeMetadataValue);
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
	public void delete(SubGroupBiotypeMetadataValueDto subGroupBiotypeMetadataValue) {
		delete(subGroupBiotypeMetadataValue, false);
	}
	
	protected void delete(SubGroupBiotypeMetadataValueDto subGroupBiotypeMetadataValue, Boolean cross) {
		subGroupBiotypeMetadataValueDao.delete(subGroupBiotypeMetadataValue.getId());
	}

}
