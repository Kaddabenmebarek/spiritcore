package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.business.Department;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Comparators;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.Quality;
import com.idorsia.research.spirit.core.constants.Status;
import com.idorsia.research.spirit.core.dto.view.Container;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.service.BiosampleService;

@Component
public class BiosampleDto implements IObject, StudyActionResult, Comparable<BiosampleDto>, Serializable{

	private static final long serialVersionUID = -5802423689908566941L;
	@Autowired
	private BiosampleService biosampleService;
	private Integer id = Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate;
	private String elb;
	private String localId;
	private Integer locationPos;
	private String sampleId;
	private Date updDate;
	private String updUser;
	private BiotypeDto biotype;
	private Department department;
	private PhaseDto inheritedPhase;
	private Quality quality;
	private String creUser;
	private StudyDto study;
	private Double amount;
	private BiosampleDto parent;
	private BiosampleDto topParent;
	private Integer containerIndex;
	private SamplingDto attachedSampling;
	private Status state;
	private LocationDto location;
	private String containerId;
	private ContainerType containerType;
	private Date expiryDate;
	private String lastAction;
	private PhaseDto endPhase;
	private Date inheritedDate;
	private Date endDate;
	private Execution creationExecution;
	private Execution terminationExecution;
	private List<AssayResultDto> results;
	private List<AdministrationDto> administrations;
	private Set<AssignmentDto> assignments;
	private List<PlannedSampleDto> plannedSamples;
	private List<BiosampleEnclosureDto> biosampleEnclosures;
	private List<BiotypeMetadataBiosampleDto> metadatas;
	private Set<BiosampleDto> children;
	private List<LinkedBiosampleDto> linkedBiosamples;
	private List<LinkedDocumentDto> documents;
	private Container container;
	private String scannedPosition;
	private transient final Map<String, Object> infos = new HashMap<>();
	private transient Boolean isFromAnOtherDB=null;
	
	public BiosampleDto() {
	}
	
	public BiosampleDto(BiotypeDto biotype) {
		this.biotype=biotype;
	}
	
	public BiosampleDto(BiotypeDto biotype, String sampleId) {
		this.biotype=biotype;
		this.sampleId=sampleId;
	}

	public BiosampleDto(String sampleId) {
		this.sampleId=sampleId;
	}

	@Override
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public Integer getLocationPos() {
		return locationPos==null ? -1 : locationPos;
	}

