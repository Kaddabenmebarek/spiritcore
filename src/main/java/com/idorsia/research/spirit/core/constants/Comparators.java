package com.idorsia.research.spirit.core.constants;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.service.AssignmentService;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.service.StudyActionService;

public class Comparators {
	
	@Autowired
	private BiosampleService biosampleService;
	
	public static final Comparator<BiosampleDto> COMPARATOR_NATURAL = new Comparator<BiosampleDto>() {
		@Override
		public int compare(BiosampleDto o1, BiosampleDto o2) {
			if(o2==null) return o1==null?0: -1;
			if(o2==o1) return 0;
			if(o1.getId()>0 && o2.getId()>0 && o1.getId()==o2.getId()) return 0;

			//Compare TopParent (ie type, date)
			BiosampleDto p1 = o1.getTopParent();
			BiosampleDto p2 = o2.getTopParent();
			int c = compareToNoHierarchyCheck(p1, p2);
			if(c!=0) return c;

			return compareToNoHierarchyCheck(o1, o2);
		}
	};
	
	public static final Comparator<BiosampleDto> COMPARATOR_GROUP_SAMPLE_NAME = new Comparator<BiosampleDto>() {
		@Override
		public int compare(BiosampleDto o1, BiosampleDto o2) {
			if(o2==null) return -1;
			if(o2==o1) return 0;
			int c;

			//Compare phases
			c = o1.getInheritedPhase()==null? (o2.getInheritedPhase()==null?0:1): o1.getInheritedPhase().compareTo(o2.getInheritedPhase());
			if(c!=0) return c;

			//Compare SampleName
			c =  CompareUtils.compare((o1.getLocalId()==null? "": o1.getLocalId()), (o2.getLocalId()==null? "": o2.getLocalId()));
			if(c!=0) return c;

			//Compare SampleId
			c =  (o1.getSampleId()==null? "": o1.getSampleId()).compareTo((o2.getSampleId()==null? "": o2.getSampleId()));
			if(c!=0) return c;

			//Should never happen
			return o1.getId()-o2.getId();
		}
	};

	public final static Comparator<AssignmentDto> STUDY_GROUP_ASSIGMENT_COMPARATOR = new Comparator<AssignmentDto>() {
		AssignmentService assignmentService = (AssignmentService) ContextShare.getContext().getBean("assignmentService");
		@Override
		public int compare(AssignmentDto o1, AssignmentDto o2) {
			if (o1==null&&o2==null)	return 0;
			if (o1==null) return -1;
			if (o2==null) return 1;
			int c = o1.getSubgroup().compareTo(o2.getSubgroup());
			if (c!=0) {
				return c;
			}
			EnclosureDto e1 = assignmentService.getEnclosure(o1);
			EnclosureDto e2 = assignmentService.getEnclosure(o2);
			if (e1==null&&e2==null)	
				return 0;
			if (e1==null) 
				return -1;
			if (e2==null) 
				return 1;
			c=e1.compareTo(e2);
			if (c!=0) {
				return c;
			}
			return CompareUtils.compareAlphaNumeric(o1.getName(), o2.getName());
		}
	};

	public static final Comparator<EnclosureDto> ENCLOSURE_COMPARATOR_BY_NAME = new Comparator<EnclosureDto>() {
		 @Override
	        public int compare(EnclosureDto o1, EnclosureDto o2) {
	            if (o2 == null) return o1 == null ? 0 : -1;
	            if (o2 == o1) return 0;
	            String n1 = o1.getName();
	            String n2 = o2.getName();
	            int c = n1 == null ? (n2 == null ? 0 : 1) : n1.compareTo(n2);
	            return c;
	        }
	};
	
	public static final Comparator<GroupDto> GROUP_COMPARATOR_BY_NAME = new Comparator<GroupDto>() {
		@Override
		public int compare(GroupDto o1, GroupDto o2) {
			if (o2 == null) return o1 == null ? 0 : -1;
			if (o2 == o1) return 0;
			String n1 = o1.getName();
			String n2 = o2.getName();
			int c = n1 == null ? (n2 == null ? 0 : 1) : n1.compareTo(n2);
			return c;
		}
	};
	
