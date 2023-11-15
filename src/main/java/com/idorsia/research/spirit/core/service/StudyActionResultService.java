package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.AdministrationDto;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.SpiritChangeHelper;

@Service
public class StudyActionResultService implements Serializable {
	
	private static final long serialVersionUID = -5424206575014549787L;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private AdministrationService administrationService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private AssayResultService assayResultService;

	public void persistStudyActionResult(StudyActionResult studyActionResult) throws Exception {
		if(studyActionResult==null) return;
		persistStudyActionResult(Collections.singletonList(studyActionResult));
	}

	public void persistStudyActionResult(Collection<StudyActionResult> studyActionResults) throws Exception {

		if(studyActionResults==null || studyActionResults.size()==0) return;

		for (StudyActionResult result : studyActionResults) {
			if(result instanceof BiosampleDto) {
				if(((BiosampleDto) result).getAttachedSampling()!=null)
					((BiosampleDto) result).getAttachedSampling().getSamples().add((BiosampleDto) result);
				biosampleService.save((BiosampleDto)result);
			}
			if(result instanceof AdministrationDto) {
				administrationService.save((AdministrationDto)result);
			}
			if(result instanceof AssignmentDto) {
				assignmentService.save((AssignmentDto)result);
			}
			if(result instanceof AssayResultDto) {
				assayResultService.save((AssayResultDto)result);
			}
		}
	}

	public void deleteStudyActionResult(StudyActionResult studyActionResult) throws Exception {
		if(studyActionResult==null) return;
		deleteStudyActionResult(Collections.singletonList(studyActionResult));
	}
	
	public void deleteStudyActionResult(Collection<StudyActionResult> studyActionResults) throws Exception {
		if(studyActionResults==null || studyActionResults.size()==0) return;
		for (StudyActionResult result : studyActionResults) {
			if(result instanceof BiosampleDto) {
				if(((BiosampleDto)result).getParent()!=null)
					((BiosampleDto)result).getParent().getChildren().remove(result);
				if(((BiosampleDto) result).getAttachedSampling()!=null)
					((BiosampleDto) result).getAttachedSampling().getSamples().remove(result);
				biosampleService.delete((BiosampleDto)result);
			}
			if(result instanceof AdministrationDto) {
				administrationService.delete((AdministrationDto)result);
			}
			if(result instanceof AssignmentDto) {
				assignmentService.delete((AssignmentDto)result);
			}
			if(result instanceof AssayResultDto) {
				assayResultService.delete((AssayResultDto)result);
			}
		}
	}
	
	public void checkBeforeDeleteStudyActionResult(String objectToChange, StudyActionResultQuery query) throws Exception {
		Cache.getInstance().clear();
		SpiritChangeHelper.getInstance().addLevelToCheck(objectToChange, query);
	}
}
