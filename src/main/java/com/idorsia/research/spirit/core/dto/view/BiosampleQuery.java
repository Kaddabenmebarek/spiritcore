package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.actelion.research.business.Department;
import com.idorsia.research.spirit.core.biosample.BiosampleLinker;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.util.MiscUtils;

public class BiosampleQuery implements Serializable {
	private static final long serialVersionUID = -4187187309745686340L;
	public static final int SELECT_ALL = 0;
	public static final int SELECT_MOST_LEFT = 1;
	public static final int SELECT_MOST_RIGHT = 2;

	private LocationDto locationRoot;

	/**Study.Id where the sample is assigned*/
	private Collection<Integer> sids;
	/**Biosample.Id*/
	private Collection<Integer> bids;

	private Collection<LocPos> locPoses;

	/**Locations*/
	private Collection<LocationDto> locations;


	private String elb;
	/**Study.Id of inherited Study*/
	private String studyIds;
	private String group;
	private String phases;
	private PhaseDto phase;
	private String sampleIds;
	private String containerIds;
	private String sampleIdOrContainerIds;
	private String keywords;
	private String comments;
	private BiotypeDto[] biotypes;

	private String sampleId;
	private String sampleNames;

	private String parentSampleIds;
	private String topSampleIds;

	private Map<BiosampleLinker, String> linker2values = new HashMap<>();

	private Date expiryDateMin;
	private Date expiryDateMax;
	private String updUser;
	private String updDate;
	private String creUser;
	private String updDays;
	private String creDays;
	private Department department;
	private boolean searchMySamples = false;
	private ContainerType containerType;
	private int selectOneMode = 0;
	private Quality minQuality = null;
	private Quality maxQuality = null;



	private boolean filterTrashed = false;
	private boolean filterNotInContainer = false;
	private boolean filterNotInLocation = false;
	private boolean filterInStudy = false;


	public void copyFrom(BiosampleQuery copy) {
		bids = copy.bids;
		elb = copy.elb;
		studyIds = copy.studyIds;
		group = copy.group;
		phase = copy.phase;
		sampleId = copy.sampleId;
		parentSampleIds = copy.parentSampleIds;
		topSampleIds = copy.topSampleIds;
		containerIds = copy.containerIds;
		sampleIdOrContainerIds = copy.sampleIdOrContainerIds;
		sampleNames = copy.sampleNames;
		locationRoot = copy.locationRoot;
		locations = copy.locations==null? null: new ArrayList<>(copy.locations);
		locPoses = copy.locPoses==null? null: new ArrayList<>(copy.locPoses);

		linker2values = new HashMap<>(copy.linker2values);
		biotypes = copy.biotypes;
		keywords = copy.keywords;
		comments = copy.comments;
		updUser = copy.updUser;
		updDate = copy.updDate;
		creUser = copy.creUser;
		creDays = copy.creDays;
		department = copy.department;
		searchMySamples = copy.searchMySamples;
		containerType = copy.containerType;
		selectOneMode = copy.selectOneMode;
		phase = copy.getPhase();
		phases = copy.getPhases();
		minQuality = copy.minQuality;
		maxQuality = copy.maxQuality;
		filterInStudy = copy.filterInStudy;
		filterNotInContainer = copy.filterNotInContainer;
		filterNotInLocation = copy.filterNotInLocation;
		filterTrashed = copy.filterTrashed;
	}

