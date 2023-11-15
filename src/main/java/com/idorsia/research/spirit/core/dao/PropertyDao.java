package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Property;

public interface PropertyDao  {

	public Property get(Integer id);
	
	public List<Property> list();
	
	public Property getPropertyByName(String name);

	public int getCount();

	public void delete(Integer id);
}
