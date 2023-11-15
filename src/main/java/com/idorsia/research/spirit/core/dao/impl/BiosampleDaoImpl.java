package com.idorsia.research.spirit.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.security.entity.User;
import com.actelion.research.util.FormatterUtils;
import com.idorsia.research.spirit.core.biosample.BiosampleLinker;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.constants.Status;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.BiosampleDao;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.BiosampleQuery;
import com.idorsia.research.spirit.core.dto.view.LocPos;
import com.idorsia.research.spirit.core.model.Biosample;
import com.idorsia.research.spirit.core.model.Biotype;
import com.idorsia.research.spirit.core.service.LocationService;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;
import com.idorsia.research.spirit.core.util.Triple;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class BiosampleDaoImpl extends AbstractDao<Biosample> implements BiosampleDao{

	@Autowired
	private LocationService locationService;
	
	private static final String TABLE_NAME = "Biosample";
	private static final String DATE_FORMAT = "dd-MMM-yy hh:mm:ss";
	private static final String DATE_FORMAT2 = "dd/MM/yy hh:mm:ss";
	
	@Override
	public Biosample get(Integer id) {
		return super.get(TABLE_NAME,Biosample.class, id);
	}
	
	@Override
	public Biosample getById(int id) {
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE ID = %s", id);
		return super.getObject(TABLE_NAME, Biosample.class, sql);
	}
	
	@Override
	public List<Biosample> getBiosampleByUser(String user) {
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE CREUSER = %s", user);
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
	}
	
	public List<Biosample> getBiosampleByInheritedPhase(Integer phaseId){
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE INHERITEDPHASE_ID = %s", phaseId);
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
	}
	
	public List<Biosample> getBiosampleByEndPhase(Integer phaseId){
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE ENDPHASE_ID = %s", phaseId);
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
	}


	@Override
	public Biosample getBiosampleBySampleId(String sampleId) {
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE SAMPLEID = '%s'", sampleId);
		return super.getObject(TABLE_NAME, Biosample.class, sql);
	}

	@Override
	public List<Biosample> getBiosampleBySampleIds(List<String> sampleIds){
		StringBuilder sql = new StringBuilder("SELECT * FROM BIOSAMPLE WHERE SAMPLEID");
		sql.append(DataUtils.fetchInClause(sampleIds));
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
	}

	
	@Override
	public List<Biosample> getBiosampleByContainerId(String containerId) {
		String sql = String.format("SELECT * FROM BIOSAMPLE WHERE CONTAINERID = '%s'", containerId);
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
	}
	

	@Override
	public List<Biosample> getByCage(String cageClicked, Integer studyId) {
		String sql = "select b.* from biosample b, enclosure e, biosample_enclosure be where be.biosample_id = b.id and be.enclosure_id = e.id and e.study_id = "+studyId+" and e.name = '" +cageClicked+ "'"; 
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
	}

	@Override
	public List<Biosample> getByStudy(Integer studyId){
		String sql = String.format("select b.* from biosample b, assignment a, stage s where a.biosample_id=b.id and a.stage_id=s.id and s.study_id=%s",studyId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}
	
	@Override
	public List<Biosample> getAllByStudy(Integer studyId){
		String sql = String.format("select b.* from biosample b, assignment a, stage s where a.biosample_id=b.id and a.stage_id=s.id and s.study_id=%s", studyId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		sql =  String.format("select b.* from biosample b, assay_result ar where ar.biosample_id=b.id and ar.study_id=%s",studyId);
		result.addAll((List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql));
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}
	
	@Override
	public List<Biosample> getByInheritedStudy(Integer studyId){
		String sql = String.format("select b.* from biosample b where b.study_id=%s",studyId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}
	
	@Override
	public List<Biosample> getBiosamplesByLocationId(Integer locationId){
		String sql = String.format("select b.* from biosample b where b.location_id=%s",locationId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}
	
	public List<Biosample> getBiosamplesByParentId(Integer parentId){
		String sql = String.format("select b.* from biosample b where b.parent_id=%s",parentId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}
	
	public List<Biosample> getBySampling(Integer samplingId){
		String sql = String.format("select b.* from biosample b where b.attachedsampling_id=%s",samplingId); 
		List<Biosample> result = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql);
		for(Biosample biosample : result) {
			addObjectInCache(biosample, Biosample.class, biosample.getId());
		}
		return result;
	}

	@Override
	public List<Biosample> getBiosampleByStudyAndSampleId(Integer studyId, List<String> animalIdOrNo) {
		String sql="";
		List<Biosample> biosamples = new ArrayList<>();
		for (int offset = 0; offset < animalIdOrNo.size(); offset += 500) {
			StringBuilder sb = new StringBuilder();
			for (int i = offset; i < animalIdOrNo.size() && i < offset + 500; i++) {
				sb.append((sb.length() > 0 ? "," : "") + "'" + (animalIdOrNo.get(i).replace("'", "''")) + "'");
			}
			if (studyId == null) {
				sql=String.format("select * from Biosample b where b.sampleId in (%s)",sb);
			} else {
				sql=String.format("select * from Biosample b, assignments a, stage s where (b.sampleId in (%s) or b.localId in (%s)) and a.stage_id=s.id ans s.study_id = %s", sb, sb, studyId);
			}
			biosamples.addAll((List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql));
		}
		return biosamples;
	}
	
	@Override
	public List<Biosample> queryBiosample(BiosampleQuery q, User user) throws Exception{
		StringBuilder clause = new StringBuilder();

		List<Entry<BiosampleLinker, String>> postprocessFilters = new ArrayList<>();

		if ((q.getBiotypes() == null || q.getBiotypes().length == 0) && user != null && !user.isSuperAdmin()) {
			clause.append(" and bt.isHidden = ").append(DataUtils.booleanToInt(Boolean.FALSE));
		}

		if (q.getSampleIdOrContainerIds() != null && q.getSampleIdOrContainerIds().length() > 0) {
			clause.append(" and (" + QueryTokenizer.expandForIn("b.sampleId", q.getSampleIdOrContainerIds()));
			clause.append(" or " + QueryTokenizer.expandForIn("b.containerId", q.getSampleIdOrContainerIds()));
			clause.append(")");
		} else {

			if (q.getBids() != null && q.getBids().size() > 0) {
				clause.append(" and " + QueryTokenizer.expandForIn("b.id", q.getBids()));
			}
			//Attached as a participant
			if ((q.getStudyIds() != null && q.getStudyIds().length() > 0) || q.getSids() != null && q.getSids().size() > 0) {
				if (q.getSids() != null && q.getSids().size() > 0) {
					clause.append(" and (" + QueryTokenizer.expandForIn("s.id", q.getSids()));
					if ((q.getStudyIds() != null && q.getStudyIds().length() > 0)) {
						clause.append(" or ");
					} else {
						clause.append(")");
					}
				} else {
					clause.append(" and (");
				}
				//Created in a study
				if (q.getStudyIds() != null && q.getStudyIds().equalsIgnoreCase("NONE")) {
					clause.append(" b.study_id is null)");
				} else if (q.getStudyIds() != null && q.getStudyIds().length() > 0) {
					clause.append(QueryTokenizer.expandOrQuery("inhs.studyid = ?", q.getStudyIds()) + ")");
				}
			}
			if (q.getSampleIds() != null && q.getSampleIds().length() > 0) {
				clause.append(" and " + QueryTokenizer.expandForIn("b.sampleId", q.getSampleIds()));
			}
			if (q.getSampleId() != null && q.getSampleId().length() > 0) {
				clause.append(" and b.sampleId = ").append(q.getSampleId());
			}
			if (q.getParentSampleIds() != null && q.getParentSampleIds().length() > 0) {
				clause.append(" and parent.sampleid = ").append( q.getParentSampleIds());
			}
			if (q.getTopSampleIds() != null && q.getTopSampleIds().length() > 0) {
				clause.append("b.top_Parent_id =").append(q.getTopSampleIds());
			}
			if (q.getContainerIds() != null && q.getContainerIds().length() > 0) {
				clause.append(" and " + QueryTokenizer.expandForIn("b.containerId", q.getContainerIds()));
			}

			if (q.getElbs() != null && q.getElbs().length() > 0) {
				clause.append(" and (" + QueryTokenizer.expandOrQuery("lower(b.elb) like lower(?)", q.getElbs()) + ")");
			}

			if (q.getSampleNames() != null && q.getSampleNames().length() > 0) {
				clause.append(" and (" + QueryTokenizer.expandOrQuery("lower(b.localid) like lower(?)", q.getSampleNames()) + ")");
			}

			if (q.getBiotypes() != null && q.getBiotypes().length > 0) {
				clause.append(" and (");
				boolean first = true;
				for (BiotypeDto biotype : q.getBiotypes()) {
					if (first)
						first = false;
					else
						clause.append(" or ");
					clause.append(" b.biotype_id = " + biotype.getId());

				}
				clause.append(") ");
			}
			if (q.getContainerType() != null) {
				clause.append(" and (lower(b.containertype) = lower('").append(q.getContainerType()).append("'))");
			}
			if(q.getLocationRoot()!=null) {
				LocationDto l = q.getLocationRoot();
				List<LocationDto> locs = new ArrayList<LocationDto>(locationService.getChildrenRec(l, 8));
				clause.append(" and " + QueryTokenizer.expandForIn("b.location_id", locationService.getIds(locs)));
			}
			if(q.getLocations()!=null && q.getLocations().size()>0) {
				clause.append(" and " + QueryTokenizer.expandForIn("b.location_id", locationService.getIds(q.getLocations())));
			}
			if(q.getLocPoses()!=null && q.getLocPoses().size()>0) {
				clause.append(" and (");
				boolean first = true;
				for (LocPos lp : q.getLocPoses()) {
					if(first) first = false; else clause.append(" or ");
					clause.append("(b.location_id= "+lp.getLocation().getId()+" and b.location_pos = " + lp.getPos() + ")");

				}
				clause.append(")");
			}
			if (q.getComments() != null && q.getComments().length() > 0) {
				clause.append(" and (" + QueryTokenizer.expandQuery("lower(b.comments) like lower(?)", q.getComments(), true, true) + ")");
			}

			if (q.getDepartment() != null) {
				List<String> usernames = new ArrayList<>();
				UserUtil.getEmployeesByDepartment(q.getDepartment().getName()).forEach(su->usernames.add(su.getUserName()));
				clause.append(" and " +QueryTokenizer.expandForIn("b.creuser", usernames));
			}

			if (q.getMinQuality() != null) {
				if (q.getMinQuality().getId() <= Quality.VALID.getId()) {
					clause.append(" and (b.quality is null or b.quality >= " + q.getMinQuality().getId() + ")");
				} else {
					clause.append(" and b.quality >= " + q.getMinQuality().getId());
				}
			}
			if (q.getMaxQuality() != null) {
				if (q.getMaxQuality().getId() >= Quality.VALID.getId()) {
					clause.append(" and (b.quality is null or b.quality <= " + q.getMaxQuality().getId() + ")");
				} else {
					clause.append(" and b.quality <= " + q.getMaxQuality().getId());
				}
			}
			if (q.getExpiryDateMin() != null) {
				String targetDate = new SimpleDateFormat(DATE_FORMAT2).format(q.getExpiryDateMin());
				clause.append(" and b.expiryDate > to_timestamp('").append(targetDate).append("','DD/MM/RR HH.MI.SS')");
				//clause.append(" and (b.expiryDate > ").append(q.getExpiryDateMin());
			}
			if (q.getExpiryDateMax() != null) {
				String targetDate = new SimpleDateFormat(DATE_FORMAT2).format(q.getExpiryDateMax());
				clause.append(" and b.expiryDate <= to_timestamp('").append(targetDate).append("','DD/MM/RR HH.MI.SS')");
				//clause.append(" and (b.expiryDate <= ").append(q.getExpiryDateMax());
			}
			if (q.getUpdUser() != null && q.getUpdUser().length() > 0) {
				clause.append(" and b.updUser = '").append(q.getUpdUser()).append("'");
			}
			if (q.getUpdDate() != null && q.getUpdDate().length() > 0) {
				String modifier = MiscUtils.extractModifier(q.getUpdDate());
				Date date = MiscUtils.extractDate(q.getUpdDate());
				if (date != null && modifier.equals("=")) {
					String date1 = new SimpleDateFormat(DATE_FORMAT2).format(date);
					String date2 = new SimpleDateFormat(DATE_FORMAT2).format(MiscUtils.addDays(date, 1));
					clause.append(" and b.upddate between to_timestamp('").append(date1).append("','DD/MM/RR HH.MI.SS')")
					.append(" and to_timestamp('").append(date2).append("','DD/MM/RR HH.MI.SS')");
					//clause.append(" and b.upddate between ").append(date).append(" and ").append(MiscUtils.addDays(date, 1));
				} else if (date != null && modifier.equals("<")) {
					clause.append(" and b.upddate < to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(MiscUtils.addDays(date, 0))).append("','DD/MM/RR HH.MI.SS')");
					//clause.append(" and b.upddate < ").append(MiscUtils.addDays(date, 0));
				} else if (date != null && modifier.equals("<=")) {
					clause.append(" and b.upddate < to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(MiscUtils.addDays(date, 1))).append("','DD/MM/RR HH.MI.SS')");
					//clause.append(" and b.upddate < ").append(MiscUtils.addDays(date, 1));
				} else if (date != null && modifier.equals(">=")) {
					clause.append(" and b.upddate > to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(MiscUtils.addDays(date, 0))).append("','DD/MM/RR HH.MI.SS')");
					//clause.append(" and b.upddate > ").append(MiscUtils.addDays(date, 0));
				} else if (date != null && modifier.equals(">")) {
					clause.append(" and b.upddate > to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(MiscUtils.addDays(date, 1))).append("','DD/MM/RR HH.MI.SS')");
					clause.append(" and b.upddate > ").append(MiscUtils.addDays(date, 1));
				}
			}
			if (q.getCreUser() != null && q.getCreUser().length() > 0) {
				clause.append(" and b.creuser = '").append(q.getCreUser()).append("'");
			}
			if (q.getCreDays() != null && q.getCreDays().length() > 0) {
				String digits = MiscUtils.extractStartDigits(q.getCreDays());
				if (digits.length() > 0) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
						clause.append(" and b.credate > to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(cal.getTime())).append("','DD/MM/RR HH.MI.SS')");
						//clause.append(" and b.credate > ").append(cal.getTime());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			if (q.getUpdDays() != null && q.getUpdDays().length() > 0) {
				String digits = MiscUtils.extractStartDigits(q.getUpdDays());
				if (digits.length() > 0) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(digits));
						clause.append(" and b.upddate > to_timestamp('").append(new SimpleDateFormat(DATE_FORMAT2).format(cal.getTime())).append("','DD/MM/RR HH.MI.SS')");
						//clause.append(" and b.upddate > ").append(cal.getTime());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			if (q.isFilterNotInContainer()) {
				clause.append(" and b.containerType is not null ");
			}
			if (q.isFilterNotInLocation()) {
				clause.append(" and b.location_id is not null ");
			}

			if (q.isFilterInStudy()) {
				clause.append(" and b.study_id is null ");
			}

			if (q.isFilterTrashed()) {
				clause.append(" and (b.state is null or (b.state <> '").append(Status.TRASHED).append("' and b.state <> '").append(Status.USEDUP).append("' and b.state <> 'TRASHED'").append(")) ");
			}

//			if (q.isSearchMySamples() && user != null) {
//				clause.append(" and b.employeeGroup = ?"+ (++i)+ "");
//				parameters.add(user.getMainGroup());
//			}

			// Query based on the linkers
			for (Entry<BiosampleLinker, String> entry : q.getLinker2values().entrySet()) {
				BiosampleLinker linker = entry.getKey();
				String val = entry.getValue();
				if (val == null)
					continue;
				if (linker.getAggregatedMetadata() != null) {
					// Linker for aggregated biosamples
					long idAgg = linker.getAggregatedMetadata().getId();
					if (linker.getType() == BiosampleLinker.LinkerType.SAMPLEID) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, Biosample b3, Biosample_Biosample bb where bb.biosample_id=b2.id and bb.linked_biosample_id = b3.id and bb.biotypemetadata_id = " + idAgg + " and b3.sampleId = '" + QueryTokenizer.escapeForSQL(val) + "'))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.SAMPLENAME) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, Biosample_Biosample bb where bb.linked_biosample_id = b2.id and bb.biotypemetadata_id = " + idAgg + " and b2.localid = '" + QueryTokenizer.escapeForSQL(val) + "'))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.COMMENTS) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, Biosample_Biosample bb where bb.linked_biosample_id = b2.id and bb.biotypemetadata_id = " + idAgg + " and lower(b2.comments) like lower('" + QueryTokenizer.escapeForSQL(val) + "')))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null && linker.getBiotypeMetadata().getDatatype()==DataType.MULTI) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, Biosample_Biosample bb, Biotype_Metadata_Biosample bmb where bb.linked_biosample_id = b2.id and bmb.id = bb.biotypemetadata_id and bb.biotypemetadata_id = " + idAgg + " and " + QueryTokenizer.expandQuery("REPLACE(lower(bmb.value), ';', ' ') LIKE lower(?)", val, true, true) + "))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, Biosample_Biosample bb, Biotype_Metadata_Biosample bmb where bb.linked_biosample_id = b2.id and bmb.id = bb.biotypemetadata_id and bb.biotypemetadata_id = " + idAgg + " and REPLACE(lower(bmb.value), ';', ' ') LIKE lower('%" + QueryTokenizer.escapeForSQL(val) + "%')))");
					} else {
						postprocessFilters.add(entry);
						System.err.println("LinkerAgg " + linker + " not managed = "+val);
					}
				} else if (linker.getHierarchyBiotype() != null) {
					long tId = linker.getHierarchyBiotype().getId();
					if (linker.getType() == BiosampleLinker.LinkerType.SAMPLEID) {
						clause.append(" and (b.parent_id in (select b2.parent_id from Biosample b2, Biotype bt where b2.biotype_id = bt.id and bt.id = " + tId + " and b2.sampleId = '" + QueryTokenizer.escapeForSQL(val) + "'))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.SAMPLENAME) {
						clause.append(" and (b.parent_id in (select b2.parent_id from Biosample b2, Biotype bt where b2.biotype_id = bt.id and bt.id = " + tId + " and lower(b2.localid) like lower('" + QueryTokenizer.escapeForSQL(val) + "')))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.COMMENTS) {
						clause.append(" and (b.parent_id in (select b2.parent_id from Biosample b2, Biotype bt where b2.biotype_id = bt.id and bt.id = " + tId + " and lower(b2.comments) like lower('" + QueryTokenizer.escapeForSQL(val) + "')))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null && linker.getBiotypeMetadata().getDatatype()==DataType.MULTI) {
						clause.append(" and (b.parent_id in (select b2.id from Biosample b2, biotype_metadata_biosample bmb where b2.id = bmb.biosample_id and " + QueryTokenizer.expandQuery("REPLACE(lower(bmb.value), ';', ' ') LIKE lower(?)", val, true, true) + "))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null) {
						clause.append(" and (b.parent_id in (select b2.id from Biosample b2, biotype_metadata_biosample bmb where b2.id = bmb.biosample_id and REPLACE(lower(bmb.value), ';', ' ') LIKE lower('%" + QueryTokenizer.escapeForSQL(val) + "%')))");
					} else {
						postprocessFilters.add(entry);
						System.err.println("LinkerHie " + linker + " not managed");
					}
				} else {
					if (linker.getType() == BiosampleLinker.LinkerType.SAMPLEID) {
						clause.append(" and " + QueryTokenizer.expandOrQuery("b.sampleId = ?", val));
					} else if (linker.getType() == BiosampleLinker.LinkerType.SAMPLENAME) {
						clause.append(" and lower(b.localid) like lower('" + QueryTokenizer.escapeForSQL(val) + "')");
					} else if (linker.getType() == BiosampleLinker.LinkerType.COMMENTS) {
						clause.append(" and lower(b.comments) like lower('" + QueryTokenizer.escapeForSQL(val) + "')");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null && linker.getBiotypeMetadata().getDatatype()==DataType.MULTI) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, biotype_metadata_biosample bmb where b2.id = bmb.biosample_id and " + QueryTokenizer.expandQuery("REPLACE(lower(bmb.value), ';', ' ') LIKE lower(?)", val, true, true) + "))");
					} else if (linker.getType() == BiosampleLinker.LinkerType.METADATA && linker.getBiotypeMetadata() != null) {
						clause.append(" and (b.id in (select b2.id from Biosample b2, biotype_metadata_biosample bmb where b2.id = bmb.biosample_id and REPLACE(lower(bmb.value), ';', ' ') LIKE lower('%" + QueryTokenizer.escapeForSQL(val) + "%')))");
					} else {
						postprocessFilters.add(entry);
						System.err.println("LinkerReg " + linker + " not managed = "+val);
					}
				}

			}
		}
		if (q.getKeywords() != null && q.getKeywords().length() > 0) {
			StringBuilder expr = new StringBuilder();
			expr.append(" \n(");
			expr.append(" lower(b.study_Id) like lower(?) or lower(st.ivv) like lower(?)");
			expr.append(" or lower(g1.name) like lower(?)");
			expr.append(" or bt.name like lower(?)");
			expr.append(" or bt2.name like lower(?)");
			expr.append(" or bt3.name like lower(?)");
			expr.append(" or b.sampleId like ?");
			expr.append(" or b.containerId like ?");
			expr.append(" or lower(b.localid) like lower(?)");
			expr.append(" or lower(parent.localid) like lower(?)");
			expr.append(" or lower(topparent.localid) like lower(?)");
			expr.append(" or lower(bmb.value) like lower(?)");
			expr.append(" or lower(bmb2.value) like lower(?)");
			expr.append(" or lower(bmb3.value) like lower(?)");
			expr.append(" or lower(b.comments) like lower(?)");
			expr.append(" or lower(b.creuser) like lower(?)");
			expr.append(" or lower(b.upduser) like lower(?)");
			expr.append(" or lower(b.elb) like lower(?)");
			expr.append(" or lower(loc.name) like lower(?)");
			expr.append(" )\n");
			clause.append(" and (" + QueryTokenizer.expandQuery(expr.toString(), q.getKeywords(), true, true) + ")");
		}

		String jpql = "select distinct b.* from biosample b";
		if (q.getKeywords() != null && q.getKeywords().length() > 0) {
			jpql += " left outer join ASSIGNMENT ass on b.id=ass.biosample_id left outer join STUDY st on b.study_Id=st.id left outer join subgroup sg1 on ass.subgroup_id=sg1.id left outer join study_group g1 on sg1.group_id=g1.id"
					+ " left outer join biosample parent on b.parent_id=parent.id left outer join biosample topparent on b.top_parent_id=topparent.id left outer join biotype bt on b.biotype_id=bt.id left outer join biotype bt2 on parent.biotype_id=bt2.id"
					+ " left outer join biotype_metadata_biosample bmb on bmb.biosample_id=b.id left outer join biotype bt3 on b.biotype_id=bt3.id left outer join biotype_metadata_biosample bmb3 on bmb3.biosample_id=topparent.id"
					+ " left outer join biotype_metadata_biosample bmb2 on bmb2.biosample_id=parent.id left outer join Biolocation loc on b.location_id=loc.id ";
		}
		if (q.getSids() != null && q.getSids().size() > 0) {
			jpql += " left outer join ASSIGNMENT a on b.id=a.BIOSAMPLE_ID left outer join STAGE st on a.STAGE_ID=st.ID left outer join study s on st.study_id=s.id";
		}
		if (q.getStudyIds() != null && q.getStudyIds().length() > 0 && !q.getStudyIds().equals("NONE")) {
			jpql += " left outer join study inhs on b.STUDY_ID=inhs.id ";
		}
		if ((q.getBiotypes() == null || q.getBiotypes().length == 0) && user != null && !user.isSuperAdmin()) {
			jpql += " left outer join biotype bt on bt.id=b.biotype_id ";
		}
		if (q.getParentSampleIds() != null && q.getParentSampleIds().length() > 0) {
			jpql += " left outer join biosample parent on parent.id=b.parent_id ";
		}

		if (clause.length() > 0) {
			assert clause.substring(0, 4).equals(" and");
			jpql += " where" + clause.substring(4);
		}
		System.out.println(jpql);
		List<Biosample> biosamples = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, jpql);
		
		return biosamples;
	}
	
	@Override
	public Map<String, String> getAnimalDBInfo(String sampleId){
		String sql = String.format("select t.type, a.sex, t.species, a.delivery_date, a.order_id, a.birthday, a.short_type, p.LICENSE_NO " +
				"from animal.animal a, animal.animal_type t, animal.ANIMAL_PROC p " +
				"where t.short_type = a.short_type and a.animal_id = %s " +
				"and p.LICENSE_ID = ( select us.LICENSE_ID from animal.animal_usage us " +
				"where us.ANIMAL_ID = %s order by us.USAGE_ID desc fetch first row only)", sampleId, sampleId);
		return getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> values = new HashMap<>();
				if (rs.next()) {
					values.put("PO Number", rs.getString("order_id"));
					values.put("Delivery Date", FormatterUtils.formatDate(rs.getDate("delivery_date")));
					values.put("Sex", rs.getString("sex") == null ? "" : rs.getString("sex").toUpperCase());
					values.put("Type", rs.getString("species") + "/" + rs.getString("type"));
					values.put("Birthday", FormatterUtils.formatDate(rs.getDate("birthday")));
					values.put("TypeID", rs.getString("short_type"));
					values.put("LicenseNo", rs.getString("LICENSE_NO"));
				}
				return values.size()>0?values:null;
			}
		});
	}
	
	@Override
	public Map<String, String> getHumanDBInfo(String sampleId) {
		String sql = String.format("select id, year_of_birth, gender from donors.spirit_to_donor_read where id = %s",sampleId);
		return getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String, String>>() {
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Map<String, String> values = new HashMap<>();
					values.put("year_of_birth", rs.getString("year_of_birth"));
					values.put("gender", FormatterUtils.formatDate(rs.getDate("gender")));
				}
				return null;
			}
		});
	}

	@Override
	public List<String> getSampleIds(ArrayList<Integer> ids) {
		List<String> result = new ArrayList<String>();
		StringBuilder sql = new StringBuilder("select * from ").append(TABLE_NAME).append(" where id "); 
		sql.append(DataUtils.fetchInInt(new ArrayList<Integer>(ids)));
		List<Biosample> biosamples = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
		for(Biosample b : biosamples) {
			result.add(b.getSampleId());
		}
		return result;
	}
	
	@Override
	public Set<Biosample> getBiosamples(Set<Integer> samplesIds) {
		StringBuilder sql = new StringBuilder("select * from " + TABLE_NAME + " where id ");
		sql.append(DataUtils.fetchInInt(new ArrayList<Integer>(samplesIds)));
		List<Biosample> samples = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
		return new HashSet<Biosample>(samples);
	}

	@Override
	public List<Biosample> list() {
		return super.getlist(TABLE_NAME,Biosample.class);
	}

	@Override
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Biosample biosample) {
		if (biosample.getId() != null && biosample.getId() > 0) {
			if(!biosample.equals(get(biosample.getId()))) {
				String sql = "UPDATE BIOSAMPLE SET comments=:comments, " 
						+ "creDate=:creDate, " 
						+ "elb=:elb, "
						+ "localId=:localId, "
						+ "location_Pos=:locationPos, "
						+ "sampleId=:sampleId, "
						+ "updDate=:updDate, "
						+ "updUser=:updUser, "
						+ "biotype_Id=:biotypeId, "
						+ "department_id=:departmentId, "
						+ "inheritedPhase_Id=:inheritedPhaseId, "
						+ "quality=:quality, "
						+ "creUser=:creUser, "
						+ "study_Id=:studyId, "
						+ "amount=:amount, "
						+ "parent_Id=:parentId, "
						+ "container_Index=:containerIndex, "
						+ "attachedSampling_Id=:attachedSamplingId, "
						+ "state=:state, "
						+ "location_Id=:locationId, "
						+ "containerId=:containerId, "
						+ "containerType=:containerType, "
						+ "expiryDate=:expiryDate, "
						+ "lastAction=:lastAction, "
						+ "endPhase_Id=:endPhaseId, "
						+ "inherited_Date=:inheritedDate, "
						+ "end_Date=:endDate "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", biosample.getId())
			    		.addValue("comments", biosample.getComments())
			    		.addValue("creDate", biosample.getCreDate())
			    		.addValue("elb", biosample.getElb())
			    		.addValue("localId", biosample.getLocalId())
			    		.addValue("locationPos", biosample.getLocationPos())
			    		.addValue("sampleId", biosample.getSampleId())
			    		.addValue("updDate", biosample.getUpdDate())
			    		.addValue("updUser", biosample.getUpdUser())
			    		.addValue("biotypeId", biosample.getBiotypeId())
			    		.addValue("departmentId", biosample.getDepartmentId())
			    		.addValue("inheritedPhaseId", biosample.getInheritedphaseId())
			    		.addValue("quality", biosample.getQuality())
			    		.addValue("creUser", biosample.getCreUser())
			    		.addValue("studyId", biosample.getStudyId())
			    		.addValue("amount", biosample.getAmount())
			    		.addValue("parentId", biosample.getParentId())
			    		.addValue("containerIndex", biosample.getContainerIndex())
			    		.addValue("attachedSamplingId", biosample.getAttachedsamplingId())
			    		.addValue("state", biosample.getState())
			    		.addValue("locationId", biosample.getLocationId())
			    		.addValue("containerId", biosample.getContainerId())
			    		.addValue("containerType", biosample.getContainerType())
			    		.addValue("expiryDate", biosample.getExpiryDate())
			    		.addValue("lastAction", biosample.getLastAction())
			    		.addValue("endPhaseId", biosample.getEndphaseId())
			    		.addValue("inheritedDate", biosample.getInheritedDate())
			    		.addValue("endDate", biosample.getEndDate());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			Integer newId = getSequence(Constants.BIOSAMPLE_SEQUENCE_NAME);
			if(biosample.getTopParentId().equals(Constants.NEWTRANSIENTID))
				biosample.setTopParentId(newId);
			biosample.setId(newId);
			addBiosample(biosample);
			addTransient(biosample);
		}
		addIdToObject(Biosample.class, biosample.getId(), biosample);
		return biosample.getId();
	}

	@Override
	public int addBiosample(Biosample biosample) {
		return super.add(TABLE_NAME, biosample);
	}

	@Override
	public void delete(int biosampleId) {
		super.delete(TABLE_NAME, biosampleId);
	}

	@Override
	public Integer getStrainID(String type) {
		String sql = String.format("select short_type from animal.animal_type where animal_type.type = '%s'",type);
		return getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(Integer.class)).get(0);
	}

	@Override
	public List<String> getContainerTypes(StudyDto study) {
		List<String> res = new ArrayList<String>();
		String sql = String.format("select distinct(b.CONTAINERTYPE) from Biosample b where b.CONTAINERTYPE is not null and b.STUDY_ID = '%s'", study.getId());
		List<Biosample> samples = (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
		for(Biosample b : samples){
			res.add(b.getContainerType());
		}
		return res;
	}

	@Override
	public List<Biotype> getBiotype(StudyDto study) {
		String sql = "select * from biotype where id in (select distinct(b.biotype_id) from biosample b where b.study_id = "+study.getId()+")";
		return getJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(Biotype.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Biosample> getBiosamplesBySampleIds(Set<String> sampleIds) {
		return getBiosampleBySampleIds(new ArrayList(sampleIds));
	}

	@Override
	public Map<String, Map<String, Triple<Integer, String, Date>>> countSampleByStudyBiotype(
			Collection<StudyDto> studies, Date minDate) {
		StringBuilder query = new StringBuilder();
		if(studies == null) {
			query.append("select '' as studyid, bt.name as biotypename, b.updUser as updateuser, count(b.id) as biosamplescount, max(b.updDate) as maxupdate ");
			query.append("from biosample b cross join biotype bt ");
			query.append("where  b.biotype_id=bt.id ");
			query.append("and b.study_id is null ");
			if(minDate != null) {
				String targetMinDate = new SimpleDateFormat(DATE_FORMAT).format(minDate);
				query.append(" and b.upddate > to_timestamp('").append(targetMinDate).append("','DD-MON-RR HH.MI.SS')");
			}
			query.append("group by bt.name , b.updUser");
		}else {
			query.append("select s.studyId as studyid, bt.name as biotypename, b.updUser as updateuser, count(b.id) as biosamplescount, max(b.updDate) as maxupdate ");
			query.append("from biosample b cross join study s cross join biotype bt ");
			query.append("where b.study_id=s.id ");
			query.append("and b.biotype_id=bt.id ");
			if(studies != null && !studies.isEmpty()) {				
				List<Integer> studyIds = new ArrayList<Integer>();
				for(StudyDto studyDto : studies) {
					studyIds.add(studyDto.getId());
				}
				query.append("and (b.study_id ").append(DataUtils.fetchInInt(studyIds)).append(") ");
			}
			query.append("group by s.studyId , bt.name , b.updUser");
		}
		return getJdbcTemplate().query(query.toString(), new ResultSetExtractor<Map<String, Map<String, Triple<Integer, String, Date>>>>() {
			public Map<String, Map<String, Triple<Integer, String, Date>>> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, Map<String, Triple<Integer, String, Date>>> map = new HashMap<>();
				if (rs.next()) {
					String sid = rs.getString("studyid");
					String key = rs.getString("biotypename");
					int n = rs.getInt("biosamplescount");
					String user = rs.getString("updateuser");
					Date date = rs.getDate("maxupdate");
					Map<String, Triple<Integer, String, Date>> m = map.get(sid);
					if (m == null) {
						m = new HashMap<>();
						//map.put(sid, m);
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
					map.put(sid, m);
				}
				return map;
			}
		});
	}

	@Override
	public List<Biosample> getBiosampleByBiotypeName(String biotypeName) {
		String sql = String.format("select b.* from Biosample b, Biotype bt where b.biotype_id = b.id and bt.name = %s", biotypeName);
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
	}

	@Override
	public List<Biosample> getBiosampleByMetadataValue(String value) {
		String sql = "select b.* from Biosample b, Biotype_Metadata_Biosample bmb where bmb.biosample_id = b.id and REPLACE(lower(bmb.value), ';', ' ') like '% "
				+ value;
		return (List<Biosample>) super.getObjectList(TABLE_NAME, Biosample.class, sql.toString());
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