	public static final Comparator<SubGroupDto> SUBGROUP_COMPARATOR_BY_NAME = new Comparator<SubGroupDto>() {
		 @Override
		 public int compare(SubGroupDto o1, SubGroupDto o2) {
	            if (o2 == null) return o1 == null ? 0 : -1;
	            if (o2 == o1) return 0;
	            String n1 = o1.getFullName();
	            String n2 = o2.getFullName();
	            int c = n1 == null ? (n2 == null ? 0 : 1) : n1.compareTo(n2);
	            return c;
	        }
	};
	
	private static int compareToNoHierarchyCheck(BiosampleDto o1, BiosampleDto o2) {
		int c;

		//Compare phases
		c = CompareUtils.compare(o1.getInheritedPhase(), o2.getInheritedPhase());
		if(c!=0) return c;

		//Compare biotype
		c = o1.getBiotype()==null? (o2.getBiotype()==null?0:1): o1.getBiotype().compareTo(o2.getBiotype());
		if(c!=0) return c;


		//Compare SampleName (if any): the comparison should compare numbers in a numeral and not in lexical comparison, so that 1<8<10
		String s1 = o1.getBiotype()==null?"": o1.getBiotype().getCategory()==BiotypeCategory.PURIFIED && !o1.getBiotype().getIsHidden()? 
				o1.getSampleId(): o1.getBiotype().getNameLabel()!=null? o1.getLocalId():"";
		String s2 = o2.getBiotype()==null?"": o2.getBiotype().getCategory()==BiotypeCategory.PURIFIED && !o2.getBiotype().getIsHidden()? 
				o2.getSampleId(): o2.getBiotype().getNameLabel()!=null? o2.getLocalId():"";
		c = CompareUtils.compare(s1, s2);
		if(c!=0) return c;

		//Compare
		c = (o1.getComments()==null?"":o1.getComments()).compareToIgnoreCase((o2.getComments()==null?"":o2.getComments()));
		if(c!=0) return c;

		//Compare containers
		c = CompareUtils.compare(o1.getContainerType(), o2.getContainerType());
		if(c!=0) return c;

		c = CompareUtils.compare(o1.getContainerId(), o2.getContainerId());
		if(c!=0) return c;

		//Compare sampleId
		return CompareUtils.compare(o1.getSampleId(), o2.getSampleId());
	}
	
	public final static Comparator<AssayResultDto> COMPARATOR_UPDDATE = new Comparator<AssayResultDto>() {
		@Override
		public int compare(AssayResultDto o1, AssayResultDto o2) {
			if(o2==null) return 1;
			return -CompareUtils.compare(o1.getUpdDate(), o2.getUpdDate());
		}
	};
	
	public final static Comparator<BiosampleDto> COMPARATOR_POS = new Comparator<BiosampleDto>() {
		@Override
		public int compare(BiosampleDto o1, BiosampleDto o2) {
			if(o2==null) return -1;
			int c = CompareUtils.compare(o1.getLocation(), o2.getLocation());
			if(c!=0) return c;
			c = CompareUtils.compare(o1.getScannedPosition(), o2.getScannedPosition());
			if(c!=0) return c;
			return o1.getLocationPos() - o2.getLocationPos();
		}
	};

	public final Comparator<BiosampleDto> COMPARATOR_NAME = new Comparator<BiosampleDto>() {
		@Override
		public int compare(BiosampleDto o1, BiosampleDto o2) {
			if(o2==null) return 1;
			return CompareUtils.compare(biosampleService.getSampleNameOrId(o1), biosampleService.getSampleNameOrId(o2));
		}
	};
	public final static Comparator<StudyAction> STUDY_ACTION_EXACT_COMPARATOR = new Comparator<StudyAction>() {
		StudyActionService studyActionService = (StudyActionService) ContextShare.getContext().getBean("studyActionService");
        @Override
        public int compare(StudyAction o1, StudyAction o2) {
            int c = o1.getName().compareTo(o2.getName());
            if (c != 0) return c;

            c = CompareUtils.compare(studyActionService.getDescription(o1), studyActionService.getDescription(o2));
            if (c != 0) return c;

            c = CompareUtils.compare(o1.getDuration(), o2.getDuration());
            if (c != 0) return c;

            c = CompareUtils.compare(o1.getType(), o2.getType());
            if (c != 0) return c;

            c = CompareUtils.compare(o1.getColor(), o2.getColor());
            if (c != 0) return c;

            //			c = CompareUtils.compare(o1.getDividingSampling(), o2.getDividingSampling());
            //			if(c!=0) return c;

            return 0;
        }
    };
}
