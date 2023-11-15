package com.idorsia.research.spirit.core.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiosampleEnclosureDao;
import com.idorsia.research.spirit.core.model.BiosampleEnclosure;

@Repository
public class BiosampleEnclosureDaoImpl extends AbstractDao<BiosampleEnclosure> implements BiosampleEnclosureDao {

	private static final String TABLE_NAME = "BIOSAMPLE_ENCLOSURE";

	@Override
	public BiosampleEnclosure get(Integer id) {
		return super.get(TABLE_NAME, BiosampleEnclosure.class, id);
	}

	@Override
	public List<BiosampleEnclosure> getByBiosample(int biosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID = '%s'",
				biosampleId);
		return (List<BiosampleEnclosure>) super.getObjectList(TABLE_NAME, BiosampleEnclosure.class, sql);
	}

	@Override
	public List<BiosampleEnclosure> list() {
		return super.getlist(TABLE_NAME, BiosampleEnclosure.class);
	}

	@Override
	public List<BiosampleEnclosure> getByEnclosure(int enclosureId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE ENCLOSURE_ID = '%s'",
				enclosureId);
		return (List<BiosampleEnclosure>) super.getObjectList(TABLE_NAME, BiosampleEnclosure.class, sql);
	}

	@Override
	public List<BiosampleEnclosure> getByPhaseIn(int phaseInId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PHASEIN_ID = '%s'",
				phaseInId);
		return (List<BiosampleEnclosure>) super.getObjectList(TABLE_NAME, BiosampleEnclosure.class, sql);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(BiosampleEnclosure biosampleEnclosure) {
		if (biosampleEnclosure.getId() != null && biosampleEnclosure.getId() > 0) {
			if(!biosampleEnclosure.equals(get(biosampleEnclosure.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET " 
					+ "BIOSAMPLE_ID=:biosampleId, " 
					+ "ENCLOSURE_ID=:enclosureId, " 
					+ "PHASEIN_ID=:phaseInId, "
					+ "PHASEOUT_ID=:phaseOutId, " 
					+ "STUDY_ID=:studyId, " 
					+ "UPDUSER=:upduser, "
					+ "UPDDATE=:upddate, "
					+ "CREDATE=:credate, " 
					+ "CREUSER=:creuser " 
					+ "WHERE ID=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", biosampleEnclosure.getId())
						.addValue("biosampleId", biosampleEnclosure.getBiosampleId())
						.addValue("enclosureId", biosampleEnclosure.getEnclosureId())
						.addValue("phaseInId", biosampleEnclosure.getPhaseinId())
						.addValue("phaseOutId", biosampleEnclosure.getPhaseoutId())
						.addValue("studyId", biosampleEnclosure.getStudyId())
						.addValue("upddate", biosampleEnclosure.getUpdDate())
						.addValue("upduser", biosampleEnclosure.getUpdUser())
						.addValue("credate", biosampleEnclosure.getCreDate())
						.addValue("creuser", biosampleEnclosure.getCreUser());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			biosampleEnclosure.setId(getSequence(Constants.BIOSAMPLE_ENCLOSURE_LINK_SEQ_NAME));
			addBiosampleEnclosure(biosampleEnclosure);
			addTransient(biosampleEnclosure);
		}
		addIdToObject(BiosampleEnclosure.class, biosampleEnclosure.getId(), biosampleEnclosure);
		return biosampleEnclosure.getId();
	}

	@Override
	public int addBiosampleEnclosure(BiosampleEnclosure biosampleEnclosure) {
		return super.add(TABLE_NAME, biosampleEnclosure);
	}

	@Override
	public void delete(int biosampleEnclosureId) {
		super.delete(TABLE_NAME, biosampleEnclosureId);
	}

}
