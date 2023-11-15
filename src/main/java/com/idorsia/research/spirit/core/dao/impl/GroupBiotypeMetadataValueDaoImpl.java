package com.idorsia.research.spirit.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.GroupBiotypeMetadataValueDao;
import com.idorsia.research.spirit.core.model.GroupBiotypeMetadataValue;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class GroupBiotypeMetadataValueDaoImpl extends AbstractDao<GroupBiotypeMetadataValue> implements GroupBiotypeMetadataValueDao {
	
	private static final String TABLE_NAME = "GROUP_BIOTYPE_METADATAVALUE";

	@Override
	public GroupBiotypeMetadataValue get(Integer id) {
		String sql = String.format("SELECT * FROM %s WHERE ID = %s", TABLE_NAME, id);
		return super.getObject(TABLE_NAME, GroupBiotypeMetadataValue.class, sql);
	}
	
	@Override
	public List<GroupBiotypeMetadataValue> getByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOTYPE_METADATA_VALUE_ID = '%s'",
				biotypeMetadataValueId);
		return (List<GroupBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, GroupBiotypeMetadataValue.class, sql);
	}

	@Override
	public List<GroupBiotypeMetadataValue> getByGroup(int groupId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE GROUP_ID = '%s'",
				groupId);
		return (List<GroupBiotypeMetadataValue>) super.getObjectList(TABLE_NAME, GroupBiotypeMetadataValue.class, sql);
	}

	@Override
	public GroupBiotypeMetadataValue getByGroupAndBiotypeMetadataValue(Integer groupId,
			Integer biotypeMetadataValueId) {
			String sql = String.format(
					"SELECT * FROM " + TABLE_NAME + " WHERE GROUP_ID=%s AND BIOTYPE_METADATA_VALUE_ID = '%s'", groupId,
					biotypeMetadataValueId);
			return super.getObject(TABLE_NAME, GroupBiotypeMetadataValue.class, sql);
	}
	
	@Override
	public void addGroupBiotypeMetadataValue(GroupBiotypeMetadataValue groupBiotypeMetadataValue) {
		super.add(TABLE_NAME, groupBiotypeMetadataValue);
	}

	@Override
	public void deleteByGroup(int groupId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where GROUP_ID=%s", groupId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByBiotypeMetadataValue(int biotypeMetadataValueId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where BIOTYPE_METADATA_VALUE_ID=%s", biotypeMetadataValueId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<GroupBiotypeMetadataValue> list() {
		return super.getlist(TABLE_NAME, GroupBiotypeMetadataValue.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public void delete(Integer groupBiotypeMetadataValue) {
		String sql = String.format("DELETE FROM %s WHERE ID = %s", TABLE_NAME, groupBiotypeMetadataValue);
		getJdbcTemplate().execute(sql);
	}
	
	@Override
	public Integer saveOrUpdate(GroupBiotypeMetadataValue groupBiotypeMetadataValue) {
		groupBiotypeMetadataValue.setUpdDate(new Date());
		groupBiotypeMetadataValue.setUpdUser(UserUtil.getUsername());
		if (groupBiotypeMetadataValue.getId() != null && groupBiotypeMetadataValue.getId() > 0) {
			if(!groupBiotypeMetadataValue.equals(get(groupBiotypeMetadataValue.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "BIOTYPE_METADATA_VALUE_ID=:biotypeMetadatValue, "
						+ "GROUP_ID=:groupId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", groupBiotypeMetadataValue.getId())
			    		.addValue("biotypeMetadatValue", groupBiotypeMetadataValue.getBiotypeMetadataValueId())
			    		.addValue("groupId", groupBiotypeMetadataValue.getGroupId())
			    		.addValue("creDate", groupBiotypeMetadataValue.getCreDate())
			    		.addValue("creUser", groupBiotypeMetadataValue.getCreUser())
			    		.addValue("updDate", groupBiotypeMetadataValue.getUpdDate())
			    		.addValue("updUser", groupBiotypeMetadataValue.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			groupBiotypeMetadataValue.setCreDate(new Date());
			groupBiotypeMetadataValue.setCreUser(UserUtil.getUsername());
			groupBiotypeMetadataValue.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addGroupBiotypeMetadataValue(groupBiotypeMetadataValue);
			addTransient(groupBiotypeMetadataValue);
		}
		addIdToObject(GroupBiotypeMetadataValue.class, groupBiotypeMetadataValue.getId(), groupBiotypeMetadataValue);
		return groupBiotypeMetadataValue.getId();
	}
}
