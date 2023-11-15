package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Privacy;

@Service
public class PrivacyDozerConverter extends DozerConverter<Privacy, Integer>{
	
	public PrivacyDozerConverter() {
		super(Privacy.class, Integer.class);
	}

	@Override
	public Integer convertTo(Privacy source, Integer destination) {
		if(source != null) {
			//FIXME IS THAT THE CASE?
			switch (source) {
			case INHERITED:
				return 0;
			case PUBLIC:
				return 1;
			case PROTECTED:
				return 2;
			case PRIVATE:
				return 3;
			default:
				break;
			}
			//return source.name();
		}
		return null;
	}

	@Override
	public Privacy convertFrom(Integer source, Privacy destination) {
		//FIXME IS THAT THE CASE?
		switch (source) {
		case 0:
			return Privacy.INHERITED;
		case 1:
			return Privacy.PUBLIC;
		case 2:
			return Privacy.PROTECTED;
		case 3:
			return Privacy.PRIVATE;
		default:
			break;
		}
//		if(source != null ) {
//			return Privacy.valueOf(source);
//		}
		return null;
	}
}
