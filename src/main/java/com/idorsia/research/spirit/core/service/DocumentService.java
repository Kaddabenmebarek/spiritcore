package com.idorsia.research.spirit.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.IOUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DocumentType;
import com.idorsia.research.spirit.core.dao.DocumentDao;
import com.idorsia.research.spirit.core.dto.DocumentDto;
import com.idorsia.research.spirit.core.dto.StudyDocumentDto;
import com.idorsia.research.spirit.core.model.Document;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class DocumentService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -370149855872742155L;

	@Autowired
	private DocumentDao documentDao;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, DocumentDto> idToDocument = (Map<Integer, DocumentDto>) getCacheMap(DocumentDto.class);

	public Document get(Integer id) {
		return documentDao.get(id);
	}

	public List<Document> list() {
		return documentDao.list();
	}

	public int getCount() {
		return documentDao.getCount();
	}

	public Integer saveOrUpdate(Document document) {
		return documentDao.saveOrUpdate(document);
	}

	public void addDocument(Document document) {
		documentDao.addDocument(document);
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public DocumentDto getDocumentDto(Integer id) {
		return map(get(id));
	}

	public DocumentDto map(Document document) {
		DocumentDto documentDto = idToDocument.get(document.getId());
		if(documentDto==null) {
			documentDto = dozerMapper.map(document, DocumentDto.class,"documentCustomMapping");
			if(idToDocument.get(document.getId())==null)
				idToDocument.put(document.getId(), documentDto);
		}
		return documentDto;
	}
	
	public DocumentDto getDocumentDto(int id) {
		return map(get(id));
	}

	@Transactional
	public void save(DocumentDto document) {
		save(document, false);
	}
	
	protected void save(DocumentDto document, Boolean cross) {
		try {
			if(document!=null && !savedItems.contains(document)) {
				savedItems.add(document);
				document.setUpdDate(new Date());
				document.setUpdUser(UserUtil.getUsername());
				if(document.getId().equals(Constants.NEWTRANSIENTID)) {
					document.setCreDate(new Date());
					document.setCreUser(UserUtil.getUsername());
				}
				document.setId(saveOrUpdate(dozerMapper.map(document, Document.class, "documentCustomMapping")));
				idToDocument.put(document.getId(), document);
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
	public void delete(DocumentDto document) throws Exception {
		delete(document, false);
	}
	
	protected void delete(DocumentDto document, Boolean cross) throws Exception {
		documentDao.delete(document.getId());
	}

	public void addZipEntry(DocumentDto document, File f) throws Exception {
		addZipEntry(document, new DocumentDto(f));
	}

	public void addZipEntry(DocumentDto document, DocumentDto doc) throws Exception {
		if(document.getType()!=DocumentType.ZIP) {
			//Convert the existing document to a zip containing this doc
			//This case may happen if the datatype has been changed to ZIP
			DocumentDto toAdd = new DocumentDto(document.getFilename(), document.getBytes());
			document.setType(DocumentType.ZIP);
			document.setFilename(null);
			document.setBytes(null);
			addZipEntry(toAdd);
			addZipEntry(doc);
			return;
		}

		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			int n = 0;
			ZipOutputStream os = new ZipOutputStream(baos);
			//Add existing entries
			if(document.getBytes()!=null && document.getBytes().length>0) {
				try(ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(document.getBytes()))) {
					ZipEntry entry;
					while((entry = is.getNextEntry())!=null) {
						os.putNextEntry(entry);
						IOUtils.redirect(is, os);
						os.closeEntry();
						is.closeEntry();
						n++;
					}
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			}
			//Add new entry
			os.putNextEntry(new ZipEntry(doc.getFilename()));
			IOUtils.redirect(doc.getBytes(), os);
			os.closeEntry();

			//Close Zip stream
			os.close();
			n++;
			document.setFilename(n + "_docs.zip");
			document.setBytes(baos.toByteArray());
		}
	}
	
	public void addZipEntry(DocumentDto doc) throws Exception {
		if(doc.getType()!=DocumentType.ZIP) {
			//Convert the existing document to a zip containing this doc
			//This case may happen if the datatype has been changed to ZIP
			DocumentDto toAdd = new DocumentDto(doc.getFilename(), doc.getBytes());
			doc.setType(DocumentType.ZIP);
			doc.setFilename(null);
			doc.setBytes(null);
			addZipEntry(toAdd);
			addZipEntry(doc);
			return;
		}

		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			int n = 0;
			ZipOutputStream os = new ZipOutputStream(baos);
			//Add existing entries
			if(doc.getBytes()!=null && doc.getBytes().length>0) {
				try(ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(doc.getBytes()))) {
					ZipEntry entry;
					while((entry = is.getNextEntry())!=null) {
						os.putNextEntry(entry);
						IOUtils.redirect(is, os);
						os.closeEntry();
						is.closeEntry();
						n++;
					}
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			}
			//Add new entry
			os.putNextEntry(new ZipEntry(doc.getFilename()));
			IOUtils.redirect(doc.getBytes(), os);
			os.closeEntry();

			//Close Zip stream
			os.close();
			n++;
			doc.setFilename(n + "_docs.zip");
			doc.setBytes(baos.toByteArray());
		}
	}

	public DocumentDto getZipEntry(DocumentDto document, int index) throws IOException {
		if(document.getType()!=DocumentType.ZIP) {
			if(index>0) return null;
			return document;
		}
		int n = 0;
		try(ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(document.getBytes()))) {
			ZipEntry entry;
			while((entry = is.getNextEntry())!=null) {
				if(n++==index) {
					return new DocumentDto(entry.getName(), IOUtils.getBytes(is));
				}
			}
		}
		return null;
	}

	public void removeZipEntry(DocumentDto document, int index) throws IOException {
		if(document.getType()!=DocumentType.ZIP) throw new IOException("Not a ZIP");
		//Recreate zip
		int n = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(ZipOutputStream os = new ZipOutputStream(baos)) {
			//Add existing entries
			try(ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(document.getBytes()))) {
				ZipEntry entry;
				while((entry = is.getNextEntry())!=null) {
					if(n++!=index) {
						os.putNextEntry(entry);
						IOUtils.redirect(is, os);
						os.closeEntry();
					}
					is.closeEntry();
				}
			}

			document.setFilename((n-1) + "_docs.zip");
			os.close();
			document.setBytes(baos.toByteArray());
		}
	}

	public Map<DocumentType, List<DocumentDto>> mapDocumentTypes(Set<DocumentDto> documents) {
		Map<DocumentType, List<DocumentDto>> res = new HashMap<>();
		for (DocumentDto d : documents) {
			List<DocumentDto> docs = res.get(d.getType());
			if(docs==null) {
				res.put(d.getType(), docs = new ArrayList<>());
			}
			docs.add(d);
		}
		return res;
	}

	public Map<String, DocumentDto> mapFilenames(Set<StudyDocumentDto> documents) {
		Map<String, DocumentDto> res = new HashMap<>();
		for (StudyDocumentDto d : documents) {
			res.put(d.getDocument().getFilename(), d.getDocument());
		}
		return res;
	}
}
