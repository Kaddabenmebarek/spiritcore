package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.business.ppg.formulation.PpgFormulationCompound;
import com.actelion.research.business.ppg.formulation.PpgTreatment;
import com.actelion.research.business.spi.formulation.SpiFormulation;
import com.actelion.research.hts.datacenter.restapi.ppg.DAOPpgTreatment;
import com.actelion.research.hts.datacenter.restapi.spi.DAOSpiFormulation;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssayResultDao;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.view.ResultQuery;
import com.idorsia.research.spirit.core.model.AssayResult;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;

@Repository
public class AssayResultDaoImpl extends AbstractDao<AssayResult> implements AssayResultDao {

	private static final String TABLE_NAME = "ASSAY_RESULT";
	private static final String DATE_FORMAT = "dd/MM/yy hh:mm:ss";

	@Override
	public AssayResult get(Integer id) {
		return super.get(TABLE_NAME, AssayResult.class, id);
	}

	@Override
	public AssayResult getAssayResultById(int id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, AssayResult.class, sql);
	}

	@Override
	public List<AssayResult> getAssayResultsByAssay(int assay_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT WHERE ASSAY_ID = %s", assay_id);
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql);
	}

	@Override
	public List<AssayResult> getAssayResultsByBiosample(int biosample_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT WHERE BIOSAMPLE_ID = %s", biosample_id);
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql);
	}

	@Override
	public List<AssayResult> getAssayResultsByPhase(int phase_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT WHERE PHASE_ID = %s", phase_id);
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql);
	}

	@Override
	public List<AssayResult> getAssayResultsByStudy(int study_id) {
		String sql = String.format("SELECT * FROM ASSAY_RESULT WHERE STUDY_ID = %s", study_id);
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql);
	}

	@Override
	public List<AssayResult> getAssayResultsByComments(String comments) {
		String sql = "SELECT * FROM ASSAY_RESULT WHERE COMMENTS LIKE %" + comments;
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql);
	}

	public List<AssayResult> getAssayResultsByIds(Collection<Integer> ids) {
		StringBuilder sql = new StringBuilder(
				"select * from ASSAY_RESULT where id ");
		sql.append(DataUtils.fetchInInt(ids));
		return (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, sql.toString());
	}
	
	public List<AssayResult> getByValueAndAttributeId(String value, Integer id){
		String sql = String.format("select * from ASSAY_RESULT ar, ASSAY_RESULT_VALUE arv where arv.value = '%s' and "
				+ "arv.assay_attribute_id = %s and arv.assay_result_id=ar.id", value, id);
		return getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<List<AssayResult>>() {
			public List<AssayResult> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<AssayResult> s = new ArrayList<AssayResult>();
				while (rs.next()) {
					AssayResult ar = rowMap(rs);
					s.add(ar);
					addObjectInCache(ar, AssayResult.class, ar.getId());
				}
				return s;
			}
		});
	}

	@Override
	public List<AssayResult> list() {
		return super.getlist(TABLE_NAME, AssayResult.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addAssayResult(AssayResult assayResult) {
		return super.add(TABLE_NAME, assayResult);
	}

	@Override
	public void delete(int assayResultId) {
		super.delete(TABLE_NAME, assayResultId);
	}

	@Override
	public Integer saveOrUpdate(AssayResult assayResult) {
		if (assayResult.getId() != null && assayResult.getId() > 0) {
			if(!assayResult.equals(get(assayResult.getId()))) {
				String sql = "UPDATE ASSAY_RESULT SET comments=:comments, " + "elb=:elb, " + "upd_date=:upd_date, "
						+ "upd_user=:upd_user, " + "cre_date=:cre_date, " + "cre_user=:cre_user, " + "quality=:quality, "
						+ "biosample_id=:biosample_id, " + "phase_id=:phase_id, " + "assay_id=:assay_id, "
						+ "study_id=:study_id, " + "execution_date=:execution_date " + "WHERE id=:id";
				SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", assayResult.getId())
						.addValue("comments", assayResult.getComments()).addValue("elb", assayResult.getElb())
						.addValue("upd_date", assayResult.getUpdDate()).addValue("upd_user", assayResult.getUpdUser())
						.addValue("cre_date", assayResult.getCreDate()).addValue("cre_user", assayResult.getCreUser())
						.addValue("quality", assayResult.getQuality())
						.addValue("biosample_id", assayResult.getBiosampleId())
						.addValue("phase_id", assayResult.getPhaseId()).addValue("assay_id", assayResult.getAssayId())
						.addValue("study_id", assayResult.getStudyId())
						.addValue("execution_date", assayResult.getExecutionDate());
				super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assayResult.setId(getSequence(Constants.ASSAY_RESULT_SEQUENCE_NAME));
			addAssayResult(assayResult);
			addTransient(assayResult);
		}
		addIdToObject(AssayResult.class, assayResult.getId(), assayResult);
		return assayResult.getId();
	}

	public AssayResult rowMap(ResultSet rs) {
		AssayResult assayResult = null;
		try {
			assayResult = new AssayResult();
			assayResult.setId(rs.getInt("id"));
			assayResult.setComments(rs.getString("comments"));
			assayResult.setElb(rs.getString("elb"));
			assayResult.setUpdDate(rs.getDate("upd_date"));
			assayResult.setUpdUser(rs.getString("upd_user"));
			assayResult.setCreDate(rs.getDate("cre_date"));
			assayResult.setCreUser(rs.getString("cre_user"));
			assayResult.setQuality(rs.getInt("quality"));
			assayResult.setBiosampleId(rs.getInt("biosample_id"));
			assayResult.setPhaseId(rs.getInt("phase_id"));
			assayResult.setAssayId(rs.getInt("assay_id"));
			assayResult.setStudyId(rs.getInt("study_id"));
			assayResult.setExecutionDate(rs.getTimestamp("execution_date"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return assayResult;
	}

	@Override
	public List<AssayResult> getResults(ResultQuery query) throws Exception {
		
		StringBuilder clause = new StringBuilder();

		if (query.getBids().size() > 0) {
			// clause.append(" and " + QueryTokenizer.expandForIn("b.id", query.getBids()));
			Collection<Integer> bIds = query.getBids();
			if (query.getBids().size() > 1000) { // prevent ORA-01795: maximum number of expressions in a list
				bIds = query.getBids().stream().limit(999).collect(Collectors.toList());
			}
			clause.append(" and b.id ").append(DataUtils.fetchInInt(bIds));
		}
		if (query.getPhase() != null) {
			clause.append(" and r.phase_id = " + query.getPhase().getId());
		}
		if (query.getSid() > 0) {
			clause.append(" and r.study_id = " + query.getSid());
		}
		if (query.getStudyIds() != null && query.getStudyIds().equalsIgnoreCase("NONE")) {
			clause.append(" and r.study_id is null");
		} else if (query.getStudyIds() != null && query.getStudyIds().length() > 0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("r.study_id = ?", query.getStudyIds()) + ")");
			clause.append(" and s.studyId= '").append(query.getStudyIds()).append("'");
		}

		if (query.getSids() != null && query.getSids().size() > 0) {
			//clause.append(" and " + QueryTokenizer.expandForIn("r.study_id", query.getSids()));
			clause.append(" and r.study_id ").append(DataUtils.fetchInInt(query.getSids()));
		}

		if (query.getSubgroupId() != null && query.getGroups().length() > 0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("as1.subgroup_id = ?", query.getSubgroupId()) + ")");
			clause.append(" and as1.subgroup_id = ").append(query.getSubgroupId());
		}
		
		if (query.getSampleIds() != null && query.getSampleIds().length() > 0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("b.sampleid = ?", query.getSampleIds()) + ")");
			clause.append(" and b.sampleid = '").append(query.getSampleIds()).append("'");
		}

		if (query.getBiosampleParentId() != null && query.getTopSampleIds().length() > 0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("b.parent_id = ?", query.getBiosampleParentId()) + ")");
			clause.append(" and b.parent_id = ").append(query.getBiosampleParentId());
		}
		
		if(query.getTopSampleIds()!=null && query.getTopSampleIds().length()>0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("b.topParent.sampleId = ?", query.getTopSampleIds()) + ")");
			clause.append(" and (b.parent_id in ").append("(select id from biosample where sampleid = '")
					.append(query.getTopSampleIds()).append("') or b.sampleid = '").append(query.getTopSampleIds())
					.append("')");	
		}

		if (query.getContainerIds() != null && query.getContainerIds().length() > 0) {
			//clause.append(" and ("+ QueryTokenizer.expandOrQuery("b.containerid = ?", query.getContainerIds()) + ")");
			clause.append(" and b.containerid = ").append(query.getContainerIds());
		}

		if (query.getElbs() != null && query.getElbs().length() > 0) {
			//clause.append(" and (" + QueryTokenizer.expandOrQuery("r.elb = ?", query.getElbs()) + ")");
			clause.append(" and r.elb = '").append(query.getElbs()).append("'");
		}

		if (query.getQuality() != null) {
			if (query.getQuality().getId() <= Quality.VALID.getId()) {
				clause.append(" and (r.quality is null or r.quality >= " + query.getQuality().getId() + ")");
			} else {
				clause.append(" and r.quality >= " + query.getQuality().getId());
			}
		}

		if (query.getAssayIds() != null && query.getAssayIds().size() >= 1) {
			clause.append(" and (1=0");
			for (int testId : query.getAssayIds()) {
				clause.append(" or (r.assay_id = " + testId);
				for (AssayAttributeDto att : query.getAttribute2Values().keySet()) {
					if (att.getAssay().getId() != testId)
						continue;
					Set<String> attVal = query.getAttribute2Values().get(att);
					if (attVal == null || attVal.size() == 0)
						continue;

					StringBuilder sb = new StringBuilder();
					for (String s : attVal) {
						if (sb.length() > 0)
							sb.append(" or ");
						if (s == null || s.length() == 0) {
							sb.append("v.text_value is null");
						} else {
							sb.append("v.text_value = '" + (s.replace("'", "''")) + "'");
						}
					}
					clause.append(" and (r.id IN (SELECT v.assay_result_id FROM Assay_Result_Value v WHERE v.assay_attribute_id = " + att.getId()
							+ " and (" + sb + ")))");
				}
				clause.append(")");
			}
			clause.append(")");
		}

		if (query.getBiosampleId() != null && query.getBiotype().length() > 0) {
			//clause.append(" and " + QueryTokenizer.expandForIn("r.biosample_id", query.getBiosampleId()));
			clause.append(" and r.biosample_id = ").append(query.getBiosampleId());
		}

		if (query.getUpdUser() != null && query.getUpdUser().length() > 0) {
			//clause.append(" and r.upd_user = ").append(query.getUpdUser());
			clause.append(" and r.upd_user = '").append(query.getUpdUser()).append("'");
		}
		if (query.getUpdDate() != null && query.getUpdDate().length() > 0) {
			String digits = MiscUtils.extractStartDigits(query.getUpdDate());
			if (digits.length() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
				//clause.append(" and r.upd_date > ").append(cal.getTime());				
				String targetMinDate = new SimpleDateFormat(DATE_FORMAT).format(cal.getTime());
				clause.append(" and r.upd_date > to_timestamp('").append(targetMinDate).append("','DD/MM/RR HH.MI.SS')");
			}
		}
		if (query.getCreDays() != null && query.getCreDays().length() > 0) {
			String digits = MiscUtils.extractStartDigits(query.getCreDays());
			if (digits.length() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
				//clause.append(" and r.tracing.creDate > ").append(cal.getTime());
				String targetMinDate = new SimpleDateFormat(DATE_FORMAT).format(cal.getTime());
				clause.append(" and r.cre_date > to_timestamp('").append(targetMinDate).append("','DD-MON-RR HH.MI.SS')");
			}
		}

		if (query.getBiotypes() != null && query.getBiotypes().size() > 0) {
//			clause.append(" and (");
//			boolean first = true;
//			for (String s : query.getBiotypes()) {
//				if (first)
//					first = false;
//				else
//					clause.append(" or ");
//				clause.append(" r.biosample.biotype.name = ").append(s);
//			}
//			clause.append(")");
			clause.append(
					" and r.biosample_id in (select bs.id from biosample bs, biotype bt where bs.biotype_id = bt.id and bt.name ")
					.append(DataUtils.fetchInClause(query.getBiotypes())).append(")");
		}

		if (query.getInputs() != null && query.getInputs().size() > 0) {
			clause.append(" and r.id IN (SELECT v.assay_result_id FROM assay_result_value v, assay_attibute aa WHERE v.assay_attribute_id = aa.id and aa.isOutput <> 'OUTPUT' and (");
			boolean first = true;
			for (String s : query.getInputs()) {
				if (first)
					first = false;
				else
					clause.append(" or ");
				clause.append(" v.text_value = ").append(s);
			}
			clause.append(")) ");
		}

		if (query.getKeywords() != null && query.getKeywords().length() > 0) {

			StringBuilder expr = new StringBuilder();
			expr.append(" 0=1");
			expr.append(" or b.sampleid like ?");
			expr.append(
					" or replace(replace(replace(replace(replace(replace(replace(lower(b.localid), '.', ''), ' ', ''), '-', ''), '_', ''), '/', ''), ':', ''), '#', '') like replace(replace(replace(replace(replace(replace(replace(lower(?), '.', ''), ' ', ''), '-', ''), '_', ''), '/', ''), ':', ''), '#', '')");
			expr.append(
					" or r.id IN (SELECT v.assay_result_id FROM Assay_Result_Value v WHERE replace(replace(replace(replace(replace(replace(replace(lower(v.text_value), '.', ''), ' ', ''), '-', ''), '_', ''), '/', ''), ':', ''), '#', '') like replace(replace(replace(replace(replace(replace(replace(lower(?), '.', ''), ' ', ''), '-', ''), '_', ''), '/', ''), ':', ''), '#', ''))");
			expr.append(" or (r.assay_id IN (SELECT t.id from Assay t WHERE LOWER(t.name) like LOWER(?)))");
			expr.append(" or r.elb like ?");
			expr.append(" or LOWER(b.name) like LOWER(?)");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2 WHERE LOWER(b2.study_Id) like LOWER(?)))");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, study s WHERE b2.study_id = s.id and LOWER(s.ivv) like LOWER(?)))");
			expr.append(" or (b.id IN (SELECT b2.id FROM Biosample b2, biotype bt WHERE b.biotype_id = bt.id and LOWER(bt.name) like LOWER(?)))");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, assignment as2, subgroup sg1, study_group g1 WHERE as2.biosample_id = n2.id and as2.subgroup_id = sg1.id and sg1.group_id = g1.id and LOWER(g1.name) like LOWER(?)))");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, phase ph WHERE b2.inheritedphase_id=ph.id and LOWER(ph.label) like LOWER(?)))");// label
																															// seems
																															// never
																															// used
																															// but
																															// could
																															// be
																															// a
																															// future
																															// feature
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, Biosample parent WHERE b2.parent_id = parent.id and LOWER(parent.sampleid) like LOWER(?)))");
			expr.append(" or (b.id IN (SELECT b2.id FROM Biosample b2, Biosample parent WHERE b2.parent_id = parent.id and LOWER(parent.localid) like LOWER(?)))");
			expr.append(" or (b.id IN (SELECT b2.id FROM Biosample b2, Biolocation loc WHERE b2.location_id = loc.id and LOWER(loc.name) like LOWER(?)))");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, biotype_metadata_biosample bmb WHERE b2.id = bmb.biosample_id and LOWER(bmb.value) like LOWER(?)))");
			expr.append(
					" or (b.id IN (SELECT b2.id FROM Biosample b2, Biosample parent, biotype_metadata_biosample bmb WHERE b.parent_id = parent.id and parent.id = bmb.biosample_id and LOWER(bmb.value) like LOWER(?)))");
