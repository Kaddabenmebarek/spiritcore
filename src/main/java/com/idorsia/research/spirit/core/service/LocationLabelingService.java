package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Direction;
import com.idorsia.research.spirit.core.constants.LocationLabeling;
import com.idorsia.research.spirit.core.dto.LocationDto;

@Service
public class LocationLabelingService implements Serializable {

	private static final long serialVersionUID = -5085943593885839724L;
	@Autowired
	private LocationService locationService;
	
	public String formatPosition(LocationLabeling locationLabeling, LocationDto loc, int pos) {
		if(pos<0) return "";
		switch(locationLabeling) {
		case NUM:
			return "" + (pos+1);
		case NUM_I:
			return "" + (locationService.getCol(loc, pos) * loc.getNrows() + locationService.getRow(loc, pos) + 1);
		case ALPHA:
			return (""+ (char)(locationService.getRow(loc, pos)+'A')) + "/" + new DecimalFormat("00").format(locationService.getCol(loc, pos)+1);
		default:
			return "";
		}
	}
	
	public String formatPosition(LocationLabeling locationLabeling, LocationDto loc, int row, int col) {
		return formatPosition(locationLabeling, loc, getPos(loc, row, col));
	}

	public int getPos(LocationLabeling locationLabeling, LocationDto loc, String posString) throws Exception {
		int pos;
		switch(locationLabeling) {
		case NUM:
			if(posString.length()==0) throw new IllegalArgumentException("Position is required");
			try {
				pos = Integer.parseInt(posString)-1;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Invalid position for "+locationService.getHierarchyFull(loc)+": "+posString);
			}
			if(locationService.getSize(loc)>=0 && (pos<0 || pos>=locationService.getSize(loc))) throw new Exception("Out of Range Position for "+locationService.getHierarchyFull(loc));
			return pos;
		case NUM_I:
			
			if(posString.length()==0) throw new IllegalArgumentException("Position is required");
			try {
				int val = Integer.parseInt(posString)-1;
				int row = val % loc.getNrows();
				int col = val / loc.getNrows();
				pos = row * loc.getNcols() + col; 
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid position for "+locationService.getHierarchyFull(loc));
			}
			
			
			if(locationService.getSize(loc)>=0 && (pos<0 || pos>=locationService.getSize(loc))) throw new Exception("Out of Range Position for "+locationService.getHierarchyFull(loc));
			return pos;
			
		case ALPHA: // could be formatted like A/1, A/01, A1, A01
			posString = posString.toUpperCase();
			if(posString.length()==0) throw new Exception("Position is required in "+locationService.getHierarchyFull(loc));
			try {
				int row = posString.charAt(0) - 'A';
				int col = posString.charAt(1)=='/'? Integer.parseInt(posString.substring(2))-1: Integer.parseInt(posString.substring(1))-1;
				if(row<0 || row>=loc.getNrows()) throw new Exception("Invalid row "+row+">="+loc.getNrows());
				if(col<0 || col>=loc.getNcols()) throw new Exception("Invalid column");
				pos = row * loc.getNcols() + col;
				return pos;
			} catch (Exception e) {
				throw new Exception("Could not parse: "+posString+"  (Required Format (" + ((char)('A'+loc.getNrows()) + "/" + new DecimalFormat("00").format(1+loc.getNcols())) + " for " + locationService.getHierarchyFull(loc)+"): "+e);
			}
		default:
			return -1;
		}
	}
	
	public int getPos(LocationDto loc, int row, int col) {
		if(loc!=null && loc.getNcols()>=0 && loc.getNrows()>=0) {
			return row*loc.getNcols()+col;
		} else {
			return -1;
		}
	}
	
	
	/**
	 * Return the next index using the appropriate direction
	 * @return
	 */
	public int getNext(LocationDto location, int startPos, Direction dir, int n) {
		if(startPos<0 || startPos>=locationService.getSize(location)) return -1;
		switch(dir) {
		case LEFT_RIGHT:
			if(startPos+n>=locationService.getSize(location)) return -1;
			return startPos+n;
		case TOP_BOTTOM:
			int row = locationService.getRow(location, startPos);
			int col = locationService.getCol(location, startPos);
			
			col = col + (row + n) / location.getNrows();			
			row = (row + n) % location.getNrows();
			if(col>=location.getNcols() || row>=location.getNrows()) return -1;
			
			return getPos(location, row, col);
		default:
			throw new IllegalArgumentException("Invalid direction: "+dir);
		}
	}
	
	public int getNextForPattern(LocationDto location, int startPos, int originalPosForPattern) {
		if(startPos<0 || startPos>=locationService.getSize(location)) return -1;
		int pos = startPos + originalPosForPattern;				
		if(pos>=locationService.getSize(location)) return -1;
		return pos;
		
	}

	public LocationLabeling get(String toStringRepresentation) {
		for (LocationLabeling l : LocationLabeling.values()) {
			if(l.getName().equalsIgnoreCase(toStringRepresentation)) return l;
		}
		return null;
	}
}
