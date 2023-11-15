package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.util.SetHashMap;

public class ResultQuery implements Serializable {

	private static final long serialVersionUID = 102127864860941592L;
	private Collection<Integer> stids = null;
	private Collection<Integer> bids = new HashSet<>();

	private int maxResults = -1;
	private PhaseDto phase;
	private String phases;
	private int sid;
	private String sampleId;
	private String containerIds;
	private String topSampleIds;
	private String biosampleParentId;
	private String studyIds;
	private String group;
	private String subgroupId;
	private String keywords;
	private String biotype;
	private String biosampleId;
	private String updUser;
	private String updDays;
	private String creDate;
	private String resultElbs;
	private Quality minQuality = null;
	private Set<Integer> assayIds = new TreeSet<>();
	private SetHashMap<AssayAttributeDto, String> attribute2values = new SetHashMap<>();
	private Set<String> inputs = new TreeSet<>();
	private Set<String> biotypes = new TreeSet<>();
	private String compoundId;

	public ResultQuery() { }

	public static ResultQuery createQueryForElb(String elbs) {
		ResultQuery query = new ResultQuery();
		query.setQuality(null);
		if(elbs!=null && elbs.length()>0) {
			query.setElbs(elbs);
		} else {
			query.setElbs("????");
		}

		return query;
	}

	public static ResultQuery createQueryForBiosampleId(int biosampleId) {
		ResultQuery query = new ResultQuery();
		query.setQuality(null);
		query.setBid(biosampleId);
		return query;
	}

	public static ResultQuery createQueryForBiosampleIds(Collection<Integer> biosampleIds) {
		ResultQuery query = new ResultQuery();
		query.setQuality(null);
		query.setBids(biosampleIds);
		return query;
	}

	public static ResultQuery createQueryForPhase(PhaseDto phase) {
		ResultQuery query = new ResultQuery();
		query.setPhase(phase);
		query.setQuality(null);
		return query;
	}

	public static ResultQuery createQueryForStudyIds(String studyIds) {
		ResultQuery query = new ResultQuery();
		query.setStudyIds(studyIds);
		return query;
	}

	public static ResultQuery createQueryForSids(Collection<Integer> sids) {
		ResultQuery query = new ResultQuery();
		query.setSids(sids);
		return query;
	}

	public void copyFrom(ResultQuery query) {
		this.bids = query.bids;
		this.phase = query.phase;
		this.topSampleIds = query.topSampleIds;
		this.sampleId = query.sampleId;
		this.studyIds = query.studyIds;
		this.group = query.group;
		this.resultElbs = query.resultElbs;
		this.keywords = query.keywords;
		this.biotype = query.biotype;
		this.updUser = query.updUser;
		this.updDays = query.updDays;
		this.compoundId = query.compoundId;

		this.setQuality(query.minQuality);
		this.setAssayIds(new HashSet<>(query.assayIds));
		this.setAttribute2Values(new SetHashMap<>(query.attribute2values));
	}

	public String getSampleIds() {
		return sampleId;
	}
	public void setSampleIds(String sampleId) {
		this.sampleId = sampleId;
	}
	public String getGroups() {
		return group;
	}
	public void setGroups(String group) {
		this.group = group;
	}
	public String getSubgroupId() {
		return subgroupId;
	}
	public void setSubgroupId(String subgroupId) {
		this.subgroupId = subgroupId;
	}
	public void setAssayIds(Set<Integer> assayIds) {
		this.assayIds = assayIds;
	}
	public Set<Integer> getAssayIds() {
		return assayIds;
	}
	public Collection<Integer> getBids() {
		return bids;
	}
	public void setElbs(String resultElbs) {
		this.resultElbs = resultElbs;
	}
	public String getElbs() {
		return resultElbs;
	}
	public void setStudyIds(String studyIdsString) {
		this.studyIds = studyIdsString;
	}
	public String getStudyIds() {
		return studyIds;
	}
	public void setBid(int biosampleId) {
		bids.clear();
		bids.add(biosampleId);
	}
	public void setBids(Collection<Integer> ids) {
		bids.clear();
		bids.addAll(ids);
	}
	public void setAttribute2Values(SetHashMap<AssayAttributeDto, String> attributes) {
		this.attribute2values = attributes;
	}
	public SetHashMap<AssayAttributeDto, String> getAttribute2Values() {
		return attribute2values;
	}
	public Set<String> getInputs() {
		return inputs;
	}
	public Set<String> getBiotypes() {
		return biotypes;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getKeywords() {
		return keywords;
	}
	public boolean isSpecificToStudy() {
		return sid!=0 || (studyIds!=null && studyIds.length()>0) || (phases!=null && phases.length()>0) || (group!=null && group.length()>0);
	}
	public void setPhase(PhaseDto phase) {
		this.phase = phase;
	}
	public PhaseDto getPhase() {
		return phase;
	}
	public String getBiotype() {
		return biotype;
	}
	public void setBiotype(String biotype) {
		this.biotype = biotype;
	}
	public String getBiosampleId() {
		return biosampleId;
	}
	public void setBiosampleId(String biosampleId) {
		this.biosampleId = biosampleId;
	}

	public String getUpdUser() {
		return updUser;
	}
	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}
	public String getUpdDate() {
		return updDays;
	}
	public void setUpdDate(String updDays) {
		this.updDays = updDays;
	}
	public Quality getQuality() {
		return minQuality;
	}
	public void setQuality(Quality quality) {
		this.minQuality = quality;
	}

