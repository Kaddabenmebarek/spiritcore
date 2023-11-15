package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.SubGroupDto;
import com.idorsia.research.spirit.core.service.SubGroupService;

@Service
public class SubGroupDozerConverter extends DozerConverter<SubGroupDto, Integer> {

	@Autowired
	private SubGroupService subGroupService=(SubGroupService) ContextShare.getContext().getBean("subGroupService");

	public SubGroupDozerConverter() {
		super(SubGroupDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(SubGroupDto source, Integer destination) {
		if (source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public SubGroupDto convertFrom(Integer source, SubGroupDto destination) {
		if (source != null && source!=0) {
			return subGroupService.getSubGroupDto(source);
		}
		return null;
	}
}
