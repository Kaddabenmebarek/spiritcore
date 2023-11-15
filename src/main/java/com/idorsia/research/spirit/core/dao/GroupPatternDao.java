package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.GroupPattern;

public interface GroupPatternDao {

	public GroupPattern get(Integer id);
	
	public List<GroupPattern> getByGroup(int groupId);
	
	public List<GroupPattern> list();

	public int getCount();
	
	public Integer saveOrUpdate(GroupPattern groupPattern);

	public int addGroupPattern(GroupPattern groupPattern);
	
	public void delete(int groupPatternId);
	
	public void deleteByActionPattern(Integer actionPatternId);

	public void deleteByGroup(Integer groupId);

	public GroupPattern getByActionPatternAndGroup(int actionPatternId, int groupId);
}
