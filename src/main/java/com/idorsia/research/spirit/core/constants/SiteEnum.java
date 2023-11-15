package com.idorsia.research.spirit.core.constants;

public enum SiteEnum {
    THREEPLAB("3P Laboratory"),
    ACM("ACM"),
    ACTELION("Actelion"),
    ACTELION_CN("Actelion Shanghaï"),
    ACTELION_CH("Actelion Switzerland"),
    AE_COLLEGE("Albert Einstein College of Medicine"),
    NY("New York, USA"),
    AMYLGEN("Amylgen"),
    AVOGADRO("Avogadro"),
    BARC("BARC"),
    BSL("Basel"),
    BB_MUNCHEN("Biobank der Blutspender München"),
    BIOTRIAL("Biotrial"),
    BMG_UNIV("Birmingham University"),
    BLUCLIN_PORT("BlueClinical Phase 1 - Portugal"),
    BOLDER("Bolder BioPATH"),
    CELERION("Celerion, Belfast"),
    CHAMP_ONC("Champions oncology"),
    CHARLES_RIVER("Charles River France"),
    COVANCE("Covance"),
    CROWNBIO("CrownBio"),
    EUROFINS("Eurofins"),
    GHENT_UNIV_BE("Ghent Universiteit Belgium"),
    HEIDELBERG_DE("Heidelberg Pharma, Heidelberg, Germany"),
    IDORSIA("Idorsia"),
    JSW("JSW Life Sciences"),
    KANAZAWA("kanazawa University"),
    MD_BIO("mdbioscinences"),
    PROVENCHER_QC("Laboratory Dr Provencher PAH, University Quebec"),
    SLEEMAN_DE("Laboratory Prof. J. Sleeman, Laboratory Prof. J. Sleeman, University Heidelberg, Germany"),
    ANDERSON_USA("MD Anderson, Houston, Texas, USA"),
    MOCO("MOCO"),
    MULTIPLE("Multiple"),
    NEUROSYS("Neurosys"),
    ONC_DIJON("Oncodesgin, Dijon, France"),
    ORA("Ora, Inc."),
    PROQINASE_DE("ProQinase, Freiburg, Germany"),
    STRAUMAN("Prof. Dr. med. A Straumann"),
    RBM("RBM"),
    SIAF("SIAF"),
    AVENTURA_USA("Translational Clinical Research Aventura, USA"),
    UNIV_FRANKFURT("University of  Frankfurt"),
    UNIV_HANNOVER("University of Veterinary Medicine Hannover"),
    UNIV_WURZBURG("University of Würzburg"),
    UNIV_ZURICH("University of Zürich"),
    WROCLAW_PL("Wroclaw Research Centre (Poland)"),
    XENTECH("Xentech, Evry, France")
    ;

    private final String description;
    SiteEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
