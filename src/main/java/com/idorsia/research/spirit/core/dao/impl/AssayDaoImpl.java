package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.AssayDao;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Assay;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.Triple;

@Repository
public class AssayDaoImpl extends AbstractDao<Assay> implements AssayDao {
	
	private final static String TABLE_NAME = "Assay";
	
	@Override
	public Assay get(Integer id) {
		try {
			return super.get(TABLE_NAME, Assay.class, id);			
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Assay getAssayById(int id) {
		String sql = String.format("SELECT * FROM ASSAY WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, Assay.class, sql);
	}
	
	@Override
	public Assay getAssayByName(String assayName) {
		String sql = String.format("SELECT * FROM ASSAY WHERE NAME = '%s'", assayName);
		return super.getObject(TABLE_NAME, Assay.class, sql);
	}
	
	@Override
	public List<Assay> getAssaysByStudy(Integer studyId) {
		String sql = String.format("SELECT distinct a.* FROM ASSAY a, ASSAY_RESULT ar WHERE "
				+ "ar.ASSAY_ID=a.ID and ar.STUDY_ID= '%s'", studyId);
		return (List<Assay>) super.getObjectList(TABLE_NAME, Assay.class, sql);
	}
	
	@Override
	public List<String> getSendLocalisation() {
		String sql = "select localisation u from send_localisation order by lower(localisation) FETCH FIRST 500 ROWS ONLY";		
		return getJdbcTemplate().query(sql, new ResultSetExtractor<List<String>>() {
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> s = new ArrayList<String>();
				while (rs.next()) {
					s.add(rs.getString(1));
				}
				return s;
			}
		});
	}
	
	@Override
	public List<String[]> getSendObservation() {
		String sql = "select observation_type, observation from send_observation order by lower(observation) FETCH FIRST 500 ROWS ONLY";
		return getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<List<String[]>>() {
			public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String[]> s = new ArrayList<String[]>();
				while (rs.next()) {
					String[] res = rowObservationMap(rs);
					s.add(res);
				}
				return s;
			}
		});
	}
	
