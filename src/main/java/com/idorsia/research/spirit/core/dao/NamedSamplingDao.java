package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.NamedSampling;

public interface NamedSamplingDao {

	public NamedSampling get(Integer id);
	
	public List<NamedSampling> list();

	public int getCount();
	
	public Integer saveOrUpdate(NamedSampling namedSampling);

	public int addNamedSampling(NamedSampling namedSampling);
	
	public void delete(int namedSamplingId);

	public List<NamedSampling> getNamedTreatmentsByStudy(Integer studyId);

	public List<NamedSampling> getNamedSamplings(StudyDto study, List<Integer> sids, User user);
	
}
