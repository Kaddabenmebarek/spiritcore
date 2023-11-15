package com.idorsia.research.spirit.core.constants;

public enum HierarchyMode {
	/**All parents and all children*/
	ALL,
	/**All parents and all children (depth of 2 max)*/
	ALL_MAX2,
	/**return parents(excluding this)*/
	PARENTS,
	/**Includes this + dividing samples and attached samples*/
	AS_STUDY_DESIGN,
	/**Includes this + attached samples, stop at dividing samples*/
	ATTACHED_SAMPLES,
	/**CHILDREN excludes 'this' */
	CHILDREN,
	/**CHILDREN_NOT_ATTACHED = CHILDREN minus all attached*/
	CHILDREN_NOT_ATTACHED,
	SIEBLINGS,
	TERMINAL
}
