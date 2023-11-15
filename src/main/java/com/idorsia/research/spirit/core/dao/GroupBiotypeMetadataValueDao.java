package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.GroupBiotypeMetadataValue;

public interface GroupBiotypeMetadataValueDao {
	
	public GroupBiotypeMetadataValue get(Integer id);

	public List<GroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId);
	
	public List<GroupBiotypeMetadataValue> getByGroup(int groupId);
	
	public GroupBiotypeMetadataValue getByGroupAndBiotypeMetadataValue(Integer groupId, Integer biotypeMetadataValueId);
	
	public List<GroupBiotypeMetadataValue> list();

	public int getCount();
	
	public void addGroupBiotypeMetadataValue(GroupBiotypeMetadataValue groupBiotypeMetadataValue);
	
	public void deleteByGroup(int groupId);

	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId);

	public void delete(Integer groupBiotypeMetadataValue);

	public Integer saveOrUpdate(GroupBiotypeMetadataValue groupBiotypeMetadataValue);
}
