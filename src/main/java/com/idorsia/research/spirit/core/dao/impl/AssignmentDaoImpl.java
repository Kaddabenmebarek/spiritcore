package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssignmentDao;
import com.idorsia.research.spirit.core.model.Assignment;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class AssignmentDaoImpl extends AbstractDao<Assignment> implements AssignmentDao {
	
	private static final String TABLE_NAME = "ASSIGNMENT";
	@Override
	public Assignment get(Integer id) {
		return super.get("Assignment", Assignment.class, id);
	}

	@Override
	public Assignment getAssignmentById(int id) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public Assignment getAssignmentByBiosampleAndStage(Integer biosampleId, Integer stageId) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE BIOSAMPLE_ID = %s AND STAGE_ID = %s", biosampleId, stageId);
		return super.getObject(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public Assignment getAssignmentByBiosampleAndSubgroup(Integer biosampleId, Integer subgoupId) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE BIOSAMPLE_ID = %s AND SUBGROUP_ID = %s", biosampleId, subgoupId);
		return super.getObject(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public List<Assignment> getAssignmentsByBiosample(Integer biosampleID) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE BIOSAMPLE_ID = %s", biosampleID);
		return (List<Assignment>) super.getObjectList(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public List<Assignment> getAssignmentsBySubgroup(Integer subgroupId) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE SUBGROUP_ID = %s", subgroupId);
		return (List<Assignment>) super.getObjectList(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public List<Assignment> getAssignmentsByStage(Integer stageId) {
		String sql = String.format("SELECT * FROM "+TABLE_NAME+" WHERE STAGE_ID = %s", stageId);
		return (List<Assignment>) super.getObjectList(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public List<Assignment> getAssignmentsByStudy(Integer studyId) {
		String sql = String.format("SELECT Assignment.* FROM "+TABLE_NAME+" assignment, Stage stage WHERE assignment.STAGE_ID = Stage.id and stage.study_id=%s", studyId);
		return (List<Assignment>) super.getObjectList(TABLE_NAME, Assignment.class, sql);
	}
	
	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addAssignment(Assignment assignment) {
		return super.add(TABLE_NAME, assignment);
	}
	
	@Override
	public void delete(int assignmentId) {
		super.delete(TABLE_NAME, assignmentId);
	}

	@Override
	public Integer saveOrUpdate(Assignment assignment) {
		if (assignment.getId() != null && assignment.getId() > 0) {
			if(!assignment.equals(get(assignment.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET no=:no, " 
						+ "stage_id=:stage_id, " 
						+ "biosample_id=:biosample_id, " 
						+ "subgroup_id=:subgroup_id, " 
						+ "name=:name, " 
						+ "elb=:elb, " 
						+ "datalist=:datalist, " 
						+ "stratification=:stratification, " 
						+ "upddate=:upddate, "
						+ "upduser=:upduser, "
						+ "credate=:credate, "
						+ "creuser=:creuser, "
						+ "remove_date=:remove_date " 
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", assignment.getId())
			    		.addValue("no", assignment.getNo())
			    		.addValue("stage_id", assignment.getStageId())
			    		.addValue("biosample_id", assignment.getBiosampleId())
			    		.addValue("subgroup_id", assignment.getSubgroupId())
			    		.addValue("name", assignment.getName())
			    		.addValue("elb", assignment.getElb())
			    		.addValue("datalist", assignment.getDatalist())
			    		.addValue("stratification", assignment.getStratification())
			    		.addValue("upddate", assignment.getUpdDate())
			    		.addValue("upduser", assignment.getUpdUser())
			    		.addValue("credate", assignment.getCreDate())
			    		.addValue("creuser", assignment.getCreUser())
			    		.addValue("remove_date", assignment.getRemoveDate());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assignment.setId(getSequence(Constants.ASSIGNMENT_SEQUENCE_NAME));
			addAssignment(assignment);
			addTransient(assignment);
		}
		addIdToObject(Assignment.class, assignment.getId(), assignment);
		return assignment.getId();
	}
	
	public Assignment rowMap(ResultSet rs) {
		Assignment assignment = null;
    	try {
    		assignment = new Assignment();
			assignment.setId(rs.getInt("id"));
			assignment.setNo(rs.getInt("no"));
			assignment.setStageId(rs.getInt("stage_id"));
			assignment.setBiosampleId(rs.getInt("biosample_id"));
			assignment.setSubgroupId(rs.getInt("subgroup_id"));
			assignment.setName(rs.getString("name"));
			assignment.setElb(rs.getString("elb"));
			assignment.setDatalist(rs.getString("datalist"));
			assignment.setStratification(rs.getLong("stratification"));
			assignment.setUpdDate(rs.getDate("upddate"));
			assignment.setUpdUser(rs.getString("upduser"));
			assignment.setCreDate(rs.getDate("credate"));
			assignment.setCreUser(rs.getString("creuser"));
			assignment.setRemoveDate(rs.getDate("remove_date"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return assignment;
	}

	@Override
	public List<Assignment> getAssignmentsByStages(List<Integer> stageIds) {
		StringBuilder sql = new StringBuilder("select * from " + TABLE_NAME + " where stage_id ");
		sql.append(DataUtils.fetchInInt(stageIds));
		return (List<Assignment>) super.getObjectList(TABLE_NAME, Assignment.class, sql.toString());
	}

	@Override
	public Map<Integer, Integer> getBiosampleSubgroupMap(List<Integer> stageIds) {
		Map<Integer, Integer> queryResults = new HashMap<Integer, Integer>();
		StringBuilder sql = new StringBuilder("select biosample_id, subgroup_id from " + TABLE_NAME + " where stage_id ");
		sql.append(DataUtils.fetchInInt(stageIds));
		getJdbcTemplate().query(sql.toString(), 
			      (rs, rowNum) -> queryResults.put(rs.getInt("biosample_id"), rs.getInt("subgroup_id")));
		return queryResults;
	}

}
