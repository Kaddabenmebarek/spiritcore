package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.DocumentType;

@Service
public class DocumentTypeDozerConverter extends DozerConverter<DocumentType, String>{
	
	public DocumentTypeDozerConverter() {
		super(DocumentType.class, String.class);
	}

	@Override
	public String convertTo(DocumentType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public DocumentType convertFrom(String source, DocumentType destination) {
		if(source != null) {
			return DocumentType.valueOf(source);
		}
		return null;
	}
}
