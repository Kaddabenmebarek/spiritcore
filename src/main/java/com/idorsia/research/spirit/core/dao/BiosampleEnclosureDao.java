package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.BiosampleEnclosure;

public interface BiosampleEnclosureDao {

	public BiosampleEnclosure get(Integer id);
	
	public List<BiosampleEnclosure> getByBiosample(int biosampleId);
	
	public List<BiosampleEnclosure> list();
	
	public List<BiosampleEnclosure> getByEnclosure(int enclosureId);
	
	public List<BiosampleEnclosure> getByPhaseIn(int phaseInId);

	public int getCount();
	
	public Integer saveOrUpdate(BiosampleEnclosure biosampleEnclosure);

	public int addBiosampleEnclosure(BiosampleEnclosure biosampleEnclosure);
	
	public void delete(int biosampleEnclosureId);
	
}
