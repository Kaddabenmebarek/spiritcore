package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.ScheduleDao;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.Schedule;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class ScheduleDaoImpl extends AbstractDao<Schedule> implements ScheduleDao {

	private static final String TABLE_NAME = "Schedule";
	
	@Override
	public Schedule get(Integer id) {
		return super.get(TABLE_NAME,Schedule.class, id);
	}

	@Override
	public List<Schedule> list() {
		return super.getlist(TABLE_NAME,Schedule.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Schedule schedule) {
		if (schedule.getId() != null && schedule.getId() > 0) {
			if(!schedule.equals(get(schedule.getId()))) {
				String sql = "UPDATE SCHEDULE SET "
						+ "START_DATE=:startDate, "
						+ "LAST_PHASE=:lastPhase, "
						+ "RRULE=:rRule, "
						+ "TIMEPOINTS=:timepoints, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", schedule.getId())
			    		.addValue("startDate", schedule.getStartDate())
			    		.addValue("lastPhase", schedule.getLastPhase())
			    		.addValue("rRule", schedule.getrRule())
			    		.addValue("timepoints", schedule.getTimePoints())
			    		.addValue("creDate", schedule.getCreDate())
			    		.addValue("creUser", schedule.getCreUser())
			    		.addValue("updDate", schedule.getUpdDate())
			    		.addValue("updUser", schedule.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			schedule.setId(getSequence(Constants.STUDY_SCHEDULE_SEQUENCE_NAME));
			addSchedule(schedule);
			addTransient(schedule);
		}
		addIdToObject(Schedule.class, schedule.getId(), schedule);
		return schedule.getId();
	}

	@Override
	public int addSchedule(Schedule schedule) {
		return super.add(TABLE_NAME, schedule);
	}

	@Override
	public void delete(int scheduleId) {
		super.delete(TABLE_NAME, scheduleId);
	}

	@Override
	public List<Schedule> getSchedulesByPhases(List<Phase> phases) {
		StringBuilder sql = new StringBuilder("SELECT SC.* FROM SCHEDULE SC, SCHEDULE_PHASE SP WHERE SP.SCHEDULE_ID = SC.ID AND SP.PHASE_ID IN (");
		for (int i = 0; i < phases.size(); i++) {
			sql.append(phases.get(i).getId());
			if (i < phases.size() - 1)
				sql.append(",");
		}
		sql.append(")");
		return (List<Schedule>) super.getObjectList(TABLE_NAME, Schedule.class, sql.toString());
	}

	@Override
	public List<Schedule> getByStudy(Integer studyId) {
		String sql = "SELECT * FROM schedule WHERE id IN ("
				+ "SELECT distinct(sp.schedule_id) "
				+ "FROM schedule_phase sp, phase p, stage st "
				+ "WHERE sp.phase_id = p.id AND p.stage_id = st.id AND st.study_id = "
				+ studyId
				+ ")";
		List<Schedule> result = (List<Schedule>) super.getObjectList(TABLE_NAME, Schedule.class, sql);
		for(Schedule schedule : result) {
			addObjectInCache(schedule, Schedule.class, schedule.getId());
		}
		return result;
	}

	@Override
	public List<Schedule> getSchedulesByStages(List<Integer> stageIds) {
		StringBuilder sql = new StringBuilder("select * from ").append(TABLE_NAME).append(" where id in (");
		sql.append("select distinct(schedule_id) from actionpatterns where stage_id ");
		sql.append(DataUtils.fetchInInt(stageIds));
		sql.append(")");		
		return (List<Schedule>) super.getObjectList(TABLE_NAME, Schedule.class, sql.toString());
	}

}