	public String getStudyIds() {
		return studyIds;
	}
	public void setStudyIds(String studyIds) {
		this.studyIds = studyIds;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public PhaseDto getPhase() {
		return phase;
	}
	public void setPhase(PhaseDto phase) {
		this.phase = phase;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * @param updDate the updDate to set
	 */
	public void setUpdDate(String updDate) {
		this.updDate = updDate;
	}


	public void setCreDays(int days) {
		this.creDays = days<=0? null: days+" days";
	}

	public void setUpdDays(int days) {
		this.updDays = days<=0? null: days+" days";
	}


	/**
	 * @return the updDate
	 */
	public String getUpdDate() {
		return updDate;
	}

	/**
	 * @param searchMySamples the searchMySamples to set
	 */
	public void setSearchMySamples(boolean searchMySamples) {
		this.searchMySamples = searchMySamples;
	}

	/**
	 * @return the searchMySamples
	 */
	public boolean isSearchMySamples() {
		return searchMySamples;
	}
	public void setBiotype(BiotypeDto biotype) {
		setBiotypes(biotype==null? null: new BiotypeDto[] {biotype});
	}
	public BiotypeDto getBiotype() {
		if(getBiotypes()==null || getBiotypes().length!=1) return null;
		return getBiotypes()[0];
	}

	public void setBiotypes(BiotypeDto[] biotypes) {
		this.biotypes = biotypes;
	}
	public BiotypeDto[] getBiotypes() {
		return biotypes;
	}
	public String getCreDays() {
		return creDays;
	}
	public String getCreUser() {
		return creUser;
	}
	public void setCreDays(String creDays) {
		this.creDays = creDays;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}
	public void setUpdDays(String updDays) {
		this.updDays = updDays;
	}
	public String getUpdDays() {
		return updDays;
	}

	public void setSampleNames(String sampleNames) {
		this.sampleNames = sampleNames;
	}
	public String getSampleNames() {
		return sampleNames;
	}

	public void setElbs(String elb) {
		this.elb = elb;
	}
	public String getElbs() {
		return elb;
	}

	public static BiosampleQuery createQueryForSampleNames(String sampleNames) {
		BiosampleQuery q = new BiosampleQuery();
		q.setSampleNames(sampleNames);
		return q;
	}
	public static BiosampleQuery createQueryForStudyIds(String studyIds) {
		BiosampleQuery q = new BiosampleQuery();
		q.setStudyIds(studyIds);
		return q;
	}
	public static BiosampleQuery createQueryForSids(Collection<Integer> sids) {
		BiosampleQuery q = new BiosampleQuery();
		q.setSids(sids);
		return q;
	}
	public static BiosampleQuery createQueryForBiotype(BiotypeDto biotype) {
		BiosampleQuery q = new BiosampleQuery();
		q.setBiotype(biotype);
		return q;
	}
	public static BiosampleQuery createQueryForPhase(PhaseDto phase) {
		BiosampleQuery q = new BiosampleQuery();
		q.setPhase(phase);
		return q;
	}

	public static BiosampleQuery createQueryForSampleIdOrContainerIds(String ids) {
		BiosampleQuery q = new BiosampleQuery();
		q.setSampleIdOrContainerIds(ids);
		if(ids==null || ids.length()==0) q.setSampleId("---NO ENTRY---");
		return q;
	}
	public static BiosampleQuery createQueryForContainerIds(Collection<String> ids) {
		BiosampleQuery q = new BiosampleQuery();
		q.setContainerIds(MiscUtils.flatten(ids, " "));
		if(ids==null || ids.size()==0) q.setSampleId("---NO ENTRY---");
		return q;
	}
	public static BiosampleQuery createQueryForLocPoses(Collection<LocPos> locPoses) {
		BiosampleQuery q = new BiosampleQuery();
		q.setLocPoses(locPoses);
		return q;
	}



	public String getContainerIds() {
		return containerIds;
	}

	public void setContainerIds(String containerIds) {
		this.containerIds = containerIds;
	}

	public String getSampleIdOrContainerIds() {
		return sampleIdOrContainerIds;
	}

	public void setSampleIdOrContainerIds(String sampleIdOrContainerIds) {
		this.sampleIdOrContainerIds = sampleIdOrContainerIds;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}
	public Collection<Integer> getBids() {
		return bids;
	}
	public void setBids(Collection<Integer> bids) {
		this.bids = bids;
	}

	public int getSelectOneMode() {
		return selectOneMode;
	}

	public void setSelectOneMode(int selectOneMode) {
		this.selectOneMode = selectOneMode;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationRoot(LocationDto locationRoot) {
		this.locationRoot = locationRoot;
	}

	/**
	 * @return the locationId
	 */
	public LocationDto getLocationRoot() {
		return locationRoot;
	}



	public boolean isEmpty() {
		return locationRoot==null && isNull(elb) && isNull(studyIds)
				&& isNull(group) && phase==null && isNull(sampleId) && isNull(topSampleIds) && isNull(containerIds) && isNull(sampleIdOrContainerIds)
				&& isNull(sampleNames)	&& isNull(sampleIds)
				&& (biotypes==null || biotypes.length==0)
				&& (sids==null || sids.size()==0)
				&& isNull(locations)
				&& isNull(updUser) && isNull(updDate) && isNull(creUser) && isNull(updDays) && isNull(creDays) && department==null
				&& !searchMySamples && containerType==null;
	}

	private boolean isNull(String s) {
		return s==null || s.trim().length()==0;
	}

	private boolean isNull(Collection<?> c) {
		return c==null || c.size()==0;
	}

	public String getSuggestedQueryName() {
		BiosampleQuery query = this;
		StringBuilder sb = new StringBuilder();
		if(query.getStudyIds()!=null) {
			sb.append(" "+query.getStudyIds());
			if(query.getGroup()!=null && query.getGroup().length()>0) {
				sb.append("/" + query.getGroup());
			}
			if(query.getPhase()!=null) {
				sb.append("/" + query.getPhase());
			}
			if(query.getPhases()!=null) {
				sb.append("/" + query.getPhases());
			}
		}



		if(query.getContainerType()!=null) sb.append(" "+query.getContainerType());
		if(query.getContainerIds()!=null) sb.append(" "+query.getContainerIds());


		if(query.getBiotypes()!=null && query.getBiotypes().length>0) sb.append(" "+query.getBiotypes()[0].getName());
		if(query.getSampleId()!=null) sb.append(" "+query.getSampleId());
		if(query.getParentSampleIds()!=null) sb.append(" "+query.getParentSampleIds());
		if(query.getTopSampleIds()!=null) sb.append(" "+query.getTopSampleIds());

		if(query.getSampleNames()!=null) sb.append(" "+query.getSampleNames());
		//		if(query.getParentName()!=null) sb.append(" "+query.getParentName());
		//		if(query.getTopName()!=null) sb.append(" "+query.getTopName());

		if(query.getKeywords()!=null) sb.append(" "+query.getKeywords());

		for(String s :query.getLinker2values().values()) {
			if(s!=null) sb.append(" "+s);
		}

		if(query.getDepartment()!=null) {
			sb.append(" "+query.getDepartment());
		}

		if(query.getCreUser()!=null) {
			sb.append(" "+query.getCreUser());
		}
		if(query.getCreDays()!=null) {
			sb.append(" "+query.getCreDays());
		}
		if(query.getUpdUser()!=null) {
			sb.append(" "+query.getUpdUser());
		}
		if(query.getUpdDays()!=null) {
			sb.append(" "+query.getUpdDays());
		}


		String n = sb.toString().trim();
		n = n.replaceAll("[ ]+", " ");
		n = n.replaceAll("[ ]+:", ":");
		return n;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getParentSampleIds() {
		return parentSampleIds;
	}

	public void setParentSampleIds(String parentSampleId) {
		this.parentSampleIds = parentSampleId;
	}

	public String getTopSampleIds() {
		return topSampleIds;
	}

	public void setTopSampleIds(String topSampleId) {
		this.topSampleIds = topSampleId;
	}

	public String getSampleIds() {
		return sampleIds;
	}
	public void setSampleIds(String sampleIds) {
		this.sampleIds = sampleIds;
	}

	public boolean isFilterTrashed() {
		return filterTrashed;
	}

	public void setFilterTrashed(boolean filterTrashed) {
		this.filterTrashed = filterTrashed;
	}

	public boolean isFilterNotInContainer() {
		return filterNotInContainer;
	}

	public void setFilterNotInContainer(boolean filterNotInContainer) {
		this.filterNotInContainer = filterNotInContainer;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the quality
	 */
	public Quality getMinQuality() {
		return minQuality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setMinQuality(Quality quality) {
		this.minQuality = quality;
	}

	public Quality getMaxQuality() {
		return maxQuality;
	}

	public void setMaxQuality(Quality maxQuality) {
		this.maxQuality = maxQuality;
	}

	/**
	 * @return the filterInStudy
	 */
	public boolean isFilterInStudy() {
		return filterInStudy;
	}


	public boolean isFilterNotInLocation() {
		return filterNotInLocation;
	}

	public void setFilterNotInLocation(boolean filterNotInLocation) {
		this.filterNotInLocation = filterNotInLocation;
	}

	/**
	 * @param filterInStudy the filterInStudy to set
	 */
	public void setFilterInStudy(boolean filterInStudy) {
		this.filterInStudy = filterInStudy;
	}

	public Map<BiosampleLinker, String> getLinker2values() {
		return linker2values;
	}

	public Collection<Integer> getSids() {
		return sids;
	}

	public void setSids(Collection<Integer> sids) {
		this.sids = sids;
	}

	/**
	 * @return the locations
	 */
	public Collection<LocationDto> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(Collection<LocationDto> locations) {
		this.locations = locations;
	}

	public Date getExpiryDateMin() {
		return expiryDateMin;
	}

	public void setExpiryDateMin(Date expiryDateMin) {
		this.expiryDateMin = expiryDateMin;
	}

	public Date getExpiryDateMax() {
		return expiryDateMax;
	}

	public void setExpiryDateMax(Date expiryDateMax) {
		this.expiryDateMax = expiryDateMax;
	}

	public String getPhases() {
		return phases;
	}
	public void setPhases(String phases) {
		this.phases = phases;
	}
	public Collection<LocPos> getLocPoses() {
		return locPoses;
	}
	public void setLocPoses(Collection<LocPos> locPoses) {
		this.locPoses = locPoses;
	}
}
