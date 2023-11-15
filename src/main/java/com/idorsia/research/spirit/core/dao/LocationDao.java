package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.view.LocationQuery;
import com.idorsia.research.spirit.core.model.Location;


public interface LocationDao {

	public Location get(Integer id);
	
	public List<Location> getByDepartment(int departmentId);
	
	public List<Location> getByParent(int parentId);
	
	public List<Location> getByLocationType(String locationType);
	
	public List<LocationDto> queryLocations(LocationQuery location, User user) throws Exception;
	
	public List<Location> list();

	public int getCount();
	
	public Integer saveOrUpdate(Location location);

	public int addLocation(Location location);
	
	public void delete(int locationId);

	public List<LocationDto> getLocationRoots();

	public Location getLocation(LocationDto parent, String name);

	public void delete(List<Integer> ids);
}
