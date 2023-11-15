package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.FavoriteStudy;

public interface FavoriteStudyDao {

	public FavoriteStudy get(Integer id);

	public List<FavoriteStudy> list();

	public int getCount();

	public Integer saveOrUpdate(FavoriteStudy favoriteStudy);

	public int addFavoriteStudy(FavoriteStudy favoriteStudy);

	public void delete(int favoriteStudyId);
	
	public void deleteByStudyAndUser(Integer studyId, Integer id);

	public List<FavoriteStudy> getByUser(Integer id);

	public List<FavoriteStudy> getByStudy(int studyId);
	
	public FavoriteStudy getByStudyAndUser(int studyId, int userId);
}
