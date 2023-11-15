package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;

import com.actelion.research.business.Department;
import com.idorsia.research.spirit.core.constants.LocationType;
import com.idorsia.research.spirit.core.dto.BiotypeDto;

public class LocationQuery implements Serializable {

	private static final long serialVersionUID = -1997060360405340083L;
	private String studyId;
	private String name;
	private LocationType locationType;
	private Department employeeGroup;
	private BiotypeDto biotype;
	private Boolean onlyOccupied;
	private Integer occupiedStatus;
	private boolean filterAdminLocation;

	public LocationQuery() {}

	/**
	 * Query by location Name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Query by location Name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param study the study to set
	 */
	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return the study
	 */
	public String getStudyId() {
		return studyId;
	}

	/**
	 * @param locationType the locationType to set
	 */
	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	/**
	 * @return the locationType
	 */
	public LocationType getLocationType() {
		return locationType;
	}

	public boolean isEmpty() {
		return getOnlyOccupied()==null && (getStudyId()==null || getStudyId().length()==0)
				&& getDepartment()==null && getBiotype()==null
				&& getLocationType()==null && (getName()==null || getName().length()==0);
	}

	/**
	 * Query location restristed to the given group
	 * @param employeeGroup the employeeGroup to set
	 */
	public void setDepartment(Department employeeGroup) {
		this.employeeGroup = employeeGroup;
	}

	/**
	 * Query location restristed to the given group
	 * @return the employeeGroup
	 */
	public Department getDepartment() {
		return employeeGroup;
	}

	/**
	 * Query location containing the give biotype
	 * @return the biotype
	 */
	public BiotypeDto getBiotype() {
		return biotype;
	}

	/**
	 * Query location containing the give biotype
	 * @param biotype the biotype to set
	 */
	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}

	/**
	 * Do we query only occupied boxes (true), empty boxes (false) or all (null)
	 * @param onlyOccupied
	 */
	public void setOnlyOccupied(Boolean onlyOccupied) {
		this.onlyOccupied = onlyOccupied;
	}

	/**
	 * Do we query only occupied boxes (true), empty boxes (false) or all (null)
	 * @return
	 */
	public Boolean getOnlyOccupied() {
		return onlyOccupied;
	}

	/**
	 * Do we query only pure location (building, freezer)
	 * @return
	 */
	public boolean isFilterAdminLocation() {
		return filterAdminLocation;
	}
	/**
	 * Set to true to return only pure location (building, freezer)
	 * @param filterAdminLocation
	 */
	public void setFilterAdminLocation(boolean filterAdminLocation) {
		this.filterAdminLocation = filterAdminLocation;
	}

	
	/**
	 * @return occupiedStatus
	 * 0 - none
	 * 1 - onlyOccupied
	 * 2 - onlyEmpty
	 * 3 - both (occupied and empty)
	 */
	public Integer getOccupiedStatus() {
		return occupiedStatus;
	}

	/**
	 * @param occupiedStatus
	 * 0 - none
	 * 1 - onlyOccupied
	 * 2 - onlyEmpty
	 * 3 - both (occupied and empty) 
	 */
	public void setOccupiedStatus(Integer occupiedStatus) {
		this.occupiedStatus = occupiedStatus;
		if(occupiedStatus == 1) {
			setOnlyOccupied(Boolean.TRUE);
		}else {
			setOnlyOccupied(Boolean.FALSE);
		}
	}

}
