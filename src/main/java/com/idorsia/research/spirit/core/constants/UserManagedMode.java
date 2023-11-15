package com.idorsia.research.spirit.core.constants;

public enum UserManagedMode {
	/**No user rights. Just one single user with admin rights */
	UNIQUE_USER,
	/**Read queries on spirit.employee and spirit.employee_groups (DB views or managed programatically by an other app)*/
	READ_ONLY,
	/**Read/write queries on spirit.employee and spirit.employee_groups (DB views or managed programatically by an other app). The password column is ignored*/
	WRITE_NOPWD,
	/**Read/write queries on spirit.employee and spirit.employee_groups (DB views or managed programatically by an other app). The password column is encrypted*/
	WRITE_PWD
}
