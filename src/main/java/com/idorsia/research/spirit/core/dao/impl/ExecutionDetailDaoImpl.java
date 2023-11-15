package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.ExecutionDetailDao;
import com.idorsia.research.spirit.core.model.ExecutionDetail;

@Repository
public class ExecutionDetailDaoImpl extends AbstractDao<ExecutionDetail> implements ExecutionDetailDao {

	private static final String TABLE_NAME = "execution_details";
	
	@Override
	public ExecutionDetail get(Integer id) {
		return super.get(TABLE_NAME, ExecutionDetail.class, id);
	}
	
	@Override
	public ExecutionDetail getByPhaseAndAssignment(int phaseLinkId, int assignmentId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE PHASELINK_ID = " + phaseLinkId + " AND ASSIGNMENT_ID = " + assignmentId;
		return super.getObject(TABLE_NAME, ExecutionDetail.class, sql);
	}
	
	@Override
	public List<ExecutionDetail> getBySchedulePhase(Integer schedulePhaseId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE PHASELINK_ID = " + schedulePhaseId;
		List<ExecutionDetail> result = (List<ExecutionDetail>) super.getObjectList(TABLE_NAME, ExecutionDetail.class, sql);	
		for(ExecutionDetail ed : result) {
			addObjectInCache(ed, ExecutionDetail.class, ed.getId());
		}
		return result;
	}

	@Override
	public List<ExecutionDetail> getByAssignment(Integer assignmentId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ASSIGNMENT_ID = " + assignmentId;
		List<ExecutionDetail> result = (List<ExecutionDetail>) super.getObjectList(TABLE_NAME, ExecutionDetail.class, sql);	
		for(ExecutionDetail ed : result) {
			addObjectInCache(ed, ExecutionDetail.class, ed.getId());
		}
		return result;
	}
	
	@Override
	public List<ExecutionDetail> getByStudyId(Integer studyId) {
		String sql = "SELECT ed.* FROM " + TABLE_NAME + " ed, ASSIGNMENT a, STAGE s WHERE ed.ASSIGNMENT_ID=a.ID and a.STAGE_ID=s.id and s.ID="+studyId;
		List<ExecutionDetail> result = (List<ExecutionDetail>) super.getObjectList(TABLE_NAME, ExecutionDetail.class, sql);	
		for(ExecutionDetail ed : result) {
			addObjectInCache(ed, ExecutionDetail.class, ed.getId());
		}
		return result;
	}

	@Override
	public List<ExecutionDetail> list() {
		return super.getlist(TABLE_NAME, ExecutionDetail.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(ExecutionDetail executionDetail) {
		if (executionDetail.getId() != null && executionDetail.getId() > 0) {
			if(!executionDetail.equals(get(executionDetail.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "PHASELINK_ID=:phaseLinkId, "
						+ "ASSIGNMENT_ID=:assignmentId, "
						+ "DEVIATION=:deviation, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", executionDetail.getId())
			    		.addValue("phaseLinkId", executionDetail.getPhaselinkId())
			    		.addValue("assignmentId", executionDetail.getAssignmentId())
			    		.addValue("deviation", executionDetail.getDeviation())
			    		.addValue("creDate", executionDetail.getCreDate())
			    		.addValue("creUser", executionDetail.getCreUser())
			    		.addValue("updDate", executionDetail.getUpdDate())
			    		.addValue("updUser", executionDetail.getUpdUser());;
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			executionDetail.setId(getSequence(Constants.STUDY_EXECUTION_DETAILS_SEQ_NAME));
			addExecutionDetail(executionDetail);
			addTransient(executionDetail);
		}
		addIdToObject(ExecutionDetail.class, executionDetail.getId(), executionDetail);
		return executionDetail.getId();
	}

	@Override
	public int addExecutionDetail(ExecutionDetail executionDetail) {
		return super.add(TABLE_NAME, executionDetail);
	}

	@Override
	public void delete(int executionDetailId) {
		super.delete(TABLE_NAME, executionDetailId);
	}
}