	public String[] rowObservationMap(ResultSet rs) {
    	try {
			return new String[] {rs.getString("observation_type"),rs.getString("observation")};
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return null;
	}	
	
	@Override
	public List<Assay> list() {
		return super.getlist("Assay", Assay.class);
	}

	@Override
	public int getCount() {
		return super.getCount("Assay");
	}

	@Override
	public int addAssay(Assay assay) {
		return super.add("Assay", assay);
	}
	
	@Override
	public void delete(int assayId) {
		super.delete("Assay", assayId);
	}

	@Override
	public Integer saveOrUpdate(Assay assay) {
		if (assay.getId() != null && assay.getId() > 0) {
			if(!assay.equals(get(assay.getId()))) {
				String sql = "UPDATE ASSAY SET " 
						+ "category=:category, " 
						+ "description=:description, " 
						+ "upd_date=:upd_date, " 
						+ "upd_user=:upd_user, "
						+ "cre_date=:cre_date, "
						+ "cre_user=:cre_user, "
						+ "name=:name, " 
						+ "disabled=:disabled " 
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", assay.getId())
			    		.addValue("category", assay.getCategory())
			    		.addValue("description", assay.getDescription())
			    		.addValue("upd_date", assay.getUpdDate())
			    		.addValue("upd_user", assay.getUpdUser())
			    		.addValue("cre_date", assay.getCreDate())
			    		.addValue("cre_user", assay.getCreUser())
			    		.addValue("name", assay.getName())
			    		.addValue("disabled", assay.getDisabled());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			assay.setId(getSequence(Constants.ASSAY_SEQUENCE_NAME));
			addAssay(assay);
			addTransient(assay);
		}
		addIdToObject(Assay.class, assay.getId(), assay);
		return assay.getId();
	}
	
	public Assay rowMap(ResultSet rs) {
		Assay assay = null;
    	try {
    		assay = new Assay();
			assay.setId(rs.getInt("id"));
			assay.setCategory(rs.getString("category"));
			assay.setDescription(rs.getString("description"));
			assay.setUpdDate(rs.getDate("upd_date"));
			assay.setUpdUser(rs.getString("upd_user"));
			assay.setCreDate(rs.getDate("cre_date"));
			assay.setCreUser(rs.getString("cre_user"));
			assay.setName(rs.getString("name"));
			assay.setDisabled(rs.getInt("disabled"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return assay;
	}

	@Override
	public Map<String, Map<String, Triple<Integer, String, Date>>> countResultsByStudyAssay(
			Collection<StudyDto> studies) {
		
		StringBuilder query = new StringBuilder();
		query.append(
				"SELECT s.studyid  AS studyid, a.name AS assayname, r.upd_user AS updateuser, COUNT(r.id) AS countres, MAX(r.upd_date) AS maxupdate ");
		query.append("FROM assay_result r CROSS JOIN biosample b CROSS JOIN study s CROSS JOIN assay a ");
		query.append("WHERE r.biosample_id = b.id AND b.study_id = s.id AND r.assay_id = a.id ");
		if (studies != null && !studies.isEmpty()) {
			List<Integer> studyIds = new ArrayList<Integer>();
			for (StudyDto studyDto : studies) {
				studyIds.add(studyDto.getId());
			}
			query.append("AND (b.study_id ").append(DataUtils.fetchInInt(studyIds)).append(") ");
		}
		query.append("GROUP BY s.studyid, a.name, r.upd_user");
		return getJdbcTemplate().query(query.toString(),
				new ResultSetExtractor<Map<String, Map<String, Triple<Integer, String, Date>>>>() {
					public Map<String, Map<String, Triple<Integer, String, Date>>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, Map<String, Triple<Integer, String, Date>>> map = new HashMap<>();
						if (rs.next()) {
							String sid = rs.getString("studyid");
							String key = rs.getString("assayname");
							int n = rs.getInt("countres");
							String user = rs.getString("updateuser");
							Date date = rs.getDate("maxupdate");
							Map<String, Triple<Integer, String, Date>> m = map.get(sid);
							if (m == null) {
								m = new HashMap<>();
								map.put(sid, m);
							}
							if (m.get(key) != null) {
								Triple<Integer, String, Date> e = m.get(key);
								m.put(key,
										new Triple<Integer, String, Date>(e.getFirst() + n,
												e.getThird().after(date) ? e.getSecond() : user,
												e.getThird().after(date) ? e.getThird() : date));
							} else {
								m.put(key, new Triple<Integer, String, Date>(n, user, date));
							}
						}
						return map;
					}
				});
	}

	@Override
	public Set<Assay> getAssaysFromElbs(String elb) {
		Set<Assay> assays = new HashSet<Assay>();
		String sql = String.format("select a.* from assay a, assay_result r where a.id = r.assay_id and r.elb = %s", elb);
		assays.addAll((List<Assay>) super.getObjectList(TABLE_NAME, Assay.class, sql));
		return assays;
	}

	@Override
	public Map<Integer, List<Integer>> getAssayResultMapByStudyAndBiotype(List<StudyDto> studies,
			BiotypeDto forcedBiotype) {
		List<Integer> studyIds = new ArrayList<Integer>();
		if((studies==null || studies.size()==0) && forcedBiotype==null)
			return new HashMap<>();
		if(studies!=null)
			for (StudyDto studyDto : studies) {
				studyIds.add(studyDto.getId());
			}
		StringBuilder query = new StringBuilder("SELECT a.id as assayid, r.id as resultid FROM assay a, assay_result r"+(forcedBiotype != null?" , biosample b, biotype bt ":" "));
		query.append("WHERE a.id = r.assay_id ");
		if(forcedBiotype != null) {			
			query.append("AND b.biotype_id = bt.id AND bt.id = ")
			.append(forcedBiotype.getId()).append(") ");
		}
		if(!studyIds.isEmpty()) {
			query.append("AND r.study_id ").append(DataUtils.fetchInInt(studyIds));
		}
		System.out.println(""+query);
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<Map<Integer, List<Integer>>>() {
			public Map<Integer, List<Integer>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Integer, List<Integer>> assayResultMap = new HashMap<>();
				while (rs.next()) {
					int assayid = rs.getInt("assayid");
					int resultid = rs.getInt("resultid");
					if (assayResultMap.get(assayid) == null) {
						List<Integer> resultIds = new ArrayList<Integer>();
						resultIds.add(resultid);
						assayResultMap.put(assayid, resultIds);
					} else {
						assayResultMap.get(assayid).add(resultid);
					}
				}
				return assayResultMap;
			}
		});
	}
}
