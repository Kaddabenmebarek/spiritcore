package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.PropertyLink;

public interface PropertyLinkDao {

	public PropertyLink get(Integer id);
	
	public PropertyLink getPropertyLinkById(int id);
	
	public List<String> getValuesByPopertyId(Integer id);
	
	public List<PropertyLink> getPropertyLinksByStudy(Integer studyId);
	
	public List<PropertyLink> getPropertyLinksByProperty(Integer propertyId);
	
	public PropertyLink getPropertyLinkByStudyAndProperty(Integer studyId, Integer propertyId);
	
	public List<PropertyLink> list();

	public int getCount();
	
	public Integer saveOrUpdate(PropertyLink propertyLink);

	public int addPropertyLink(PropertyLink propertyLink);
	
	public void delete(int propertyLinkId);
}
