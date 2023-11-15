package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.StudyDao;
import com.idorsia.research.spirit.core.dto.view.StudyQuery;
import com.idorsia.research.spirit.core.model.Study;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;
import com.idorsia.research.spirit.core.util.SpiritRights;

@Repository
public class StudyDaoImpl extends AbstractDao<Study> implements StudyDao {
	
	private static final String TABLE_NAME = "Study";
	private static final String DATE_FORMAT = "dd-MMM-yy hh:mm:ss";
	private static final String DATE_FORMAT2 = "dd/MM/yy hh:mm:ss";
	
	@Autowired
	private SpiritRights spiritRights; 
	
	@Override
	public Study get(Integer id) {
		return super.get(TABLE_NAME, Study.class, id);
	}

	@Override
	public Study getStudyById(int id) {
		String sql = String.format("SELECT * FROM STUDY WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, Study.class, sql);
	}

	public List<Study> getRecentStudies(User user){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, user==null? -365: -365*5);
		
		String targetDate = new SimpleDateFormat(DATE_FORMAT2).format(cal.getTime());		
		String sql = String.format("SELECT * FROM STUDY WHERE CREDATE > to_timestamp('%s','DD/MM/RR HH.MI.SS')", targetDate);
		
		return (List<Study>) super.getObjectList(TABLE_NAME, Study.class, sql);
	}
	
	@Override
	public Study getStudyByStudyId(String studyId) {
		String sql = String.format("SELECT * FROM STUDY WHERE STUDYID = '%s'", studyId);
		return super.getObject(TABLE_NAME, Study.class, sql);
	}
	
	@Override
	public List<String> getStudyIds() {
		List<String> studyIds = new ArrayList<String>();
		String sql = "select studyid from "+TABLE_NAME+" order by id desc";
		for(Study study : super.getObjectList(TABLE_NAME, Study.class, sql)) {
			studyIds.add(study.getStudyId());
		}
		return studyIds;
	}
	
