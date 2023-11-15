package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.service.AssignmentService;

@Service
public class AssignmentDozerConverter extends DozerConverter<AssignmentDto, Integer>{
	
	@Autowired
	private AssignmentService assignmentService = (AssignmentService) ContextShare.getContext().getBean("assignmentService");
	
	public AssignmentDozerConverter() {
		super(AssignmentDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(AssignmentDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public AssignmentDto convertFrom(Integer source, AssignmentDto destination) {
		if(source != null) {
			return assignmentService.getAssignmentDto(source);
		}
		return null;
	}

}
