package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.LinkedDocument;

public interface LinkedDocumentDao {
	
	LinkedDocument get(Integer id);

	int addLinkedDocument(LinkedDocument linkedDocument);

	void delete(Integer linkedDocumentId);

	List<LinkedDocument> getLinkedDocumentsByBiosample(Integer biosampleId);

	List<LinkedDocument> getLinkedDocumentsByLinkedDocument(Integer linkedBiosampleId);

	List<LinkedDocument> getLinkedDocumentsByMetadata(Integer metadataId);

	Integer saveOrUpdate(LinkedDocument linkedDocument);
}
