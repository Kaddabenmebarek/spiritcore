package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Document;

public interface DocumentDao {

	public Document get(Integer id);
	
	public List<Document> list();

	public int getCount();
	
	public Integer saveOrUpdate(Document document);

	public void addDocument(Document document);
	
	public void delete(int documentId);
	
}
