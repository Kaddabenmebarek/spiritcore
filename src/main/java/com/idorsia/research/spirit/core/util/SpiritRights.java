package com.idorsia.research.spirit.core.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.actelion.research.business.Department;
import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.constants.Privacy;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Location;
import com.idorsia.research.spirit.core.model.Study;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.service.LocationService;
import com.idorsia.research.spirit.core.service.SpiritPropertyService;
import com.idorsia.research.spirit.core.service.StudyService;
import com.idorsia.research.spirit.core.util.property.PropertyKey;

@Service
public class SpiritRights implements Serializable {
	
	private static final long serialVersionUID = 2205669619250194153L;
	@Autowired
	private StudyService studyService;
	@Autowired
	private SpiritPropertyService spiritPropertyService;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private LocationService locationService;
	
	private PropertyKey p = PropertyKey.STUDY_STATES_READ;
	
	public enum ActionType {
		READ_STUDY("Read Study", "Who is allowed to read a study (its samples and results are not necessarily readable)"),
		WORK_STUDY("Work Study", "Who is allowed to change the status of a study"),
		EDIT_STUDY("Modify Study", "Who is allowed to edit a study (its samples and results are not necessarily editable)"),
		DELETE_STUDY("Delete Study", "Who is allowed to delete a study (its samples and results?"),

		READ_BIOSAMPLE("Read Biosample", "Who is allowed to read a biosample. Note: it is necessary to also have read rights on the study to read samples on a study."),
		WORK_BIOSAMPLE("Work Biosample", "Who is allowed to modify a biosample as part of the normal workflow (location, status). Note: it is necessary to also have read rights on the study to edit samples on a study."),
		EDIT_BIOSAMPLE("Edit Biosample", "Who is allowed to edit a biosample. Note: it is necessary to also have read rights on the study to edit samples on a study."),
		DELETE_BIOSAMPLE("Delete Biosample", "Who is allowed to edit a biosample. Note: it is necessary to also have read rights on the study to delete samples on a study."),

		READ_LOCATION("Read Location", "Who is allowed to read a protected/private location. ('public' location are always readable)"),
		EDIT_LOCATION("Edit Location", "Who is allowed to edit a location. ('public' location are always editable)"),
		DELETE_LOCATION("Delete Location", "Who is allowed to edit a location. ('public' location are always editable)"),

		READ_RESULT("Read Result", "Who is allowed to read a result. Note: it is necessary to also have read rights on the study to read results on a study."),
		EDIT_RESULT("Edit Result", "Who is allowed to edit an admin location. Note: it is necessary to also have edit rights on the study to edit results on a study."),
		DELETE_RESULT("Delete Result", "Who is allowed to edit an admin location. Note: it is necessary to also have edit rights on the study to delete results on a study.");


		private final String display;
		private final String tooltip;

		private ActionType(String display, String tooltip) {
			this.display = display;
			this.tooltip = tooltip;
		}

		public String getDisplay() {
			return display;
		}

		public String getTooltip() {
			return tooltip;
		}

	}

	public enum UserType {
		CREATOR,
		UPDATER
	}

