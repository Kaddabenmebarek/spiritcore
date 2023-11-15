package com.idorsia.research.spirit.core.model;

import java.util.Date;

public class AnimalDBPojo {
	private Integer animalId;
    private Date endDate;
    private Integer license;
    private Integer severity;
    private String comment;

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getLicense() {
        return license;
    }

    public void setLicense(Integer license) {
        this.license = license;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
