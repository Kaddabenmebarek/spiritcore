package com.idorsia.research.spirit.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.LocationType;
import com.idorsia.research.spirit.core.constants.LocationType.LocationCategory;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.LocationDao;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.view.LocationQuery;
import com.idorsia.research.spirit.core.model.Location;
import com.idorsia.research.spirit.core.service.LocationService;
import com.idorsia.research.spirit.core.util.DataUtils;
import com.idorsia.research.spirit.core.util.QueryTokenizer;
import com.idorsia.research.spirit.core.util.SpiritRights;

@Repository
public class LocationDaoImpl extends AbstractDao<Location> implements LocationDao {

	private static final String TABLE_NAME = "Biolocation";
	@Autowired
	private LocationService locationService;
	@Autowired
	private SpiritRights spiritRights;
	
	public Location get(Integer id) {
		return super.get(TABLE_NAME, Location.class, id);
	}
	
	@Override
	public List<Location> getByDepartment(int departmentId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE DEPARTMENT_ID = '%s'",
				departmentId);
		return (List<Location>) super.getObjectList(TABLE_NAME, Location.class, sql);
	}

	@Override
	public List<Location> getByParent(int parentId) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE PARENT_ID = '%s'",
				parentId);
		return (List<Location>) super.getObjectList(TABLE_NAME, Location.class, sql);
	}

	@Override
	public List<Location> getByLocationType(String locationType) {
		String sql = String.format("SELECT * FROM " + TABLE_NAME + " WHERE LOCATIONTYPE = '%s'",
				locationType);
		return getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(Location.class));
	}
	
	@Override
	public List<LocationDto> getLocationRoots() {
		StringBuilder sb = new StringBuilder();
		for (LocationType t : LocationType.getPossibleRoots()) {
			sb.append((sb.length()>0?",":"") + "'" + t.name() + "'");
		}
		String sql = "select * from "+TABLE_NAME+" location where location.parent_id is null and location.locationtype in (" + sb.toString() + ")";
		List<LocationDto> res = locationService.map((List<Location>) super.getObjectList(TABLE_NAME, Location.class, sql));
		locationService.sort(res);
		return res;
	}
	
	public List<LocationDto> queryLocations(LocationQuery q, User user) throws Exception{
		String sql = "select * from "+TABLE_NAME+" location where 1 = 1";
		List<LocationDto> res = new ArrayList<>();

		boolean belongstoStudy = q.getStudyId()!=null && q.getStudyId().length()>0;

		//* 0 - none
		//* 1 - not empty
		//* 2 - empty
		//* 3 - both (occupied and empty)
		if (q.getOccupiedStatus() != null) {
			if (q.getOccupiedStatus() == 0) return res;
			else if (q.getOccupiedStatus() == 2)/*empty*/ {
				sql += " and not exists(select b from Biosample b where b.location_id = location.id) and not exists(select * from Location l2 where l2.parent_id = location.id)";
			} else if (q.getOccupiedStatus() == 1 || q.getOccupiedStatus() == 3) {
				if (!(q.getOccupiedStatus() == 3 && !belongstoStudy)) {
					if (belongstoStudy) {
						sql += " and (exists(select * from Biosample b, Study s Where b.study_id = s.id and b.location_id = location.id ";
						sql += " and s.studyid = '"+q.getStudyId()+"'";
					}else {
						sql += " and (exists(select * from Biosample b Where b.location_id = location.id ";
					}
					sql += ")";
					if (q.getOccupiedStatus() == 3) {
						sql += " OR not exists(select * from Biosample b where b.location_id = location.id)";
					}
					sql += ")";
				}
			}
		}
		if(q.getDepartment()!=null) {
			sql += " and location.department_id = "+q.getDepartment();
		}

		if(q.getName()!=null && q.getName().length()>0) {

			boolean searchId = false;
			if(q.getName().startsWith(Constants.LOCATION_PREFIX)) {
				try {
					int id = Integer.parseInt(q.getName().substring(Constants.LOCATION_PREFIX.length()));
					sql += " and location.id = " + id;
					searchId = true;
				} catch(Exception e) {
					//Not an id
					e.printStackTrace();
				}
			}
			if(!searchId) {
				sql += " and (" + QueryTokenizer.expandQuery("lower(location.name) like ? ", q.getName().toLowerCase(), true, true) + ")";
			}
		}
		if(q.getLocationType()!=null) {
			sql += " and location.locationtype = '"+ q.getLocationType()+"'";
		}

		if(q.getBiotype()!=null) {
			sql += " and exists(select * from Biosample b, Biotype bt where b.location_id = location.id and b.biotype_id = bt.id and lower(bt.name) = lower('"+q.getBiotype()+"'))";
		}

		List<Location> locations = (List<Location>) super.getObjectList(TABLE_NAME, Location.class, sql);

		for (Location location : locations) {
			if(user!=null && !spiritRights.canRead(location, user)) continue;
			res.add(locationService.map(location));

		}
		locationService.sort(res);

		//Remove moveable location if needed (and keep the first non-moveable parent)
		if(q.isFilterAdminLocation()) {
			List<LocationDto> filtered = new ArrayList<>();
			for (LocationDto l : res) {
				LocationDto l2 = l;
				while(l2!=null && l2.getLocationtype().getCategory()== LocationCategory.MOVEABLE) {
					l2 = l2.getParent();
				}
				if(l2!=null && !filtered.contains(l2)) {
					filtered.add(l2);
				}
			}
			res = filtered;
		}
		return res;
	}

	@Override
	public List<Location> list() {
		return super.getlist(TABLE_NAME, Location.class);
	}

	@Override	
	public int getCount() {
		return super.getCount(TABLE_NAME);
	}

	@Override
	public Integer saveOrUpdate(Location location) {
		if (location.getId() != null && location.getId() > 0) {
			if(!location.equals(get(location.getId()))) {
				String sql = "UPDATE "+TABLE_NAME+" SET "
						+ "NCOLS=:ncols, "
						+ "NAME=:name, "
						+ "PRIVACY=:privacy, "
						+ "NROWS=:nrows, "
						+ "UPDDATE=:updDate, "
						+ "UPDUSER=:updUser, "
						+ "DEPARTMENT_ID=:departmentId, "
						+ "PARENT_ID=:parentId, "
						+ "LOCATIONTYPE=:locationType, "
						+ "CREDATE=:creDate, "
						+ "CREUSER=:creUser, "
						+ "LABELING=:labeling, "
						+ "DESCRIPTION=:description, "
						+ "FLAG=:flag "
						+ "WHERE id=:id";
			    SqlParameterSource namedParameters = new MapSqlParameterSource()
			    		.addValue("id", location.getId())
			    		.addValue("ncols", location.getNcols())
			    		.addValue("name", location.getName())
			    		.addValue("privacy", location.getPrivacy())
			    		.addValue("nrows", location.getNrows())
			    		.addValue("departmentId", location.getDepartmentId())
			    		.addValue("parentId", location.getParentId())
			    		.addValue("locationType", location.getLocationtype())
			    		.addValue("labeling", location.getLabeling())
			    		.addValue("description", location.getDescription())
			    		.addValue("flag", location.getFlag())
			    		.addValue("creDate", location.getCreDate())
			    		.addValue("creUser", location.getCreUser())
			    		.addValue("updDate", location.getUpdDate())
			    		.addValue("updUser", location.getUpdUser());
			    super.saveOrUpdate(sql, namedParameters);
			}
		} else {
			location.setId(getSequence(Constants.LOCATION_SEQUENCE_NAME));
			addLocation(location);
			addTransient(location);
		}
		addIdToObject(Location.class, location.getId(), location);
		return location.getId();
	}

	@Override
	public int addLocation(Location location) {
		return super.add(TABLE_NAME, location);
	}

	@Override
	public void delete(int locationId) {
		super.delete(TABLE_NAME, locationId);
	}

	@Override
	public Location getLocation(LocationDto parent, String locationName) {
		String query;
		if(parent==null || parent.getId()<=0) {
			query = "SELECT * FROM biolocation where name = '"+locationName+"' and PARENT_ID is null";
		} else {
			query = "SELECT * FROM biolocation where name = '"+locationName+"' and PARENT_ID = " + parent.getId();
		}
		return super.getObject(TABLE_NAME, Location.class, query);
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
