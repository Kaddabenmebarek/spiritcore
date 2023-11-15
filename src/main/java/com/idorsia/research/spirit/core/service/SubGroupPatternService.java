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
import com.idorsia.research.spirit.core.dao.SubGroupPatternDao;
import com.idorsia.research.spirit.core.dto.SubGroupPatternDto;
import com.idorsia.research.spirit.core.model.SubGroupPattern;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SubGroupPatternService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -6555398862637060675L;
	@SuppressWarnings("unchecked")
	private static Map<Integer, SubGroupPatternDto> idToSubGroupPattern = (Map<Integer, SubGroupPatternDto>) getCacheMap(SubGroupPatternDto.class);
	
	@Autowired
	private SubGroupPatternDao subGroupPatternDao;
	@Autowired
	private ActionPatternsService actionPatternsService;
	@Autowired
	private SubGroupService subGroupService;

	public SubGroupPattern get(Integer id) {
		return subGroupPatternDao.get(id);
	}

	public SubGroupPattern getByAcionPattern(int actionPatternId) {
		return subGroupPatternDao.getByAcionPattern(actionPatternId);
	}

	public List<SubGroupPattern> list() {
		return subGroupPatternDao.list();
	}

	public List<SubGroupPattern> getBySubgroup(int subgroupId) {
		return subGroupPatternDao.getBySubgroup(subgroupId);
	}

	public SubGroupPattern getByActionPatternAndSubGroup(int actionpatternid, int subgroupid) {
		return subGroupPatternDao.getByActionPatternAndSubGroup(actionpatternid, subgroupid);
	}

	public int getCount() {
		return subGroupPatternDao.getCount();
	}

	public Integer saveOrUpdate(SubGroupPattern subgroupPattern) {
		return subGroupPatternDao.saveOrUpdate(subgroupPattern);
	}

	public SubGroupPatternDao getSubGroupPatternDao() {
		return subGroupPatternDao;
	}

	public void setSubGroupPatternDao(SubGroupPatternDao subGroupPatternDao) {
		this.subGroupPatternDao = subGroupPatternDao;
	}
	
	protected void deleteByActionPattern(Integer actionPatternId) {
		subGroupPatternDao.deleteByActionPattern(actionPatternId);
	}
	
	public void deleteBySubGroup(Integer subGroupId) {
		subGroupPatternDao.deleteBySubGroup(subGroupId);		
	}
	
	public int addSubGroupPattern(SubGroupPattern s) {
		return subGroupPatternDao.addSubGroupPattern(s);
	}

	public Set<SubGroupPatternDto> map(List<SubGroupPattern> subGroupPatterns) {
		Set<SubGroupPatternDto> res = new HashSet<SubGroupPatternDto>();
		for(SubGroupPattern subGroupPattern : subGroupPatterns) {
			res.add(map(subGroupPattern));
		}
		return res;
	}
	
	public SubGroupPatternDto map(SubGroupPattern subGroupPattern) {
		SubGroupPatternDto subGroupPatternDto = idToSubGroupPattern.get(subGroupPattern.getId());
		if(subGroupPatternDto==null) {
			subGroupPatternDto = dozerMapper.map(subGroupPattern, SubGroupPatternDto.class,"subGroupPatternCustomMapping");
			if(idToSubGroupPattern.get(subGroupPattern.getId())==null)
				idToSubGroupPattern.put(subGroupPattern.getId(), subGroupPatternDto);
			else
				subGroupPatternDto = idToSubGroupPattern.get(subGroupPattern.getId());
		}
		return subGroupPatternDto;
	}

	@Transactional
	public void save(SubGroupPatternDto subGroupPattern) throws Exception {
		save(subGroupPattern, false);
	}
	
	protected void save(SubGroupPatternDto subGroupPattern, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(subGroupPattern)) {
				savedItems.add(subGroupPattern);
				if(subGroupPattern.getActionpattern().getId().equals(Constants.NEWTRANSIENTID))
					actionPatternsService.save(subGroupPattern.getActionpattern(), true);
				if(subGroupPattern.getSubgroup().getId().equals(Constants.NEWTRANSIENTID))
					subGroupService.save(subGroupPattern.getSubgroup(), true);
				subGroupPattern.setUpdDate(new Date());
				subGroupPattern.setUpdUser(UserUtil.getUsername());
				if(subGroupPattern.getId().equals(Constants.NEWTRANSIENTID)) {
					subGroupPattern.setCreDate(new Date());
					subGroupPattern.setCreUser(UserUtil.getUsername());
				}
				subGroupPattern.setId(saveOrUpdate(dozerMapper.map(subGroupPattern, SubGroupPattern.class, "subGroupPatternCustomMapping")));
				idToSubGroupPattern.put(subGroupPattern.getId(), subGroupPattern);
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
	public void delete(SubGroupPatternDto subGroupPattern) {
		delete(subGroupPattern);
	}
	
	protected void delete(SubGroupPatternDto subGroupPattern, Boolean cross) {
		subGroupPatternDao.delete(subGroupPattern.getId());
	}
}
