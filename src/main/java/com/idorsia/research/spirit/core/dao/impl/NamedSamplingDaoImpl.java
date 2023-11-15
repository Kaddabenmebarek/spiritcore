package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.NamedSamplingDao;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.NamedSampling;
import com.idorsia.research.spirit.core.util.QueryTokenizer;

@Repository
public class NamedSamplingDaoImpl extends AbstractDao<NamedSampling> implements NamedSamplingDao {

	private static final String TABLE_NAME = "Namedsampling";
	
	@Override
	public NamedSampling get(Integer id) {
		return super.get(TABLE_NAME, NamedSampling.class, id);
	}

	@Override
	public List<NamedSampling> getNamedTreatmentsByStudy(Integer studyId){
		String sql = String.format("select * from NAMEDSAMPLING where study_id=%s",studyId); 
		List<NamedSampling> result = (List<NamedSampling>) super.getObjectList(TABLE_NAME, NamedSampling.class, sql);
		for(NamedSampling namedTreatment : result) {
			addObjectInCache(namedTreatment, NamedSampling.class, namedTreatment.getId());
		}
		return result;
	}
	
	@Override
	public List<NamedSampling> list() {
		return super.getlist(TABLE_NAME, NamedSampling.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(NamedSampling namedSampling) {
		if (namedSampling.getId() != null && namedSampling.getId() > 0) {
			if(!namedSampling.equals(get(namedSampling.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "NAME=:name, "
						+ "STUDY_ID=:studyId, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser, "
						+ "NECROPSY=:necropsy, "
						+ "COLOR=:color "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", namedSampling.getId())
			    		.addValue("name", namedSampling.getName())
			    		.addValue("studyId", namedSampling.getStudyId())
			    		.addValue("creDate", namedSampling.getCreDate())
			    		.addValue("creUser", namedSampling.getCreUser())
			    		.addValue("updDate", namedSampling.getUpdDate())
			    		.addValue("updUser", namedSampling.getUpdUser())
			    		.addValue("necropsy", namedSampling.getNecropsy())
			    		.addValue("color", namedSampling.getColor());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			namedSampling.setId(getSequence(Constants.STUDY_SAMPLING_SEQUENCE_NAME));
			addNamedSampling(namedSampling);
			addTransient(namedSampling);
		}
		addIdToObject(namedSampling.getClass(), namedSampling.getId(), namedSampling);
		return namedSampling.getId();
	}

	@Override
	public int addNamedSampling(NamedSampling namedSampling) {
		return super.add(TABLE_NAME, namedSampling);
	}

	@Override
	public void delete(int namedSamplingId) {
		super.delete(TABLE_NAME, namedSamplingId);

	}

	@Override
	public List<NamedSampling> getNamedSamplings(StudyDto study, List<Integer> sids, User user) {
		String query = "SELECT * FROM namedsampling "
		+ "where (id in (select namedsampling_id from sampling) "
		+ "and creuser = '"+ user.getUsername() +"' "
		+ "and study_id is NULL) "
		+ "or "
		+ "(" + QueryTokenizer.expandForIn("study_id", sids) + ")";
		
		return (List<NamedSampling>) super.getObjectList(TABLE_NAME, NamedSampling.class, query);
		
	}

}
