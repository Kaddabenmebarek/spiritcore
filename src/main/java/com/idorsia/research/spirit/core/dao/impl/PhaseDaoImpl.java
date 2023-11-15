package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.PhaseDao;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.SchedulePhase;
import com.idorsia.research.spirit.core.model.Stage;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class PhaseDaoImpl extends AbstractDao<Phase> implements PhaseDao {

	private static final String TABLE_NAME = "Phase";
	
	@Override
	public Phase get(Integer id) {
		return super.get(TABLE_NAME,Phase.class, id);
	}

	@Override
	public List<Phase> list() {
		return super.getlist(TABLE_NAME,Phase.class);
	}
	

	@Override
	public Set<Integer> getBySchedule(Set<Integer> matchingSchedules) {
		StringBuilder sql = new StringBuilder("select * from schedule_phase where schedule_id ");
		sql.append(DataUtils.fetchInInt(matchingSchedules));
		List<SchedulePhase> spList =  getJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SchedulePhase.class));
		Set<Integer> results = new TreeSet<Integer>();
		for(SchedulePhase sp : spList) {
			results.add(sp.getPhaseId());
		}
		return results;
	}


	@Override
	public Phase getByStageAndDuration(double phaseDuration, Integer stageId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID = " + stageId + "AND PHASE = " + phaseDuration;
		return super.getObject(TABLE_NAME, Phase.class, sql);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Phase phase) {
		if (phase.getId() != null && phase.getId() > 0) {
			if(!phase.equals(get(phase.getId()))) {
				String sql = "UPDATE PHASE SET "
						+ "stage_Id=:stageId, "
						+ "phase=:phase, "
						+ "label=:label "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", phase.getId())
			    		.addValue("stageId", phase.getStageId())
			    		.addValue("phase", phase.getPhase())
			    		.addValue("label", phase.getLabel());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			phase.setId(getSequence(Constants.PHASE_SEQUENCE_NAME));
			addPhase(phase);
			addTransient(phase);
		}
		addIdToObject(Phase.class, phase.getId(), phase);
		return phase.getId();
	}

	@Override
	public int addPhase(Phase phase) {
		return super.add(TABLE_NAME, phase);
	}

	@Override
	public void delete(int phaseId) {
		super.delete(TABLE_NAME, phaseId);
	}

	@Override
	public List<Phase> getPhasesByStage(Integer stageId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID = '%s'",
				stageId);
		return (List<Phase>) super.getObjectList(TABLE_NAME, Phase.class, sql);
	}

	@Override
	public List<Phase> getPhasesByStages(List<Stage> stages) {
		StringBuilder sb = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE STAGE_ID IN (");
		for (int i = 0; i < stages.size(); i++) {
			sb.append(stages.get(i).getId());
			if (i < stages.size() - 1)
				sb.append(",");
		}
		sb.append(")");
		return (List<Phase>) super.getObjectList(TABLE_NAME, Phase.class, sb.toString());
	}
	
	@Override
	public List<Phase> getPhasesByStudyId(Integer studyId) {
		String sql = String.format("SELECT p.* FROM PHASE p, STAGE s where p.STAGE_ID=s.ID and s.STUDY_ID = '%s'",studyId);
		List<Phase> results = (List<Phase>) super.getObjectList(TABLE_NAME, Phase.class, sql);
		for(Phase phase : results) {
			addObjectInCache(phase, Phase.class, phase.getId());
		}
		return results;
	}

	@Override
	public List<Phase> getPhasesByStudy(int studyId){
		String sql = String.format("SELECT p.* FROM " + TABLE_NAME + " p, stage stage WHERE p.stage_id=stage.id and stage.study_id=%s",studyId);
		return (List<Phase>) super.getObjectList(TABLE_NAME, Phase.class, sql);
	}

}
