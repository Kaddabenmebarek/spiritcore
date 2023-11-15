package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SpiritPropertyDao;
import com.idorsia.research.spirit.core.model.SpiritProperty;

@Repository
public class SpiritPropertyDaoImpl extends AbstractDao<SpiritProperty> implements SpiritPropertyDao {

	private final static String TABLE_NAME = "SPIRIT_PROPERTY";
	
	@Override
	public SpiritProperty getById(String id) {
		String sql = String.format("SELECT * FROM SPIRIT_PROPERTY WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, SpiritProperty.class, sql);
	}

	@Override
	public List<SpiritProperty> getAll() {
		String sql = String.format("SELECT * FROM SPIRIT_PROPERTY");
		return (List<SpiritProperty>) super.getObjectList(TABLE_NAME, SpiritProperty.class, sql);
	}

}
