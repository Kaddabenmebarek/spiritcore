package com.idorsia.research.spirit.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.FoodWater;

public interface FoodWaterDao {

	public FoodWater get(Integer id);

	public List<FoodWater> list();

	public int getCount();

	public Integer saveOrUpdate(FoodWater foodWater);

	public int addFoodWater(FoodWater foodWater);

	public void delete(int foodWaterId);

	public Map<Integer, List<FoodWater>> getByEnclosures(List<Integer> enclosureIds);

	public List<FoodWater> getByEnclosure(Integer enclosureId);

	public FoodWater getByEnclosureAndDate(Integer enclosureId, String timepointDate);

	public List<FoodWater> getByStudy(Integer studyId);

	public List<FoodWater> getByStudyAndDate(Integer studyId, String timepointDate);

	public List<FoodWater> getFoodWater(StudyDto study, Date date);
}
