package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.ActionPatternsDao;
import com.idorsia.research.spirit.core.model.ActionPatterns;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class ActionPatternsDaoImpl extends AbstractDao<ActionPatterns> implements ActionPatternsDao {

	private static final String TABLE_NAME = "ActionPatterns";
	
	@Override
	public ActionPatterns get(Integer id) {
		return super.get(TABLE_NAME,ActionPatterns.class, id);
	}

	@Override
	public List<ActionPatterns> getByIds(List<Integer> actionPatternsIds) {
		StringBuilder sql = new StringBuilder(
				"select * from ").append(TABLE_NAME).append(" where id ");
		sql.append(DataUtils.fetchInInt(actionPatternsIds));
		return (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql.toString());
	}
	
	public List<ActionPatterns> getByAssignment(Integer assignmentId){
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP, ASSIGNMENT_PATTERN ASP where ASP.ACTIONPATTERN_ID = ap.ID AND ASPP.ASSIGNMENT_ID = " + assignmentId; 
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql);
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}

	public List<ActionPatterns> getBySubGroup(Integer subGroupId){
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP, SUBGROUP_PATTERN SGP where SGP.ACTIONPATTERN_ID = ap.ID AND SGP.SUBGROUP_ID = " + subGroupId; 
		List<ActionPatterns> results = getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(ActionPatterns.class));
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}

	public List<ActionPatterns> getByGroup(Integer groupId){
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP, GROUP_PATTERN GP where GP.ACTIONPATTERN_ID = ap.ID AND GP.GROUP_ID = " + groupId; 
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql);
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}
	
	public List<ActionPatterns> getByAction(Integer actionId, String type){
		String sql = String.format("SELECT * from " + TABLE_NAME + " WHERE ACTION_ID=%s AND ACTION_TYPE='%s'", actionId, type);
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql);
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}


	@Override
	public List<ActionPatterns> list() {
		return super.getlist(TABLE_NAME,ActionPatterns.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(ActionPatterns actionPattern) {
		if (actionPattern.getId() != null && actionPattern.getId() > 0) {
			if(!actionPattern.equals(get(actionPattern.getId()))){
				String sql = "UPDATE ACTIONPATTERNS SET "
						+ "ACTION_ID=:actionId, "
						+ "SCHEDULE_ID=:scheduleId, "
						+ "ACTION_TYPE=:actionType, "
						+ "ACTION_PARAMETERS=:actionParameters, "
						+ "NO=:no, "
						+ "STAGE_ID=:stageId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", actionPattern.getId())
			    		.addValue("actionId", actionPattern.getActionId())
			    		.addValue("scheduleId", actionPattern.getScheduleId())
			    		.addValue("actionType", actionPattern.getActionType())
			    		.addValue("actionParameters", actionPattern.getActionParameters())
			    		.addValue("no", actionPattern.getNo())
			    		.addValue("stageId", actionPattern.getStageId())
			    		.addValue("creDate", actionPattern.getCreDate())
			    		.addValue("creUser", actionPattern.getCreUser())
			    		.addValue("updDate", actionPattern.getUpdDate())
			    		.addValue("updUser", actionPattern.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			actionPattern.setId(getSequence(Constants.STUDY_ACTIONPATTERNS_SEQUENCE_NAME));
			addActionPattern(actionPattern);
			addTransient(actionPattern);
		}
		addIdToObject(ActionPatterns.class, actionPattern.getId(), actionPattern);
		return actionPattern.getId();
	}

	@Override
	public int addActionPattern(ActionPatterns actionPattern) {
		return super.add(TABLE_NAME,actionPattern);
	}

	@Override
	public void delete(int actionPatternId) {
		super.delete(TABLE_NAME, actionPatternId);
	}
	
	@Override
	public List<ActionPatterns> getActionTypeByStage(Integer stageId) {
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP where AP.STAGE_ID = " + stageId;
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql);
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}
	
	@Override
	public List<ActionPatterns> getActionPatternsByParent(Integer parentId) {
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP where parent_id = "+ parentId;
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(ActionPatterns.class, sql);
		for(ActionPatterns ap : results) {
			addObjectInCache(ap, ActionPatterns.class, ap.getId());
		}
		return results;
	}
	
	@Override
	public ActionPatterns getActionTypeBySchdule(Integer scheduleId) {
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP where AP.SCHEDULE_ID = " + scheduleId;
		try{
			ActionPatterns result =  super.getObject(TABLE_NAME, ActionPatterns.class, sql);
			addObjectInCache(result, ActionPatterns.class, result.getId());
			return result;
		}catch (Exception e) {
			System.out.println("action pettern missing schedul " + scheduleId);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<ActionPatterns> getStageDefinition(Integer stageId) {
		String sql = "SELECT AP.* from " + TABLE_NAME + " AP, STAGE_PATTERN SP where SP.ACTIONPATTERN_ID = ap.ID AND SP.STAGE_ID = " + stageId;
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql);
		return results;
	}

	@Override
	public Set<ActionPatterns> getBySubGoupsAndSchedules(Set<Integer> subGroupIds, Set<Integer> scheduleIds) {
		StringBuilder sql = new StringBuilder("SELECT AP.* from ").append(TABLE_NAME).append(" AP");
		sql.append(", SUBGROUP_PATTERN SGP ");
		sql.append("where SGP.ACTIONPATTERN_ID = AP.ID ");
		sql.append("AND SGP.SUBGROUP_ID").append(DataUtils.fetchInInt(new ArrayList<Integer>(subGroupIds)));
		sql.append("AND AP.SCHEDULE_ID").append(DataUtils.fetchInInt(new ArrayList<Integer>(scheduleIds)));
		List<ActionPatterns> results =  (List<ActionPatterns>) super.getObjectList(TABLE_NAME, ActionPatterns.class, sql.toString());
		return new HashSet<ActionPatterns>(results);
	}

	@Override
	public Map<Integer, List<Integer>> getActionsPhasesMap(List<Integer> stageIds) {
		Map<Integer, Integer> queryRes = new HashMap<Integer, Integer>();
		String sql = "select distinct(sp.phase_id), a.id "
				+ "from schedule_phase sp, actionpatterns ap, assay a "
				+ "where ap.schedule_id = sp.schedule_id and ap.action_id = a.id and ap.action_type = 'MEASUREMENT' "
				+ "and ap.stage_id"
		 		+ DataUtils.fetchInInt(new ArrayList<Integer>(stageIds));
		getJdbcTemplate().query(sql, 
			      (rs, rowNum) -> queryRes.put(rs.getInt("phase_id"), rs.getInt("id")));
		
		Map<Integer, List<Integer>> actionsPhasesMap = new HashMap<Integer, List<Integer>>();
		for(Entry<Integer,Integer> entry : queryRes.entrySet()) {
			if(actionsPhasesMap.get(entry.getValue()) != null) {
				actionsPhasesMap.get(entry.getValue()).add(entry.getKey());
			}else {
				List<Integer> phaseIds = new ArrayList<Integer>();
				phaseIds.add(entry.getKey());
				actionsPhasesMap.put(entry.getValue(), phaseIds);
			}
		}
		return actionsPhasesMap;
	}

	@Override
	public Map<Integer, Integer> getActionsPhasesMap(Integer actionpatternId) {
		Map<Integer, Integer> queryRes = new HashMap<Integer, Integer>();
		String sql = String.format(
				"select a.id, sp.phase_id from schedule_phase sp, actionpatterns ap, assay a "
				+ "where ap.schedule_id = sp.schedule_id and ap.action_id = a.id and ap.id = %s",
				actionpatternId);
		getJdbcTemplate().query(sql, (rs, rowNum) -> queryRes.put(rs.getInt("id"), rs.getInt("phase_id")));
		return queryRes;
	}

	@Override
	public String getActionTypeByActionId(Integer id) {
		String sql = String.format("select * from " + TABLE_NAME + " where action_id=%s and ROWNUM=1",id);
		ActionPatterns ap =  super.getObject(TABLE_NAME, ActionPatterns.class, sql);
		return ap.getActionType();
	}
}
