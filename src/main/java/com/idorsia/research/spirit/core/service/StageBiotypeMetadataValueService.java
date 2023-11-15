package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.StageBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.dto.StageBiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.model.StageBiotypeMetadataValue;

@Service
public class StageBiotypeMetadataValueService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -6440442640764584470L;
	@Autowired
	private StageBiotypeMetadataValueDao stageBiotypeMetadataValueDao;
	@Autowired
	private BiotypeMetadataValueService biotypeMetadataValueService;
	@Autowired
	private StageService stageService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, StageBiotypeMetadataValueDto> idToStageBiotypeMetadataValue = (Map<Integer, StageBiotypeMetadataValueDto>) getCacheMap(StageBiotypeMetadataValueDto.class);
	
	public StageBiotypeMetadataValue get(Integer id) {
		return stageBiotypeMetadataValueDao.get(id);
	}
	
	public List<StageBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		return stageBiotypeMetadataValueDao.getByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	public List<StageBiotypeMetadataValue> getByStage(int stageId) {
		return stageBiotypeMetadataValueDao.getByStage(stageId);
	}

	public List<StageBiotypeMetadataValue> list() {
		return stageBiotypeMetadataValueDao.list();
	}

	public int getCount() {
		return stageBiotypeMetadataValueDao.getCount();
	}

	@Transactional
	public void addStageBiotypeMetadataValue(StageBiotypeMetadataValue stageBiotypeMetadataValue) {
		stageBiotypeMetadataValueDao.addStageBiotypeMetadataValue(stageBiotypeMetadataValue);
	}

	public void delete(Integer stageBiotypeMetadataValueId) {
		stageBiotypeMetadataValueDao.delete(stageBiotypeMetadataValueId);
	}
	
	protected void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		stageBiotypeMetadataValueDao.deleteByBiotypeMetadataValue(biotypeMetadataValueId);
	}

	protected void deleteByStage(int stageId) {
		stageBiotypeMetadataValueDao.deleteByStage(stageId);
	}
	
	public StageBiotypeMetadataValueDao getStageBiotypeMetadataValueDao() {
		return stageBiotypeMetadataValueDao;
	}

	public void setStageBiotypeMetadataValueDao(StageBiotypeMetadataValueDao stageBiotypeMetadataValueDao) {
		this.stageBiotypeMetadataValueDao = stageBiotypeMetadataValueDao;
	}

	public List<StageBiotypeMetadataValueDto> map(List<StageBiotypeMetadataValue> values){
		List<StageBiotypeMetadataValueDto> results = new ArrayList<>();
		for(StageBiotypeMetadataValue sbmv : values) {
			results.add(map(sbmv));
		}
		return results;
	}
	
	public StageBiotypeMetadataValueDto map(StageBiotypeMetadataValue stageBiotypeMetadataValue) {
		StageBiotypeMetadataValueDto stageBiotypeMetadataValueDto = idToStageBiotypeMetadataValue.get(stageBiotypeMetadataValue.getId());
		if(stageBiotypeMetadataValueDto==null) {
			stageBiotypeMetadataValueDto = dozerMapper.map(stageBiotypeMetadataValue, StageBiotypeMetadataValueDto.class,"stageBiotypeMetadataValueCustomMapping");
			if(idToStageBiotypeMetadataValue.get(stageBiotypeMetadataValue.getId())==null)
				idToStageBiotypeMetadataValue.put(stageBiotypeMetadataValue.getId(), stageBiotypeMetadataValueDto);
			else
				stageBiotypeMetadataValueDto = idToStageBiotypeMetadataValue.get(stageBiotypeMetadataValue.getId());
		}
		return stageBiotypeMetadataValueDto;
	}

	@Transactional
	public void save(StageBiotypeMetadataValueDto stageBiotypeMetadataValue) throws Exception {
		save(stageBiotypeMetadataValue, false);
	}
	
	protected void save(StageBiotypeMetadataValueDto stageBiotypeMetadataValue, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(stageBiotypeMetadataValue)) {
				savedItems.add(stageBiotypeMetadataValue);
				biotypeMetadataValueService.save(stageBiotypeMetadataValue.getBiotypeMetadataValue(), true);
				if(stageBiotypeMetadataValue.getStage().getId().equals(Constants.NEWTRANSIENTID))
					stageService.save(stageBiotypeMetadataValue.getStage(), true);
				stageBiotypeMetadataValueDao.saveOrUpdate(dozerMapper.map(stageBiotypeMetadataValue, StageBiotypeMetadataValue.class, "stageBiotypeMetadataValueCustomMapping"));
				idToStageBiotypeMetadataValue.put(stageBiotypeMetadataValue.getId(), stageBiotypeMetadataValue);
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
	public void delete(StageBiotypeMetadataValueDto stageBiotypeMetadataValue) {
		delete(stageBiotypeMetadataValue, false);
	}
	
	protected void delete(StageBiotypeMetadataValueDto stageBiotypeMetadataValue, Boolean cross) {
		stageBiotypeMetadataValueDao.delete(stageBiotypeMetadataValue.getId());
	}

}
