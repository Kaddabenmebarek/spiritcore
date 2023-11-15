package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.dto.StudyDocumentDto;
import com.idorsia.research.spirit.core.model.StudyDocument;

public interface StudyDocumentDao {
	
	StudyDocument get(Integer id);

	List<StudyDocument> getByStudyId(Integer studyId);

	StudyDocument getByDocumentId(Integer documentId);

	void delete(StudyDocumentDto linkedBiosample);

	int addStudyDocument(StudyDocument studyDocument);

	public Integer saveOrUpdate(StudyDocument studyDocument);

}
