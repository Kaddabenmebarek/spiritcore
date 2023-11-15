package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Set;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.dto.BiosampleDto;

public class Container implements Comparable<Container>, Serializable{

	private static final long serialVersionUID = 8315573139612459641L;
	private String containerId;
	private ContainerType containerType;
	private BiosampleDto createdFor;
	private Set<BiosampleDto> biosamples = null;

	public Container() {
	}

	public Container(ContainerType type) {
		this(type, null);
	}

	public Container(String containerId) {
		this(ContainerType.UNKNOWN, null);
	}

	public Container(ContainerType type, String containerId) {
		this.containerType = type;
		this.containerId = containerId;
	}
	
	public BiosampleDto getCreatedFor() {
		return createdFor;
	}
	
	public void setCreatedFor(BiosampleDto createdFor) {
		this.createdFor = createdFor;
	}
	
	/**
	 *Do not call this method directly, call the service instead
	 *ContainerService.getBiosamples(this);
	 */
	@Deprecated
	public Set<BiosampleDto> getBiosamples() {
		return biosamples;
	}
	
	/**
	 * Do not call this method directly, call the service instead
	 * ContainerService.setBiosamples(this, biosamples);
	 */
	public void setBiosamples(Set<BiosampleDto> biosamples) {
		this.biosamples = biosamples;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}

	@Override
	public int compareTo(Container c) {
		if(c==null) return -1;
		int cmp = CompareUtils.compare(getContainerType(), c.getContainerType());
		if(cmp!=0) return cmp;

		cmp = CompareUtils.compare(getContainerId(), c.getContainerId());
		return cmp;
	}

	/**
	 * 2 containers are equals if they have the same type and the same not-null id.
	 * Ie. 2 containers without Id will always be different
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Container)) return false;
		if(this==obj) return true;

		Container c2 = (Container) obj;
		int cmp = CompareUtils.compare(getContainerType(), c2.getContainerType());
		if(cmp!=0) return false;

		cmp = CompareUtils.compare(getContainerId(), c2.getContainerId());
		return cmp==0 && getContainerId()!=null && getContainerId().length()>0;
	}
}
