package com.idorsia.research.spirit.core.dao.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.FoodWaterDao;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.FoodWater;
import com.idorsia.research.spirit.core.util.DataUtils;

@Repository
public class FoodWaterDaoImpl extends AbstractDao<FoodWater> implements FoodWaterDao {

	private static final String TABLE_NAME = "Food_water";
	private static final String DATE_FORMAT = "YYYY-MM-dd HH:mm:ss";
	
	@Override
	public FoodWater get(Integer id) {
		return super.get(TABLE_NAME,FoodWater.class, id);
	}

	@Override
	public List<FoodWater> list() {
		return super.getlist(TABLE_NAME,FoodWater.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}
	
	@Override
	public Map<Integer, List<FoodWater>> getByEnclosures(List<Integer> enclosureIds) {
		Map<Integer, List<FoodWater>> result = new HashMap<Integer, List<FoodWater>>();
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE ENCLOSURE_ID");
		sql.append(DataUtils.fetchInInt(enclosureIds));
		List<FoodWater> fwList = getJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(FoodWater.class));
		for(FoodWater fw : fwList) {
			if(result.get(fw.getEnclosureId()) != null) {
				result.get(fw.getEnclosureId()).add(fw);
			}else {
				List<FoodWater> fws = new ArrayList<FoodWater>();
				fws.add(fw);
				result.put(fw.getEnclosureId(), fws);
			}
		}
		return result;
	}
	
	@Override
	public List<FoodWater> getByEnclosure(Integer enclosureId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ENCLOSURE_ID = '%s'",
				enclosureId);
		return (List<FoodWater>) super.getObjectList(TABLE_NAME, FoodWater.class, sql);
	}
	
	@Override
	public FoodWater getByEnclosureAndDate(Integer enclosureId, String timepointDate) {
		FoodWater fw = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE TO_CHAR(FW_DATE, 'YYYY-MM-DD') = '" + timepointDate
				+ "' AND ENCLOSURE_ID = " + enclosureId;
		List<FoodWater> certList = (List<FoodWater>) super.getObjectList(TABLE_NAME, FoodWater.class, sql);
		fw = DataAccessUtils.singleResult(certList);

		return fw;
	}
	

	@Override
	public List<FoodWater> getByStudy(Integer studyId) {
		String sql = String.format("select fw.* from food_water fw, enclosure enc where fw.enclosure_id = enc.id and enc.study_id = %s",
				studyId);
		return (List<FoodWater>) super.getObjectList(TABLE_NAME, FoodWater.class, sql);
	}

	@Override
	public List<FoodWater> getByStudyAndDate(Integer studyId, String timepointDate) {
		String sql = String.format("select fw.* from food_water fw, enclosure enc where fw.enclosure_id = enc.id and enc.study_id = %s and TO_CHAR(fw.FW_DATE, 'YYYY-MM-DD')='%s'",
				studyId, timepointDate);
		return (List<FoodWater>) super.getObjectList(TABLE_NAME, FoodWater.class, sql);
	}
	
	@Override
	public Integer saveOrUpdate(FoodWater foodWater) {
		if (foodWater.getId() != null && foodWater.getId() > 0) {
			if(!foodWater.equals(get(foodWater.getId()))) {
				String sql = "UPDATE FOOD_WATER SET "
						+ "FW_DATE=:fwDate, "
						+ "ENCLOSURE_ID=:enclosureId, "
						+ "WATERTARE=:waterTare, "
						+ "WATERWEIGHT=:waterWeight, "
						+ "FOODWEIGHT=:foodWeight, "
						+ "FOODTARE=:foodTare, "					
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", foodWater.getId())
			    		.addValue("fwDate", foodWater.getFwDate())
			    		.addValue("enclosureId", foodWater.getEnclosureId())
			    		.addValue("waterTare", foodWater.getWaterTare())
			    		.addValue("waterWeight", foodWater.getWaterWeight())
			    		.addValue("foodWeight", foodWater.getFoodWeight())
			    		.addValue("foodTare", foodWater.getFoodTare())		    		
			    		.addValue("creDate", foodWater.getCreDate())
			    		.addValue("creUser", foodWater.getCreUser())
			    		.addValue("updDate", foodWater.getUpdDate())
			    		.addValue("updUser", foodWater.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			foodWater.setId(getSequence(Constants.FOOD_WATER_SEQUENCE_NAME));
			addFoodWater(foodWater);
			addTransient(foodWater);
		}
		addIdToObject(foodWater.getClass(), foodWater.getId(), foodWater);
		return foodWater.getId();
	}

	@Override
	public int addFoodWater(FoodWater foodWater) {
		return super.add(TABLE_NAME, foodWater);
	}

	@Override
	public void delete(int foodWaterId) {
		super.delete(TABLE_NAME, foodWaterId);
	}

	@Override
	public List<FoodWater> getFoodWater(StudyDto study, Date date) {
		StringBuilder query = new StringBuilder("select fw.* from food_water fw, enclosure enc");
		query.append(" where fw.enclosure_id = enc.id");
		if(date != null) {
			String targetDate = new SimpleDateFormat(DATE_FORMAT).format(date);
			query.append(" and fw.fw_date = to_date('").append(targetDate).append("', 'YYYY-MM-DD HH24:MI:SS')");
		}
		query.append(" and enc.study_id = ").append(study.getId());
		List<FoodWater> resp = (List<FoodWater>) super.getObjectList(TABLE_NAME, FoodWater.class, query.toString());
		return resp;
	}
}
