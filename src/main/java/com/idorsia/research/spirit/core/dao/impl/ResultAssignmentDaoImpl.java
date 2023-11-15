package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.ResultAssignmentDao;
import com.idorsia.research.spirit.core.model.ResultAssignment;

@Repository
public class ResultAssignmentDaoImpl extends AbstractDao<ResultAssignment> implements ResultAssignmentDao{

	private static final String TABLE_NAME = "RESULT_ASSIGNMENT";

	@Override
	public ResultAssignment get(Integer id) {
		return super.get(TABLE_NAME, ResultAssignment.class, id);
	}

	@Override
	public ResultAssignment getResultAssignmentById(int id) {
		String sql = String.format("SELECT * FROM RESULT_ASSIGNMENT WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, ResultAssignment.class, sql);
	}

	@Override
	public List<ResultAssignment> getResultAssignmentByAssignment(Integer assignmentId) {
		String sql = String.format("SELECT * FROM RESULT_ASSIGNMENT WHERE ASSIGNMENT_ID = %s", assignmentId);
		return (List<ResultAssignment>) super.getObjectList(TABLE_NAME, ResultAssignment.class, sql);
	}

	public List<ResultAssignment> getResultAssignmentByResult(Integer resultId){
		String sql = String.format("SELECT * FROM RESULT_ASSIGNMENT WHERE ASSAY_RESULT_ID = %s", resultId);
		return (List<ResultAssignment>) super.getObjectList(TABLE_NAME, ResultAssignment.class, sql);
	}

	
	@Override
	public ResultAssignment getResultAssignmentbyAssignmentAndAssay(Integer assignmentId, Integer assayId) {
		String sql = String.format("SELECT * FROM RESULT_ASSIGNMENT RA, ASSAY A, ASSAY_RESULT AR WHERE RA.ASSIGNMENT_ID =%s "
				+ "and RA.ASSAY_RESULT_ID=AR.ID and AR.ASSAY_ID=A.ID and A.ID = %s", assignmentId, assayId);
		return super.getObject(TABLE_NAME, ResultAssignment.class, sql);
	}
	
	@Override
	public int addResultAssignment(ResultAssignment resultAssignment) {
		return super.add(TABLE_NAME, resultAssignment);
	}
	
	@Override
	public void delete(int resultAssignmentId) {
		super.delete(TABLE_NAME, resultAssignmentId);
	}

	@Override
	public Integer saveOrUpdate(ResultAssignment resultAssignment) {
		if (resultAssignment.getId() != null && resultAssignment.getId() > 0) {
			if(!resultAssignment.equals(get(resultAssignment.getId()))) {
				String sql = "UPDATE RESULT_ASSIGNMENT SET assignment_id=:assignment_id, " 
						+ "assay_result_id=:assay_result_id, " 
						+ "upddate=:upddate, " 
						+ "upduser=:upduser, "
						+ "credate=:credate, "
						+ "creuser=:creuser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", resultAssignment.getId())
			    		.addValue("assignment_id", resultAssignment.getAssignmentId())
			    		.addValue("assay_result_id", resultAssignment.getAssayResultId())
			    		.addValue("upddate", resultAssignment.getUpdDate())
			    		.addValue("upduser", resultAssignment.getUpdUser())
			    		.addValue("credate", resultAssignment.getCreDate())
			    		.addValue("creuser", resultAssignment.getCreUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			resultAssignment.setId(getSequence(Constants.RESULT_ASSIGNMENT_SEQ_NAME));
			addResultAssignment(resultAssignment);
			addTransient(resultAssignment);
		}
		addIdToObject(ResultAssignment.class, resultAssignment.getId(), resultAssignment);
		return resultAssignment.getId();
	}
	
	public ResultAssignment rowMap(ResultSet rs) {
		ResultAssignment resultAssignment = null;
    	try {
    		resultAssignment = new ResultAssignment();
    		resultAssignment.setId(rs.getInt("id"));
    		resultAssignment.setAssignmentId(rs.getInt("assignment_id"));
    		resultAssignment.setAssayResultId(rs.getInt("assay_result_id"));
    		resultAssignment.setUpdDate(rs.getDate("upddate"));
    		resultAssignment.setUpdUser(rs.getString("upduser"));
    		resultAssignment.setCreDate(rs.getDate("credate"));
    		resultAssignment.setCreUser(rs.getString("creuser"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return resultAssignment;
	}
}
