package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.business.Department;
import com.actelion.research.security.entity.User;
import com.actelion.research.util.CompareUtils;
import com.actelion.research.util.services.Cache;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.LocationLabeling;
import com.idorsia.research.spirit.core.constants.LocationType;
import com.idorsia.research.spirit.core.constants.LocationType.LocationCategory;
import com.idorsia.research.spirit.core.constants.Privacy;
import com.idorsia.research.spirit.core.dao.LocationDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.view.Container;
import com.idorsia.research.spirit.core.dto.view.LocPos;
import com.idorsia.research.spirit.core.dto.view.LocationQuery;
import com.idorsia.research.spirit.core.model.Location;
import com.idorsia.research.spirit.core.util.SpiritRights;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class LocationService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 777429008539664425L;

	@Autowired
	private LocationDao locationDao;
	
	@Autowired
	private BiosampleService biosampleService;
	
	@Autowired
	private LocationLabelingService locationLabelingService;
	
	@Autowired
	private SpiritRights spiritRights;
	
	private static Map<LocationDto, URL> stores = null;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, LocationDto> idToLocation = (Map<Integer, LocationDto>) getCacheMap(LocationDto.class);

	public Location get(Integer id) {
		return locationDao.get(id);
	}

	public List<Location> getByDepartment(int departmentId) {
		return locationDao.getByDepartment(departmentId);
	}

	public List<Location> getByParent(int parentId) {
		return locationDao.getByParent(parentId);
	}

	public List<Location> getByLocationType(String locationType) {
		return locationDao.getByLocationType(locationType);
	}

	public List<Location> list() {
		return locationDao.list();
	}

	public int getCount() {
		return locationDao.getCount();
	}

	public Integer saveOrUpdate(Location location) {
		return locationDao.saveOrUpdate(location);
	}

	public int addLocation(Location location) {
		return locationDao.addLocation(location);
	}

	@Transactional
	public void delete(List<LocationDto> locations) throws Exception {
		locationDao.delete(getIds(locations));
	}

	public LocationDao getLocationDao() {
		return locationDao;
	}

	public void setLocationDao(LocationDao locationDao) {
		this.locationDao = locationDao;
	}

	public LocationDto getLocationDto(Integer id) {
		return map(get(id));
	}

	public LocationDto map(Location location) {
		if(location==null)
			return null;
		LocationDto locationDto = idToLocation.get(location.getId());
		if(locationDto==null) {
			locationDto = dozerMapper.map(location,LocationDto.class,"locationCustomMapping");
			if(idToLocation.get(location.getId())==null)
				idToLocation.put(location.getId(), locationDto);
			else
				locationDto=idToLocation.get(location.getId());
		}
		return locationDto;
	}
	
	
	private Location mapFromDto(LocationDto locationDto) {
		Location location = dozerMapper.map(locationDto, Location.class, "locationCustomMapping");
		return location;
	}
	
	public List<LocationDto> map(List<Location> locations) {
		List<LocationDto> res = new ArrayList<>();
		for(Location location : locations) {
			res.add(map(location));
		}
		return res;
	}
	
	public List<Location> mapFromDto(List<LocationDto> locationDtos) {
		List<Location> res = new ArrayList<>();
		for(LocationDto location : locationDtos) {
			res.add(mapFromDto(location));
		}
		return res;
	}

	@Transactional
	public void save(List<LocationDto> locations) {
		for(LocationDto location : locations) {
			try {				
				save(location, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}

	@Transactional
	public void save(LocationDto location) throws Exception {
		save(location, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(LocationDto location, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(location)) {
				savedItems.add(location);
				if(location.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(location);
				if(location.getParent()!=null && location.getParent().getId()==Constants.NEWTRANSIENTID)save(location.getParent(), true);
				location.setUpdDate(new Date());
				location.setUpdUser(UserUtil.getUsername());
				if(location.getId().equals(Constants.NEWTRANSIENTID)) {
					location.setCreDate(new Date());
					location.setCreUser(UserUtil.getUsername());
				}
				location.setId(saveOrUpdate(dozerMapper.map(location, Location.class, "locationCustomMapping")));
				idToLocation.put(location.getId(), location);
				if(location.getBiosamplesNoMapping()!=null)
					for(BiosampleDto biosample : location.getBiosamples())
						biosampleService.save(biosample, true);
				if(location.getChildrenNoMapping()!=null)
					for(LocationDto child : location.getChildren()) 
						save(child, true);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void deleteChildren(LocationDto location) throws Exception {
		if(location.getChildrenNoMapping()!=null) {
			for(Location l : getByParent(location.getId())) {
				Boolean found = false;
				for(LocationDto child : location.getChildren()){
					if(l.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					delete(map(l), true);
				}
			}
		}
	}

	@Transactional
	public void delete(LocationDto location) throws Exception {
		delete(location, false);
	}
	
	public void delete(LocationDto location, Boolean cross) throws Exception {
		for(BiosampleDto biosample : location.getBiosamples()) { 
			biosampleService.setLocation(biosample, null);
		}
		locationDao.delete(location.getId());
	}
	
	public void removeBiosample(LocationDto location, BiosampleDto biosample) {
		removeBiosample(location, biosample, false);
	}

	public void removeBiosample(LocationDto location, BiosampleDto biosample, boolean cross) {
		//prevent endless loop
		if (!location.getBiosamples().contains(biosample))
			return ;
		//remove old member
		location.getBiosamples().remove(biosample);
		//remove child's owner
		if (!cross) {
			biosampleService.setLocation(biosample, null, true);
		}
		
	}

	public void addBiosample(LocationDto location, BiosampleDto biosample) {
		addBiosample(location, biosample, false);
	}

	public void addBiosample(LocationDto location, BiosampleDto biosample, boolean cross) {
		//prevent endless loop
		if (location.getBiosamples().contains(biosample))
			return ;
		//add new member
		location.getBiosamples().add(biosample);
		//update child if request is not from it
		if (!cross) {
			biosampleService.setLocation(biosample, location, true);
		}
	}

	public int getSize(LocationDto loc) {
		return loc.getNcols()<=0 ||loc.getNrows()<=0? -1: loc.getNcols()*loc.getNrows();
	}
	
	public int getRow(LocationDto loc, int position) {
		if(loc!=null && loc.getNcols()>0 && loc.getNrows()>0) {
			return position/loc.getNcols();
		} else {
			return 1;
		}
	}
	public int getCol(LocationDto loc, int position) {
		if(loc!=null && loc.getNcols()>0 && loc.getNrows()>0) {
			return position%loc.getNcols();
		} else {
			return 1;
		}
	}

	public Privacy getInheritedPrivacy(LocationDto location) {
		LocationDto l = location;
		int depth=0;
		while(l!=null && (++depth)<10) {
			if(l.getPrivacy()==null || l.getPrivacy()==Privacy.INHERITED) {
				l = l.getParent();
			} else {
				return l.getPrivacy();
			}
		}
		if(depth==10) {
			System.err.println("Cycle in "+location+":"+getHierarchy(location));
		}
		return Privacy.PUBLIC;
	}
	
	public List<LocationDto> getHierarchy(LocationDto location) {
		LinkedList<LocationDto> res = new LinkedList<>();
		LocationDto l = location;
		int depth = 0;
		while(l!=null && (++depth<10)) {
			res.addFirst(l);
			l = l.getParent();
		}
		return res;
	}
	
	public String getHierarchyFull(LocationDto location) {
		StringBuilder res = new StringBuilder();
		LocationDto loc = location;
		int depth = 0;
		while(loc!=null) {
			res.insert(0, loc.getName() + (depth>0? Constants.LOCATION_SEPARATOR:""));
			loc = loc.getParent();
			if(++depth>=10) break;
		}
		return res.toString();
	}

	public String getHierarchyMedium(LocationDto location) {
		StringBuilder res = new StringBuilder();
		LocationDto loc = location;
		int depth = 0;
		while(true) {
			String name = loc.getName();
			if(name.indexOf("(")>4 && name.indexOf("(")<name.lastIndexOf(")")) name = (name.substring(0, name.indexOf("(")) + name.substring(name.lastIndexOf(")")+1)).trim();
			res.insert(0, name + (depth>0? Constants.LOCATION_SEPARATOR:""));
			if(loc.getLocationtype().getCategory()==LocationCategory.ADMIN) break;
			loc = loc.getParent();
			if(++depth>=2 || loc==null) break;
		}
		return res.toString();
	}

	public void updateLocation(BiosampleDto biosample, String locationPart, User user) throws Exception{
		assert biosample!=null;
		if(locationPart!=null && locationPart.length()>0) {
			LocPos loc = getCompatibleLocationPos(locationPart, user);
			if(loc==null) {
				loc = new LocPos(new LocationDto(locationPart));
			}
			if(!loc.getLocation().equals(biosample.getLocation()) || loc.getPos()!=biosample.getLocationPos()) {
				setLocPos(biosample, loc.getLocation(), loc.getPos());
			}
		} else {
			setLocPos(biosample, null, -1);
		}
	}
	
	public LocPos getCompatibleLocationPos(String value, User user) throws Exception{
		if(value==null) return null;
		int index = value.lastIndexOf(':');
		String fullLocation;
		String pos;
		if(index>=0) {
			fullLocation = value.substring(0, index).trim();
			pos = value.substring(index+1);
			if(fullLocation.length()==0) throw new Exception("Invalid location format: "+value);
		} else {
			fullLocation = value.trim();
			pos = "";
			if(fullLocation.length()==0 || fullLocation.equals("N/A")) { //No location
				return null;
			}
		}
		LocationDto location = getCompatibleLocation(fullLocation, user);
		LocPos locPos = location==null? null: new LocPos(location, parsePosition(location, pos));
		return locPos;
	}

	public int parsePosition(LocationDto location, String posString) throws Exception {
		if(location.getLabeling()==null) return -1;
		return locationLabelingService.getPos(location.getLabeling(), location, posString);
	}
	
	public LocationDto getCompatibleLocation(String mediumLocation, User user) {
		Set<LocationDto> res;
		try {
			res = queryLocations(mediumLocation, user);
		} catch (Exception e) {
			return null;
		}
		if(res.size()>1) {
			return null;
		} else if(res.size()==0) {
			return null;
		} else { //possibles.size==1
			try {
				return res.iterator().next();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	public List<LocationDto> queryLocation(LocationQuery q, User user) {
		List<LocationDto> locationDtos = null;
		try {
			locationDtos = locationDao.queryLocations(q, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<LocationDto> res = new ArrayList<LocationDto>();
		if(locationDtos != null) {
			List<Location> locations = mapFromDto(locationDtos);
			for (Location location : locations) {
				if(user!=null && !spiritRights.canRead(location, user)) 
					continue;
				
				res.add(map(location));
			}
		}
		return res;
	}
	
	public Set<LocationDto> queryLocations(String mediumLocation, User user) throws Exception {
		String[] split = mediumLocation.split(Constants.LOCATION_SEPARATOR, -1);
		LocationDto current = null;
		for (int i = 0; i < split.length; i++) {
			if(current==null) {
				current = getByName(getLocationRoots(), split[i]);
			} else {
				current = getByName(current.getChildren(), split[i]);
			}
			if(current==null) break;

			if(i==split.length-1) return Collections.singleton(current);
		}

		//Otherwise we search compatible location by querying the last given location
		String last = mediumLocation.trim();
		if(last.lastIndexOf(Constants.LOCATION_SEPARATOR.trim())>=0) {
			last = last.substring(last.lastIndexOf(Constants.LOCATION_SEPARATOR.trim())+Constants.LOCATION_SEPARATOR.trim().length()).trim();
		}
		Set<LocationDto> possibles = new HashSet<>();
		LocationQuery q = new LocationQuery();
		q.setName(last);
		List<LocationDto> locations = locationDao.queryLocations(q, user);

		String curatedLoc2 = mediumLocation.replaceAll("\\s*"+Constants.LOCATION_SEPARATOR+"\\s*", Constants.LOCATION_SEPARATOR).trim();
		for (LocationDto location : locations) {
			String curatedLoc1 = getHierarchyFull(location);
			if(!(Constants.LOCATION_SEPARATOR+ curatedLoc1).endsWith(Constants.LOCATION_SEPARATOR + curatedLoc2)) continue;
			possibles.add(location);
		}
		return possibles;
	}
	
	public List<LocationDto> getLocationRoots() {
		return locationDao.getLocationRoots();
	}

	public LocationDto getByName(List<LocationDto> locations, String name) {
		for (LocationDto loc : locations) {
			if(loc.getName().equals(name)) return loc;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void setLocPos(BiosampleDto biosample, LocationDto loc, int pos) {
		LocationDto location = biosample.getLocation();
		int locPos = biosample.getLocationPos();
		if(loc==location && (loc==null || locPos==pos)) {
			return;
		}
		if(loc==null) {
			location.getBiosamples().remove(biosample);
			location = null;
			locPos = -1;
		} else {
			if(loc!=location) {
				if(location!=null) {
					location.getBiosamples().remove(biosample);
				}
				biosample.setLocation(loc);
				location.getBiosamples().add(biosample);
			}
			biosample.setLocationPos(pos);
		}
	}

	public void sort(List<LocationDto> locations) {
		Collections.sort(locations, new Comparator<LocationDto>() {
			@Override
			public int compare(LocationDto o1, LocationDto o2) {
				if(o2==null) return o1==null ? 0 : -1;
				if(o1==null) return 1;
				if(o1==o2) return 0;
				return CompareUtils.compareSpecial(getHierarchyFull(o1), getHierarchyFull(o2));
			}
		});
	}

	public List<Container> getContainers(LocationDto location) {
		List<Container> containers = new ArrayList<>(biosampleService.getContainers(location.getBiosamples(), true));
		return containers;
	}

	public int getOccupancy(LocationDto location) {
		return location.getBiosamples().size();
	}

	public String getLocationId(LocationDto location) {
		return location.getId()>=0?Constants.LOCATION_PREFIX+ location.getId():"";
	}

	public Map<Integer, Container> getContainersMap(LocationDto location) {
		Map<Integer, Container> res = new HashMap<>();
		if(location.getLabeling()==LocationLabeling.NONE) {
			List<BiosampleDto> biosamples = new ArrayList<>(location.getBiosamples());
			Collections.sort(biosamples, (o1,o2) -> o1.getLocationPos()-o2.getLocationPos());
			List<Container> containers = biosampleService.getContainers(biosamples, true);
			int index = 0;
			for (Container c : containers) {
				res.put(index++, c);
			}
		} else if(location.getBiosamples()!=null) {
			for (BiosampleDto b : location.getBiosamples()) {
				if(b.getLocationPos()>=0) {
					res.put(b.getLocationPos(), b.getContainer());
				} else {
					try {
						res.put(parsePosition(location, b.getScannedPosition()), b.getContainer());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return res;
	}

	public Set<LocationDto> getChildrenRec(LocationDto location, int maxDepth) {
		Set<LocationDto> childrenRec = new HashSet<>();
		childrenRec.add(location);
		if(maxDepth>0) {
			for (LocationDto l : location.getChildren()) {
				childrenRec.addAll(getChildrenRec(l, maxDepth-1));
			}
		}
		return childrenRec;
	}

	@SuppressWarnings("deprecation")
	public void setLocationType(LocationDto location, LocationType type) {
		location.setLocationtype(type);
		if(type!=null && location.getNcols()<=0 && location.getNrows()<=0) {
			location.setLabeling(type.getPositionType());
			location.setNcols(type.getDefaultCols());
			location.setNrows(type.getDefaultRows());
		}
	}

	public boolean isEmpty(LocationDto location) {
		return location.getBiosamples().isEmpty();
	}
	
	public LocationDto getLocation(LocationDto parent, String name) {
		Location location = locationDao.getLocation(parent, name);
		return map(location);		
	}

	public void mapBiosamples(LocationDto location) {
		location.setBiosamples(biosampleService.map(biosampleService.getBiosamplesByLocationId(location.getId())));
	}
	
	public void mapChildren(LocationDto location) {
		List<LocationDto> children = new ArrayList<>();
		for(Location child : getByParent(location.getId())) {
			children.add(map(child));
		}
		location.setChildren(children);
	}

	@SuppressWarnings("deprecation")
	public LocationDto duplicate(LocationDto loc)  {
		LocationDto location = new LocationDto();

		location.setLocationtype(loc.getLocationtype());
		location.setName(loc.getName());

		location.setPrivacy(loc.getPrivacy());
		location.setDepartment(loc.getDepartment());

		location.setLabeling(loc.getLabeling());
		location.setNrows(loc.getNrows());
		location.setNcols(loc.getNcols());
		return location;

	}
	
	public List<LocationDto> duplicate(List<LocationDto> locations) {
		locations = new ArrayList<>(locations);
		sort(locations);

		//Duplicate each location
		List<LocationDto> res = new ArrayList<>();
		IdentityHashMap<LocationDto, LocationDto> old2new = new IdentityHashMap<>();
		for(LocationDto l: locations) {
			LocationDto clone = duplicate(l);
			boolean changeName;
			if(l.getParent()!=null) {
				if(old2new.get(l.getParent())!=null) {
					changeName = false;
					clone.setParent(old2new.get(l.getParent()));
				} else {
					changeName = true;
					clone.setParent(l.getParent());
				}
			} else {
				changeName = true;
			}
			if(changeName) {
				//Find a new possible Name
				String name = l.getName();
				if(name.indexOf(" (Copy ")>0) name = name.substring(0, name.indexOf(" (Copy "));
				int n = 1;
				while(getLocation(l.getParent(), name + " (Copy "+n+")")!=null) n++;
				clone.setName(name + " (Copy "+n+")");
			}

			res.add(clone);
			old2new.put(l, clone);
		}
		return res;
	}

	public Set<LocationDto> getAutomatedStoreLocation() {
		if(getAutomaticStores()==null) return new HashSet<LocationDto>();
		return getAutomaticStores().keySet();
	}
	
	public Map<LocationDto, URL> getAutomaticStores() {
		if(stores==null) {
			synchronized (this) {
				if(stores==null) {
					stores = new HashMap<>();
					try {
						LocationDto loc = getCompatibleLocation(Constants.ARKTIC_LOCATION, null);
						if(loc!=null) stores.put(loc, new URL("https://ares/spirit/order/orderNew.xhtml"));
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return stores;
	}

	public boolean isInAutomatedStore(LocationDto location) {
		return location!=null && getAutomatedStoreLocation().contains(location);
	}

	public Set<LocationDto> getParents(List<LocationDto> locations) {
		Set<LocationDto> res = new HashSet<>();
		if(locations==null) 
			return res;
		for (LocationDto loc : locations) {
			if(loc.getParent()!=null) {
				res.add(loc.getParent());
			}
		}
		return res;
	}

	public void moveLocations(List<LocationDto> locations, LocationDto parent, User user) throws Exception {
		if(parent!=null) {
			List<LocationDto> hierarchy = getHierarchy(parent);
			for (LocationDto l : locations) {
				if(hierarchy.contains(l)) 
					throw new Exception("This would create a cycle from " + l + " to " + parent);
			}
		}
		for (LocationDto l : locations) {
			l.setParent(parent);
			save(l);
		}
		Cache.getInstance().remove("locationRoots");
		Cache.getInstance().remove("locations");
	}

	public Department getInheritedDepartment(LocationDto location) {
		int depth=0;
		while(location!=null && (++depth)<10) {
			if(location.getPrivacy()==null || location.getPrivacy()==Privacy.INHERITED) {
				location = location.getParent();
			} else {
				return location.getDepartment();
			}
		}
		if(depth==10) {
			System.err.println("Cycle in "+this+":"+getHierarchy(location));
		}
		return null;
	}
}
