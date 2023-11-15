package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;

import javax.measure.Quantity;
import javax.measure.Unit;

import com.idorsia.research.spirit.core.constants.BatchType;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.Execution;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.uom.IdorsiaUnits;

import tech.units.indriya.quantity.Quantities;

public class AdministrationDto implements IObject, StudyActionResult, Serializable{

	private static final long serialVersionUID = -220626070217832961L;
	private Integer id = Constants.NEWTRANSIENTID;
	private NamedTreatmentDto namedTreatment;
	private BiosampleDto biosample;
	private PhaseDto phase;
	private String batchId;
	private BatchType batchType;
	private Float effectiveamount;
	private Integer effectiveamountunitId;
	private String elb;
	private String admComment;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;
	private Execution execution;
	
	private Quantity<?> effectiveAmountQuantity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NamedTreatmentDto getNamedTreatment() {
		return namedTreatment;
	}

	public void setNamedTreatment(NamedTreatmentDto namedTreatment) {
		this.namedTreatment = namedTreatment;
	}

	public BiosampleDto getBiosample() {
		return biosample;
	}

	/**Do not call this method directly but call the service instead 
	 *AdministrationService.setBiosample(this, biosample);
	 */
	@Deprecated
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}

	public PhaseDto getPhase() {
		return phase;
	}

	public void setPhase(PhaseDto phase) {
		this.phase = phase;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public BatchType getBatchType() {
		return batchType;
	}

	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
	}

	public Float getEffectiveamount() {
		return effectiveamount;
	}

	public void setEffectiveamount(Float effectiveamount) {
		this.effectiveamount = effectiveamount;
	}

	public Integer getEffectiveamountunitId() {
		return effectiveamountunitId;
	}

	public void setEffectiveamountunitId(Integer effectiveamountunitId) {
		this.effectiveamountunitId = effectiveamountunitId;
	}

	public Date getExecutiondate() {
		return execution==null? null : execution.getExecutionDate();
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public String getAdmComment() {
		return admComment;
	}

	public void setAdmComment(String admComment) {
		this.admComment = admComment;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}
	
    public Quantity<?> getEffectivAmountQuantity() {
        if (effectiveAmountQuantity==null && getEffectiveamount() != null && getEffectiveamountunitId() != null) {
        	effectiveAmountQuantity = Quantities.getQuantity(getEffectiveamount(), (Unit<?>) IdorsiaUnits.getUnitById(getEffectiveamountunitId()));
        }
        return effectiveAmountQuantity;
    }

	public void setEffectiveAmountQuantity(Quantity<?> effectiveAmountQuantity) {
		this.effectiveAmountQuantity = effectiveAmountQuantity;
        if (effectiveAmountQuantity == null) {
            setEffectiveamount(null);
            setEffectiveamountunitId(null);
        } else {
            setEffectiveamount(Float.valueOf(effectiveAmountQuantity.getValue().toString()));
            setEffectiveamountunitId(IdorsiaUnits.getUnitId(effectiveAmountQuantity.getUnit()));
        }
	}
}
