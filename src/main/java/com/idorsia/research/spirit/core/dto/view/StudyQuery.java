package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StudyQuery implements Serializable {
	private static final long serialVersionUID = 293564411873273897L;
	private String studyIds = "";
	private String localIds = "";
	private String keywords = "";
	private String state = "";
	private String type = "";
	private Map<String, String> metadataMap = new HashMap<>();
	private String user = "";
	private String updDays = "";
	private String creDays = "";

	private int recentStartDays = -1;

	public StudyQuery() {
	}

	public static StudyQuery createForStudyIds(String studyIds) {
		StudyQuery q = new StudyQuery();
		q.setStudyIds(studyIds);
		return q;
	}
	public static StudyQuery createForLocalId(String localIds) {
		StudyQuery q = new StudyQuery();
		q.setLocalIds(localIds);
		return q;
	}
	public static StudyQuery createForState(String state) {
		StudyQuery q = new StudyQuery();
		q.setState(state);
		return q;
	}
	public String getStudyIds() {
		return studyIds;
	}
	public void setStudyIds(String studyIds) {
		this.studyIds = studyIds;
	}
	public String getLocalIds() {
		return localIds;
	}
	public void setLocalIds(String localIds) {
		this.localIds = localIds;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, String> getMetadataMap() {
		return metadataMap;
	}
	public String getMetadata(String metadata) {
		return metadataMap.get(metadata);
	}
	public void setMetadata(String metadata, String value) {
		metadataMap.put(metadata, value);
	}

	public void copyFrom(StudyQuery query) {
		this.studyIds = query.studyIds;
		this.keywords = query.keywords;
		this.user = query.user;
		this.state = query.state;
		this.type = query.type;
		this.updDays = query.updDays;
		this.creDays = query.creDays;
		this.recentStartDays = query.recentStartDays;
		this.metadataMap = new HashMap<>(query.metadataMap);
	}

	public String getUpdDays() {
		return updDays;
	}
	public void setUpdDays(String updDays) {
		this.updDays = updDays;
	}

	public String getCreDays() {
		return creDays;
	}
	public void setCreDays(String creDays) {
		this.creDays = creDays;
	}

	public void setCreDays(int days) {
		setCreDays(days<=0? null: days+" days");
	}

	public void setUpdDays(int days) {
		setUpdDays(days<=0? null: days+" days");
	}


	public int getRecentStartDays() {
		return recentStartDays;
	}
	public void setRecentStartDays(int recentStartDays) {
		this.recentStartDays = recentStartDays;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		if ( studyIds != null && !studyIds.equals("") ) {
			buf.append("Study Ids: ");
			buf.append(studyIds);
		}
		if ( keywords != null && !keywords.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("Keywords='");
			buf.append(keywords);
			buf.append("'");
		}
		if ( user != null && !user.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("User='");
			buf.append(user);
			buf.append("'");
		}
		if ( state != null && !state.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("Status='");
			buf.append(state);
			buf.append("'");
		}
		if ( type != null && !type.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("Type'");
			buf.append(type);
			buf.append("'");
		}
		if ( updDays != null && !updDays.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("Upd Days='");
			buf.append(updDays);
			buf.append("'");
		}
		if ( creDays != null && !creDays.equals("") ) {
			if ( buf.length() > 0 ) buf.append(" | ");
			buf.append("Cre Days'");
			buf.append(creDays);
			buf.append("'");
		}
		if ( metadataMap != null ) {
			for (Map.Entry<String, String> entry : metadataMap.entrySet()) {
				if ( entry == null || entry.getKey() == null || entry.getKey().equals("")) {
					continue;
				}
				
				if ( entry.getKey().equals("Dir") && !entry.getValue().equals("") ) {
					if ( buf.length() > 0 ) buf.append(" | ");
					buf.append("Study Director='");
					buf.append(entry.getValue());
					buf.append("'");
				}
				if ( entry.getKey().equals("PriInv") && !entry.getValue().equals("") ) {
					if ( buf.length() > 0 ) buf.append(" | ");
					buf.append("Principal Investigator='");
					buf.append(entry.getValue());
					buf.append("'");
				}
				if ( entry.getKey().equals("GLP") && !entry.getValue().equals("") ) {
					if ( buf.length() > 0 ) buf.append(" | ");
					buf.append("GLP='");
					buf.append(entry.getValue());
					buf.append("'");
				}
			}
		}
		if ( buf.length() == 0 ) {
			return "n/a";
		}
			
		return buf.toString();
	}

}
