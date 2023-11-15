package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.GroupPatternDao;
import com.idorsia.research.spirit.core.model.GroupPattern;

@Repository
public class GroupPatternDaoImpl extends AbstractDao<GroupPattern> implements GroupPatternDao {

	private static final String TABLE_NAME = "group_pattern";
	
	@Override
	public GroupPattern get(Integer id) {
		return super.get(TABLE_NAME, GroupPattern.class, id);
	}
	
	@Override
	public GroupPattern getByActionPatternAndGroup(int actionPatternId, int groupId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ACTIONPATTERN_ID = " + actionPatternId + " AND GROUP_ID = " + groupId;
		return super.getObject(TABLE_NAME, GroupPattern.class, sql);
	}
	
	@Override
	public List<GroupPattern> getByGroup(int groupId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE GROUP_ID = " + groupId;
		return (List<GroupPattern>) super.getObjectList(TABLE_NAME, GroupPattern.class, sql);
	}

	@Override
	public List<GroupPattern> list() {
		return super.getlist(TABLE_NAME, GroupPattern.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(GroupPattern groupPattern) {
		if (groupPattern.getId() != null && groupPattern.getId() > 0) {
			if(!groupPattern.equals(get(groupPattern.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "ACTIONPATTERN_ID=:actionPatternId, "
						+ "GROUP_ID=:groupId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", groupPattern.getId())
			    		.addValue("actionPatternId", groupPattern.getActionpatternId())
			    		.addValue("groupId", groupPattern.getGroupId())
			    		.addValue("creDate", groupPattern.getCreDate())
			    		.addValue("creUser", groupPattern.getCreUser())
			    		.addValue("updDate", groupPattern.getUpdDate())
			    		.addValue("updUser", groupPattern.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			groupPattern.setId(getSequence(Constants.GROUP_PATTERN_SEQUENCE_NAME));
			addGroupPattern(groupPattern);
			addTransient(groupPattern);
		}
		addIdToObject(GroupPattern.class, groupPattern.getId(), groupPattern);
		return groupPattern.getId();
	}

	@Override
	public int addGroupPattern(GroupPattern groupPattern) {
		return super.add(TABLE_NAME, groupPattern);
	}

	@Override
	public void delete(int groupPatternId) {
		super.delete(TABLE_NAME, groupPatternId);
	}
	
	@Override
	public void deleteByGroup(Integer groupId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where group_id=%s", groupId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByActionPattern(Integer actionPatternId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where ACTIONPATTERN_ID=%s", actionPatternId);
		getJdbcTemplate().update(sql);
	}
}