	@Override
	public List<Study> list() {
		List<Study> studies = super.getlist(TABLE_NAME,Study.class);
		Collections.sort(studies, new Comparator<Study>() {
			@Override
			public int compare(Study o1, Study o2) {
				return o2.getStudyId().compareTo(o1.getStudyId());
			}
		});
		return studies;
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public int addStudy(Study study) {
		return super.add(TABLE_NAME, study);
	}
	
	@Override
	public void delete(int studyId) {
		super.delete(TABLE_NAME, studyId);
	}

	@Override
	public Integer saveOrUpdate(Study study) {
		if (study.getId() != null && study.getId() > 0) {
			if(!study.equals(get(study.getId()))) {
				String sql = "UPDATE STUDY SET comments=:comments, " 
						+ "credate=:credate, " 
						+ "ivv=:ivv, " 
						+ "description=:description, "
						+ "project=:project, " 
						+ "write_users=:write_users, " 
						+ "upddate=:upddate, " 
						+ "upduser=:upduser, " 
						+ "creuser=:creuser, "
						+ "read_users=:read_users, " 
						+ "studyid=:studyid, " 
						+ "status=:status, " 
						+ "department_id=:department_id, " 
						+ "rnd_experimenter=:rnd_experimenter, " 
						+ "blind_users=:blind_users, " 
						+ "department2_id=:department2_id, "
						+ "department3_id=:department3_id, " 
						+ "synchrosamples=:synchrosamples, " 
						+ "owner=:owner, " 
						+ "studytype=:studytype, " 
						+ "elb=:elb " 
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", study.getId())
			    		.addValue("comments", study.getComments())
			    		.addValue("credate", study.getCreDate())
			    		.addValue("ivv", study.getIvv())
			    		.addValue("description", study.getDescription())
			    		.addValue("project", study.getProject())
			    		.addValue("write_users", study.getWriteUsers())
			    		.addValue("upddate", study.getUpdDate())
			    		.addValue("upduser", study.getUpdUser())
			    		.addValue("creuser", study.getCreUser())
			    		.addValue("read_users", study.getReadUsers())
			    		.addValue("studyid", study.getStudyId())
			    		.addValue("status", study.getStatus())
			    		.addValue("department_id", study.getDepartmentId())
			    		.addValue("rnd_experimenter", study.getRndExperimenter())
			    		.addValue("blind_users", study.getBlindUsers())
			    		.addValue("department2_id", study.getDepartment2Id())
			    		.addValue("department3_id", study.getDepartment3Id())
			    		.addValue("synchrosamples", study.getSynchroSamples())
			    		.addValue("owner", study.getOwner())
			    		.addValue("studytype", study.getStudyType())
			    		.addValue("elb", study.getElb());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			study.setId(getSequence(Constants.STUDY_SEQUENCE_NAME));
			addStudy(study);
			addTransient(study);
		}
		addIdToObject(Study.class, study.getId(), study);
		return study.getId();
	}
	
	@Override
	public void changeOwner(Study study, String updUser, String creUser) {
		study.setUpdUser(updUser);
		study.setCreUser(creUser);
		saveOrUpdate(study);
	}
	
	public Study rowMap(ResultSet rs) {
		Study study = null;
    	try {
    		study = new Study();
			study.setId(rs.getInt("id"));
			study.setComments(rs.getString("comments"));
			study.setCreDate(rs.getDate("creDate"));
			study.setIvv(rs.getString("ivv"));
			study.setDescription(rs.getString("description"));
			study.setProject(rs.getString("project"));
			study.setWriteUsers(rs.getString("write_users"));
			study.setUpdDate(rs.getDate("updDate"));
			study.setUpdUser(rs.getString("updUser"));
			study.setCreUser(rs.getString("creUser"));
			study.setReadUsers(rs.getString("read_users"));
			study.setStudyId(rs.getString("studyId"));
			study.setStatus(rs.getString("status"));
			study.setDepartmentId(rs.getInt("department_Id"));
			study.setRndExperimenter(rs.getString("rnd_Experimenter"));
			study.setBlindUsers(rs.getString("blind_Users"));
			study.setDepartment2Id(rs.getInt("department2_Id"));
			study.setDepartment3Id(rs.getInt("department3_Id"));
			study.setSynchroSamples(rs.getInt("synchroSamples"));
			study.setOwner(rs.getString("owner"));
			study.setStudyType(rs.getString("studyType"));
			study.setElb(rs.getString("elb"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return study;
	}

	@Override
	public List<Study> queryStudies(StudyQuery q, User user) throws Exception{
		assert q!=null;
		long s = System.currentTimeMillis();

		String jpql = "SELECT s.* FROM Study s where 1=1 ";
		StringBuilder clause = new StringBuilder();
		List<Object> parameters = new ArrayList<>();

		if(q.getStudyIds()!=null && q.getStudyIds().length()>0) {
			clause.append(" and (" + QueryTokenizer.expandOrQuery("s.STUDYID = ?", q.getStudyIds()) + ")");
		}

		if(q.getLocalIds()!=null && q.getLocalIds().length()>0) {
			clause.append(" and (" + QueryTokenizer.expandOrQuery("s.IVV = ?", q.getLocalIds()) + ")");
		}

		if(q.getKeywords()!=null && q.getKeywords().length()>0) {
			String expr = "lower(s.STUDYID) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.IVV) like lower('"+q.getKeywords()+"')" +
					" or lower(s.WRITE_USERS) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.READ_USERS) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.BLIND_USERS) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.STATUS) like lower('"+q.getKeywords()+"')" +
					" or lower(s.CREUSER) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.UPDUSER) like lower('%"+q.getKeywords()+"%')" +
					" or lower(s.DESCRIPTION) like lower('%"+q.getKeywords()+"%')" +
					" or s.id in (select distinct nt.STUDY_ID from NamedTreatment nt where lower(nt.name) like lower('%"+q.getKeywords()+"%') or "
						+ "nt.ppg_treatment_instance_id in (select distinct ti.id as treat_inst_id from OSIRIS.treatment_instance ti, "
						+ "OSIRIS.TREATMENT t, OSIRIS.FORMULATION f, OSIRIS.compound_recipe r, OSIRIS.FORMU_COMPOUND fc "
						+ "where ti.TREATMENT_ID = t.ID and t.FORMULATION_ID = f.ID and f.ID = r.FORMULATION_ID and r.FORMU_CMPD_ID = fc.ID and lower(fc.ACT_No) like lower('%"+q.getKeywords()+"%')) or "
						+ "nt.SPI_TREATMENT_ID in (select formulation_id from osiris.spi_formu_cmpd where active_cmpd_id in (select id from osiris.spi_active_cmpd "
						+ "where lower(name) like lower ('%"+q.getKeywords()+"%'))))";
			clause.append(" and (" + QueryTokenizer.expandQuery(expr, q.getKeywords(), true, true) + ")");
		}

		if(q.getState()!=null && q.getState().length()>0) {
			clause.append(" and s.state = "+q.getState());
			parameters.add(q.getState());
		}

		if(q.getType()!=null && q.getType().length()>0) {
			clause.append(" and s.STUDYTYPE = " + q.getType());
			parameters.add(q.getType());
		}
		if(q.getMetadataMap().size()>0) {
			for (Map.Entry<String, String> e : q.getMetadataMap().entrySet()) {
				if(e.getValue().length()>0) {
					clause.append(" and s.id in (select pl.study_id from property_link pl, property p where lower(p.name)='"+e.getKey().toLowerCase()+"' and lower(pl.value)='"+e.getValue().toLowerCase()+"')");
					parameters.add("%" + e.getValue() + "%");
				}
			}
		}
		if(q.getUser()!=null && q.getUser().length()>0) {
			clause.append(" and (lower(s.WRITE_USERS) like lower("+q.getUser()+") or lower(s.READ_USERS) like lower("+q.getUser()+") or lower(s.CREUSER) like lower("+q.getUser()+"))");
			parameters.add("%"+q.getUser()+"%");
			parameters.add("%"+q.getUser()+"%");
			parameters.add("%"+q.getUser()+"%");
		}


		if(q.getUpdDays()!=null && q.getUpdDays().length()>0) {
			String digits = MiscUtils.extractStartDigits(q.getUpdDays());
			if(digits.length()>0) {
				try {
					String targetDate = new SimpleDateFormat(DATE_FORMAT).format(q.getUpdDays());
					clause.append(" and s.UPDDATE > to_timestamp('"+targetDate+"','DD-MON-RR HH.MI.SS')");
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
					parameters.add(cal.getTime());
				} catch (Exception e) {
				}
			}
		}
		if(q.getCreDays()!=null && q.getCreDays().length()>0) {
			String digits = MiscUtils.extractStartDigits(q.getCreDays());
			if(digits.length()>0) {
				try {
					clause.append(" and s.CREDATE > " + q.getCreDays());
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
					parameters.add(cal.getTime());
				} catch (Exception e) {
				}
			}
		}
		if(clause.length()>0) jpql += clause.toString();
		
		List<Study> studies = (List<Study>) getObjectList(TABLE_NAME, Study.class, jpql); 
		
		Collections.sort(studies, (o1,o2) -> -o1.getCreDate().compareTo(o2.getCreDate()));
		//Post Filter according to user rights
		if(user!=null) {
			for (Iterator<Study> iterator = studies.iterator(); iterator.hasNext();) {
				Study study = iterator.next();
				if(!spiritRights.canRead(study, user)) iterator.remove();
			}
		}
		LoggerFactory.getLogger(StudyDaoImpl.class).info("queryStudies() in "+(System.currentTimeMillis()-s)+"ms");
		
		return studies;
	}

	@Override
	public List<Study> getStudyAfterDate(Date date) {
		String targetDate = new SimpleDateFormat(DATE_FORMAT).format(date);
		String sql = String.format("select s.* from Study s where s.upddate > to_timestamp('%s','DD-MON-RR HH.MI.SS')", targetDate);
		return (List<Study>) super.getObjectList(TABLE_NAME, Study.class, sql);
	}

	@Override
	public List<Study> getStudyTargetedByBiosampleAfterDate(Date date) {
		String targetDate = new SimpleDateFormat(DATE_FORMAT).format(date);
		String sql = String.format("select s.* from Study s, Biosample b where b.study_id = s.id and b.upddate > to_timestamp('%s','DD-MON-RR HH.MI.SS')", targetDate);
		return (List<Study>) super.getObjectList(TABLE_NAME, Study.class, sql);
	}

	@Override
	public List<Study> getStudyTargetedByResultAfterDate(Date date) {
		String targetDate = new SimpleDateFormat(DATE_FORMAT).format(date);
		String sql = String.format("select s.* from Study s, Biosample b, Assay_Result r where b.study_id = s.id and b.id = r.biosample_id and r.upd_date > to_timestamp('%s','DD-MON-RR HH.MI.SS')", targetDate);
		return (List<Study>) super.getObjectList(TABLE_NAME, Study.class, sql);
	}

}
