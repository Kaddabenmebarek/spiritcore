package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.dao.PropertyDao;
import com.idorsia.research.spirit.core.model.Property;

@Service
public class PropertyService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 6127465881204997265L;
	@Autowired
	private PropertyDao propertyDao;

	public Property get(Integer id) {
		return propertyDao.get(id);
	}

	public List<Property> list() {
		return propertyDao.list();
	}

	public int getCount() {
		return propertyDao.getCount();
	}

	public Property getPropertyByName(String name) {
		return propertyDao.getPropertyByName(name);
	}

	public PropertyDao getPropertyDao() {
		return propertyDao;
	}

	public void setPropertyDao(PropertyDao propertyDao) {
		this.propertyDao = propertyDao;
	}
	
	@Transactional
	public void delete(Property property) {
		delete(property, false);
	}
	
	protected void delete(Property property, Boolean cross) {
		propertyDao.delete(property.getId());
	}
}
