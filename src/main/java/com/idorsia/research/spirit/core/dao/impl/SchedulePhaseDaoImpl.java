package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.SchedulePhaseDao;
import com.idorsia.research.spirit.core.model.SchedulePhase;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class SchedulePhaseDaoImpl extends AbstractDao<SchedulePhase> implements SchedulePhaseDao {

	private static final String TABLE_NAME = "SCHEDULE_PHASE";
	
	@Override
	public SchedulePhase get(Integer id) {
		return super.get(TABLE_NAME, SchedulePhase.class, id);
	}

	@Override
	public List<SchedulePhase> getByPhase(int phaseId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PHASE_ID = %s",
				phaseId);
		return (List<SchedulePhase>) super.getObjectList(TABLE_NAME, SchedulePhase.class, sql);
	}
	
	@Override
	public List<SchedulePhase> getBySchedules(Set<Integer> scheduleIds) {
		StringBuilder sql = new StringBuilder("SELECT * FROM "+TABLE_NAME+" WHERE SCHEDULE_ID ");
		sql.append(DataUtils.fetchInInt(scheduleIds));
		return (List<SchedulePhase>) super.getObjectList(TABLE_NAME, SchedulePhase.class, sql.toString());
	}
	
	@Override
	public List<SchedulePhase> getBySchedule(int scheduleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE SCHEDULE_ID = '%s'",
				scheduleId);
		return (List<SchedulePhase>) super.getObjectList(TABLE_NAME, SchedulePhase.class, sql);
	}

	@Override
	public List<SchedulePhase> list() {
		return super.getlist(TABLE_NAME, SchedulePhase.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(SchedulePhase schedulePhase) {
		if (schedulePhase.getId() != null && schedulePhase.getId() > 0) {
			if(!schedulePhase.equals(get(schedulePhase.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "SCHEDULE_ID=:scheduleId, "
						+ "PHASE_ID=:phaseId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", schedulePhase.getId())
			    		.addValue("scheduleId", schedulePhase.getScheduleId())
			    		.addValue("phaseId", schedulePhase.getPhaseId())
			    		.addValue("creDate", schedulePhase.getCreDate())
			    		.addValue("creUser", schedulePhase.getCreUser())
			    		.addValue("updDate", schedulePhase.getUpdDate())
			    		.addValue("updUser", schedulePhase.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			schedulePhase.setId(getSequence(Constants.STUDY_SCHEDULE_PHASE_SEQUENCE_NAME));
			addSchedulePhase(schedulePhase);
			addTransient(schedulePhase);
		}
		addIdToObject(SchedulePhase.class, schedulePhase.getId(), schedulePhase);
		return schedulePhase.getId();
	}

	@Override
	public int addSchedulePhase(SchedulePhase schedulePhase) {
		return super.add(TABLE_NAME, schedulePhase);
	}

	@Override
	public void delete(int schedulePhaseId) {
		super.delete(TABLE_NAME, schedulePhaseId);
	}

}
