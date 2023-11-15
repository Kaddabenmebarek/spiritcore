package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SubGroupPatternDao;
import com.idorsia.research.spirit.core.model.SubGroupPattern;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class SubGroupPatternDaoImpl extends AbstractDao<SubGroupPattern> implements SubGroupPatternDao {

	private static final String TABLE_NAME = "SUBGROUP_PATTERN";

	@Override
	public SubGroupPattern get(Integer id) {
		return super.get(TABLE_NAME, SubGroupPattern.class, id);
	}

	@Override
	public SubGroupPattern getByAcionPattern(int actionPatternId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ACTIONPATTERN_ID = '%s'", actionPatternId);
		return super.getObject(TABLE_NAME, SubGroupPattern.class, sql);
	}

	@Override
	public SubGroupPattern getByActionPatternAndSubGroup(int actionPatternId, int subgroupId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ACTIONPATTERN_ID = " + actionPatternId
				+ " AND SUBGROUP_ID = " + subgroupId;
		return super.getObject(TABLE_NAME, SubGroupPattern.class, sql);
	}

	@Override
	public List<SubGroupPattern> list() {
		return super.getlist(TABLE_NAME, SubGroupPattern.class);
	}

	@Override
	public List<SubGroupPattern> getBySubgroup(int subgroupId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SUBGROUP_ID = '%s'", subgroupId);
		return (List<SubGroupPattern>) super.getObjectList(TABLE_NAME, SubGroupPattern.class, sql);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(SubGroupPattern subgroupPattern) {
		if (subgroupPattern.getId() != null && subgroupPattern.getId() > 0) {
			if(!subgroupPattern.equals(get(subgroupPattern.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "ACTIONPATTERN_ID=:actionPatternId, "
						+ "SUBGROUP_ID=:subgroupId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", subgroupPattern.getId())
			    		.addValue("actionPatternId", subgroupPattern.getActionpatternId())
			    		.addValue("subgroupId", subgroupPattern.getSubgroupId())
			    		.addValue("creDate", subgroupPattern.getCreDate())
			    		.addValue("creUser", subgroupPattern.getCreUser())
			    		.addValue("updDate", subgroupPattern.getUpdDate())
			    		.addValue("updUser", subgroupPattern.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			subgroupPattern.setId(getSequence(Constants.SUBGROUP_PATTERN_SEQUENCE_NAME));
			addSubGroupPattern(subgroupPattern);
			addTransient(subgroupPattern);
		}
		addIdToObject(SubGroupPattern.class, subgroupPattern.getId(), subgroupPattern);
		return subgroupPattern.getId();
	}

	@Override
	public int addSubGroupPattern(SubGroupPattern subgroupPattern) {
		return super.add(TABLE_NAME, subgroupPattern);
	}

	@Override
	public void delete(int groupPatternId) {
		super.delete(TABLE_NAME, groupPatternId);
	}
	
	@Override
	public void deleteBySubGroup(Integer groupId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where subgroup_id=%s", groupId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByActionPattern(Integer actionPatternId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where ACTIONPATTERN_ID=%s", actionPatternId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<Integer> getBySubGoups(List<Integer> subGoupIds) {
		List<Integer> actionPatterIds = new ArrayList<Integer>();
		StringBuilder sql = new StringBuilder("select actionpattern_id from subgroup_pattern where subgroup_id ");
		sql.append(DataUtils.fetchInInt(subGoupIds));
		List<SubGroupPattern> results = (List<SubGroupPattern>) super.getObjectList(TABLE_NAME, SubGroupPattern.class, sql.toString());
		for(SubGroupPattern sgp : results) {
			actionPatterIds.add(sgp.getActionpatternId());
		}
		return actionPatterIds;
	}

}
