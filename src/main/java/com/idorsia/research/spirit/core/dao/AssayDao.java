package com.idorsia.research.spirit.core.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Assay;
import com.idorsia.research.spirit.core.util.Triple;

public interface AssayDao {
	
	public Assay get(Integer id);
	
	public Assay getAssayById(int id);
	
	public Assay getAssayByName(String name);
	
	public List<Assay> getAssaysByStudy(Integer studyId);
	
	public List<Assay> list();
	
	public List<String> getSendLocalisation();

	public List<String[]> getSendObservation();

	public int getCount();
	
	public Integer saveOrUpdate(Assay assay);

	public int addAssay(Assay assay);
	
	public void delete(int assayId);

	public Map<String, Map<String, Triple<Integer, String, Date>>> countResultsByStudyAssay(
			Collection<StudyDto> studies);

	public Set<Assay> getAssaysFromElbs(String elb);

	public Map<Integer, List<Integer>> getAssayResultMapByStudyAndBiotype(List<StudyDto> studies,
			BiotypeDto forcedBiotype);
}
