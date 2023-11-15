package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Set;

import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Biotype;

public interface BiotypeDao {

	public Biotype get(Integer id);
	
	public List<Biotype> list();
	
	public List<Biotype> getByParentId(Integer parentId);

	public int getCount();
	
	public Integer saveOrUpdate(Biotype biotype);

	public int addBiotype(Biotype biotype);
	
	public void delete(int biotypeId);

	public Biotype getByName(String name);

	public List<String> getAutoCompletionFieldsForName(BiotypeDto biotype, StudyDto study);

	public List<String> getAutoCompletionFieldsForSampleId(BiotypeDto biotype, BiotypeMetadataDto fromAgregated,
			StudyDto study);

	public List<String> getAutoCompletionFields(BiotypeMetadataDto metadataType, StudyDto study);

	public int countRelation(int id);

	public List<Biotype> getBiotypes(String studyId, Set<Integer> assayIds);

}
