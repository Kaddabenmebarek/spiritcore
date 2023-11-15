package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.DocumentDto;
import com.idorsia.research.spirit.core.service.DocumentService;

@Service
public class DocumentDozerConverter extends DozerConverter<DocumentDto, Integer>{
	
	private DocumentService documentService=(DocumentService) ContextShare.getContext().getBean("documentService");
	
	public DocumentDozerConverter() {
		super(DocumentDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(DocumentDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public DocumentDto convertFrom(Integer source, DocumentDto destination) {
		if(source != null && source != Constants.NEWTRANSIENTID) {
			return documentService.getDocumentDto(source);
		}
		return null;
	}
}
