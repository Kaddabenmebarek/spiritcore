package com.idorsia.research.spirit.core.service.dozer;

import java.util.HashMap;
import java.util.Map;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.actelion.research.business.Department;
import com.actelion.research.services.DBDepartment;
import com.idorsia.research.spirit.core.constants.Constants;

@Service
public class DepartmentDozerConverter extends DozerConverter<Department, Integer>{
	
	Map<Integer, Department> idToDepartment = new HashMap<>();
	
	public DepartmentDozerConverter() {
		super(Department.class, Integer.class);
	}

	@Override
	public Integer convertTo(Department source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public Department convertFrom(Integer source, Department destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			Department department = idToDepartment.get(source);
			if(department==null){
				department = DBDepartment.loadDepartment(source);
				idToDepartment.put(source, department);
			}
			return department;
		}
		return null;
	}
}
