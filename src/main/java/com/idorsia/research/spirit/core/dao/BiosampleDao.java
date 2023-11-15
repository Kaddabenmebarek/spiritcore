package com.idorsia.research.spirit.core.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.BiosampleQuery;
import com.idorsia.research.spirit.core.model.Biosample;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.util.Triple;

public interface BiosampleDao {

	public Biosample get(Integer id);
	
	public List<Biosample> getBiosampleByInheritedPhase(Integer phaseId);
	
	public List<Biosample> getBiosampleByEndPhase(Integer phaseId);
	
	public List<Biosample> getBiosampleByUser(String user);
	
	public Biosample getBiosampleBySampleId(String sampleId);
	
	public List<Biosample> getBiosampleBySampleIds(List<String> sampleIds);
	
	public List<Biosample> getBiosampleByContainerId(String containerId);

	public List<Biosample> getByStudy(Integer studyId);

	public List<Biosample> getAllByStudy(Integer studyId);

	public List<Biosample> getBiosamplesByLocationId(Integer locationId);
	
	public List<Biosample> getBiosamplesByParentId(Integer parentId);
	
	public List<Biosample> getBySampling(Integer samplingId);
	
	public List<Biosample> getByInheritedStudy(Integer studyId);
	
	public Map<String, String> getAnimalDBInfo(String sampleId);
	
	public List<Biosample> list();

	public int getCount();
	
	public Integer saveOrUpdate(Biosample biosample);

	public int addBiosample(Biosample biosample);
	
	public void delete(int biosampleId);

	public Set<Biosample> getBiosamples(Set<Integer> samplesIds);

	public List<Biosample> getByCage(String cageClicked, Integer studyId);

	public Integer getStrainID(String type);

	public List<String> getSampleIds(ArrayList<Integer> ids);

	public Map<String, String> getHumanDBInfo(String sampleId);

	public List<Biosample> getBiosampleByStudyAndSampleId(Integer studyId, List<String> animalIdOrNo);

	public List<Biosample> queryBiosample(BiosampleQuery query, User user) throws Exception;

	public List<String> getContainerTypes(StudyDto study);

	public List<Biotype> getBiotype(StudyDto study);

	public List<Biosample> getBiosamplesBySampleIds(Set<String> sampleIds);

	public Map<String, Map<String, Triple<Integer, String, Date>>> countSampleByStudyBiotype(
			Collection<StudyDto> studies, Date minDate);

	public List<Biosample> getBiosampleByBiotypeName(String biotypeName);

	public List<Biosample> getBiosampleByMetadataValue(String value);

	public Biosample getById(int id);

	public void delete(List<Integer> ids);
}
