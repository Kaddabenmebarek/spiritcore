package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.BiotypeMetadataValue;

public interface BiotypeMetadataValueDao {
	
	public BiotypeMetadataValue get(Integer id);
	
	public BiotypeMetadataValue getById(int id);
	
	public List<BiotypeMetadataValue> getByGroup(Integer groupId);

	public List<BiotypeMetadataValue> getBySubGroup(Integer subGroupId);

	public List<BiotypeMetadataValue> getByStage(Integer stageId);
	
	public List<BiotypeMetadataValue> getByBiotypeMetadata(Integer biotypeMetadataId);
	
	public List<BiotypeMetadataValue> list();

	public int getCount();

	public Integer saveOrUpdate(BiotypeMetadataValue enclosure);

	public int addBiotypeMetadataValue(BiotypeMetadataValue enclosure);

	public void delete(int enclosureId);
}
