package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SpiritProperty;

public interface SpiritPropertyDao {

	public SpiritProperty getById(String id);
	
	public List<SpiritProperty> getAll();

}
