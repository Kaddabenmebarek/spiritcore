package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssayAttributeDao;
import com.idorsia.research.spirit.core.model.AssayAttribute;

@Repository
public class AssayAttributeDaoImpl extends AbstractDao<AssayAttribute> implements AssayAttributeDao{
	
	private static final String TABLE_NAME = "ASSAY_ATTRIBUTE";

	@Override
	public AssayAttribute get(Integer id) {
		return super.get("Assay_Attribute", AssayAttribute.class, id);
	}

	@Override
	public AssayAttribute getAssayAttributeById(int id) {
		String sql = String.format("SELECT * FROM ASSAY_ATTRIBUTE WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, AssayAttribute.class, sql);
	}
	
	@Override
	public List<AssayAttribute> getAssayAttributesByName(String assayAttributeName) {
		String sql = String.format("SELECT * FROM ASSAY_ATTRIBUTE WHERE NAME = '%s'", assayAttributeName);
		return (List<AssayAttribute>) super.getObjectList(TABLE_NAME, AssayAttribute.class, sql);
	}
	
	@Override
	public List<AssayAttribute> getAssayAttributesByAssay(Integer assayId) {
		String sql = String.format("SELECT * FROM ASSAY_ATTRIBUTE WHERE ASSAY_ID = '%s'", assayId);
		return (List<AssayAttribute>) super.getObjectList(TABLE_NAME, AssayAttribute.class, sql);
	}
	
	@Override
	public List<AssayAttribute> getAssayAttributesByStudy(Integer studyId) {
		String sql = String.format("SELECT at.* FROM ASSAY_ATTRIBUTE at, ASSAY a, ASSAY_RESULT ar WHERE "
				+ "at.ASSAY_ID=a.ID and ar.ASSAY_ID=a.ID and ar.STUDY_ID= '%s'", studyId);
		return (List<AssayAttribute>) super.getObjectList(TABLE_NAME, AssayAttribute.class, sql);
	}
	
	@Override
	public AssayAttribute getAssayAttributeByAssayAndName(Integer assayId, String name) {
		String sql = String.format("SELECT * FROM ASSAY_ATTRIBUTE WHERE ASSAY_ID= %s AND NAME = '%s'", assayId, name);
		return super.getObject(TABLE_NAME, AssayAttribute.class, sql);
	}
	
	@Override
	public List<String> getRegisteredValues(Integer attributeId){
		String sql = String.format("SELECT text_value FROM assay_result_value WHERE assay_attribute_id = %s AND text_value IS NOT NULL group by text_value fetch first 500 rows only", attributeId);
		return getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(String.class));
	}
	
	@Override
	public List<String> getInputFields(Integer id, String studyIds) {
		StringBuilder studyIdquery=new StringBuilder();
		boolean first=true;
		for(String s : studyIds.split(",")) {
			if(first)
				first=false;
			else
				studyIdquery.append(",");
			studyIdquery.append("'"+s+"'");
		}
		String sql = String.format("SELECT distinct(rv.text_value) FROM assay_result_value rv, assay_result r, biosample b, study s" +
				" where rv.assay_result_id=r.id and r.biosample_id=b.id and b.study_id=s.id and s.studyId in (%s) and rv.assay_attribute_id=%s", studyIdquery, id);
		return getJdbcTemplate().query(sql, new ResultSetExtractor<List<String>>() {
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> s = new ArrayList<String>();
				while (rs.next()) {
					s.add(rs.getString(1));
				}
				return s;
			}
		});
	}
	
	@Override
	public List<AssayAttribute> list() {
		return super.getlist("Assay_Attribute", AssayAttribute.class);
	}

	@Override
	public int getCount() {
		return super.getCount("Assay_Attribute");
	}

	@Override
	public int addAssayAttribute(AssayAttribute assayAttribute) {
		return super.add("Assay_Attribute", assayAttribute);
	}
	
	@Override
	public void delete(int assayAttributeId) {
		super.delete("Assay_Attribute", assayAttributeId);
	}

	@Override
	public Integer saveOrUpdate(AssayAttribute assayAttribute) {
		if (assayAttribute.getId() != null && assayAttribute.getId() > 0) {
			if(!assayAttribute.equals(get(assayAttribute.getId()))) {
				String sql = "UPDATE ASSAY_ATTRIBUTE SET "
						+ "datatype=:datatype, " 
						+ "idx=:idx, " 
						+ "output_type=:output, " 
						+ "required=:required, " 
						+ "name=:name, "
						+ "unit=:unit, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser, "
						+ "assay_id=:assay_id, "
						+ "parameters=:parameters " 
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("datatype", assayAttribute.getDatatype())
			    		.addValue("idx", assayAttribute.getIdx())
			    		.addValue("output", assayAttribute.getOutputType())
			    		.addValue("required", assayAttribute.getRequired())
			    		.addValue("name", assayAttribute.getName())
			    		.addValue("unit", assayAttribute.getUnit())
			    		.addValue("creDate", assayAttribute.getCreDate())
			    		.addValue("creUser", assayAttribute.getCreUser())
			    		.addValue("updDate", assayAttribute.getUpdDate())
			    		.addValue("updUser", assayAttribute.getUpdUser())
			    		.addValue("assay_id", assayAttribute.getAssayId())
			    		.addValue("parameters", assayAttribute.getParameters())
			    		.addValue("id", assayAttribute.getId());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assayAttribute.setId(getSequence(Constants.ASSAY_ATTRIBUTE_SEQUENCE_NAME));
			addAssayAttribute(assayAttribute);
			addTransient(assayAttribute);
		}
		addIdToObject(AssayAttribute.class, assayAttribute.getId(), assayAttribute);
		return assayAttribute.getId();
	}
	
	public AssayAttribute rowMap(ResultSet rs) {
		AssayAttribute assayAttribute = null;
    	try {
    		assayAttribute = new AssayAttribute();
			assayAttribute.setId(rs.getInt("id"));
			assayAttribute.setDatatype(rs.getString("datatype"));
			assayAttribute.setIdx(rs.getInt("idx"));
			assayAttribute.setOutputType(rs.getString("output_type"));
			assayAttribute.setRequired(rs.getInt("required"));
			assayAttribute.setName(rs.getString("name"));
			assayAttribute.setUnit(rs.getString("unit"));
			assayAttribute.setAssayId(rs.getInt("assay_id"));
			assayAttribute.setParameters(rs.getString("parameters"));
			assayAttribute.setUpdDate(rs.getDate("UPDDATE"));
			assayAttribute.setUpdUser(rs.getString("UPDUSER"));
			assayAttribute.setCreDate(rs.getDate("CREDATE"));
			assayAttribute.setCreUser(rs.getString("CREUSER"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return assayAttribute;
	}
}
