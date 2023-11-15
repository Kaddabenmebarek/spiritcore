package com.idorsia.research.spirit.core.constants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Constants {

	public final static String ACTION_SEQUENCE_NAME = "ACTION_SEQUENCE";
	public final static String ASSAY_ATTRIBUTE_SEQUENCE_NAME = "ASSAY_ATTRIBUTE_SEQUENCE";
	public final static String ASSAY_RESULT_DETAIL_SEQ_NAME = "ASSAY_RESULT_DETAIL_SEQ";
	public final static String ASSAY_RESULT_SEQUENCE_NAME = "ASSAY_RESULT_SEQUENCE";
	public final static String ASSAY_RESULT_STUDY_ASSIGNMENT_SEQ_NAME = "ASSAY_RESULT_STUDY_ASSIGNMENT_SEQ";
	public final static String ASSAY_RESULT_VALUE_SEQUENCE_NAME = "ASSAY_RESULT_VALUE_SEQUENCE";
	public final static String ASSAY_SEQUENCE_NAME = "ASSAY_SEQUENCE";
	public static final String RESULT_ASSIGNMENT_SEQ_NAME = "RESULT_ASSIGNMENT_SEQ";
	public final static String ASSIGNMENT_SEQUENCE_NAME = "ASSIGNMENT_SEQUENCE";
	public final static String BARCODE_SEQUENCE_NAME = "BARCODE_SEQUENCE";
	public final static String BIOLOCATION_SEQUENCE_NAME = "BIOLOCATION_SEQUENCE";
	public final static String BIOSAMPLE_ACTION_SEQUENCE_NAME = "BIOSAMPLE_ACTION_SEQUENCE";
	public final static String BIOSAMPLE_ENCLOSURE_LINK_SEQ_NAME = "BIOSAMPLE_ENCLOSURE_LINK_SEQ";
	public final static String BIOSAMPLE_SEQUENCE_NAME = "BIOSAMPLE_SEQUENCE";
	public final static String BIOSAMPLE_TYPE_SEQUENCE_NAME = "BIOSAMPLE_TYPE_SEQUENCE";
	public static final String BIOTYPE_METADATA_VALUE_SEQUENCE_NAME = "BIOTYPE_METADATAVALUE_SEQUENCE";
	public final static String BIOTYPE_METADATA_BIOSAMPLE_SEQUENCE_NAME = "BIOTYPE_METADATA_BIOSAMPLE_SEQUENCE";
	public final static String BIOTYPE_METADATA_SEQUENCE_NAME = "BIOTYPE_METADATA_SEQUENCE";
	public final static String BIOTYPE_SEQUENCE_NAME = "BIOTYPE_SEQUENCE";
	public final static String CONTAINER_SEQUENCE_NAME = "CONTAINER_SEQUENCE";
	public final static String DOCUMENT_SEQUENCE_NAME = "DOCUMENT_SEQUENCE";
	public final static String EMPLOYEE_SEQUENCE_NAME = "EMPLOYEE_SEQUENCE";
	public final static String FOOD_WATER_SEQUENCE_NAME = "FOOD_WATER_SEQUENCE";
	public final static String GROUP_PATTERN_SEQUENCE_NAME = "GROUP_PATTERN_SEQUENCE";
	public final static String GROUP_SEQUENCE_NAME = "GROUP_SEQUENCE";
	public final static String LOCATION_SEQUENCE_NAME = "LOCATION_SEQUENCE";
	public final static String MEASUREMENT_SEQUENCE_NAME = "MEASUREMENT_SEQUENCE";
	public final static String METADATA_SEQUENCE_NAME = "METADATA_SEQUENCE";
	public final static String PHASE_SEQUENCE_NAME = "PHASE_SEQUENCE";
	public final static String RESULT_VIEW_SEQUENCE_NAME = "RESULT_VIEW_SEQUENCE";
	public final static String SAMPLING_MEASUREMENT_ATTRIBUTE_SEQUENCE_NAME = "SAMPLING_MEASUREMENT_ATTRIBUTE_SEQUENCE";
	public final static String SAMPLING_MEASUREMENT_SEQUENCE_NAME = "SAMPLING_MEASUREMENT_SEQUENCE";
	public final static String SAMPLING_PARAMETER_SEQUENCE_NAME = "SAMPLING_PARAMETER_SEQUENCE";
	public final static String SAMPLING_SEQUENCE_NAME = "SAMPLING_SEQUENCE";
	public final static String SEND_LOCALISATION_SEQUENCE_NAME = "SEND_LOCALISATION_SEQUENCE";
	public final static String SEND_OBSERVATION_SEQUENCE_NAME = "SEND_OBSERVATION_SEQUENCE";
	public final static String STAGE_PATTERN_SEQUENCE_NAME = "STAGE_PATTERN_SEQUENCE";
	public final static String STUDY_ACTIONPATTERNS_SEQUENCE_NAME = "STUDY_ACTIONPATTERNS_SEQUENCE";
	public final static String PLANNED_SAMPLE_SEQUENCE_NAME = "PLANNED_SAMPLE_SEQUENCE";
	public final static String STUDY_ADMINISTRATION_SEQ_NAME = "STUDY_ADMINISTRATION_SEQ";
	public final static String STUDY_BIOSAMPLEREMOVAL_SEQ_NAME = "STUDY_BIOSAMPLE-REMOVAL_SEQ";
	public final static String STUDY_BIOSAMPLE_REMOVAL_SEQ_NAME = "STUDY_BIOSAMPLE_REMOVAL_SEQ";
	public final static String STUDY_ENCLOSURE_SEQUENCE_NAME = "STUDY_ENCLOSURE_SEQUENCE";
	public final static String STUDY_EXECUTION_DETAILS_SEQ_NAME = "STUDY_EXECUTION_DETAILS_SEQ";
	public final static String STUDY_NAMEDTREATMENT_SEQ_NAME = "STUDY_NAMEDTREATMENT_SEQ";
	public final static String STUDY_PROPERTY_LINK_SEQUENCE_NAME = "STUDY_PROPERTY_LINK_SEQUENCE";
	public final static String STUDY_PROPERTY_SEQUENCE_NAME = "STUDY_PROPERTY_SEQUENCE";
	public final static String STUDY_SAMPLING_SEQUENCE_NAME = "STUDY_SAMPLING_SEQUENCE";
	public final static String STUDY_SCHEDULE_PHASE_SEQUENCE_NAME = "STUDY_SCHEDULE_PHASE_SEQUENCE";
	public final static String STUDY_SCHEDULE_SEQUENCE_NAME = "STUDY_SCHEDULE_SEQUENCE";
	public final static String STUDY_SEQUENCE_NAME = "STUDY_SEQUENCE";
	public final static String STUDY_STAGE_SEQUENCE_NAME = "STUDY_STAGE_SEQUENCE";
	public final static String STUDY_SUBGROUP_SEQUENCE_NAME = "STUDY_SUBGROUP_SEQUENCE";
	public final static String SUBGROUP_PATTERN_SEQUENCE_NAME = "SUBGROUP_PATTERN_SEQUENCE";
	public final static String TREATMENT_SEQUENCE_NAME = "TREATMENT_SEQUENCE";
	public final static String FAVSTUDY_SEQUENCE_NAME = "FAVSTUDY_SEQ";
	public final static String ASSIGNMENT_PATTERN_SEQUENCE_NAME = "ASSIGNMENT_PATTERN_SEQUENCE";
	public final static String ANIMAL = "Animal";
	public final static String HUMAN = "Human";
	public final static String DATEOFDEATH = "DateOfDeath";
	public final static String STAINING = "Staining";
	public static final String SECTIONNO = "SectionNo";

	
	public final static String STUDYID_PREFIX = "S-0";
	public final static Integer NEWTRANSIENTID = 0;
	public static final String WEIGHING_TESTNAME = "Weighing";
	public static final String LENGTH_TESTNAME = "Length";
	public static final String FOODWATER_TESTNAME = "FoodWater";
	public static final String OBSERVATION_TESTNAME =  "Observation";
	public static final String CLINICAL_SIGN_TESTNAME =  "ClinicalSign";
	public static final String PKBIOANALYSISTESTNAME = "PK Bioanalysis";
	public static final String TUMOR_SIZE_TESTNAME = "Tumor Size";
	public static final String TUMOR_SIZE2_TESTNAME = "Tumor Size: 2 tumors";
	public static final String STUDY_PROPETY_CLINICAL =  "CLINICAL";
	public static final String STUDY_PROPETY_LICENSENO = "LICENSENO";
	public static final String STUDY_PROPETY_PROJECT = "PROJECT";
	public static final String STUDY_PROPETY_SITE = "SITE";
	public static final String STUDY_PROPETY_EXPERIMENTER = "EXPERIMENTER";
	public static final String STUDY_PROPETY_DISEASEAREA = "DISEASEAREA";
	public static final List<String> CLINICALSIGN_TIMEPOINTS=Arrays.asList(new String[]{"Before Dosing", "After Dosing", "C Max", "Morning", "Afternoon", "Evening"});;

	
	public static final String DISEASE = "DISEASE";
	public static final String THERAPY = "THERAPY";
	public static final String MEASUREMENT = "MEASUREMENT";
	public static final String SAMPLING = "SAMPLING";
	public static final String DISPOSAL = "DISPOSAL";
	public static final String GROUPASSIGN = "GROUPASSIGN";
	
	public static final String ADMIN = "admin";

	public static final String SEX_METADATA_NAME = "Sex";
	public static final String LICENSE_NO_METADATA_NAME="LicenseNo";
	public static final String DATASOURCE_METADATA_NAME = "DataSource";
	public static final String BIRTHDAY_METADATA_NAME="Birthday";
	public static final String DELIVERY_DATE_METADATA_NAME = "Delivery Date";
	public static final String EXPERIMENTER_METADATA_NAME = "Experimenter";


	public static final String TYPE_METADATA = "Type";

	public static final String ANIMALDB = "ANIMALDB";
	public static final String LOCATION_PREFIX = "LOC";
	
	public static final int LOGENTRY_COMMENTS_MAX_SIZE = 248;
	public static final String BLOC_SEPARATOR = "-";
	public static final String LOCATION_SEPARATOR = "/";
	public static final int MAX_HOLE=100;
	public static final String FOOD_ATTRIBUTE_NAME = "Food";
	public static final String WATER_ATTRIBUTE_NAME = "Water";
	public static final String[] VALID_VALUES = new String[] {"<LOD", ">LOD", "N/A", "NA", "?"};
	
	public static final String END_PHASE = "end phase";
	public static final String ASSIGNMENT = "assignment";
	public static final String ASSIGNED_SUBJECT = "assigned subject";
	public static final String STUDY = "study";
	public static final String ACTION = "action";
	public static final String ACTION_DEFINITION = "action definition";
	public static final String GROUP = "group";
	public static final String GROUP_SIZE = "group size";
	public static final String STAGE = "stage";
	public static final String SCHEDULE = "schedule";
	public static final String ARKTIC_LOCATION = "ARBB/H89.EG.E3.1/Arktic";
	public static final SimpleDateFormat DAYFORMAT = new SimpleDateFormat("yyyyMMdd");
	public static final String SOFTWARE_NAME="Spirit";
	public static final String AUX_RESULT_ALL = "AllResult";
	
	public static final int MAX_THREAD = 600;
}
