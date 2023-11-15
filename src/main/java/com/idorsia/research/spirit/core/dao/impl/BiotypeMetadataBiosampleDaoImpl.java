package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataBiosampleDao;
import com.idorsia.research.spirit.core.model.BiotypeMetadataBiosample;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class BiotypeMetadataBiosampleDaoImpl extends AbstractDao<BiotypeMetadataBiosample> implements BiotypeMetadataBiosampleDao {

	private static final String TABLE_NAME = "Biotype_Metadata_Biosample";
	@Override
	public BiotypeMetadataBiosample get(Integer id) {
		return super.get(TABLE_NAME, BiotypeMetadataBiosample.class, id);
	}

	@Override
	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleByBiosampleAndMetadata(Integer biosampleId,
			Integer biotypeMetadataId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID = %s AND METADATA_ID = %s",
				biosampleId, biotypeMetadataId);
		return super.getObject(TABLE_NAME, BiotypeMetadataBiosample.class, sql);
	}
	
	@Override
	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleById(int id) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ID = %s",id);
		return super.getObject(TABLE_NAME, BiotypeMetadataBiosample.class, sql);
	}
	
	@Override
	public List<BiotypeMetadataBiosample> getMetadataByBiosamples(List<Integer> ids){
		String sql = String.format("SELECT * FROM "+ TABLE_NAME + " WHERE BIOSAMPLE_ID " + DataUtils.fetchInInt(new ArrayList<Integer>(ids)));
		return (List<BiotypeMetadataBiosample>) super.getObjectList(TABLE_NAME, BiotypeMetadataBiosample.class, sql);
	}

	@Override
	public List<BiotypeMetadataBiosample> getBiotypeMetadataBiosamplesByBiosample(Integer biosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID = '%s'",
				biosampleId);
		return (List<BiotypeMetadataBiosample>) super.getObjectList(TABLE_NAME, BiotypeMetadataBiosample.class, sql);
	}
	
	@Override
	public Integer saveOrUpdate(BiotypeMetadataBiosample biotypeMetadataBiosample) {
		if (biotypeMetadataBiosample.getId() != null && biotypeMetadataBiosample.getId() > 0) {
			if(!biotypeMetadataBiosample.equals(get(biotypeMetadataBiosample.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET biosample_id=:biosample_id, " 
						+ "metadata_id=:metadata_id, " 
						+ "CREDATE=:credate, "
						+ "CREUSER=:creuser, "
						+ "UPDDATE=:upddate, "
						+ "UPDUSER=:upduser, "
						+ "value=:value " 
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", biotypeMetadataBiosample.getId())
			    		.addValue("biosample_id", biotypeMetadataBiosample.getBiosampleId())
			    		.addValue("metadata_id", biotypeMetadataBiosample.getMetadataId())
						.addValue("upddate", biotypeMetadataBiosample.getUpdDate())
						.addValue("upduser", biotypeMetadataBiosample.getUpdUser())
						.addValue("credate", biotypeMetadataBiosample.getCreDate())
						.addValue("creuser", biotypeMetadataBiosample.getCreUser())
			    		.addValue("value", biotypeMetadataBiosample.getValue());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			biotypeMetadataBiosample.setId(getSequence(Constants.BIOTYPE_METADATA_BIOSAMPLE_SEQUENCE_NAME));
			addBiotypeMetadataBiosample(biotypeMetadataBiosample);
			addTransient(biotypeMetadataBiosample);
		}
		addIdToObject(BiotypeMetadataBiosample.class, biotypeMetadataBiosample.getId(), biotypeMetadataBiosample);
		return biotypeMetadataBiosample.getId();
	}
	
	@Override
	public int addBiotypeMetadataBiosample(BiotypeMetadataBiosample biotypeMetadataBiosample) {
		return super.add(TABLE_NAME, biotypeMetadataBiosample);
	}
	
	public BiotypeMetadataBiosample rowMap(ResultSet rs) {
		BiotypeMetadataBiosample biotypeMetadataBiosample = null;
    	try {
    		biotypeMetadataBiosample = new BiotypeMetadataBiosample();
    		biotypeMetadataBiosample.setId(rs.getInt("id"));
    		biotypeMetadataBiosample.setBiosampleId(rs.getInt("biosample_id"));
    		biotypeMetadataBiosample.setMetadataId(rs.getInt("metadata_id"));
    		biotypeMetadataBiosample.setValue(rs.getString("value"));
    		biotypeMetadataBiosample.setUpdDate(rs.getDate("UPDDATE"));
    		biotypeMetadataBiosample.setUpdUser(rs.getString("UPDUSER"));
    		biotypeMetadataBiosample.setCreDate(rs.getDate("CREDATE"));
    		biotypeMetadataBiosample.setCreUser(rs.getString("CREUSER"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return biotypeMetadataBiosample;
	}
	
	@Override
	public void delete(int id) {
		super.delete(TABLE_NAME, id);
	}


}
