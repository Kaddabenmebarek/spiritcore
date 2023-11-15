package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.AssayAttribute;

public interface AssayAttributeDao {

	public AssayAttribute get(Integer id);
	
	public AssayAttribute getAssayAttributeById(int id);
	
	public List<AssayAttribute> getAssayAttributesByName(String name);
	
	public List<AssayAttribute> getAssayAttributesByAssay(Integer assayId);
	
	public List<AssayAttribute> getAssayAttributesByStudy(Integer studyId);
	
	public List<String> getRegisteredValues(Integer attributeId);
	
	public List<String> getInputFields(Integer id, String studyIds);

	public AssayAttribute getAssayAttributeByAssayAndName(Integer assay_Id, String name);
	
	public List<AssayAttribute> list();

	public int getCount();
	
	public Integer saveOrUpdate(AssayAttribute assay);

	public int addAssayAttribute(AssayAttribute assay);
	
	public void delete(int assayId);
}