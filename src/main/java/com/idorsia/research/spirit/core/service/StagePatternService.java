package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.StagePatternDao;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.StagePatternDto;
import com.idorsia.research.spirit.core.model.StagePattern;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class StagePatternService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -239456944976423526L;
	@SuppressWarnings("unchecked")
	private static Map<Integer, StagePatternDto> idToStagePattern = (Map<Integer, StagePatternDto>) getCacheMap(StagePatternDto.class);
	
	@Autowired
	private StagePatternDao stagePatternDao;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private StageService stageService;

	public StagePattern get(Integer id) {
		return stagePatternDao.get(id);
	}

	public StagePattern getByActionPattern(int actionPatternId) {
		return stagePatternDao.getByActionPattern(actionPatternId);
	}

	public List<StagePattern> getByStage(int stageId) {
		return stagePatternDao.getByStage(stageId);
	}

	public List<StagePattern> list() {
		return stagePatternDao.list();
	}

	public int getCount() {
		return stagePatternDao.getCount();
	}

	public Integer saveOrUpdate(StagePattern stagePattern) {
		return stagePatternDao.saveOrUpdate(stagePattern);
	}

	public int addStagePattern(StagePattern stagePattern) {
		return stagePatternDao.addStagePattern(stagePattern);
	}

	protected void deleteByStage(int stageId) {
		stagePatternDao.deleteByStage(stageId);
	}

	protected void deleteByActionPattern(int stagePatternId) {
		stagePatternDao.deleteByActionPattern(stagePatternId);
	}

	public List<Integer> getByStages(List<StageDto> stages) {
		List<Integer> stageIds = new ArrayList<Integer>();
		for(StageDto st : stages) {
			stageIds.add(st.getId());
		}
		return stagePatternDao.getByStages(stageIds);
	}

	public StagePatternDao getStagePatternDao() {
		return stagePatternDao;
	}

	public void setStagePatternDao(StagePatternDao stagePatternDao) {
		this.stagePatternDao = stagePatternDao;
	}

	public Set<StagePatternDto> map(List<StagePattern> stagePatterns) {
		Set<StagePatternDto> res = new HashSet<StagePatternDto>();
		for(StagePattern stagePattern : stagePatterns) {
			res.add(map(stagePattern));
		}
		return res;
	}
	
	public StagePatternDto map(StagePattern stagePattern) {
		StagePatternDto stagePatternDto = idToStagePattern.get(stagePattern.getId());
		if(stagePatternDto==null) {
			stagePatternDto = dozerMapper.map(stagePattern, StagePatternDto.class,"stagePatternCustomMapping");
			if(idToStagePattern.get(stagePattern.getId())==null)
				idToStagePattern.put(stagePattern.getId(), stagePatternDto);
			else
				stagePatternDto = idToStagePattern.get(stagePattern.getId());
		}
		return stagePatternDto;
	}

	@Transactional
	public void save(StagePatternDto stagePattern) throws Exception {
		save(stagePattern, false);
	}
	
	protected void save(StagePatternDto stagePattern, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(stagePattern)) {
				savedItems.add(stagePattern);
				if(stagePattern.getActionpattern().getId().equals(Constants.NEWTRANSIENTID))
					actionPatternsService.save(stagePattern.getActionpattern(), true);
				if(stagePattern.getStage().getId().equals(Constants.NEWTRANSIENTID))
					stageService.save(stagePattern.getStage(), true);
				stagePattern.setUpdDate(new Date());
				stagePattern.setUpdUser(UserUtil.getUsername());
				if(stagePattern.getId().equals(Constants.NEWTRANSIENTID)) {
					stagePattern.setCreDate(new Date());
					stagePattern.setCreUser(UserUtil.getUsername());
				}
				stagePattern.setId(saveOrUpdate(dozerMapper.map(stagePattern, StagePattern.class, "stagePatternCustomMapping")));
				idToStagePattern.put(stagePattern.getId(), stagePattern);
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
	public void delete(StagePatternDto stagePattern) {
		delete(stagePattern, false);
	}
	
	protected void delete(StagePatternDto stagePattern, Boolean cross) {
		stagePatternDao.delete(stagePattern.getId());
	}

}
