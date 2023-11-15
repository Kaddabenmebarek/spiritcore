package com.idorsia.research.spirit;

import org.dozer.DozerBeanMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.idorsia.research.spirit.core.conf.AppConfig;
import com.idorsia.research.spirit.core.service.ActionPatternsService;
import com.idorsia.research.spirit.core.service.AdministrationService;
import com.idorsia.research.spirit.core.service.AssayAttributeService;
import com.idorsia.research.spirit.core.service.AssayResultService;
import com.idorsia.research.spirit.core.service.AssayResultValueService;
import com.idorsia.research.spirit.core.service.AssayService;
import com.idorsia.research.spirit.core.service.AssignmentService;
import com.idorsia.research.spirit.core.service.BiosampleEnclosureService;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.service.BiotypeMetadataBiosampleService;
import com.idorsia.research.spirit.core.service.BiotypeMetadataService;
import com.idorsia.research.spirit.core.service.BiotypeMetadataValueService;
import com.idorsia.research.spirit.core.service.BiotypeService;
import com.idorsia.research.spirit.core.service.DocumentService;
import com.idorsia.research.spirit.core.service.EnclosureService;
import com.idorsia.research.spirit.core.service.ExecutionDetailService;
import com.idorsia.research.spirit.core.service.FavoriteStudyService;
import com.idorsia.research.spirit.core.service.FoodWaterService;
import com.idorsia.research.spirit.core.service.GroupPatternService;
import com.idorsia.research.spirit.core.service.GroupService;
import com.idorsia.research.spirit.core.service.LocationService;
import com.idorsia.research.spirit.core.service.NamedSamplingService;
import com.idorsia.research.spirit.core.service.NamedTreatmentService;
import com.idorsia.research.spirit.core.service.PhaseService;
import com.idorsia.research.spirit.core.service.PlannedSampleService;
import com.idorsia.research.spirit.core.service.PropertyLinkService;
import com.idorsia.research.spirit.core.service.PropertyService;
import com.idorsia.research.spirit.core.service.ResultAssignmentService;
import com.idorsia.research.spirit.core.service.SamplingMeasurementService;
import com.idorsia.research.spirit.core.service.SamplingParameterService;
import com.idorsia.research.spirit.core.service.SamplingService;
import com.idorsia.research.spirit.core.service.SchedulePhaseService;
import com.idorsia.research.spirit.core.service.ScheduleService;
import com.idorsia.research.spirit.core.service.StagePatternService;
import com.idorsia.research.spirit.core.service.StageService;
import com.idorsia.research.spirit.core.service.StudyActionService;
import com.idorsia.research.spirit.core.service.StudyService;
import com.idorsia.research.spirit.core.service.SubGroupPatternService;
import com.idorsia.research.spirit.core.service.SubGroupService;

public class AbstractSpiritTest {

	private static AnnotationConfigApplicationContext context;
	private static DozerBeanMapper dozerMapper;
	// services
	private static StudyService studyService;
	private static BiosampleService biosampleService;
	private static AssignmentService assignmentService;
	private static StageService stageService;
	private static AssayService assayService;
	private static AssayAttributeService assayAttributeService;
	private static AssayResultService assayResultService;
	private static ResultAssignmentService resultAssignmentService;
	private static AssayResultValueService assayResultValueService;
	private static PhaseService phaseService;
	private static ActionPatternsService actionPatternsService;
	private static ScheduleService scheduleService;
	private static FoodWaterService foodWaterService;
	private static EnclosureService enclosureService;
	private static BiotypeService biotypeService;
	private static BiotypeMetadataValueService biotypeMetadataValueService;
	private static SamplingService samplingService;
	private static GroupService groupService;
	private static SubGroupService subGroupService;
	private static NamedSamplingService namedSamplingService;
	private static LocationService locationService;
	private static StudyActionService actionService;
	private static PropertyService propertyService;
	private static PropertyLinkService propertyLinkService;
	private static NamedTreatmentService namedTreatmentService;
	private static GroupPatternService groupPatternService;
	private static ExecutionDetailService executionDetailService;
	private static AdministrationService administrationService;
	private static PlannedSampleService plannedSampleService;
	private static BiotypeMetadataBiosampleService biotypeMetadataBiosampleService;
	private static DocumentService documentService;
	private static BiotypeMetadataService biotypeMetadataService;
	private static BiosampleEnclosureService biosampleEnclosureService;
	private static SamplingMeasurementService samplingMeasurementService;
	private static SamplingParameterService samplingParameterService;
	private static SchedulePhaseService schedulePhaseService;
	private static StagePatternService stagePatternService;
	private static SubGroupPatternService subGroupPatternService;
	private static FavoriteStudyService favoriteStudyService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		dozerMapper = (DozerBeanMapper) context.getBean("org.dozer.Mapper");
		studyService = (StudyService) context.getBean("studyService");
		biosampleService = (BiosampleService) context.getBean("biosampleService");
		stageService = (StageService) context.getBean("stageService");
		assignmentService = (AssignmentService) context.getBean("assignmentService");
		assayService = (AssayService) context.getBean("assayService");
		assayAttributeService = (AssayAttributeService) context.getBean("assayAttributeService");
		assayResultService = (AssayResultService) context.getBean("assayResultService");
		resultAssignmentService = (ResultAssignmentService) context.getBean("resultAssignmentService");
		assayResultValueService = (AssayResultValueService) context.getBean("assayResultValueService");
		phaseService = (PhaseService) context.getBean("phaseService");
		actionPatternsService = (ActionPatternsService) context.getBean("actionPatternsService");
		scheduleService = (ScheduleService) context.getBean("scheduleService");
		foodWaterService = (FoodWaterService) context.getBean("foodWaterService");
		enclosureService = (EnclosureService) context.getBean("enclosureService");
		biotypeService = (BiotypeService) context.getBean("biotypeService");
		biotypeMetadataValueService = (BiotypeMetadataValueService) context.getBean("biotypeMetadataValueService");
		samplingService = (SamplingService) context.getBean("samplingService");
		groupService = (GroupService) context.getBean("groupService");
		subGroupService = (SubGroupService) context.getBean("subGroupService");
		propertyService = (PropertyService) context.getBean("propertyService");
		propertyLinkService = (PropertyLinkService) context.getBean("propertyLinkService");
		namedSamplingService = (NamedSamplingService) context.getBean("namedSamplingService");
		locationService = (LocationService) context.getBean("locationService");
		actionService = (StudyActionService) context.getBean("studyActionService");
		namedTreatmentService = (NamedTreatmentService) context.getBean("namedTreatmentService");
		groupPatternService = (GroupPatternService) context.getBean("groupPatternService");
		executionDetailService = (ExecutionDetailService) context.getBean("executionDetailService");
		administrationService = (AdministrationService) context.getBean("administrationService");
		plannedSampleService = (PlannedSampleService) context.getBean("plannedSampleService");
		documentService = (DocumentService) context.getBean("documentService");
		samplingMeasurementService = (SamplingMeasurementService) context.getBean("samplingMeasurementService");
		biotypeMetadataService = (BiotypeMetadataService) context.getBean("biotypeMetadataService");
		biosampleEnclosureService = (BiosampleEnclosureService) context.getBean("biosampleEnclosureService");
		biotypeMetadataBiosampleService = (BiotypeMetadataBiosampleService) context.getBean("biotypeMetadataBiosampleService");
		samplingMeasurementService = (SamplingMeasurementService) context.getBean("samplingMeasurementService");
		samplingParameterService = (SamplingParameterService) context.getBean("samplingParameterService");
		schedulePhaseService = (SchedulePhaseService) context.getBean("schedulePhaseService");
		stagePatternService = (StagePatternService) context.getBean("stagePatternService");
		subGroupPatternService = (SubGroupPatternService) context.getBean("subGroupPatternService");
		favoriteStudyService = (FavoriteStudyService) context.getBean("favoriteStudyService");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	public static AnnotationConfigApplicationContext getContext() {
		return context;
	}

