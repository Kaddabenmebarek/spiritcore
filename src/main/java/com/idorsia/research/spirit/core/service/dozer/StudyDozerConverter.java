package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.service.StudyService;

@Service
public class StudyDozerConverter extends DozerConverter<StudyDto, Integer>{
	
	@Autowired
	private StudyService studyService = (StudyService) ContextShare.getContext().getBean("studyService");
	
	public StudyDozerConverter() {
		super(StudyDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(StudyDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public StudyDto convertFrom(Integer source, StudyDto destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			return studyService.getStudyDto(source);
		}
		return null;
	}
}
