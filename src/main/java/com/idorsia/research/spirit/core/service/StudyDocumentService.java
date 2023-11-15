package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DocumentType;
import com.idorsia.research.spirit.core.dao.StudyDocumentDao;
import com.idorsia.research.spirit.core.dto.DocumentDto;
import com.idorsia.research.spirit.core.dto.StudyDocumentDto;
import com.idorsia.research.spirit.core.model.StudyDocument;

@Service
public class StudyDocumentService extends AbstractService implements Serializable{

	private static final long serialVersionUID = -1272739822398536820L;
	@Autowired
	private StudyDocumentDao studyDocumentDao;
	@Autowired
	private StudyService studyService;
	@Autowired
	private DocumentService documentService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, StudyDocumentDto> idToStudyDocument = (Map<Integer, StudyDocumentDto>) getCacheMap(StudyDocumentDto.class);
	
	public StudyDocument get(Integer id) {
		return studyDocumentDao.get(id);
	}
	
	public int addStudyDocument(StudyDocument studyDocument) {
		return studyDocumentDao.addStudyDocument(studyDocument);
	}

	public List<StudyDocument> getByStudyId(Integer studyId){
		return studyDocumentDao.getByStudyId(studyId);
	}

	public StudyDocument getByDocumentId(Integer documentId) {
		return studyDocumentDao.getByDocumentId(documentId);
	}
	
	public StudyDocumentDto map(StudyDocument studyDocument) {
		StudyDocumentDto studyDocumentDto = idToStudyDocument.get(studyDocument.getId());
		if(studyDocumentDto==null) {
			studyDocumentDto = dozerMapper.map(studyDocument,StudyDocumentDto.class,"studyDocumentCustomMapping");
			if(idToStudyDocument.get(studyDocument.getId())==null)
				idToStudyDocument.put(studyDocument.getId(), studyDocumentDto);
			else
				studyDocumentDto = idToStudyDocument.get(studyDocument.getId());
		}
		return studyDocumentDto;
	}
	
	public List<StudyDocumentDto> map(Collection<StudyDocument> studyDocuments) {
		List<StudyDocumentDto> result = new ArrayList<>();
		for(StudyDocument studyDocument : studyDocuments) {
			result.add(map(studyDocument));
		}
		return result;
	}

	@Transactional
	public void save(StudyDocumentDto studyDocument) throws Exception {
		save(studyDocument, false);
	}
	
	protected void save(StudyDocumentDto studyDocument, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(studyDocument)) {
				savedItems.add(studyDocument);
				if(studyDocument.getStudy().getId().equals(Constants.NEWTRANSIENTID)) {
					studyService.save(studyDocument.getStudy());
				}
				if(studyDocument.getDocument().getId().equals(Constants.NEWTRANSIENTID)) {
					documentService.save(studyDocument.getDocument());
				}
				studyDocumentDao.saveOrUpdate(dozerMapper.map(studyDocument, StudyDocument.class, "studyDocumentCustomMapping"));
				idToStudyDocument.put(studyDocument.getId(), studyDocument);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@Transactional
	public void delete(StudyDocumentDto studyDocument) {
		delete(studyDocument, false);
	}
	
	protected void delete(StudyDocumentDto studyDocument, Boolean cross) {
		studyDocumentDao.delete(studyDocument);
	}
	
	public Map<DocumentType, List<DocumentDto>> mapDocumentTypes(Set<StudyDocumentDto> documents) {
		Map<DocumentType, List<DocumentDto>> res = new HashMap<>();
		for (StudyDocumentDto d : documents) {
			List<DocumentDto> docs = res.get(d.getDocument().getType());
			if(docs==null) {
				res.put(d.getDocument().getType(), docs = new ArrayList<>());
			}
			docs.add(d.getDocument());
		}
		return res;
	}
}
