package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Date;

public class Execution implements Serializable {
	private static final long serialVersionUID = 4675639491880097349L;
	private Date executionDate;

    public Execution() {
    }

    public Execution(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Date getExecutionDate() {
        return executionDate==null ? null : Date.from(executionDate.toInstant());
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
}
