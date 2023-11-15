package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssayResultValueDao;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.model.AssayResultValue;
import com.idorsia.research.spirit.core.service.AssayResultService;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class AssayResultValueDaoImpl extends AbstractDao<AssayResultValue> implements AssayResultValueDao {
	
	private static final String TABLE_NAME = "ASSAY_RESULT_VALUE";

	@Autowired
	private AssayResultService assayResultService;
	
	private Map<Integer, List<AssayResultValue>> assayResultToAssayResultValue = new HashMap<>();
	
	@Override
	public AssayResultValue get(Integer id) {
		return super.get(TABLE_NAME, AssayResultValue.class, id);
	}

	@Override
	public AssayResultValue getAssayResultValueById(int id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, AssayResultValue.class, sql);
	}
	
	@Override
	public List<AssayResultValue> getAssayResultValuesByAssayResult(int assay_result_id) {
		List<AssayResultValue> values = assayResultToAssayResultValue.get(assay_result_id);
		if(values!=null)
			return values;
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ASSAY_RESULT_ID = %s", assay_result_id);
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}
	
	@Override
	public List<AssayResultValue> getAssayResultValuesByStudy(Integer studyId) {
		String sql = String.format("SELECT arv.* FROM ASSAY_RESULT_VALUE arv, ASSAY_RESULT ar "
				+ "WHERE arv.ASSAY_RESULT_ID=ar.id and ar.STUDY_ID = %s", studyId);
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}

	@Override
	public List<AssayResultValue> getAssayResultValuesByDocument(int document_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE DOCUMENT_ID = %s", document_id);
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}
	
	public List<AssayResultValue> getAssayResultValuesByAssayAttribute(Integer assay_attribute_id){
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ASSAY_ATTRIBUTE_ID = %s", assay_attribute_id);
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}
	
	@Override
	public List<AssayResultValue> getAssayResultValuesByAssayAttributeAndValue(Integer assay_attribute_id, String value){
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ASSAY_ATTRIBUTE_ID = %s and (text_value=%s or text_value like %s or text_value like %s)", assay_attribute_id, "'"+value+"'", "('%"+value+"; %')", "('%; "+value+"%')");
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}


	public List<AssayResultValue> getOutputResultValues(List<Integer> ids){
		String sql = String.format("SELECT arv.* FROM ASSAY_RESULT_VALUE arv, ASSAY_ATTRIBUTE aa WHERE ASSAY_RESULT_ID"+DataUtils.fetchInInt(new ArrayList<Integer>(ids))+" AND arv.TEXT_VALUE is not null AND arv.ASSAY_ATTRIBUTE_ID=aa.ID AND aa.OUTPUT_TYPE='OUTPUT'");
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}
	
	public List<AssayResultValue> getResultValues(List<Integer> ids){
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ASSAY_RESULT_ID"+DataUtils.fetchInInt(new ArrayList<Integer>(ids))+" order by assay_result_id");
		return (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
	}

	@Override
	public AssayResultValue getAssayResultsByAssayResultAndAssayAttribute(int assay_result_id,
			int assay_attribute_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT_VALUE WHERE ASSAY_RESULT_ID = %s AND ASSAY_ATTRIBUTE_ID = %s", assay_result_id, assay_attribute_id);
		return super.getObject(TABLE_NAME, AssayResultValue.class, sql);
	}

	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addAssayResultValue(AssayResultValue assayResultValue) {
		return super.add(TABLE_NAME, assayResultValue);
	}
	
	@Override
	public void delete(int assayResultValueId) {
		super.delete(TABLE_NAME, assayResultValueId);
	}

	@Override
	public Integer saveOrUpdate(AssayResultValue assayResultValue) {
		if (assayResultValue.getId() != null && assayResultValue.getId() > 0) {
			if(!assayResultValue.equals(get(assayResultValue.getId()))){
				String sql = "UPDATE ASSAY_RESULT_VALUE SET text_value=:text_value, " 
						+ "assay_attribute_id=:assay_attribute_id, "
						+ "assay_result_id=:assay_result_id, "
						+ "cre_date=:cre_date, "
						+ "upd_date=:upd_date, "
						+ "cre_user=:cre_user, "
						+ "upd_user=:upd_user, "
						+ "document_id=:document_id "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", assayResultValue.getId())
			    		.addValue("text_value", assayResultValue.getTextValue())
			    		.addValue("assay_attribute_id", assayResultValue.getAssayAttributeId())
			    		.addValue("assay_result_id", assayResultValue.getAssayResultId())
			    		.addValue("cre_date", assayResultValue.getCreDate())
			    		.addValue("upd_date", assayResultValue.getUpdDate())
			    		.addValue("cre_user", assayResultValue.getCreUser())
			    		.addValue("upd_user", assayResultValue.getUpdUser())
			    		.addValue("document_id", assayResultValue.getDocumentId());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assayResultValue.setId(getSequence(Constants.ASSAY_RESULT_VALUE_SEQUENCE_NAME));
			addAssayResultValue(assayResultValue);
			addTransient(assayResultValue);
		}
		addIdToObject(AssayResultValue.class, assayResultValue.getId(), assayResultValue);
		return assayResultValue.getId();
	}
	
	public AssayResultValue rowMap(ResultSet rs) {
		AssayResultValue assayResultValue = null;
    	try {
    		assayResultValue = new AssayResultValue();
			assayResultValue.setId(rs.getInt("id"));
			assayResultValue.setTextValue(rs.getString("text_value"));
			assayResultValue.setAssayAttributeId(rs.getInt("assay_attribute_id"));
			assayResultValue.setAssayResultId(rs.getInt("assay_result_id"));
			assayResultValue.setDocumentId(rs.getInt("document_id"));
			assayResultValue.setCreDate(rs.getDate("cre_date"));
			assayResultValue.setUpdDate(rs.getDate("upd_date"));
			assayResultValue.setCreUser(rs.getString("cre_user"));
			assayResultValue.setUpdUser(rs.getString("upd_user"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return assayResultValue;
	}
	
	@SuppressWarnings("unused")
	private void addInMap(AssayResultValue assayResultValue) {
		List<AssayResultValue> arvs = assayResultToAssayResultValue.get(assayResultValue.getAssayResultId());
		if(arvs==null) {
			arvs=new ArrayList<>();
			assayResultToAssayResultValue.put(assayResultValue.getAssayResultId(), arvs);
		}
		arvs.add(assayResultValue);
	}

	@Override
	public int countRelations(AssayAttributeDto testAttribute) {
		String sql = String.format(
				"select count(*) from assay_result_value where text_value is not null and length(text_value)>0 and assay_attribute_id = %s",
				testAttribute.getId());
		return getJdbcTemplate().queryForObject(sql, BeanPropertyRowMapper.newInstance(Integer.class));
	}

	@Override
	public List<AssayResult> getResultsByAttributeAndValue(String value, Integer assayAttributeId) {
		List<AssayResult> result = new ArrayList<AssayResult>();
		String sql = String.format(
				"select * from assay_result_value where text_value = %s and assay_attribute_id = %s",
				value, assayAttributeId);
		List<AssayResultValue> assayResultVals = (List<AssayResultValue>) super.getObjectList(TABLE_NAME, AssayResultValue.class, sql);
		for (AssayResultValue arv : assayResultVals) {
			result.add(assayResultService.getAssayResultById(arv.getAssayAttributeId()));
		}
		return result;
	}
	
}