	public static DozerBeanMapper getDozerMapper() {
		return dozerMapper;
	}

	public static StudyService getStudyService() {
		return studyService;
	}

	public static BiosampleService getBiosampleService() {
		return biosampleService;
	}

	public static StageService getStageService() {
		return stageService;
	}

	public static AssignmentService getAssignmentService() {
		return assignmentService;
	}

	public static AssayService getAssayService() {
		return assayService;
	}

	public static AssayAttributeService getAssayAttributeService() {
		return assayAttributeService;
	}

	public static AssayResultService getAssayResultService() {
		return assayResultService;
	}
	
	public static ResultAssignmentService getResultAssignmentService() {
		return resultAssignmentService;
	}

	public static AssayResultValueService getAssayResultValueService() {
		return assayResultValueService;
	}

	public static PhaseService getPhaseService() {
		return phaseService;
	}

	public static ActionPatternsService getActionPatternsService() {
		return actionPatternsService;
	}

	public static ScheduleService getScheduleService() {
		return scheduleService;
	}

	public static FoodWaterService getFoodWaterService() {
		return foodWaterService;
	}

	public static EnclosureService getEnclosureService() {
		return enclosureService;
	}

	public static BiotypeService getBiotypeService() {
		return biotypeService;
	}
	
	public static BiotypeMetadataValueService getBiotypeMetadataValueService() {
		return biotypeMetadataValueService;
	}

	public static SamplingService getSamplingService() {
		return samplingService;
	}

	public static GroupService getGroupService() {
		return groupService;
	}

	public static SubGroupService getSubGroupService() {
		return subGroupService;
	}

	public static NamedSamplingService getNamedSamplingService() {
		return namedSamplingService;
	}

	public static LocationService getLocationService() {
		return locationService;
	}

	public static StudyActionService getActionService() {
		return actionService;
	}

	public static NamedTreatmentService getNamedTreatmentService() {
		return namedTreatmentService;
	}

	public static GroupPatternService getGroupPatternService() {
		return groupPatternService;
	}

	public static ExecutionDetailService getExecutionDetailService() {
		return executionDetailService;
	}

	public static AdministrationService getAdministrationService() {
		return administrationService;
	}

	public static PlannedSampleService getPlannedSampleService() {
		return plannedSampleService;
	}
	
	public static BiotypeMetadataBiosampleService getBiotypeMetadataBiosampleService() {
		return biotypeMetadataBiosampleService;
	}

	public static PropertyService getPropertyService() {
		return propertyService;
	}

	public static PropertyLinkService getPropertyLinkService() {
		return propertyLinkService;
	}

	public static DocumentService getDocumentService() {
		return documentService;
	}

	public static BiotypeMetadataService getBiotypeMetadataService() {
		return biotypeMetadataService;
	}

	public static BiosampleEnclosureService getBiosampleEnclosureService() {
		return biosampleEnclosureService;
	}

	public static SamplingMeasurementService getSamplingMeasurementService() {
		return samplingMeasurementService;
	}

	public static SamplingParameterService getSamplingParameterService() {
		return samplingParameterService;
	}

	public static SchedulePhaseService getSchedulePhaseService() {
		return schedulePhaseService;
	}

	public static StagePatternService getStagePatternService() {
		return stagePatternService;
	}

	public static SubGroupPatternService getSubGroupPatternService() {
		return subGroupPatternService;
	}

	public static FavoriteStudyService getFavoriteStudyService() {
		return favoriteStudyService;
	}

}
