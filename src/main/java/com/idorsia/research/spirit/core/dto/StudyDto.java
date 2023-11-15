package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.business.Department;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.service.StudyService;

@Component
public class StudyDto implements IObject, Comparable<StudyDto>, Serializable{
	
	private static final long serialVersionUID = -2344086369664916283L;
	@Autowired
	private StudyService studyService;
	private Integer id = Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate;
	private String ivv;
	private String description;
	private String project;
	private String writeUsers;
	private Date updDate;
	private String updUser;
	private String creUser;
	private String readUsers;
	private String studyId="";
	private String status;
	private String rndExperimenter;
	private String blindUsers;
	private Department department;
	private Department department2;
	private Department department3;
	private Boolean synchroSamples;
	private String owner;
	private String studyType;
	private String elb;
	private List<StageDto> stages;
	private List<EnclosureDto> enclosures;
	private List<PropertyLinkDto> properties;
	private Set<NamedTreatmentDto> namedTreatments;
	private Set<NamedSamplingDto> namedSamplings;
	private Set<StudyDocumentDto> documents;
	private Set<String> adminUsersSet;
	private Set<String> expertUsersSet;
	private Set<String> blindAllUsersSet;
	private Set<String> blindDetailsUsersSet;
	private HashMap<StudyAction, Integer> actionDuration = new HashMap<>();
	
	public StudyDto() {
	}

	public StudyDto(StudyDto study) {
		this.comments=study.getComments();
		this.ivv=study.getIvv();
		this.description=study.getDescription();
		this.project=study.getProject();
		this.writeUsers=study.getWriteUsers();
		this.readUsers=study.getReadUsers();
		this.status=study.getStatus();
		this.department=study.getDepartment();
		this.rndExperimenter=study.getRndExperimenter();
		this.blindUsers=study.getBlindUsers();
		this.department2=study.getDepartment2();
		this.department3=study.getDepartment3();
		this.synchroSamples=study.getSynchroSamples();
		this.owner=study.getOwner();
		this.studyType=study.getStudyType();
		this.elb=study.getElb();
	}

