package com.idorsia.research.spirit.core.constants;

public enum Surgery {
	NONE("none"),
	CARDIOVASCULAR("Cardiovascular"),
	CAROTID_ARTERY("Carotid artery (C)"),
	DIGESTIVE("Digestive"),
	DOUBLE_BILE_DUCT_CANNULATION("Double bile duct cannulation (BDC)"),
	DOUBLE_BILE_DUCT_CANNULATION_JUGULAR_VEIN("Double bile-duct cannulation + jugular vein (BDC + JV)"),
	DOUBLE_BILE_DUCT_CANNULATION_JUGULAR_VEIN_CAROTID_ARTERY("Double bile-duct cannulation + jugular vein + carotid artery (BDC + JV + C)"),
	INTEGUMENTARY("Integumentary"),
	INTRADUODENAL_CATHETER("Intraduodenal catheter"),
	JUGULAR_VEIN("Jugular vein (JV)"),
	JUGULAR_VEIN_CAROTID_ARTERY("Jugular vein + carotid artery (JV + C)"),
	LYMPHATIC("Lymphatic"),
	MUSCULAR("Muscular"),
	NERVOUS("Nervous"),
	PORTAL_VEIN_CANNULATION("Portal vein cannulation (PV)"),
	REPRODUCTIVE("Reproductive"),
	RESPIRATORY("Respiratory"),
	SKELETAL("Skeletal"),
	URINARY("Urinary");

	private final String name;

	private Surgery(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
