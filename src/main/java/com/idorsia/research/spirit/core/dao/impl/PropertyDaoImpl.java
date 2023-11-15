package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.PropertyDao;
import com.idorsia.research.spirit.core.model.Property;

@Repository
public class PropertyDaoImpl extends AbstractDao<Property> implements PropertyDao {

	private static final String TABLE_NAME = "PROPERTY";
	
	@Override
	public Property get(Integer id) {
		return super.get(TABLE_NAME, Property.class, id);
	}

	@Override
	public List<Property> list() {
		return super.getlist(TABLE_NAME, Property.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}
	
	@Override
	public Property getPropertyByName(String name) {
		String sql = String.format("SELECT * FROM PROPERTY WHERE NAME = '%s'", name);
		return super.getObject(TABLE_NAME, Property.class, sql);
	}
	
	@Override
	public void delete(Integer propertyId) {
		super.delete(TABLE_NAME, propertyId);
	}
}
