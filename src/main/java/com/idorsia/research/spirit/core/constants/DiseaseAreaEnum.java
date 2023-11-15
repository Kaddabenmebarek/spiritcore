package com.idorsia.research.spirit.core.constants;

public enum DiseaseAreaEnum {
	ALS("ALS"),
	AGING("Aging"),
	CNS("CNS"),
	CV("CV"),
	CYSTIC("Cystic fibrosis"),
	EDM("EDM"),
	FIBRO("Fibrosis"),
	GM("GM"),
	HF("HF"),
	IMUNO("Immuno"),
	IMUNO_PS("Immuno PS"),
	IMUNO_SLE("Immuno SLE"),
	KF("KF"),
	INFLAMA("Lung inflammation"),
	MS("MS"),
	ONCO("Oncology"),
	PAH("PAH"),
	PKD("PKD"),
	RA("RA"),
	RHT("RHT"),
	TOX("TOX"),
	VASCU("Vascular Dementia")
    ;

    private final String description;
    DiseaseAreaEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
