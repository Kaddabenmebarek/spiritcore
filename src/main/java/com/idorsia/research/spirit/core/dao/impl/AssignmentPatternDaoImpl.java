package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssignmentPatternDao;
import com.idorsia.research.spirit.core.model.AssignmentPattern;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class AssignmentPatternDaoImpl extends AbstractDao<AssignmentPattern> implements AssignmentPatternDao {
	
	private static final String TABLE_NAME = "ASSIGNMENT_PATTERN";

	@Override
	public AssignmentPattern get(Integer id) {
		return super.get(TABLE_NAME, AssignmentPattern.class, id);
	}

	@Override
	public List<AssignmentPattern> getByAssignment(int assignmentId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ASSIGNMENT_ID = '%s'",
				assignmentId);
		return (List<AssignmentPattern>) super.getObjectList(TABLE_NAME, AssignmentPattern.class, sql);
	}

	@Override
	public List<AssignmentPattern> list() {
		return super.getlist(TABLE_NAME, AssignmentPattern.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(AssignmentPattern assignmentPattern) {
		if (assignmentPattern.getId() != null && assignmentPattern.getId() > 0) {
			if(!assignmentPattern.equals(get(assignmentPattern.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "PATTERN_ID=:patternId, "
						+ "ASSIGNMENT_ID=:assignmentId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", assignmentPattern.getId())
			    		.addValue("patternId", assignmentPattern.getPatternId())
			    		.addValue("assignmentId", assignmentPattern.getAssignmentId())
			    		.addValue("creDate", assignmentPattern.getCreDate())
			    		.addValue("creUser", assignmentPattern.getCreUser())
			    		.addValue("updDate", assignmentPattern.getUpdDate())
			    		.addValue("updUser", assignmentPattern.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assignmentPattern.setId(getSequence(Constants.ASSIGNMENT_PATTERN_SEQUENCE_NAME));
			addAssignmentPattern(assignmentPattern);
			addTransient(assignmentPattern);
		}
		addIdToObject(AssignmentPattern.class, assignmentPattern.getId(), assignmentPattern);
		return assignmentPattern.getId();
	}

	@Override
	public int addAssignmentPattern(AssignmentPattern assignmentPattern) {
		return super.add(TABLE_NAME, assignmentPattern);
	}

	@Override
	public void delete(int assignmentPatternId) {
		super.delete(TABLE_NAME, assignmentPatternId);
	}
	
	@Override
	public void deleteByAssignment(int assignmentId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where assignment_id=%s", assignmentId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public void deleteByActionPattern(int actionPatternId) {
		String sql = String.format("DELETE from " + TABLE_NAME + " where PATTERN_ID=%s", actionPatternId);
		getJdbcTemplate().update(sql);
	}

	@Override
	public List<Integer> getByAssignments(List<Integer> assignmentIds) {
		List<Integer> actionPatterIds = new ArrayList<Integer>();
		StringBuilder sql = new StringBuilder(
				"select * from ").append(TABLE_NAME).append(" where assignment_id ");
		sql.append(DataUtils.fetchInInt(assignmentIds));
		List<AssignmentPattern> results = (List<AssignmentPattern>) super.getObjectList(TABLE_NAME,
				AssignmentPattern.class, sql.toString());
		for (AssignmentPattern sp : results) {
			actionPatterIds.add(sp.getPatternId());
		}
		return actionPatterIds;
	}

}
