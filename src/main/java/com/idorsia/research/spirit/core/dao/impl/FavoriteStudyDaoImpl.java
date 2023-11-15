package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.FavoriteStudyDao;
import com.idorsia.research.spirit.core.model.FavoriteStudy;

@Repository
public class FavoriteStudyDaoImpl extends AbstractDao<FavoriteStudy> implements FavoriteStudyDao {

	private static final String TABLE_NAME = "FAVORITE_STUDY";
	
	@Override
	public FavoriteStudy get(Integer id) {
		return super.get(TABLE_NAME, FavoriteStudy.class, id);
	}

	@Override
	public List<FavoriteStudy> list() {
		return super.getlist(TABLE_NAME, FavoriteStudy.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(FavoriteStudy favoriteStudy) {
		if (favoriteStudy.getId() != null && favoriteStudy.getId() > 0) {
			if(!favoriteStudy.equals(get(favoriteStudy.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET " + "USER_ID=:userId, " + "STUDY_ID=:studyId " + "WHERE ID=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", favoriteStudy.getId())
						.addValue("userId", favoriteStudy.getUserId()).addValue("studyId", favoriteStudy.getStudyId());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			favoriteStudy.setId(getSequence(Constants.FAVSTUDY_SEQUENCE_NAME));
			addFavoriteStudy(favoriteStudy);
			addTransient(favoriteStudy);
		}
		addIdToObject(FavoriteStudy.class, favoriteStudy.getId(), favoriteStudy);
		return favoriteStudy.getId();
	}

	@Override
	public int addFavoriteStudy(FavoriteStudy favoriteStudy) {
		return super.add("FAVORITE_STUDY",favoriteStudy);
	}

	@Override
	public void delete(int favoriteStudyId) {
		super.delete(TABLE_NAME, favoriteStudyId);
	}
	
	public void deleteByStudyAndUser(Integer studyId, Integer userId) {
		String sql = String.format("DELETE from favorite_study where STUDY_ID=%s AND USER_ID=%s", studyId, userId);
		getJdbcTemplate().execute(sql);
	}


	@Override
	public List<FavoriteStudy> getByUser(Integer userId) {
		String sql = String.format("select * from favorite_study where USER_ID=%s",userId);
		List<FavoriteStudy> result = (List<FavoriteStudy>) super.getObjectList(TABLE_NAME, FavoriteStudy.class, sql);
		return result;
	}

	@Override
	public List<FavoriteStudy> getByStudy(int studyId) {
		String sql = String.format("SELECT * FROM FAVORITE_STUDY WHERE STUDY_ID = %s", studyId);
		return (List<FavoriteStudy>) super.getObjectList(TABLE_NAME, FavoriteStudy.class, sql);
	}
	
	@Override
	public FavoriteStudy getByStudyAndUser(int studyId, int userId) {
		String sql = String.format("SELECT * FROM FAVORITE_STUDY WHERE STUDY_ID = %s and USER_ID=%s", studyId, userId);
		return super.getObject(TABLE_NAME, FavoriteStudy.class, sql);
	}

	public FavoriteStudy rowMap(ResultSet rs) {
		FavoriteStudy fs = null;
    	try {
    		fs = new FavoriteStudy();
			fs.setId(rs.getInt("id"));
			fs.setUserId(rs.getInt("user_id"));
			fs.setStudyId(rs.getInt("study_id"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return fs;
	}
	
}
