package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.LinkedDocumentDao;
import com.idorsia.research.spirit.core.dto.LinkedDocumentDto;
import com.idorsia.research.spirit.core.model.LinkedDocument;

@Service
public class LinkedDocumentService extends AbstractService implements Serializable{
	
	private static final long serialVersionUID = 6008214225091352855L;
	@Autowired
	private LinkedDocumentDao linkedDocumentDao;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private BiotypeMetadataService biotypeMetadataService;
	@Autowired
	private BiosampleService biosampleService;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, LinkedDocumentDto> idToLinkedDocument = (Map<Integer, LinkedDocumentDto>) getCacheMap(LinkedDocumentDto.class);
	
	public LinkedDocument get(Integer id) {
		return linkedDocumentDao.get(id);
	}
	
	public int addLinkedDocument(LinkedDocument linkedDocument) {
		return linkedDocumentDao.addLinkedDocument(linkedDocument);
	}
	
	public int addLinkedDocument(LinkedDocumentDto linkedDocument) {
		return linkedDocumentDao.addLinkedDocument(map(linkedDocument));
	}

	public List<LinkedDocument> getLinkedDocumentsByBiosample(Integer biosampleId) {
		return linkedDocumentDao.getLinkedDocumentsByBiosample(biosampleId);
	}

	public List<LinkedDocument> getLinkedDocumentsByLinkedDocument(Integer linkedDocumentId) {
		return linkedDocumentDao.getLinkedDocumentsByLinkedDocument(linkedDocumentId);
	}

	public List<LinkedDocument> getLinkedDocumentsByMetadata(Integer metadataId) {
		return linkedDocumentDao.getLinkedDocumentsByMetadata(metadataId);
	}
	
	public LinkedDocumentDao getLinkedDocumentDao() {
		return linkedDocumentDao;
	}

	public void setLinkedDocumentDao(LinkedDocumentDao linkedDocumentDao) {
		this.linkedDocumentDao = linkedDocumentDao;
	}
	
	public LinkedDocumentDto map(LinkedDocument linkedDocument) {
		LinkedDocumentDto linkedDocumentDto = idToLinkedDocument.get(linkedDocument.getId());
		if(linkedDocumentDto==null) {
			linkedDocumentDto = dozerMapper.map(linkedDocument,LinkedDocumentDto.class,"linkedDocumentCustomMapping");
			if(idToLinkedDocument.get(linkedDocument.getId())==null)
				idToLinkedDocument.put(linkedDocument.getId(), linkedDocumentDto);
			else
				linkedDocumentDto = idToLinkedDocument.get(linkedDocument.getId());
		}
		return linkedDocumentDto;
	}
	
	public LinkedDocument map(LinkedDocumentDto linkedDocument) {
		LinkedDocument linkedDocumentDto = dozerMapper.map(linkedDocument,LinkedDocument.class,"linkedDocumentCustomMapping");
		return linkedDocumentDto;
	}
	
	public List<LinkedDocumentDto> map(List<LinkedDocument> linkedDocuments) {
		List<LinkedDocumentDto> result = new ArrayList<>();
		for(LinkedDocument linkedDocument : linkedDocuments) {
			result.add(map(linkedDocument));
		}
		return result;
	}

	@Transactional
	public void save(LinkedDocumentDto linkedDocument) throws Exception {
		save(linkedDocument, false);
	}
	
	protected void save(LinkedDocumentDto linkedDocument, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(linkedDocument)) {
				savedItems.add(linkedDocument);
				if(linkedDocument.getLinkedDocument().getId().equals(Constants.NEWTRANSIENTID))
					documentService.save(linkedDocument.getLinkedDocument());
				if(linkedDocument.getMetadata().getId().equals(Constants.NEWTRANSIENTID))
					biotypeMetadataService.save(linkedDocument.getMetadata());
				if(linkedDocument.getBiosample().getId().equals(Constants.NEWTRANSIENTID))
					biosampleService.save(linkedDocument.getBiosample());
				linkedDocumentDao.saveOrUpdate(dozerMapper.map(linkedDocument, LinkedDocument.class, "linkedDocumentCustomMapping"));
				idToLinkedDocument.put(linkedDocument.getId(), linkedDocument);
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
	public void delete(LinkedDocumentDto linkedDocument) throws Exception {
		delete(linkedDocument, false);
	}
	
	protected void delete(LinkedDocumentDto linkedDocument, Boolean cross) throws Exception {
		linkedDocumentDao.delete(linkedDocument.getId());
		documentService.delete(linkedDocument.getLinkedDocument(), true);
	}
}
