package com.idorsia.research.spirit.core.constants;

public enum BarcodeType {
	/** The biosampleId must be scanned and the containerId = biosampleId */
	MATRIX,
	/** The biosampleId must be generated and the containerId = biosampleId */
	NOBARCODE,
	/** The biosampleId must be generated and the containerId also */
	GENERATE
}
