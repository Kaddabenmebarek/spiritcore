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
import com.idorsia.research.spirit.core.dao.GroupPatternDao;
import com.idorsia.research.spirit.core.dto.GroupPatternDto;
import com.idorsia.research.spirit.core.model.GroupPattern;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class GroupPatternService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 2709325540942904077L;
	@Autowired
	private GroupPatternDao groupPatternDao;
	@Autowired
	private ActionPatternsService actionPatternService;
	@Autowired
	private GroupService groupService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, GroupPatternDto> idToGroupPattern = (Map<Integer, GroupPatternDto>) getCacheMap(GroupPatternDto.class);

	public GroupPattern get(Integer id) {
		return groupPatternDao.get(id);
	}
	
	public List<GroupPattern> getByGroup(int groupId) {
		return groupPatternDao.getByGroup(groupId);
	}

	public GroupPattern getByActionPatternAndGroup(int actionPatternId, int groupId) {
		return groupPatternDao.getByActionPatternAndGroup(actionPatternId, groupId);
	}

	public List<GroupPattern> list() {
		return groupPatternDao.list();
	}

	public int getCount() {
		return groupPatternDao.getCount();
	}

	public Integer saveOrUpdate(GroupPattern groupPattern) {
		return groupPatternDao.saveOrUpdate(groupPattern);
	}

	public int addGroupPattern(GroupPattern groupPattern) {
		return groupPatternDao.addGroupPattern(groupPattern);
	}

	public GroupPatternDao getGroupPatternDao() {
		return groupPatternDao;
	}

	public void setGroupPatternDao(GroupPatternDao groupPatternDao) {
		this.groupPatternDao = groupPatternDao;
	}

	protected void deleteByActionPattern(Integer actionPatternId) {
		groupPatternDao.deleteByActionPattern(actionPatternId);
	}

	protected void deleteByGroup(Integer groupId) {
		groupPatternDao.deleteByGroup(groupId);
	}

	public Set<GroupPatternDto> map(List<GroupPattern> groupPatterns) {
		Set<GroupPatternDto> res = new HashSet<GroupPatternDto>();
		for(GroupPattern groupPattern : groupPatterns) {
			res.add(map(groupPattern));
		}
		return res;
	}
	
	public GroupPatternDto map(GroupPattern grouptPattern) {
		GroupPatternDto groupPatternDto = idToGroupPattern.get(grouptPattern.getId());
		if(groupPatternDto==null) {
			groupPatternDto = dozerMapper.map(grouptPattern, GroupPatternDto.class,"groupPatternCustomMapping");
			if(idToGroupPattern.get(grouptPattern.getId())==null)
				idToGroupPattern.put(grouptPattern.getId(), groupPatternDto);
			else
				groupPatternDto = idToGroupPattern.get(grouptPattern.getId());
		}
		return groupPatternDto;
	}
	
	@Transactional
	public void save(GroupPatternDto grouptPattern) throws Exception {
		save(grouptPattern, false);
	}
	
	protected void save(GroupPatternDto groupPattern, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(groupPattern)) {
				savedItems.add(groupPattern);
				if(groupPattern.getActionpattern().getId().equals(Constants.NEWTRANSIENTID))
					actionPatternService.save(groupPattern.getActionpattern(), true);
				if(groupPattern.getGroup().getId().equals(Constants.NEWTRANSIENTID))
					groupService.save(groupPattern.getGroup(), true);
				groupPattern.setUpdDate(new Date());
				groupPattern.setUpdUser(UserUtil.getUsername());
				if(groupPattern.getId().equals(Constants.NEWTRANSIENTID)) {
					groupPattern.setCreDate(new Date());
					groupPattern.setCreUser(UserUtil.getUsername());
				}
				groupPattern.setId(saveOrUpdate(dozerMapper.map(groupPattern, GroupPattern.class, "groupPatternCustomMapping")));
				idToGroupPattern.put(groupPattern.getId(), groupPattern);
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
	public void delete(GroupPatternDto groupPattern) {
		delete(groupPattern, false);
	}
	
	protected void delete(GroupPatternDto groupPattern, Boolean cross) {
		groupPatternDao.delete(groupPattern.getId());
	}

}
