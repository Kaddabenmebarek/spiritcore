package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SubGroupBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.model.SubGroupBiotypeMetadataValue;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class SubGroupBiotypeMetadataValueDaoImpl extends AbstractDao<SubGroupBiotypeMetadataValue>
		implements SubGroupBiotypeMetadataValueDao {

	private static final String TABLE_NAME = "SUBGROUP_BIOTYPE_METADATAVALUE";

	@Override
	public SubGroupBiotypeMetadataValue get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, SubGroupBiotypeMetadataValue.class, sql);
	}

	@Override
	public List<SubGroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPE_METADATA_VALUE_ID = '%s'",
				biotypeMetadataValueId);
		return (List<SubGroupBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, SubGroupBiotypeMetadataValue.class,
				sql);
	}

	@Override
	public List<SubGroupBiotypeMetadataValue> getBySubGroup(int subGroupId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SUBGROUP_ID = '%s'", subGroupId);
		return (List<SubGroupBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, SubGroupBiotypeMetadataValue.class,
				sql);
	}

	@Override
	public void addSubGroupBiotypeMetadataValue(SubGroupBiotypeMetadataValue subGroupBiotypeMetadataValue) {
		super.add(TABLE_NAME, subGroupBiotypeMetadataValue);
	}

	@Override
	public SubGroupBiotypeMetadataValue getBySubGroupAndBiotypeMetadataValue(Integer subGroupId,
			Integer biotypeMetadataValueId) {
		String sql = String.format(
				"SELECT * FROM " + TABLE_NAME + " WHERE SUBGROUP_ID=%s AND BIOTYPE_METADATA_VALUE_ID = '%s'", subGroupId,
				biotypeMetadataValueId);
		return super.getObject(TABLE_NAME, SubGroupBiotypeMetadataValue.class, sql);
	}

	@Override
	public void deleteBySubGroup(int subGroupId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where SUBGROUP_ID=%s", subGroupId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where BIOTYPE_METADATA_VALUE_ID=%s",
				biotypeMetadataValueId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<SubGroupBiotypeMetadataValue> list() {
		return super.getlist(TABLE_NAME, SubGroupBiotypeMetadataValue.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public void delete(Integer subGroupBiotypeMetadataValue) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s", TABLE_NAME, subGroupBiotypeMetadataValue);
		getJdbcTemplate().execute(sql);
	}
	
	@Override
	public Integer saveOrUpdate(SubGroupBiotypeMetadataValue subgroupBiotypeMetadataValue) {
		subgroupBiotypeMetadataValue.setUpdDate(new Date());
		subgroupBiotypeMetadataValue.setUpdUser(UserUtil.getUsername());
		if (subgroupBiotypeMetadataValue.getId() != null && subgroupBiotypeMetadataValue.getId() > 0) {
			if(!subgroupBiotypeMetadataValue.equals(get(subgroupBiotypeMetadataValue.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "BIOTYPE_METADATA_VALUE_ID=:biotypeMetadatValue, "
						+ "SUBGROUP_ID=:subgroupId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", subgroupBiotypeMetadataValue.getId())
			    		.addValue("biotypeMetadatValue", subgroupBiotypeMetadataValue.getBiotypeMetadataValueId())
			    		.addValue("stageId", subgroupBiotypeMetadataValue.getSubgroupId())
			    		.addValue("creDate", subgroupBiotypeMetadataValue.getCreDate())
			    		.addValue("creUser", subgroupBiotypeMetadataValue.getCreUser())
			    		.addValue("updDate", subgroupBiotypeMetadataValue.getUpdDate())
			    		.addValue("updUser", subgroupBiotypeMetadataValue.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			subgroupBiotypeMetadataValue.setCreDate(new Date());
			subgroupBiotypeMetadataValue.setCreUser(UserUtil.getUsername());
			subgroupBiotypeMetadataValue.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addSubGroupBiotypeMetadataValue(subgroupBiotypeMetadataValue);
			addTransient(subgroupBiotypeMetadataValue);
		}
		addIdToObject(SubGroupBiotypeMetadataValue.class, subgroupBiotypeMetadataValue.getId(), subgroupBiotypeMetadataValue);
		return subgroupBiotypeMetadataValue.getId();
	}
}
