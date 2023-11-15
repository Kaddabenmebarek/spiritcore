package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.model.BiotypeMetadataValue;

@Repository
public class BiotypeMetadataValueDaoImpl extends AbstractDao<BiotypeMetadataValue> implements BiotypeMetadataValueDao{
	
	private static final String TABLE_NAME = "Biotype_MetadataValue";
	
	@Override
	public BiotypeMetadataValue get(Integer id) {
		return super.get(TABLE_NAME, BiotypeMetadataValue.class, id);
	}
	
	@Override
	public BiotypeMetadataValue getById(int id) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ID='%s'", id);
		return super.getObject(TABLE_NAME, BiotypeMetadataValue.class, sql);
	}
	
	public List<BiotypeMetadataValue> getByGroup(Integer groupId){
		String sql = String.format("SELECT bmv.* FROM " + TABLE_NAME + " bmv, GROUP_BIOTYPE_METADATAVALUE gbmv WHERE gbmv.BIOTYPE_METADATA_VALUE_ID=bmv.id and gbmv.GROUP_ID='%s'", groupId);
		return (List<BiotypeMetadataValue>) super.getObjectList(TABLE_NAME, BiotypeMetadataValue.class, sql);
	}

	public List<BiotypeMetadataValue> getBySubGroup(Integer subGroupId){
		String sql = String.format("SELECT bmv.* FROM " + TABLE_NAME + " bmv, SUBGROUP_BIOTYPE_METADATAVALUE sbmv WHERE sbmv.BIOTYPE_METADATA_VALUE_ID=bmv.id and sbmv.SUBGROUP_ID='%s'", subGroupId);
		return (List<BiotypeMetadataValue>) super.getObjectList(TABLE_NAME, BiotypeMetadataValue.class, sql);
	}

	public List<BiotypeMetadataValue> getByStage(Integer stageId){
		String sql = String.format("SELECT bmv.* FROM " + TABLE_NAME + " bmv, STAGE_BIOTYPE_METADATAVALUE sbmv WHERE sbmv.BIOTYPE_METADATA_VALUE_ID=bmv.id and sbmv.STAGE_ID='%s'", stageId);
		return (List<BiotypeMetadataValue>) super.getObjectList(TABLE_NAME, BiotypeMetadataValue.class, sql);
	}
	
	protected BiotypeMetadataValue rowMap(ResultSet rs) {
		BiotypeMetadataValue biotypeMetadataValue = null;
    	try {
    		biotypeMetadataValue = new BiotypeMetadataValue();
    		biotypeMetadataValue.setId(rs.getInt("id"));
    		biotypeMetadataValue.setBiotypemetadataId(rs.getInt("biotypemetadata_id"));
    		biotypeMetadataValue.setValue(rs.getString("value"));
    		biotypeMetadataValue.setCredate(rs.getDate("credate"));
    		biotypeMetadataValue.setUpddate(rs.getDate("upddate"));
    		biotypeMetadataValue.setCreuser(rs.getString("creuser"));
    		biotypeMetadataValue.setUpduser(rs.getString("upduser"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return biotypeMetadataValue;
	}

	@Override
	public List<BiotypeMetadataValue> getByBiotypeMetadata(Integer biotypeMetadataId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPEMETADATA_ID='%s'", biotypeMetadataId);
		return (List<BiotypeMetadataValue>) super.getObjectList(TABLE_NAME, BiotypeMetadataValue.class, sql);
	}

	@Override
	public List<BiotypeMetadataValue> list() {
		return super.getlist(TABLE_NAME, BiotypeMetadataValue.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(BiotypeMetadataValue biotypeMetadataValue) {
		if (biotypeMetadataValue.getId() != null && biotypeMetadataValue.getId() > 0) {
			if(!biotypeMetadataValue.equals(get(biotypeMetadataValue.getId()))) {
				String sql = "UPDATE "+TABLE_NAME + " SET "
						+ "BIOTYPEMETADATA_ID=:BIOTYPEMETADATA_ID, " 
						+ "VALUE=:VALUE, "
						+ "CREUSER=:CREUSER, "
						+ "UPDUSER=:UPDUSER, "
						+ "CREDATE=:CREDATE, "
						+ "UPDDATE=:UPDDATE "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", biotypeMetadataValue.getId())
			    		.addValue("BIOTYPEMETADATA_ID", biotypeMetadataValue.getBiotypemetadataId())
			    		.addValue("VALUE", biotypeMetadataValue.getValue())
			    		.addValue("UPDDATE", biotypeMetadataValue.getUpddate())
			    		.addValue("CREUSER", biotypeMetadataValue.getCreuser())
			    		.addValue("UPDUSER", biotypeMetadataValue.getUpduser())
			    		.addValue("CREDATE", biotypeMetadataValue.getCredate());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			biotypeMetadataValue.setId(getSequence(Constants.BIOTYPE_METADATA_VALUE_SEQUENCE_NAME));
			addBiotypeMetadataValue(biotypeMetadataValue);
			addTransient(biotypeMetadataValue);
		}
		addIdToObject(BiotypeMetadataValue.class, biotypeMetadataValue.getId(), biotypeMetadataValue);
		return biotypeMetadataValue.getId();
	}

	@Override
	public int addBiotypeMetadataValue(BiotypeMetadataValue biotypeMetadataValue) {
		return super.add(TABLE_NAME, biotypeMetadataValue);
	}

	@Override
	public void delete(int biotypeMetadataValueId) {
		super.delete(TABLE_NAME, biotypeMetadataValueId);
	}
}
