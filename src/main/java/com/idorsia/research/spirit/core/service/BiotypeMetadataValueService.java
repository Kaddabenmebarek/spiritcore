package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadataValue;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class BiotypeMetadataValueService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = -5760382169183050132L;
	@Autowired
	private BiotypeMetadataValueDao biotypeMetadataValueDao;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;

	@SuppressWarnings("unchecked")
	private static Map<Integer, BiotypeMetadataValueDto> idToBiotypeMetadataValue= (Map<Integer, BiotypeMetadataValueDto>) getCacheMap(BiotypeMetadataValueDto.class);

	public int addBiotypeMetadataValue(BiotypeMetadataValue biotypeMetadataValue) {
		return biotypeMetadataValueDao.addBiotypeMetadataValue(biotypeMetadataValue);
	}

	public BiotypeMetadataValue getById(int id) {
		return biotypeMetadataValueDao.getById(id);
	}

	public List<BiotypeMetadataValue> getByGroup(Integer groupId) {
		return biotypeMetadataValueDao.getByGroup(groupId);
	}

	public List<BiotypeMetadataValue> getBySubGroup(Integer subGroupId) {
		return biotypeMetadataValueDao.getBySubGroup(subGroupId);
	}

	public List<BiotypeMetadataValue> getByStage(Integer stageId) {
		return biotypeMetadataValueDao.getByStage(stageId);
	}

	public Integer saveOrUpdate(BiotypeMetadataValue biotypeMetadataValue) {
		return biotypeMetadataValueDao.saveOrUpdate(biotypeMetadataValue);
	}

	public BiotypeMetadataValue get(Integer id) {
		return biotypeMetadataValueDao.get(id);
	}

	public List<BiotypeMetadataValue> getByBiotypeMetadata(Integer biotypeMetadataId) {
		return biotypeMetadataValueDao.getByBiotypeMetadata(biotypeMetadataId);
	}

	public Set<BiotypeMetadataValueDto> map(List<BiotypeMetadataValue> biotypeMetadataValues) {
		Set<BiotypeMetadataValueDto> res = new HashSet<BiotypeMetadataValueDto>();
		for(BiotypeMetadataValue biotypeMetadataValue : biotypeMetadataValues) {
			res.add(map(biotypeMetadataValue));
		}
		return res;
	}
	
	public BiotypeMetadataValueDto map(BiotypeMetadataValue biotypeMetadataValue) {
		if(biotypeMetadataValue==null)
			return null;
		BiotypeMetadataValueDto biotypeMetadataValueDto = idToBiotypeMetadataValue.get(biotypeMetadataValue.getId());
		if(biotypeMetadataValueDto==null) {
			biotypeMetadataValueDto = dozerMapper.map(biotypeMetadataValue,BiotypeMetadataValueDto.class,"biotypeMetadataValueCustomMapping");
			if(idToBiotypeMetadataValue.get(biotypeMetadataValue.getId())==null)
				idToBiotypeMetadataValue.put(biotypeMetadataValue.getId(), biotypeMetadataValueDto);
			else
				biotypeMetadataValueDto = idToBiotypeMetadataValue.get(biotypeMetadataValue.getId());
		}
		return biotypeMetadataValueDto;
	}
	
	public BiotypeMetadataValueDto getBiotypeMetadataValueDto(Integer id) {
		return map(get(id));
	}

	@Transactional
	public void save(BiotypeMetadataValueDto metadata) throws Exception {
		save(metadata, false);
	}
	
	protected void save(BiotypeMetadataValueDto metadata, Boolean cross) throws Exception {
		try{
			if(!savedItems.contains(metadata)) {
				savedItems.add(metadata);
				if(metadata.getBiotypeMetadata().getId().equals(Constants.NEWTRANSIENTID))
					biotypeMetadataService.save(metadata.getBiotypeMetadata());
				metadata.setUpdDate(new Date());
				metadata.setUpdUser(UserUtil.getUsername());
				if(metadata.getId().equals(Constants.NEWTRANSIENTID)) {
					metadata.setCreDate(new Date());
					metadata.setCreUser(UserUtil.getUsername());
				}
				metadata.setId(saveOrUpdate(dozerMapper.map(metadata, BiotypeMetadataValue.class, "biotypeMetadataValueCustomMapping")));
				idToBiotypeMetadataValue.put(metadata.getId(), metadata);
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
	public void delete(BiotypeMetadataValueDto metadata) {
		delete(metadata, false);
	}
	
	protected void delete(BiotypeMetadataValueDto metadata, Boolean cross) {
		biotypeMetadataValueDao.delete(metadata.getId());
	}

	public BiotypeMetadataValueDto newBioTypeMetadataValue(String value, BiotypeMetadataDto biotypeMetadata) {
		BiotypeMetadataValueDto biotypeMetadataValueDto = new BiotypeMetadataValueDto();
		biotypeMetadataValueDto.setValue(value);
		biotypeMetadataValueDto.setBiotypeMetadata(biotypeMetadata);
		return biotypeMetadataValueDto;
	}

}
