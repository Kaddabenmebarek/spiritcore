package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.idorsia.research.spirit.core.model.Enclosure;

public interface EnclosureDao {
	
	public Enclosure get(Integer id);

	public List<Enclosure> list();

	public int getCount();

	public Integer saveOrUpdate(Enclosure enclosure);

	public int addEnclosure(Enclosure enclosure);

	public void delete(int enclosureId);

	public Set<Enclosure> getBySamples(Set<Integer> samplesIds, int studyId);

	public Set<Enclosure> getByStudy(Integer studyId);

	public Map<String, List<Integer>> getCageBiosampleMap(List<Integer> cageIds);

	public List<Enclosure> getEnclosuresByStudy(Integer studyId);

}
