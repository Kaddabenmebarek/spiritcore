package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.LinkedBiosample;

public interface LinkedBiosampleDao {
	
	LinkedBiosample get(Integer id);

	int addLinkedBiosample(LinkedBiosample linkedBiosample);

	void delete(Integer linkedBiosampleId);

	List<LinkedBiosample> getLinkedBiosamplesByBiosample(Integer biosampleId);

	List<LinkedBiosample> getLinkedBiosamplesByLinkedBiosample(Integer linkedBiosampleId);

	List<LinkedBiosample> getLinkedBiosamplesByMetadata(Integer metadataId);

	Integer saveOrUpdate(LinkedBiosample map);

}
