package com.idorsia.research.spirit.core.util;

import com.actelion.research.util.CompareUtils;

public class Triple<FIRST, SECOND, THIRD> implements Comparable<Triple<FIRST, SECOND, THIRD>>{
	private final FIRST first;
	private final SECOND second;
	private final THIRD third;
	
	public Triple(FIRST first, SECOND second, THIRD third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public FIRST getFirst() {
		return first;
	}
	
	public SECOND getSecond() {
		return second;
	}
	
	public THIRD getThird() {
		return third;
	}
	
	@Override
	public int hashCode() {
		return ((first==null?0: first.hashCode()) + (second==null?0: second.hashCode()))%Integer.MAX_VALUE;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		Triple<FIRST, SECOND, THIRD> p = (Triple<FIRST, SECOND, THIRD>) obj;
		if(first==null && p.first!=null) return false;
		if(first!=null && !first.equals(p.first)) return false;
		
		if(second==null && p.second!=null) return false; 
		if(second!=null && !second.equals(p.second)) return false;
		
		if(third==null && p.third!=null) return false; 
		if(third!=null && !third.equals(p.third)) return false;
		
		return true;
	}
	
	@Override
	public int compareTo(Triple<FIRST, SECOND, THIRD> o) {
		if(o==null) return -1;
		int c = CompareUtils.compare(first, o.first);
		if(c!=0) return c;
		c = CompareUtils.compare(second, o.second);
		if(c!=0) return c;
		c = CompareUtils.compare(third, o.third);
		return c;
	}
	
	
	@Override
	public String toString() {
		return "<" + first+", " + second + ", " + third + ">";
	}

}
