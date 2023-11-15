package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.StageDao;
import com.idorsia.research.spirit.core.model.Stage;

@Repository
public class StageDaoImpl extends AbstractDao<Stage> implements StageDao {

	private static final String TABLE_NAME = "Stage";
	
	@Override
	public Stage get(Integer id) {
		return super.get(TABLE_NAME,Stage.class, id);
	}

	@Override
	public Stage getStageById(int id) {
		String sql = String.format("SELECT * FROM STAGE WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, Stage.class, sql);
	}

	@Override
	public Stage getBySchedule(int scheduleId) {
		String sql = String.format(
				"select st.* from stage st, phase ph, schedule_phase sp where ph.stage_id = st.id and ph.id = sp.phase_id and sp.schedule_id = %s",
				scheduleId);
		List<Stage> stgs = (List<Stage>) super.getObjectList(TABLE_NAME, Stage.class, sql);
		return stgs.get(0);
	}


	@Override
	public List<Stage> getStagesByStudyId(Integer studyId) {
		String sql = String.format("SELECT * FROM STAGE WHERE STUDY_ID = %s", studyId);
		return (List<Stage>) super.getObjectList(TABLE_NAME, Stage.class, sql);
	}
	

	@Override
	public List<Stage> getStagesByStudyIdentifier(String studyIdentifier) {
		String sql = String.format("SELECT ST.* FROM STAGE ST, STUDY S WHERE ST.STUDY_ID = S.ID AND S.STUDYID = '%s'", studyIdentifier);
		return (List<Stage>) super.getObjectList(TABLE_NAME, Stage.class, sql);
	}

	@Override
	public void changeOwner(Stage stage, String updUser, String creUser) {
		stage.setUpdUser(updUser);
		stage.setCreUser(creUser);
		saveOrUpdate(stage);
	}

	@Override
	public List<Stage> list() {
		return super.getlist(TABLE_NAME,Stage.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Stage stage) {
		if (stage.getId() != null && stage.getId() > 0) {
			if(!stage.equals(get(stage.getId()))) {
				String sql = "UPDATE STAGE SET "
						+ "study_Id=:studyId, "
						+ "dynamic=:dynamic, "
						+ "biotype_Id=:biotypeId, "
						+ "name=:name, "
						+ "next_Id=:nextId, "
						+ "startDate=:startDate, "
						+ "offset_Of_D0=:offsetOfDO, "
						+ "offSet_From_PreviousStage=:offSetFromPreviousStage, "
						+ "starting_Day=:startingDay, "
						+ "creDate=:creDate, "
						+ "creUser=:creUser, "
						+ "updDate=:updDate, "
						+ "updUser=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", stage.getId())
			    		.addValue("studyId", stage.getStudyId())
			    		.addValue("dynamic", stage.getDynamic())
			    		.addValue("biotypeId", stage.getBiotypeId())
			    		.addValue("name", stage.getName())
			    		.addValue("nextId", stage.getNextId())
			    		.addValue("startDate", stage.getStartDate())
			    		.addValue("offsetOfDO", stage.getOffsetOfD0())
			    		.addValue("offSetFromPreviousStage", stage.getOffsetFromPreviousstage())
			    		.addValue("duration", stage.getDuration())
			    		.addValue("startingDay", stage.getStartingDay())
			    		.addValue("creDate", stage.getCreDate())
			    		.addValue("creUser", stage.getCreUser())
			    		.addValue("updDate", stage.getUpdDate())
			    		.addValue("updUser", stage.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			stage.setId(getSequence(Constants.STUDY_STAGE_SEQUENCE_NAME));
			addStage(stage);
			addTransient(stage);
		}
		addIdToObject(Stage.class, stage.getId(), stage);
		return stage.getId();
	}

	@Override
	public int addStage(Stage stage) {
		return super.add(TABLE_NAME, stage);
	}

	@Override
	public void delete(int stageId) {
		super.delete(TABLE_NAME, stageId);
	}
	
	public Stage rowMap(ResultSet rs) {
		Stage stage = null;
    	try {
    		stage = new Stage();
    		stage.setId(rs.getInt("id"));
    		stage.setStudyId(rs.getInt("study_id"));
    		stage.setDynamic(rs.getInt("dynamic") );
    		stage.setBiotypeId(rs.getInt("biotype_id"));
    		stage.setName(rs.getString("name"));
    		stage.setNextId(rs.getInt("next_id"));
    		stage.setStartDate(rs.getDate("startdate"));
    		stage.setOffsetOfD0(rs.getLong("OFFSET_OF_D0"));
    		stage.setOffsetFromPreviousstage(rs.getLong("OFFSET_FROM_PREVIOUSSTAGE"));
    		stage.setDuration(rs.getInt("duration"));
    		stage.setStartingDay(rs.getString("starting_day"));
    		stage.setCreDate(rs.getDate("creDate"));
    		stage.setCreUser(rs.getString("creuser"));
    		stage.setUpdDate(rs.getDate("upddate"));
    		stage.setUpdUser(rs.getString("upduser"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return stage;
	}

}
