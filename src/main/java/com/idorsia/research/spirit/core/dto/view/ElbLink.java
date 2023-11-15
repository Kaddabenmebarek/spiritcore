package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import com.actelion.research.util.CompareUtils;

public class ElbLink implements Comparable<ElbLink>, Serializable {
	private static final long serialVersionUID = -1919563144597779926L;
	public String elb;
	boolean inSpirit;
	URL url;
	public String title;
	String scientist;
	Date creDate;
	Date pubDate;

	public String getElb() {
		return elb;
	}

	public boolean isInSpirit() {
		return inSpirit;
	}

	public boolean isInNiobe() {
		return title != null;
	}

	public URL getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String getScientist() {
		return scientist;
	}

	public Date getCreDate() {
		return creDate;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public void setInSpirit(boolean inSpirit) {
		this.inSpirit = inSpirit;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setScientist(String scientist) {
		this.scientist = scientist;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	public String toString() {
		return elb + ": " + (title != null ? title : "") + (scientist != null ? " " + scientist : "")
				+ (pubDate != null ? " Pub:" + pubDate : "");
	}

	@Override
	public int compareTo(ElbLink o) {
		return CompareUtils.compare(elb, o.elb);
	}
}
