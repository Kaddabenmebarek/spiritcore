package com.idorsia.research.spirit.core.dao;

import java.util.Collection;
import java.util.List;

import com.idorsia.research.spirit.core.dto.view.ResultQuery;
import com.idorsia.research.spirit.core.model.AssayResult;

public interface AssayResultDao {
	
	public AssayResult get(Integer id);
	
	public AssayResult getAssayResultById(int id);
	
	public List<AssayResult> getAssayResultsByAssay(int assay_id);
	
	public List<AssayResult> getAssayResultsByBiosample(int sample_id);
	
	public List<AssayResult> getAssayResultsByPhase(int phase_id);
	
	public List<AssayResult> getAssayResultsByStudy(int study_id);
	
	public List<AssayResult> getAssayResultsByComments(String comments);
	
	public List<AssayResult> getAssayResultsByIds(Collection<Integer> ids);
	
	public List<AssayResult> getByValueAndAttributeId(String value, Integer id);
	
	public List<AssayResult> list();

	public int getCount();
	
	public Integer saveOrUpdate(AssayResult assayResult);

	public int addAssayResult(AssayResult assayResult);
	
	public void delete(int assayId);

	public List<AssayResult> getResults(ResultQuery query) throws Exception;

	public List<String> getElbsForStudy(String studyId);

	public List<String> getResultsByStudyAndAttribute(Integer studyId, Integer attributeId);

	public void delete(List<Integer> ids);
}
