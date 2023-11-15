package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.StageBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.model.StageBiotypeMetadataValue;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class StageBiotypeMetadataValueDaoImpl extends AbstractDao<StageBiotypeMetadataValue>
		implements StageBiotypeMetadataValueDao {

	private static final String TABLE_NAME = "STAGE_BIOTYPE_METADATAVALUE";

	@Override
	public StageBiotypeMetadataValue get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, StageBiotypeMetadataValue.class, sql);
	}

	@Override
	public List<StageBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPE_METADATA_VALUE_ID = '%s'",
				biotypeMetadataValueId);
		return (List<StageBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, StageBiotypeMetadataValue.class, sql);
	}

	@Override
	public StageBiotypeMetadataValue getByStageAndBiotypeMetadataValue(Integer stageId,
			Integer biotypeMetadataValueId) {
		String sql = String.format(
				"SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID=%s AND BIOTYPE_METADATA_VALUE_ID = '%s'", stageId,
				biotypeMetadataValueId);
		return super.getObject(TABLE_NAME, StageBiotypeMetadataValue.class, sql);
	}

	@Override
	public List<StageBiotypeMetadataValue> getByStage(int stageId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID = '%s'", stageId);
		return (List<StageBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, StageBiotypeMetadataValue.class, sql);
	}

	@Override
	public void addStageBiotypeMetadataValue(StageBiotypeMetadataValue stageBiotypeMetadataValue) {
		super.add(TABLE_NAME, stageBiotypeMetadataValue);
	}

	@Override
	public void deleteByStage(int stageId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where STAGE_ID=%s", stageId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where BIOTYPE_METADATA_VALUE_ID=%s",
				biotypeMetadataValueId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<StageBiotypeMetadataValue> list() {
		return super.getlist(TABLE_NAME, StageBiotypeMetadataValue.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public void delete(Integer stageBiotypeMetadataValue) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s", TABLE_NAME, stageBiotypeMetadataValue);
		getJdbcTemplate().execute(sql);
	}
	
	@Override
	public Integer saveOrUpdate(StageBiotypeMetadataValue stageBiotypeMetadataValue) {
		stageBiotypeMetadataValue.setUpdDate(new Date());
		stageBiotypeMetadataValue.setUpdUser(UserUtil.getUsername());
		if (stageBiotypeMetadataValue.getId() != null && stageBiotypeMetadataValue.getId() > 0) {
			if(!stageBiotypeMetadataValue.equals(get(stageBiotypeMetadataValue.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "BIOTYPE_METADATA_VALUE_ID=:biotypeMetadatValue, "
						+ "STAGE_ID=:stageId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", stageBiotypeMetadataValue.getId())
			    		.addValue("biotypeMetadatValue", stageBiotypeMetadataValue.getBiotypeMetadataValueId())
			    		.addValue("stageId", stageBiotypeMetadataValue.getStageId())
			    		.addValue("creDate", stageBiotypeMetadataValue.getCreDate())
			    		.addValue("creUser", stageBiotypeMetadataValue.getCreUser())
			    		.addValue("updDate", stageBiotypeMetadataValue.getUpdDate())
			    		.addValue("updUser", stageBiotypeMetadataValue.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			stageBiotypeMetadataValue.setCreDate(new Date());
			stageBiotypeMetadataValue.setCreUser(UserUtil.getUsername());
			stageBiotypeMetadataValue.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addStageBiotypeMetadataValue(stageBiotypeMetadataValue);
			addTransient(stageBiotypeMetadataValue);
		}
		addIdToObject(StageBiotypeMetadataValue.class, stageBiotypeMetadataValue.getId(), stageBiotypeMetadataValue);
		return stageBiotypeMetadataValue.getId();
	}
}
