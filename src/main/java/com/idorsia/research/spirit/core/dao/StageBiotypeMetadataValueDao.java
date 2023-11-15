package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.StageBiotypeMetadataValue;

public interface StageBiotypeMetadataValueDao {
	
	public StageBiotypeMetadataValue get(Integer id);

	public List<StageBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId);
	
	public List<StageBiotypeMetadataValue> getByStage(int stageId);
	
	public StageBiotypeMetadataValue getByStageAndBiotypeMetadataValue(Integer stageId, Integer biotypeMetadataValueId);
	
	public List<StageBiotypeMetadataValue> list();

	public int getCount();
	
	public void addStageBiotypeMetadataValue(StageBiotypeMetadataValue stageBiotypeMetadataValue);
	
	public void deleteByStage(int stageId);

	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId);

	public void delete(Integer stageBiotypeMetadataValue);

	public Integer saveOrUpdate(StageBiotypeMetadataValue stageBiotypeMetadataValue);
}
