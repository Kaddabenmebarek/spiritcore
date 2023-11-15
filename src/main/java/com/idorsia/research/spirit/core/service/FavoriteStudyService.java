package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.dao.FavoriteStudyDao;
import com.idorsia.research.spirit.core.model.FavoriteStudy;
import com.idorsia.research.spirit.core.model.Study;

@Service
public class FavoriteStudyService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 2187925689765617187L;

	@Autowired
	private FavoriteStudyDao favoriteStudyDao;
	
	@Autowired
	private StudyService studyService;

	public FavoriteStudy get(Integer id) {
		return favoriteStudyDao.get(id);
	}

	public List<FavoriteStudy> list() {
		return favoriteStudyDao.list();
	}

	public List<Study> getByUser(Integer id) {
		List<Study> results = new ArrayList<Study>();
		List<FavoriteStudy> fsList = favoriteStudyDao.getByUser(id);
		for(FavoriteStudy fs : fsList) {
			results.add(studyService.get(fs.getStudyId()));
		}
		return results;
	}

	public int getCount() {
		return favoriteStudyDao.getCount();
	}

	public Integer saveOrUpdate(FavoriteStudy favoriteStudy) {
		return favoriteStudyDao.saveOrUpdate(favoriteStudy);
	}

	public int addFavoriteStudy(FavoriteStudy favoriteStudy) {
		return favoriteStudyDao.addFavoriteStudy(favoriteStudy);
	}

	@Transactional
	public void delete(int favoriteStudyId) {
		delete(favoriteStudyId, false);
	}
	
	protected void delete(int favoriteStudyId, Boolean cross) {
		favoriteStudyDao.delete(favoriteStudyId);
	}

	@Transactional
	public void deleteByStudyAndUser(Integer studyId, Integer id) {
		favoriteStudyDao.deleteByStudyAndUser(studyId, id);
	}

	public List<FavoriteStudy> getByStudy(int studyId) {
		return favoriteStudyDao.getByStudy(studyId);
	}

	public FavoriteStudy getByStudyAndUser(int studyId, int userId) {
		return favoriteStudyDao.getByStudyAndUser(studyId, userId);
	}

	public FavoriteStudyDao getFavoriteStudyDao() {
		return favoriteStudyDao;
	}

	public void setFavoriteStudyDao(FavoriteStudyDao favoriteStudyDao) {
		this.favoriteStudyDao = favoriteStudyDao;
	}
}
