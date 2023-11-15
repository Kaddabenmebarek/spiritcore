package com.idorsia.research.spirit.core.constants;

public enum FindDuplicateMethod {
	RETURN_ALL("Return ALL duplicated results"),
	RETURN_FIRST_ELB("Return the FIRST result (from the first elb or first id)"),
	RETURN_EXCEPT_FIRST_ELB("Return all except the FIRST result (from the first elb or first id)"),
	RETURN_OLDEST_2MNS("Return the OLDEST results (and at least 2mns older)"),
	RETURN_NEWEST_2MNS("Return the NEWEST results (and at least 2mns newer)");

	private String desc;
	private FindDuplicateMethod(String desc) {this.desc = desc;}
	@Override
	public String toString() {return desc;}

}
