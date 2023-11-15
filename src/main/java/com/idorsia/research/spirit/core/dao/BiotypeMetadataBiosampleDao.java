package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.BiotypeMetadataBiosample;

public interface BiotypeMetadataBiosampleDao {

	public int addBiotypeMetadataBiosample(BiotypeMetadataBiosample biotypeMetadataBiosample);

	public Integer saveOrUpdate(BiotypeMetadataBiosample biotypeMetadataBiosample);

	public void delete(int id);

	public BiotypeMetadataBiosample get(Integer id);
	
	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleById(int id);

	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleByBiosampleAndMetadata(Integer biosampleId,
			Integer biotypeMetadataId);

	public List<BiotypeMetadataBiosample> getBiotypeMetadataBiosamplesByBiosample(Integer biosampleId);

	public List<BiotypeMetadataBiosample> getMetadataByBiosamples(List<Integer> idsTemp);
}