	/**
	 * @param sid the study.id to set
	 */
	public void setSid(int sid) {
		this.sid = sid;
	}

	/**
	 * @return the studyId
	 */
	public int getSid() {
		return sid;
	}

	public String getTopSampleIds() {
		return topSampleIds;
	}

	public void setTopSampleIds(String topSampleIds) {
		this.topSampleIds = topSampleIds;
	}
	
	public String getBiosampleParentId() {
		return biosampleParentId;
	}

	public void setBiosampleParentId(String biosampleParentId) {
		this.biosampleParentId = biosampleParentId;
	}

	public String getQueryKey() {
		ResultQuery query = this;
		StringBuilder sb = new StringBuilder();
		if(query.getStudyIds()!=null) {
			sb.append(query.getStudyIds());
			if(query.getGroups()!=null && query.getGroups().length()>0) {
				sb.append("/" + query.getGroups());
			}
			if(query.getPhase()!=null) {
				sb.append("/" + query.getPhase());
			}
		}
		if(query.getBiotype()!=null) {
			sb.append(" "+query.getBiotype());
		}

		if(query.getElbs()!=null) {
			sb.append(" "+query.getElbs());
		}
		if(query.getTopSampleIds()!=null) {
			sb.append(" "+query.getTopSampleIds());
		}
		if(query.getContainerIds()!=null) {
			sb.append(" "+query.getContainerIds());
		}
		if(query.getSampleIds()!=null) {
			sb.append(" "+query.getSampleIds());
		}
		if(query.getAssayIds().size()>0) {
			for (Integer id : query.getAssayIds()) {
				sb.append(" "+id);
			}
		}
		if(query.getCompoundIds()!=null) {
			sb.append(" "+query.getCompoundIds());
		}
		if(inputs.size()>0) {
			for (String s : inputs) {
				sb.append(" "+s);
			}
		}
		if(query.getUpdUser()!=null && query.getUpdUser().length()>0) {
			sb.append(" "+query.getUpdUser());
		}
		if(query.getUpdDate()!=null && query.getUpdDate().length()>0) {
			sb.append(" "+query.getUpdDate()+"days");
		}
		String n = sb.toString().trim();
		n = n.replaceAll("[ ]+", " ");
		n = n.replaceAll("[ ]+:", ":");
		return n;
	}

	public String getCreDays() {
		return creDate;
	}
	public void setCreDays(String creDate) {
		this.creDate = creDate;
	}

	public void setCreDays(int days) {
		this.creDate = days<=0? null: days+" days";
	}

	public void setUpdDays(int days) {
		this.updDays = days<=0? null: days+" days";
	}

	public boolean isEmpty() {
		if(sid>0) return false;
		if(studyIds!=null && studyIds.length()>0) return false;

		String sugg = getQueryKey();
		return sugg.length()==0 || sugg.equals("AllTests");
	}


	public Collection<Integer> getSids() {
		return stids;
	}
	public void setSids(Collection<Integer> stids) {
		this.stids = stids;
	}

	public String getPhases() {
		return phases;
	}
	public void setPhases(String phases) {
		this.phases = phases;
	}
	public String getContainerIds() {
		return containerIds;
	}
	public void setContainerIds(String containerIds) {
		this.containerIds = containerIds;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!(obj instanceof ResultQuery)) return false;
		return getQueryKey().equals(((ResultQuery)obj).getQueryKey());
	}

	public String getCompoundIds() {
		return compoundId;
	}

	public void setCompoundIds(String compoundId) {
		this.compoundId = compoundId;
	}

}
