package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.BiotypeMetadata;

public interface BiotypeMetadataDao {

	public BiotypeMetadata get(Integer id);
	
	public List<BiotypeMetadata> getByBiotype(int biotypeId);
	
	public List<BiotypeMetadata> list();

	public int getCount();
	
	public Integer saveOrUpdate(BiotypeMetadata biotypeMetadata);

	public int addBiotypeMetadata(BiotypeMetadata biotypeMetadata);
	
	public void delete(int biotypeMetadataId);

	public int countRelations(int id);
}