	public void setLocationPos(Integer locationPos) {
		this.locationPos = locationPos;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public BiotypeDto getBiotype() {
		return biotype;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setBiotype(this, biotype);
	 */
	@Deprecated
	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public PhaseDto getInheritedPhase() {
		return inheritedPhase;
	}

	public void setInheritedPhase(PhaseDto inheritedPhase) {
		this.inheritedPhase = inheritedPhase;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public StudyDto getStudy() {
		return study;
	}

	public void setStudy(StudyDto study) {
		this.study = study;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public BiosampleDto getParent() {
		return parent;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setParent(this, parent);
	 */
	@Deprecated
	public void setParent(BiosampleDto parent) {
		this.parent = parent;
	}
	
	public BiosampleDto getTopParent() {
		return topParent==null?this:topParent;
	}

	public void setTopParent(BiosampleDto topParent) {
		this.topParent = topParent;
	}

	public Integer getContainerIndex() {
		return containerIndex;
	}

	public void setContainerIndex(Integer containerIndex) {
		this.containerIndex = containerIndex;
	}

	public SamplingDto getAttachedSampling() {
		return attachedSampling;
	}

	public void setAttachedSampling(SamplingDto attachedSampling) {
		this.attachedSampling = attachedSampling;
	}

	public Status getState() {
		return state;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setState(this, phase, date)
	 */
	@Deprecated
	public void setState(Status state) {
		this.state = state;
	}

	public LocationDto getLocation() {
		return location;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setLocation(this, location);
	 */
	@Deprecated
	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public String getContainerId() {
		return containerId;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setContainerId(this, containerId);
	 */
	@Deprecated
	public void setContainerId(String containerId) {
		this.containerId=containerId;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setContainerType(this, containerType);
	 */
	@Deprecated
	public void setContainerType(ContainerType containerType) {
		this.containerType=containerType;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public PhaseDto getEndPhase() {
		return endPhase;
	}

	public void setEndPhase(PhaseDto endPhase) {
		this.endPhase = endPhase;
	}

	public Date getInheritedDate() {
		return inheritedDate;
	}

	public void setInheritedDate(Date inheritedDate) {
		this.inheritedDate = inheritedDate;
		if(creationExecution==null)
			creationExecution = new Execution();
		creationExecution.setExecutionDate(inheritedDate);
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		if(terminationExecution==null)
			terminationExecution = new Execution();
		terminationExecution.setExecutionDate(endDate);
	}

	public Set<AssignmentDto> getAssignments() {
		if(assignments == null) {
			getBiosampleService().mapAssignments(this);
		}
		return assignments;
	}
	
	@Deprecated
	public Set<AssignmentDto> getAssignmentsNoMapping() {
		return assignments;
	}
	
	/**Do not call this method directly but call the service instead 
	 *BiosampleService.addAssignment(this, assignment)/removeAssignment(this, assignment);
	 */
	@Deprecated
	public void setAssignments(Set<AssignmentDto> assignments) {
		this.assignments=assignments;
	}

	public Execution getCreationExecution() {
		return creationExecution;
	}

	public void setTerminationExecution(Execution terminationExecution) {
		this.terminationExecution=terminationExecution;
		this.endDate = terminationExecution.getExecutionDate();
	}
	
	public Execution getTerminationExecution() {
		return terminationExecution;
	}

	public void setCreationExecution(Execution creationExecution) {
		this.creationExecution=creationExecution;
		this.inheritedDate = creationExecution.getExecutionDate();
	}

	public List<AssayResultDto> getResults() {
		if(results == null) {
			getBiosampleService().mapResults(this);
		}
		return results;
	}
	
	@Deprecated
	public List<AssayResultDto> getResultsNoMapping() {
		return results;
	}
	
	/**Do not call this method directly but call the service instead 
	 *BiosampleService.addResult(this, result)/removeResult(this, result);
	 */
	@Deprecated
	public void setResults(List<AssayResultDto> results) {
		this.results=results;
	}

	public List<AdministrationDto> getAdministrations() {
		if(administrations == null) {
			getBiosampleService().mapAdministrations(this);
		}
		return administrations;
	}
	
	@Deprecated
	public List<AdministrationDto> getAdministrationsNoMapping() {
		return administrations;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.addAdministration(this, administration)/removeAdministration(this, administration);
	 */
	@Deprecated
	public void setAdministrations(List<AdministrationDto> administrations) {
		this.administrations = administrations;
	}

	public List<BiosampleEnclosureDto> getBiosampleEnclosures() {
		if(biosampleEnclosures == null) {
			getBiosampleService().mapBiosampleEnclosures(this);
		}
		return biosampleEnclosures;
	}

	@Deprecated
	public List<BiosampleEnclosureDto> getBiosampleEnclosuresNoMapping() {
		return biosampleEnclosures;
	}
	
	/**Do not call this method directly but call the service instead 
	 *BiosampleService.addBiosampleEnclosure(this, biosampleEnclosure)/removeBiosampleEnclosure(this, biosampleEnclosure);
	 */
	@Deprecated
	public void setBiosampleEnclosures(List<BiosampleEnclosureDto> biosampleEnclosures) {
		this.biosampleEnclosures = biosampleEnclosures;
	}

	public List<PlannedSampleDto> getPlannedSamples() {
		if(plannedSamples == null) {
			getBiosampleService().mapPlannedSamples(this);
		}
		return plannedSamples;
	}
	
	@Deprecated
	public List<PlannedSampleDto> getPlannedSamplesNoMapping() {
		return plannedSamples;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.addPlannedSample(this, plannedSample)/removePlannedSample(this, plannedSample);
	 */
	@Deprecated
	public void setPlannedSamples(List<PlannedSampleDto> plannedSamples) {
		this.plannedSamples = plannedSamples;
	}

	public List<BiotypeMetadataBiosampleDto> getMetadatas() {
		if(metadatas == null) {
			getBiosampleService().mapMetadatas(this);
		}
		return metadatas;
	}
	
	@Deprecated
	public List<BiotypeMetadataBiosampleDto> getMetadatasNoMapping() {
		return metadatas;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setMetadatas(this, metadata);
	 */
	@Deprecated
	public void setMetadatas(List<BiotypeMetadataBiosampleDto> metadatas) {
		this.metadatas = metadatas;
	}
	
	@Override
	public String toString() {
		return getSampleId();
	}

	public Set<BiosampleDto> getChildren() {
		if(children == null) {
			getBiosampleService().mapChildren(this);
		}
		return children;
	}

	@Deprecated
	public Set<BiosampleDto> getChildrenNoMapping() {
		return children;
	}
	
	public void setChildren(Set<BiosampleDto> children) {
		this.children = children;
	}

	@Override
	public int compareTo(BiosampleDto o) {
		return Comparators.COMPARATOR_NATURAL.compare(this, o);
	}

	public Container getContainer() {
		return container;
	}

	/**Do not call this method directly but call the service instead 
	 *BiosampleService.setContainer(this, container);
	 */
	@Deprecated
	public void setContainer(Container container) {
		this.container = container;
		if(container==null) {
			containerType=null;
			containerId=null;
		}else {
			containerType=container.getContainerType();
			containerId=container.getContainerId();
		}
	}

	public String getScannedPosition() {
		return scannedPosition;
	}

	public void setScannedPosition(String scannedPosition) {
		this.scannedPosition = scannedPosition;
	}

	public List<LinkedBiosampleDto> getLinkedBiosamples() {
		if(linkedBiosamples == null) {
			getBiosampleService().mapLinkedBiosamples(this);
		}
		return linkedBiosamples;
	}

	@Deprecated
	public List<LinkedBiosampleDto> getLinkedBiosamplesNoMapping() {
		return linkedBiosamples;
	}
	
	public void setLinkedBiosamples(List<LinkedBiosampleDto> linkedBiosamples) {
		this.linkedBiosamples = linkedBiosamples;
	}

	public List<LinkedDocumentDto> getDocuments() {
		if(documents == null) {
			getBiosampleService().mapDocuments(this);
		}
		return documents;
	}
	
	@Deprecated
	public List<LinkedDocumentDto> getDocumentsNoMapping() {
		return documents;
	}

	public void setDocuments(List<LinkedDocumentDto> documents) {
		this.documents = documents;
	}

	public BiosampleService getBiosampleService() {
		if(biosampleService == null) {
			biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");
		}
		return biosampleService;
	}

	public Map<String, Object> getAuxiliaryInfos() {
		return infos;
	}

	public Boolean getIsFromAnOtherDB() {
		return isFromAnOtherDB==Boolean.TRUE;
	}

	public void setIsFromAnOtherDB(Boolean isFromAnOtherDB) {
		this.isFromAnOtherDB = isFromAnOtherDB;
	}
	
	
}
