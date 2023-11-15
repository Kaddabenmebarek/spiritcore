package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.service.GroupService;

@Service
public class GroupDozerConverter extends DozerConverter<GroupDto, Integer>{
	
	@Autowired
	private GroupService groupService = (GroupService) ContextShare.getContext().getBean("groupService");

	
	public GroupDozerConverter() {
		super(GroupDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(GroupDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public GroupDto convertFrom(Integer source, GroupDto destination) {
		if(source != null && source!=0) {
			return groupService.getGroupDto(source);
		}
		return null;
	}
	
}