	/**
	 * Is the user blinded for this study (all groups are blinded)
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean isBlindAll(StudyDto study, User user) {
		if(study==null || user==null) return false;
		return studyService.getBlindAllUsersAsSet(study).contains(user.getUsername());
	}

	/**
	 * Is the user blinded for this study (only the treatments, and actionGroup names are blinded)
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean isBlind(StudyDto study, User user) {
		if(study==null || user==null) return false;
		return studyService.getBlindDetailsUsersAsSet(study).contains(user.getUsername()) || studyService.getBlindAllUsersAsSet(study).contains(user.getUsername());
	}

	/**
	 * Return true if the user can write or blind the study.
	 * All write users can also work as blind, but not read is not enought to work as blind
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean canBlind(StudyDto study, User user) {
		return isBlind(study, user) || canWork(study, user);
	}


	/**
	 * Is the user allowed to read the study?
	 *
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean canRead(Study study, User user) {
		return canRead(studyService.map(study), user);
	}
	
	public boolean canRead(StudyDto study, User user) {
		if(user==null) return false;
		if(study==null) return true;
		if(study.getId()<=0) return true;

		//Check states specific roles
		String[] roles = spiritPropertyService.getValues(p, study.getStatus());
		if(roles.length>0) {
			if(MiscUtils.contains(roles, "NONE")) return false;
			if(MiscUtils.contains(roles, "ALL")) return true;
			if(MiscUtils.contains(roles, "RESTRICTED")) return studyService.isMember(study, UserUtil.getUsername());
			if(MiscUtils.contains(roles, UserUtil.getRoles())) return true;
			return false;
		}

		//Otherwise, check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.READ_STUDY, role)) return true;
		}

		//Check groups
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			if(UserUtil.getUsername().equals(study.getCreUser()) && spiritPropertyService.isChecked(ActionType.READ_STUDY, UserType.CREATOR)) return true;
			if(UserUtil.getUsername().equals(study.getUpdUser()) && spiritPropertyService.isChecked(ActionType.READ_STUDY, UserType.UPDATER)) return true;
		}

		//Return true by default if roles have not been defined and the system is open
		return spiritPropertyService.getUserRoles().length<=1 && spiritPropertyService.isOpen();
	}

	/**
	 * True if the user can work on the study: promote, add samples, ...
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean canWork(StudyDto study, User user) {
		if(user==null) return false;
		if(study==null) return false;
		if(study.getId()<=0) return true;

		//Check if the study is sealed: then no rights
		if("true".equals(spiritPropertyService.getValue(PropertyKey.STUDY_STATES_SEALED, study.getStatus()))) {
			return false;
		}

		//Check generic roles
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(Department eg: study.getDepartments()) {
				if(UserUtil.isMember(eg)) return true;
			}

			if(study.getCreUser().equals(UserUtil.getUsername()) && spiritPropertyService.isChecked(ActionType.WORK_STUDY, UserType.CREATOR)) return true;
			if(study.getUpdUser().equals(UserUtil.getUsername()) && spiritPropertyService.isChecked(ActionType.WORK_STUDY, UserType.UPDATER)) return true;
			for(String uid: UserUtil.getManagedUsers()) {
				if(studyService.getExpertUsersAsSet(study).contains(uid)) return true;
			}
			return canEdit(study, user);
		} else {

			String[] roles = spiritPropertyService.getValues(PropertyKey.STUDY_STATES_WORK, study.getStatus());
			if(MiscUtils.contains(roles, "NONE")) return false;
			if(MiscUtils.contains(roles, "ALL")) return true;
			if(MiscUtils.contains(roles, UserUtil.getRoles())) return true;


			for (String role : UserUtil.getRoles()) {
				if(spiritPropertyService.isChecked(ActionType.WORK_STUDY, role)) return true;
			}
			return false;
		}
	}

	/**
	 * True if the user can edit the study, (and also add samples/results to it)
	 *
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean canEdit(StudyDto study, User user) {
		if(study==null) return true;
		if(user==null) return false;

		//Check if the study is sealed: then no rights
		if("true".equals(spiritPropertyService.getValue(PropertyKey.STUDY_STATES_SEALED, study.getStatus()))) {
			return false;
		}

		//Check states specific roles
		String[] roles = spiritPropertyService.getValues(PropertyKey.STUDY_STATES_EDIT, study.getStatus());
		if(MiscUtils.contains(roles, "NONE")) return false;
		if(MiscUtils.contains(roles, "ALL")) return true;
		if(MiscUtils.contains(roles, UserUtil.getRoles())) return true;

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.EDIT_STUDY, null, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			if(UserUtil.getUsername().equals(study.getCreUser()) && spiritPropertyService.isChecked(ActionType.EDIT_STUDY, UserType.CREATOR)) return true;
			if(UserUtil.getUsername().equals(study.getUpdUser()) && spiritPropertyService.isChecked(ActionType.EDIT_STUDY, UserType.UPDATER)) return true;
			for(String uid: UserUtil.getManagedUsers()) {
				if(studyService.getAdminUsersAsSet(study).contains(uid)) {
					return true;
				}
			}
		}

		//Return true by default if roles have not been defined
		return spiritPropertyService.getUserRoles().length<=1;
	}

	/**
	 * True if the user can delete the study
	 * @param study
	 * @param user
	 * @return
	 */
	public boolean canDelete(StudyDto study, User user) {
		if(user==null) return false;
		if(study==null) return false;

		//Check if the study is sealed: then no rights
		if("true".equals(spiritPropertyService.getValue(PropertyKey.STUDY_STATES_SEALED, study.getStatus()))) {
			return false;
		}
		//Check generic roles
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			if(study.getCreUser().equals(UserUtil.getUsername()) && spiritPropertyService.isChecked(ActionType.DELETE_STUDY, UserType.CREATOR)) return true;
			if(study.getUpdUser().equals(UserUtil.getUsername()) && spiritPropertyService.isChecked(ActionType.DELETE_STUDY, UserType.UPDATER)) return true;
		}
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.DELETE_STUDY, role)) return true;
		}

		return false;
	}


	/**
	 * True if the user can edit the namedSampling, ie: he can edit the linked
	 * @param ns
	 * @param user
	 * @return
	 */
	public boolean canEdit(NamedSamplingDto ns, User user) {
		if(user==null) return false;
		if(ns==null) return false;
		if(ns.getStudy()!=null) {
			return canEdit(ns.getStudy(), user);
		} else {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(ns.getCreUser())) return true;
			}
			return false;
		}
	}

	/**
	 * True if the user can read all biosamples
	 * @param biosamples
	 * @param user
	 * @return
	 */
	public boolean canReadBiosamples(Collection<BiosampleDto> biosamples, User user) {
		if(biosamples==null) return true;
		for (BiosampleDto biosample : biosamples) {
			if(!canRead(biosample, user)) return false;
		}
		return true;
	}

	/**
	 * True if the user can read the biosample
	 * @param biosample
	 * @param user
	 * @return
	 */
	public boolean canRead(BiosampleDto biosample, User user) {
		if(user==null) return false;
		if(biosample==null || biosample.getId()<=0) return true;

		//Study right
		if(biosample.getStudy()!=null && !canRead(biosample.getStudy(), user)) return false;

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.READ_BIOSAMPLE, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			if(UserUtil.getUsername().equals(biosample.getCreUser()) && spiritPropertyService.isChecked(ActionType.READ_BIOSAMPLE, UserType.CREATOR)) return true;
			if(UserUtil.getUsername().equals(biosample.getUpdUser()) && spiritPropertyService.isChecked(ActionType.READ_BIOSAMPLE, UserType.UPDATER)) return true;
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(biosample.getCreUser()) && spiritPropertyService.isChecked(ActionType.READ_BIOSAMPLE, UserType.CREATOR)) return true;
				if(uid.equals(biosample.getUpdUser()) && spiritPropertyService.isChecked(ActionType.READ_BIOSAMPLE, UserType.UPDATER)) return true;
			}

			//Everybody in the actionGroup has the rights
			if(biosample.getDepartment()!=null && UserUtil.isMember(biosample.getDepartment())) return true;
		}

		//Otherwise it depends of the location: public, protected (biosample shown without location), or member of private location
		LocationDto location = biosample.getLocation();
		if(location!=null && location.getPrivacy()== Privacy.PUBLIC) return true;
		else if(location!=null && location.getPrivacy()==Privacy.PRIVATE && locationService.getInheritedDepartment(location)!=null && !UserUtil.isMember(locationService.getInheritedDepartment(location))) return false;

		//Return true by default if roles have not been defined and the system is open
		return spiritPropertyService.getUserRoles().length<=1 && spiritPropertyService.isOpen();

	}

	/**
	 * True if the user can work all biosamples
	 * @param biosamples
	 * @param user
	 * @return
	 */
	public boolean canWorkBiosamples(Collection<BiosampleDto> biosamples, User user) {
		if(biosamples==null) return true;
		for (BiosampleDto biosample : biosamples) {
			if(!canWork(biosample, user)) return false;
		}
		return true;
	}

	/**
	 * True if the user can edit all biosamples
	 * @param biosamples
	 * @param user
	 * @return
	 */
	public boolean canEditBiosamples(Collection<BiosampleDto> biosamples, User user) {
		if(biosamples==null) return true;
		for (BiosampleDto biosample : biosamples) {
			if(!canEdit(biosample, user)) return false;
		}
		return true;
	}

	/**
	 * A user can edit a biosample if:
	 * - he is the creator, owner, or is in the department of the sample
	 * - or he is responsible of the biosample's study (if any)
	 * - or he has edit access on the biosample's location (if any)
	 *
	 * @param biosample
	 * @param user
	 * @return
	 */
	public boolean canWork(BiosampleDto biosample, User user) {
		if(user==null) return false;
		if(biosample==null) return true;
		if(biosample.getId()<=0) return true;

		// expert users / groups can work
		Set<StudyDto> studies = biosampleService.getStudies(biosample);
		for (StudyDto study : studies) {
			if (study == null) continue;
			String expertUsers = study.getReadUsers();
			if (expertUsers != null && !expertUsers.isEmpty() && expertUsers.toLowerCase().contains(UserUtil.getUsername().toLowerCase()) ) {
				return true;
			}
			for (Department group : study.getDepartments()) {
				for (Department userGroup : UserUtil.getDepartments()) {
					if (group.getName().equalsIgnoreCase(userGroup.getName().toLowerCase())) {
						return true;
					}
				}
			}
		}

		//Study rights
		if(biosample.getStudy()!=null) {
			if(!canWork(biosample.getStudy(), user) && !canBlind(biosample.getStudy(), user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.WORK_BIOSAMPLE, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(biosample.getCreUser()) && spiritPropertyService.isChecked(ActionType.WORK_BIOSAMPLE, UserType.CREATOR)) return true;
				if(uid.equals(biosample.getUpdUser()) && spiritPropertyService.isChecked(ActionType.WORK_BIOSAMPLE, UserType.UPDATER)) return true;
			}
			Department employeeGroup = biosample.getDepartment();
			if (employeeGroup != null && UserUtil.isMember(employeeGroup))
				return true;

			//Check generic roles
			for (String role : UserUtil.getRoles()) {
				if(spiritPropertyService.isChecked(ActionType.WORK_BIOSAMPLE, role)) return true;
			}
			return biosample.getId()<=0 || biosample.getStudy()!=null && (canRead(biosample.getStudy(), user) || canBlind(biosample.getStudy(), user));
		}

		//Return true by default if roles have not been defined
		return spiritPropertyService.getUserRoles().length<=1;

	}


	/**
	 * A user can edit a biosample if:
	 * - he is the creator, owner, or is in the department of the sample
	 * - or he is responsible of the biosample's study (if any)
	 * - or he has edit access on the biosample's location (if any)
	 *
	 * @param biosample
	 * @param user
	 * @return
	 */
	public boolean canEdit(BiosampleDto biosample, User user) {
		if(user==null) return false;
		if(biosample==null) return true;
		if(biosample.getId()<=0) return true;

		//Study rights
		if(biosample.getStudy()!=null) {
			if(!canWork(biosample.getStudy(), user) && !canBlind(biosample.getStudy(), user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.EDIT_BIOSAMPLE, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(biosample.getCreUser()) && spiritPropertyService.isChecked(ActionType.EDIT_BIOSAMPLE, UserType.CREATOR)) return true;
				if(uid.equals(biosample.getUpdUser()) && spiritPropertyService.isChecked(ActionType.EDIT_BIOSAMPLE, UserType.UPDATER)) return true;
			}
			if(biosample.getDepartment()!=null && UserUtil.isMember(biosample.getDepartment())) return true;

			//Check generic roles
			for (String role : UserUtil.getRoles()) {
				if(spiritPropertyService.isChecked(ActionType.EDIT_BIOSAMPLE, role)) return true;
			}
			return biosample.getId()<=0 || biosample.getStudy()!=null && (canRead(biosample.getStudy(), user) || canBlind(biosample.getStudy(), user));
		}

		//Return true by default if roles have not been defined
		return spiritPropertyService.getUserRoles().length<=1;

	}

	public boolean canDelete(BiosampleDto biosample, User user) {
		if(user==null) return false;
		if(biosample==null) return false;

		//Study rights
		if(biosample.getStudy()!=null) {
			if(!canWork(biosample.getStudy(), user) && !canBlind(biosample.getStudy(), user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.DELETE_BIOSAMPLE, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(biosample.getCreUser()) && spiritPropertyService.isChecked(ActionType.DELETE_BIOSAMPLE, UserType.CREATOR)) return true;
				if(uid.equals(biosample.getUpdUser()) && spiritPropertyService.isChecked(ActionType.DELETE_BIOSAMPLE, UserType.UPDATER)) return true;
			}
		}

		//Allow the study admin to delete a sample when the study design is changed
		if(biosample.getStudy()!=null && canEdit(biosample.getStudy(), user)) return true;

		return false;
	}


	public boolean canRead(Location location, User user) {
		return canRead(locationService.map(location), user);
	}
	
	/**
	 * The user can read any location except the protected and private that are not under their department
	 * @param location
	 * @return
	 */
	public boolean canRead(LocationDto location, User user) {
		if(location==null) return true;
		if(user==null) return false;
		if(locationService.getInheritedPrivacy(location)==Privacy.PUBLIC) return true;

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.READ_LOCATION, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			if(UserUtil.isMember(locationService.getInheritedDepartment(location))) return true;
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(location.getCreUser()) && spiritPropertyService.isChecked(ActionType.READ_LOCATION, UserType.CREATOR)) return true;
				if(uid.equals(location.getUpdUser()) && spiritPropertyService.isChecked(ActionType.READ_LOCATION, UserType.UPDATER)) return true;
			}
		}
		return false;
	}

	/**
	 * - for a new location, the user must have update rights on the container
	 * - for editing location, the user must have
	 * @param location
	 * @return
	 */
	public boolean canEdit(LocationDto location, User user) {
		if(user==null) return false;
		if(location==null) return false;

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.EDIT_LOCATION, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(location.getCreUser()) && spiritPropertyService.isChecked(ActionType.EDIT_LOCATION, UserType.CREATOR)) return true;
				if(uid.equals(location.getUpdUser()) && spiritPropertyService.isChecked(ActionType.EDIT_LOCATION, UserType.UPDATER)) return true;
			}
			if(locationService.getInheritedDepartment(location)!=null && UserUtil.isMember(locationService.getInheritedDepartment(location))) return true;
			if(locationService.getInheritedPrivacy(location)==Privacy.PUBLIC) return true;
		}
		return false;
	}

	/**
	 * - for a new location, the user must have update rights on the container
	 * - for editing location, the user must have
	 * @param location
	 * @return
	 */
	public boolean canDelete(LocationDto location, User user) {
		if(user==null) return false;
		if(location==null) return false;

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.DELETE_LOCATION, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(location.getCreUser()) && spiritPropertyService.isChecked(ActionType.DELETE_LOCATION, UserType.CREATOR)) return true;
				if(uid.equals(location.getUpdUser()) && spiritPropertyService.isChecked(ActionType.DELETE_LOCATION, UserType.UPDATER)) return true;
			}
			if(locationService.getInheritedDepartment(location)!=null && UserUtil.isMember(locationService.getInheritedDepartment(location))) return true;
			if(location.getPrivacy()==Privacy.PUBLIC) return true;
		}

		return false;
	}



	/**
	 * True if the user can edit all results
	 * @param results
	 * @param user
	 * @return
	 */
	public boolean canEditResults(Collection<AssayResultDto> results, User user) {
		if(results==null) return true;
		for (AssayResultDto result : results) {
			if(!canEdit(result, user)) return false;
		}
		return true;
	}



	/**
	 * True if the user can read the study
	 */
	public boolean canRead(AssayResultDto result, User user) {
		if(user==null) return false;
		if(result.getBiosample()!=null) {
			StudyDto study = result.getBiosample().getStudy();
			if(study!=null && !canRead(study, user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.READ_RESULT, role)) return true;
		}

		//Return true by default if roles have not been defined
		return spiritPropertyService.getUserRoles().length<=1;
	}

	/**
	 * To edit a result, the user need to have either:
	 * - rights on the biosample
	 * - rights on the study
	 * - have a specific role
	 * - be the owner/updater if given access
	 */
	public boolean canEdit(AssayResultDto result, User user) {
		if(user==null || result==null) return false;
		if(result.getId()<=0) return true;


		//Check study rights first
		if(result.getBiosample()!=null) {
			if(!canRead(result.getBiosample(), user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.EDIT_RESULT, role)) return true;
		}

		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(result.getCreUser()) && spiritPropertyService.isChecked(ActionType.EDIT_RESULT, UserType.CREATOR)) return true;
				if(uid.equals(result.getUpdUser()) && spiritPropertyService.isChecked(ActionType.EDIT_RESULT, UserType.UPDATER)) return true;
			}
			if(canEdit(result.getStudy(), user)) return true;
		}


		return false;
	}

	/**
	 * To edit a result, the user need to have either:
	 * - have a specific role
	 * - be the owner/updater if given access
	 */
	public boolean canDelete(AssayResultDto result, User user) {
		if(user==null) return false;

		if(result==null) return false;
		if(result.getId()<=0) return false;
		if(result.getCreUser()==null) return true;


		//Check biosample rights first
		if(result.getBiosample()!=null) {
			if(!canRead(result.getBiosample(), user)) return false;
		}

		//Check generic roles
		for (String role : UserUtil.getRoles()) {
			if(spiritPropertyService.isChecked(ActionType.DELETE_RESULT, role)) return true;
		}
		//Check actionGroup/hierarchy rights (if needed)
		if(spiritPropertyService.isChecked(PropertyKey.USER_USEGROUPS)) {
			for(String uid: UserUtil.getManagedUsers()) {
				if(uid.equals(result.getCreUser()) && spiritPropertyService.isChecked(ActionType.DELETE_RESULT, UserType.CREATOR)) return true;
				if(uid.equals(result.getUpdUser()) && spiritPropertyService.isChecked(ActionType.DELETE_RESULT, UserType.UPDATER)) return true;
			}
		}
		return false;
	}

	public static boolean isSuperAdmin(User user) {
		return user!=null && UserUtil.isSuperAdmin();
	}

}
