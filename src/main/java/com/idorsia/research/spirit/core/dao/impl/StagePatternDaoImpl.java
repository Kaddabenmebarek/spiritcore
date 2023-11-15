package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.StagePatternDao;
import com.idorsia.research.spirit.core.model.StagePattern;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class StagePatternDaoImpl extends AbstractDao<StagePattern> implements StagePatternDao {
	
	private static final String TABLE_NAME = "STAGE_PATTERN";

	@Override
	public StagePattern get(Integer id) {
		return super.get(TABLE_NAME, StagePattern.class, id);
	}

	@Override
	public StagePattern getByActionPattern(int actionPatternId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ACTIONPATTERN_ID = '%s'",
				actionPatternId);
		return super.getObject(TABLE_NAME, StagePattern.class, sql);
	}

	@Override
	public List<StagePattern> getByStage(int stageId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID = '%s'",
				stageId);
		return (List<StagePattern>) super.getObjectList(TABLE_NAME, StagePattern.class, sql);
	}

	@Override
	public List<StagePattern> list() {
		return super.getlist(TABLE_NAME, StagePattern.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(StagePattern stagePattern) {
		if (stagePattern.getId() != null && stagePattern.getId() > 0) {
			if(!stagePattern.equals(get(stagePattern.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "ACTIONPATTERN_ID=:actionPatternId, "
						+ "STAGE_ID=:stageId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", stagePattern.getId())
			    		.addValue("actionPatternId", stagePattern.getActionpatternId())
			    		.addValue("stageId", stagePattern.getStageId())
			    		.addValue("creDate", stagePattern.getCreDate())
			    		.addValue("creUser", stagePattern.getCreUser())
			    		.addValue("updDate", stagePattern.getUpdDate())
			    		.addValue("updUser", stagePattern.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			stagePattern.setId(getSequence(Constants.STAGE_PATTERN_SEQUENCE_NAME));
			addStagePattern(stagePattern);
			addTransient(stagePattern);
		}
		addIdToObject(StagePattern.class, stagePattern.getId(), stagePattern);
		return stagePattern.getId();
	}

	@Override
	public int addStagePattern(StagePattern stagePattern) {
		return super.add(TABLE_NAME, stagePattern);
	}

	@Override
	public void delete(int stagePatternId) {
		super.delete(TABLE_NAME, stagePatternId);
	}
	
	@Override
	public void deleteByStage(int stageId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where stage_id=%s", stageId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByActionPattern(int actionPatternId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where ACTIONPATTERN_ID=%s", actionPatternId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<Integer> getByStages(List<Integer> stageIds) {
		List<Integer> actionPatterIds = new ArrayList<Integer>();
		StringBuilder sql = new StringBuilder("select * from ").append(TABLE_NAME).append(" where stage_id ");
		sql.append(DataUtils.fetchInInt(stageIds));
		List<StagePattern> results = (List<StagePattern>) super.getObjectList(TABLE_NAME, StagePattern.class,
				sql.toString());
		for (StagePattern sp : results) {
			actionPatterIds.add(sp.getId());
		}
		return actionPatterIds;
	}

}
