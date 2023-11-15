package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.service.LocationLabelingService;
import com.idorsia.research.spirit.core.service.LocationService;

public class LocPos implements Comparable<LocPos>, Serializable {

	private static final long serialVersionUID = -3669716558485777879L;
	@Autowired
	private LocationLabelingService locationLabelingService;
	@Autowired
	private LocationService locationService;

	private LocationDto location;
	private int pos = -1;


	public LocPos() {
	}

	public LocPos(LocationDto location) {
		this(location, -1);
	}
	public LocPos(LocationDto location, int pos) {
		this.location = location;
		this.pos = pos;
	}

	public LocationDto getLocation() {
		return location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getRow() {
		if(getLocation()==null) return 0;
		return locationService.getRow(location, pos);
	}

	public int getCol() {
		if(getLocation()==null) return 0;
		return locationService.getCol(location, pos);
	}


	@Override
	public String toString() {
		return location==null?"": locationLabelingService.formatPosition(location.getLabeling(), location, pos);
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof LocPos)) return false;
		LocPos l = (LocPos) obj;
		return CompareUtils.compare(location, l.location)==0 && pos==l.pos;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public int compareTo(LocPos o) {
		int c = CompareUtils.compare(location, o.getLocation());
		if(c!=0) return c;
		return pos-o.pos;
	}
}
