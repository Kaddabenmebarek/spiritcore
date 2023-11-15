package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.PlannedSampleDao;
import com.idorsia.research.spirit.core.model.PlannedSample;

@Repository
public class PlannedSampleDaoImpl extends AbstractDao<PlannedSample> implements PlannedSampleDao {

	private static final String TABLE_NAME = "PLANNED_SAMPLE";

	@Override
	public PlannedSample get(Integer id) {
		return super.get(TABLE_NAME, PlannedSample.class, id);
	}

	@Override
	public PlannedSample getPlannedSampleById(int id) {
		String sql = String.format("SELECT * FROM PLANNED_SAMPLE WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, PlannedSample.class, sql);
	}
	
	@Override
	public List<PlannedSample> getPlannedSamplesByStage(Integer stageId) {
		String sql = String.format("SELECT * FROM PLANNED_SAMPLE WHERE STAGE_ID = %s", stageId);
		return (List<PlannedSample>) super.getObjectList(TABLE_NAME, PlannedSample.class, sql);
	}
	
	@Override
	public List<PlannedSample> getPlannedSamplesByBiosample(Integer biosample_id) {
		String sql = String.format("SELECT * FROM PLANNED_SAMPLE WHERE BIOSAMPLE_ID = %s", biosample_id);
		return (List<PlannedSample>) super.getObjectList(TABLE_NAME, PlannedSample.class, sql);	
	}
	
	@Override
	public PlannedSample getPlannedSampleByStageAndBiosample(Integer stageId, Integer biosampleId) {
		String sql = String.format("SELECT * FROM PLANNED_SAMPLE WHERE STAGE_ID= %s AND BIOSAMPLE_ID=%s", stageId, biosampleId);
		return super.getObject(TABLE_NAME, PlannedSample.class, sql);
	}
	
	@Override
	public int addPlannedSample(PlannedSample plannedSample) {
		return super.add(TABLE_NAME, plannedSample);
	}
	
	@Override
	public void delete(int plannedSampleId) {
		super.delete(TABLE_NAME, plannedSampleId);
	}

	@Override
	public Integer saveOrUpdate(PlannedSample plannedSample) {
		if (plannedSample.getId() != null && plannedSample.getId() > 0) {
			if(!plannedSample.equals(get(plannedSample.getId()))) {
				String sql = "UPDATE PLANNED_SAMPLE SET biosample_id=:biosample_id, " 
						+ "name=:name, " 
						+ "upd_date=:upd_date, " 
						+ "upd_user=:upd_user, "
						+ "cre_date=:cre_date, "
						+ "cre_user=:cre_user, "
						+ "stage_id=:stage_id, " 
						+ "weight=:weight, " 
						+ "datalist=:datalist "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", plannedSample.getId())
			    		.addValue("biosample_id", plannedSample.getBiosampleId())
			    		.addValue("name", plannedSample.getName())
			    		.addValue("upd_date", plannedSample.getUpdDate())
			    		.addValue("upd_user", plannedSample.getUpdUser())
			    		.addValue("cre_date", plannedSample.getCreDate())
			    		.addValue("cre_user", plannedSample.getCreUser())
			    		.addValue("stage_id", plannedSample.getStageId())
			    		.addValue("weight", plannedSample.getWeight())
			    		.addValue("datalist", plannedSample.getDatalist());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			plannedSample.setId(getSequence(Constants.PLANNED_SAMPLE_SEQUENCE_NAME));
			addPlannedSample(plannedSample);
			addTransient(plannedSample);
		}
		addIdToObject(PlannedSample.class, plannedSample.getId(), plannedSample);
		return plannedSample.getId();
	}
	
	public PlannedSample rowMap(ResultSet rs) {
		PlannedSample plannedSample = null;
    	try {
    		plannedSample = new PlannedSample();
			plannedSample.setId(rs.getInt("id"));
			plannedSample.setBiosampleId(rs.getInt("biosample_id"));
			plannedSample.setName(rs.getString("name"));
			plannedSample.setUpdDate(rs.getDate("upd_date"));
			plannedSample.setUpdUser(rs.getString("upd_user"));
			plannedSample.setCreDate(rs.getDate("cre_date"));
			plannedSample.setCreUser(rs.getString("cre_user"));
			plannedSample.setStageId(rs.getInt("stage_id"));
			plannedSample.setWeight(rs.getDouble("weight"));
			plannedSample.setDatalist(rs.getString("datalist"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return plannedSample;
	}

}