//			expr.append(
//					" or (b.id IN (SELECT b2.id FROM Biosample b2 JOIN b2.linkedBiosamples b3 WHERE LOWER(b3.serializedMetadata) like LOWER(?)))");
			expr.append(" or LOWER(b.comments) like LOWER(?)");
			expr.append(" or LOWER(b.creuser) like LOWER(?)");
			expr.append(" or LOWER(b.upduser) like LOWER(?)");

			clause.append(
					" and (" + QueryTokenizer.expandQuery(expr.toString(), query.getKeywords(), true, true) + ")");
		}

		if (query.getCompoundIds() != null && query.getCompoundIds().length() > 0) {
			clause.append(" and (r.study_id in " + "(select nt.study_id from NamedTreatment nt, Treatment tmnt where nt.named_treatment_id = tmnt.id and "
					+ QueryTokenizer.expandQuery("tmnt.compound LIKE ?", query.getCompoundIds(), false,
							true)
					+ " or " + QueryTokenizer.expandQuery("tmnt.compound2 LIKE ?", query.getCompoundIds(),
							false, true)
					+ ") ");
			Set<Integer> ppgTreatmentInstanceIds = extractPpgTreatments(query.getCompoundIds());
			clause.append(" or r.study_id in " + "(select nt.study_id from NamedTreatment nt where "
					+ QueryTokenizer.expandForIn("nt.ppg_Treatment_Instance_Id", ppgTreatmentInstanceIds) + ")");
			Set<Integer> spiTreatmentInstanceIds = extractSpiTreatments(query.getCompoundIds());
			clause.append(" or r.study_id in " + "(select nt.study_id from NamedTreatment nt where "
					+ QueryTokenizer.expandForIn("nt.spi_Treatment_Id", spiTreatmentInstanceIds) + "))");
		}

		String jpql = "select r.* from assay_result r left outer join biosample b on r.biosample_id=b.id ";

		if (query.getStudyIds() != null)
			jpql += "join study s on r.study_id=s.id ";
		
		if (query.getGroups() != null)
			jpql += "join assignment as1 on as1.biosample_id = b.id ";

		if (clause.length() > 0) {
			assert clause.subSequence(0, 4).equals(" and") : "clause == '" + clause + "'";
			jpql += " where" + clause.substring(4);
		}

		jpql = DataUtils.makeQueryJPLCompatible(jpql);

		List<AssayResult> results = (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, jpql);
		
		return results;
	}
	
	
	private static Set<Integer> extractPpgTreatments(String compoundIds) throws Exception {
		Set<Integer> ppgTreatmentInstanceId = new HashSet<>();
		String[] cmpds = QueryTokenizer.tokenize(compoundIds, "\t\n,; ");
		for(String cmpd:cmpds) {
			PpgTreatment treatmentCmpd = new PpgTreatment();
			List<PpgFormulationCompound> formuCmpds = new ArrayList<>();
			PpgFormulationCompound formulationCompound = new PpgFormulationCompound();
			formulationCompound.setActNumber(cmpd);
			formuCmpds.add(formulationCompound);
			treatmentCmpd.setCompounds(formuCmpds);
			ppgTreatmentInstanceId.addAll(getTreatmentInstanceIds(treatmentCmpd));
		}
		return ppgTreatmentInstanceId;
	}
	
	private static Set<Integer> extractSpiTreatments(String compoundIds) throws Exception {
		Set<Integer> spiTreatmentInstanceId = new HashSet<>();
		String[] cmpds = QueryTokenizer.tokenize(compoundIds, "\t\n,; ");
		for(String cmpd:cmpds) {
			List<SpiFormulation> formulations = DAOSpiFormulation.getFormulations(cmpd);
			for(SpiFormulation spiFormulation : formulations) {
				spiTreatmentInstanceId.add(spiFormulation.getId());
			}
		}
		return spiTreatmentInstanceId;
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set<Integer> getTreatmentInstanceIds(PpgTreatment ppgTreatment) throws Exception {
        Set<Integer> treatmentInstanceIds = new HashSet<>();
        boolean enoughCriteria = true;
        List<String> criterias = new ArrayList();
                for (PpgFormulationCompound compound : ppgTreatment.getCompounds()) {
                    if (compound.getActNumber() != null) {
                        criterias.add("act_no="+compound.getActNumber());
                        enoughCriteria = true;
                        break;
                    }
                }
        if (enoughCriteria) {
            try {
                List<PpgTreatment> treatments = DAOPpgTreatment.getTreatments(ppgTreatment);
                for (PpgTreatment treatment : treatments) {
                    treatmentInstanceIds.add(treatment.getTreatmentInstanceId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return treatmentInstanceIds;
    }

	@Override
	public List<String> getElbsForStudy(String studyId) {
		List<String> elbs = new ArrayList<String>();
		String query = String.format("select r.* from Assay_Result r, Study s where r.study_id = s.id and s.studyid = '%s'", studyId);
		List<AssayResult> results = (List<AssayResult>) super.getObjectList(TABLE_NAME, AssayResult.class, query);
		for(AssayResult result : results) {
			elbs.add(result.getElb());
		}
		return elbs;
	}

	@Override
	public List<String> getResultsByStudyAndAttribute(Integer studyId, Integer attributeId) {
		String query = String.format(
				"select rv.text_value from ASSAY_RESULT_VALUE rv, ASSAY_RESULT r where rv.assay_result_id = r.id and r.study_id = %s and rv.assay_attribute_id = %s",
				studyId, attributeId);
		return getJdbcTemplate().query(query, BeanPropertyRowMapper.newInstance(String.class));
	}
	
	@Override
	public void delete(List<Integer> ids) {
		List<Integer> idsTemp = new ArrayList<>();
		int i=0;
		int j=0;
		while(ids.size()>i) {
			while(j%1000<999 && ids.size()>i) {
				Integer id = ids.get(i);
				if(id!=Constants.NEWTRANSIENTID) {
					idsTemp.add(id);
					j++;
				}
				i++;
			}
			j=0;
			if(idsTemp.size()>0) {
				super.getJdbcTemplate().update("DELETE from " + TABLE_NAME + " b WHERE ID"+DataUtils.fetchInInt(new ArrayList<Integer>(idsTemp)));
			}
			idsTemp.clear();
		}
	}
}