	public Integer getId() {
		return id;
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

	public String getIvv() {
		return ivv;
	}

	public void setIvv(String ivv) {
		this.ivv = ivv;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getWriteUsers() {
		return writeUsers;
	}

	public void setReadUsers(String readUsers) {
		this.expertUsersSet=null;
		this.readUsers = readUsers;
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

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public String getReadUsers() {
		return readUsers;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getRndExperimenter() {
		return rndExperimenter;
	}

	public void setRndExperimenter(String rndExperimenter) {
		this.rndExperimenter = rndExperimenter;
	}

	public String getBlindUsers() {
		return blindUsers;
	}

	public void setBlindUsers(String blindUsers) {
		this.blindUsers = blindUsers;
	}

	public Department getDepartment2() {
		return department2;
	}

	public void setDepartment2(Department department2) {
		this.department2 = department2;
	}

	public Department getDepartment3() {
		return department3;
	}

	public void setDepartment3(Department department3) {
		this.department3 = department3;
	}

	public List<Department> getDepartments() {
		List<Department> res = new ArrayList<>();
		if(department!=null) res.add(department);
		if(department2!=null) res.add(department2);
		if(department3!=null) res.add(department3);
		return Collections.unmodifiableList(res);
	}
	
	public Boolean getSynchroSamples() {
		return synchroSamples!=Boolean.FALSE;
	}

	public void setSynchroSamples(Boolean synchroSamples) {
		this.synchroSamples = synchroSamples;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}
	
	public List<StageDto> getStages(){
		if(stages == null) {
			getStudyService().mapStages(this);
		}
		return this.stages;
	}
	
	@Deprecated
	public List<StageDto> getStagesNoMapping(){
		return this.stages;
	}
	
	/**Do not call this method directly but call the service instead 
	 *StudyService.addStage(this, stage)/removeStage(this, stage);
	 */
	@Deprecated
	public void setStages(List<StageDto> stages) {
		this.stages=stages;
		Collections.sort(stages);
	}
	
	public List<EnclosureDto> getEnclosures() {
		if(enclosures == null) {
			getStudyService().mapEnclosures(this);
		}
		return enclosures;
	}
	
	@Deprecated
	public List<EnclosureDto> getEnclosuresNoMapping() {
		return enclosures;
	}

	/**Do not call this method directly but call the service instead 
	 *StudyService.addEnclosure(this, enclosure)/removeEnclosure(this, enclosure);
	 */
	@Deprecated
	public void setEnclosures(List<EnclosureDto> enclosures) {
		this.enclosures = enclosures;
	}
	
	public List<PropertyLinkDto> getProperties() {
		if(properties == null) {
			getStudyService().mapProperties(this);
		}
		return properties;
	}
	
	@Deprecated
	public List<PropertyLinkDto> getPropertiesNoMapping() {
		return properties;
	}

	public void setProperties(List<PropertyLinkDto> properties) {
		this.properties = properties;
	}
	
	public Set<NamedTreatmentDto> getNamedTreatments() {
		if(namedTreatments == null) {
			getStudyService().mapNamedTreatments(this);
		}
		return namedTreatments;
	}
	
	@Deprecated
	public Set<NamedTreatmentDto> getNamedTreatmentsNoMapping() {
		return namedTreatments;
	}

	/**Do not call this method directly but call the service instead 
	 *StudyService.addNamedTreatment(this, namedTreatment)/removeNamedTreatment(this, namedTreatment);
	 */
	@Deprecated
	public void setNamedTreatments(Set<NamedTreatmentDto> namedTreatments) {
		this.namedTreatments = namedTreatments;
	}

	public Set<NamedSamplingDto> getNamedSamplings() {
		if(namedSamplings == null) {
			getStudyService().mapNamedSampling(this);
		}
		return namedSamplings;
	}

	@Deprecated
	public Set<NamedSamplingDto> getNamedSamplingsNoMapping() {
		return namedSamplings;
	}
	
	/**Do not call this method directly but call the service instead 
	 *StudyService.addNamedSampling(this, namedSampling)/removeNamedSampling(this, namedSampling);
	 */
	@Deprecated
	public void setNamedSamplings(Set<NamedSamplingDto> namedSamplings) {
		this.namedSamplings = namedSamplings;
	}

	@Override
	public int compareTo(StudyDto s) {
		if(s==null) return -1;
		int c = -(getCreDate()==null?new Date():getCreDate()).compareTo(s.getCreDate()==null?new Date():s.getCreDate());
		if(c!=0) return c;
		c = -(getStudyId()==null?"":getStudyId()).compareTo(s.getStudyId()==null?"":s.getStudyId());
		return c;
	}
	
	@Override
	public String toString() {
		return this.getStudyId();
	}

	public Set<String> getAdminUsersSet() {
		return adminUsersSet;
	}

	public void setAdminUsersSet(Set<String> adminUsersSet) {
		this.adminUsersSet = adminUsersSet;
	}
	
	public void setWriteUsers(String writeUsers) {
		adminUsersSet = null;
		this.writeUsers = writeUsers;
	}

	public Set<String> getExpertUsersSet() {
		return expertUsersSet;
	}

	public void setExpertUsersSet(Set<String> expertUsersSet) {
		this.expertUsersSet = expertUsersSet;
	}

	public Set<String> getBlindAllUsersSet() {
		return blindAllUsersSet;
	}

	public void setBlindAllUsersSet(Set<String> blindAllUsersSet) {
		this.blindAllUsersSet = blindAllUsersSet;
	}

	public Set<String> getBlindDetailsUsersSet() {
		return blindDetailsUsersSet;
	}

	public void setBlindDetailsUsersSet(Set<String> blindDetailsUsersSet) {
		this.blindDetailsUsersSet = blindDetailsUsersSet;
	}

	public HashMap<StudyAction, Integer> getActionDuration() {
		return this.actionDuration;
	}

	public void setActionDuration(HashMap<StudyAction, Integer> actionDuration) {
		this.actionDuration = actionDuration;
	}

	public Set<StudyDocumentDto> getDocuments() {
		if(documents == null) {
			getStudyService().mapDocuments(this);
		}
		return documents;
	}
	
	@Deprecated
	public Set<StudyDocumentDto> getDocumentsNoMapping() {
		return documents;
	}

	public void setDocuments(Set<StudyDocumentDto> documents) {
		this.documents = documents;
	}

	public StudyService getStudyService() {
		if(studyService == null) {
			studyService = (StudyService) ContextShare.getContext().getBean("studyService");
		}
		return studyService;
	}
}
