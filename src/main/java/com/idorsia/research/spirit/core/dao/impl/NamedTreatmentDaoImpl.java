package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.NamedTreatmentDao;
import com.idorsia.research.spirit.core.model.NamedTreatment;

@Repository
public class NamedTreatmentDaoImpl extends AbstractDao<NamedTreatment> implements NamedTreatmentDao {

	private static final String TABLE_NAME = "Namedtreatment";
	
	@Override
	public NamedTreatment get(Integer id) {
		return super.get(TABLE_NAME, NamedTreatment.class, id);
	}
	
	public List<NamedTreatment> getNamedTreatmentsByStudy(Integer studyId){
		String sql = String.format("select * from NAMEDTREATMENT where study_id=%s",studyId); 
		List<NamedTreatment> result = (List<NamedTreatment>) super.getObjectList(TABLE_NAME, NamedTreatment.class, sql);
		for(NamedTreatment namedTreatment : result) {
			addObjectInCache(namedTreatment, NamedTreatment.class, namedTreatment.getId());
		}
		return result;
	}

	@Override
	public List<NamedTreatment> list() {
		return super.getlist(TABLE_NAME, NamedTreatment.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(NamedTreatment namedTreatment) {
		if (namedTreatment.getId() != null && namedTreatment.getId() > 0) {
			if(!namedTreatment.equals(get(namedTreatment.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "DISEASE=:disease, "
						+ "NAME=:name, "
						+ "COLOR=:color, "
						+ "PPG_TREATMENT_INSTANCE_ID=:ppgTreatmentInstanceId, "
						+ "SPI_TREATMENT_ID=:spiTreatmentId, "
						+ "SURGERY=:surgery, "
						+ "LOCATION=:location, "
						+ "COMMENTS=:comments, "
						+ "CREDATE=:creaDate, "
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", namedTreatment.getId())
			    		.addValue("disease", namedTreatment.getDisease())
			    		.addValue("name", namedTreatment.getName())
			    		.addValue("color", namedTreatment.getColor())
			    		.addValue("ppgTreatmentInstanceId", namedTreatment.getPpgTreatmentInstanceId())
			    		.addValue("spiTreatmentId", namedTreatment.getSpiTreatmentId())
			    		.addValue("surgery", namedTreatment.getSurgery())
			    		.addValue("location", namedTreatment.getLocation())
			    		.addValue("comments", namedTreatment.getComments())
			    		.addValue("creaDate", namedTreatment.getCreDate())
			    		.addValue("creUser", namedTreatment.getCreUser())
			    		.addValue("updDate", namedTreatment.getUpdDate())
			    		.addValue("updUser", namedTreatment.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			namedTreatment.setId(getSequence(Constants.STUDY_NAMEDTREATMENT_SEQ_NAME));
			addNamedTreatment(namedTreatment);
			addTransient(namedTreatment);
		}
		addIdToObject(NamedTreatment.class, namedTreatment.getId(), namedTreatment);
		return namedTreatment.getId();
	}

	@Override
	public int addNamedTreatment(NamedTreatment namedTreatment) {
		return super.add(TABLE_NAME, namedTreatment);
	}

	@Override
	public void delete(int namedTreatmentId) {
		super.delete(TABLE_NAME, namedTreatmentId);
	}

}
