package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.model.AssayResultValue;

public interface AssayResultValueDao {
	
	public AssayResultValue get(Integer id);
	
	public AssayResultValue getAssayResultValueById(int id);
	
	public List<AssayResultValue> getAssayResultValuesByAssayResult(int assay_result_id);
	
	public List<AssayResultValue> getAssayResultValuesByStudy(Integer studyId);
	
	public List<AssayResultValue> getAssayResultValuesByDocument(int document_id);
	
	public List<AssayResultValue> getAssayResultValuesByAssayAttribute(Integer assay_attribute_id);
	
	public List<AssayResultValue> getAssayResultValuesByAssayAttributeAndValue(Integer assay_attribute_id, String value);
	
	public List<AssayResultValue> getOutputResultValues(List<Integer> ids);
	
	public List<AssayResultValue> getResultValues(List<Integer> ids);
	
	public AssayResultValue getAssayResultsByAssayResultAndAssayAttribute(int assay_result_id, int assay_attribute_id);
	
	public Integer saveOrUpdate(AssayResultValue assayResultValue);

	public int addAssayResultValue(AssayResultValue assayResultValue);
	
	public void delete(int assayId);

	public int countRelations(AssayAttributeDto testAttribute);

	public List<AssayResult> getResultsByAttributeAndValue(String value, Integer assayAttributeId);
}
