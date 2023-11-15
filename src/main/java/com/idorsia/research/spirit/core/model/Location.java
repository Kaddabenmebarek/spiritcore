package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Location implements Serializable, IObject {

	private static final long serialVersionUID = -156420162100931385L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer ncols;
	private String name;
	private Integer privacy;
	private Integer nrows;
	private Integer departmentId;
	private Integer parentId;
	private String locationtype;
	private String labeling;
	private String description;
	private String flag;
	private String creUser;
	private String updUser;
	private Date creDate;
	private Date updDate;

	public Location() {
		super();
	}

	public Location(Integer id, Integer ncols, String name, Integer privacy, Integer nrows, String locationtype) {
		super();
		this.id = id;
		this.ncols = ncols;
		this.name = name;
		this.privacy = privacy;
		this.nrows = nrows;
		this.locationtype = locationtype;
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

	public Integer getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Integer privacy) {
		this.privacy = privacy;
	}

	public Integer getNrows() {
		return nrows;
	}

	public void setNrows(Integer nrows) {
		this.nrows = nrows;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getLocationtype() {
		return locationtype;
	}

	public void setLocationtype(String locationtype) {
		this.locationtype = locationtype;
	}

	public String getLabeling() {
		return labeling;
	}

	public void setLabeling(String labeling) {
		this.labeling = labeling;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((flag == null) ? 0 : flag.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((labeling == null) ? 0 : labeling.hashCode());
		result = prime * result + ((locationtype == null) ? 0 : locationtype.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ncols == null) ? 0 : ncols.hashCode());
		result = prime * result + ((nrows == null) ? 0 : nrows.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((privacy == null) ? 0 : privacy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (creDate == null) {
			if (other.creDate != null)
				return false;
		} else if (!creDate.equals(other.creDate))
			return false;
		if (creUser == null) {
			if (other.creUser != null)
				return false;
		} else if (!creUser.equals(other.creUser))
			return false;
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (flag == null) {
			if (other.flag != null)
				return false;
		} else if (!flag.equals(other.flag))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (labeling == null) {
			if (other.labeling != null)
				return false;
		} else if (!labeling.equals(other.labeling))
			return false;
		if (locationtype == null) {
			if (other.locationtype != null)
				return false;
		} else if (!locationtype.equals(other.locationtype))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ncols == null) {
			if (other.ncols != null)
				return false;
		} else if (!ncols.equals(other.ncols))
			return false;
		if (nrows == null) {
			if (other.nrows != null)
				return false;
		} else if (!nrows.equals(other.nrows))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (privacy == null) {
			if (other.privacy != null)
				return false;
		} else if (!privacy.equals(other.privacy))
			return false;
		return true;
	}

}
