package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.util.services.QueryTokenizer;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AdministrationDao;
import com.idorsia.research.spirit.core.model.Administration;
import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.service.PhaseService;

@Repository
public class AdministrationDaoImpl extends AbstractDao<Administration> implements AdministrationDao {

	private static final String TABLE_NAME = "administration";
	
	@Autowired
	PhaseService phaseService;

	@Override
	public Administration get(Integer id) {
		return super.get(TABLE_NAME, Administration.class, id);
	}

	@Override
	public List<Administration> list() {
		return super.getlist(TABLE_NAME, Administration.class);
	}

	@Override
	public List<Administration> getByNamedTreatment(int namedTreatmentId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE NAMED_TREATMENT_ID = '%s'",
				namedTreatmentId);
		return (List<Administration>) super.getObjectList(TABLE_NAME, Administration.class, sql);
	}

	@Override
	public List<Administration> getByBiosample(int biosampleId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE BIOSAMPLE_ID = '%s'", biosampleId);
		return (List<Administration>) super.getObjectList(TABLE_NAME, Administration.class, sql);
	}

	@Override
	public List<Administration> getByPhase(int phaseId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PHASE_ID = '%s'", phaseId);
		return (List<Administration>) super.getObjectList(TABLE_NAME, Administration.class, sql);
	}
	
	@Override
	public List<Administration> getByStudyId(Integer studyId) {
		String sql = String.format("SELECT a.* FROM ADMINISTRATION a, PHASE p, STAGE s"
				+ " WHERE a.phase_id=p.id and p.stage_id = s.id and s.STUDY_ID= '%s'",studyId);
		return (List<Administration>) super.getObjectList(TABLE_NAME, Administration.class, sql);
	}
	
	@Override
	public List<Administration> getByBiosamplesAndPhase(List<Integer> biosamples, Integer phaseId) {
	    List<Administration> results = new ArrayList<>();
		String sql = "select * from ADMINISTRATION where " + QueryTokenizer.expandForIn("biosample_id", biosamples) + (phaseId == null ? "" : (" and phase_id =" + phaseId));
	    if (biosamples.size() > 0) {
	    	return (List<Administration>) super.getObjectList(TABLE_NAME, Administration.class, sql);
	    }
	    return results;
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addAdministration(Administration administration) {
		return super.add(TABLE_NAME, administration);
	}
	
	@Override
	public Integer saveOrUpdate(Administration administration) {
		if (administration.getId() != null && administration.getId() > 0) {
			if(!administration.equals(get(administration.getId()))) {
				String sql = "UPDATE " + TABLE_NAME + " SET "
						+ "NAMED_TREATMENT_ID=:namedTreatmentId, "
						+ "BIOSAMPLE_ID=:biosampleId, " 
						+ "PHASE_ID=:phaseId, " 
						+ "BATCH_ID=:batchId, " 
						+ "BATCH_TYPE=:batchType, "
						+ "EFFECTIVE_AMOUNT_VALUE=:effectiveAmountValue, "
						+ "EFFECTIVE_AMOUNT_UNIT_ID=:effectiveAmountUnitId, " 
						+ "EXECUTION_DATE=:executionDate, "
						+ "ELB=:elb, " 
						+ "ADM_COMMENT=:admComment, " 
						+ "CREDATE=:creDate, " 
						+ "CREUSER=:creUser, "
						+ "UPDDATE=:updDate, " 
						+ "UPDUSER=:updUser " 
						+ "WHERE id=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", administration.getId())
						.addValue("namedTreatmentId", administration.getNamedTreatmentId())
						.addValue("biosampleId", administration.getBiosampleId())
						.addValue("phaseId", administration.getPhaseId())
						.addValue("batchId", administration.getBatchId())
						.addValue("batchType", administration.getBatchType())
						.addValue("effectiveAmountValue", administration.getEffectiveamount())
						.addValue("effectiveAmountUnitId", administration.getEffectiveamountunitId())
						.addValue("executionDate", administration.getExecutiondate())
						.addValue("elb", administration.getElb()).addValue("admComment", administration.getAdmComment())
						.addValue("creDate", administration.getCreDate()).addValue("creUser", administration.getCreUser())
						.addValue("updDate", administration.getUpdDate()).addValue("updUser", administration.getUpdUser());
				;
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			administration.setId(getSequence(Constants.STUDY_ADMINISTRATION_SEQ_NAME));
			addAdministration(administration);
			addTransient(administration);
		}
		addIdToObject(Administration.class, administration.getId(), administration);
		return administration.getId();
	}
	
	public Administration rowMap(ResultSet rs) {
		Administration administration = null;
    	try {
    		administration = new Administration();
    		administration.setId(rs.getInt("id"));
    		administration.setNamedTreatmentId(rs.getInt("NAMED_TREATMENT_ID"));
			administration.setBiosampleId(rs.getInt("BIOSAMPLE_ID"));
			administration.setPhaseId(rs.getInt("PHASE_ID"));
			administration.setBatchId(rs.getString("BATCH_ID"));
			administration.setBatchType(rs.getString("BATCH_TYPE"));
			administration.setEffectiveamount(rs.getFloat("EFFECTIVE_AMOUNT_VALUE"));
			administration.setEffectiveamountunitId(rs.getInt("EFFECTIVE_AMOUNT_UNIT_ID"));
			administration.setExecutiondate(rs.getDate("EXECUTION_DATE"));
    		administration.setElb(rs.getString("ELB"));
    		administration.setAdmComment(rs.getString("ADM_COMMENT"));
    		administration.setUpdDate(rs.getDate("UPDDATE"));
    		administration.setUpdUser(rs.getString("UPDUSER"));
    		administration.setCreDate(rs.getDate("CREDATE"));
    		administration.setCreUser(rs.getString("CREUSER"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return administration;
	}

	@Override
	public void delete(int administrationId) {
		super.delete(TABLE_NAME, administrationId);
	}

	@Override
	public void deleteAllByStudy(int studyId) {
		Set<Integer> administrations= new HashSet<>();
		for(Phase phase : phaseService.getPhases(studyId)) {
			getByPhase(phase.getId()).forEach(a->administrations.add(a.getId()));
		}
		super.deleteAll(TABLE_NAME, administrations);
	}

}
