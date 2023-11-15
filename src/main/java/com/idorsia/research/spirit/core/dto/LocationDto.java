package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.business.Department;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.LocationFlag;
import com.idorsia.research.spirit.core.constants.LocationLabeling;
import com.idorsia.research.spirit.core.constants.LocationType;
import com.idorsia.research.spirit.core.constants.Privacy;
import com.idorsia.research.spirit.core.service.LocationService;

@Component
public class LocationDto implements IObject, Comparable<LocationDto>, Serializable{

	private static final long serialVersionUID = -6637747177325167923L;
	@Autowired
	private LocationService locationService;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer ncols=0;
	private String name;
	private Privacy privacy=Privacy.INHERITED;
	private Integer nrows=0;
	private Department department;
	private LocationDto parent;
	private LocationType locationtype;
	private LocationLabeling labeling;
	private String description;
	private LocationFlag flag;
	private String creUser;
	private String updUser;
	private Date creDate;
	private Date updDate;
	private List<BiosampleDto> biosamples;
	private List<LocationDto> children;
	
	public LocationDto(LocationDto parent, String name) {
		this.parent = parent;
		this.name = name;
		this.labeling = LocationLabeling.NONE;
	}

	public LocationDto(String name) {
		this(null, name);
	}

	public LocationDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNcols() {
		return ncols;
	}

	public void setNcols(Integer ncols) {
		this.ncols = ncols;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

	public Integer getNrows() {
		return nrows;
	}

	public void setNrows(Integer nrows) {
		this.nrows = nrows;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public LocationDto getParent() {
		return parent;
	}

	public void setParent(LocationDto parent) {
		this.parent = parent;
	}

	public LocationType getLocationtype() {
		return locationtype;
	}

	/**Do not call this method directly but call the service instead 
	 *LocationService.setLocationType(this, locationType);
	 */
	@Deprecated
	public void setLocationtype(LocationType locationtype) {
		this.locationtype = locationtype;
	}

	public LocationLabeling getLabeling() {
		return labeling==null?LocationLabeling.NONE:labeling;
	}

	public void setLabeling(LocationLabeling labeling) {
		this.labeling = labeling;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocationFlag getFlag() {
		return flag;
	}

	public void setFlag(LocationFlag flag) {
		this.flag = flag;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public List<BiosampleDto> getBiosamples() {
		if(biosamples == null) {
			getLocationService().mapBiosamples(this);
		}
		return biosamples;
	}

	@Deprecated
	public List<BiosampleDto> getBiosamplesNoMapping() {
		return biosamples;
	}
	
	public void setBiosamples(List<BiosampleDto> biosamples) {
		this.biosamples = biosamples;
	}

	public List<LocationDto> getChildren() {
		if(children == null) {
			getLocationService().mapChildren(this);
		}
		return children;
	}
	
	@Deprecated
	public List<LocationDto> getChildrenNoMapping() {
		return children;
	}

	public void setChildren(List<LocationDto> children) {
		this.children = children;
	}

	public LocationService getLocationService() {
		if(locationService == null) {
			locationService = (LocationService) ContextShare.getContext().getBean("locationService");
		}
		return locationService;
	}

	@Override
	public int compareTo(LocationDto o) {
		if(o==null) return 1;
        return (name==null?"":name).compareTo(o.getName());
	}
	
}
