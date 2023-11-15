package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SubGroupBiotypeMetadataValue;

public interface SubGroupBiotypeMetadataValueDao {
	
	public SubGroupBiotypeMetadataValue get(Integer id);

	public List<SubGroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId);
	
	public List<SubGroupBiotypeMetadataValue> getBySubGroup(int subGroupId);
	
	public SubGroupBiotypeMetadataValue getBySubGroupAndBiotypeMetadataValue(Integer subGroupId, Integer biotypeMetadataValueId);
	
	public List<SubGroupBiotypeMetadataValue> list();

	public int getCount();
	
	public void addSubGroupBiotypeMetadataValue(SubGroupBiotypeMetadataValue subGroupBiotypeMetadataValue);
	
	public void deleteBySubGroup(int subGroupId);

	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId);

	public void delete(Integer subGroupBiotypeMetadataValue);

	public Integer saveOrUpdate(SubGroupBiotypeMetadataValue subgroupBiotypeMetadataValue);
}
