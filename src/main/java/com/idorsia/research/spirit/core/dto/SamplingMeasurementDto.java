package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.service.SamplingMeasurementService;

public class SamplingMeasurementDto implements IObject, Serializable, Comparable<SamplingMeasurementDto>{

	private static final long serialVersionUID = -7826065168119723312L;
	@Autowired
	private SamplingMeasurementService samplingMeasurementService;
	private Integer id = Constants.NEWTRANSIENTID;
	private SamplingDto sampling;
	private AssayDto assay;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<SamplingMeasurementAttributeDto> attributes;
	private Measurement measurement;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SamplingDto getSampling() {
		return sampling;
	}

	public void setSampling(SamplingDto sampling) {
		this.sampling = sampling;
	}

	public AssayDto getAssay() {
		return assay;
	}

	public void setAssay(AssayDto assay) {
		this.assay = assay;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public List<SamplingMeasurementAttributeDto> getAttributesNoMapping() {
		return attributes;
	}
	
	public List<SamplingMeasurementAttributeDto> getAttributes() {
		if(attributes == null) {
			getSamplingMeasurementService().mapAttributes(this);
		}
		return attributes;
	}

	public void setAttributes(List<SamplingMeasurementAttributeDto> attributes) {
		Collections.sort(attributes);
		this.attributes = attributes;
	}
	
	public SamplingMeasurementService getSamplingMeasurementService() {
		if(samplingMeasurementService == null) {
			samplingMeasurementService = (SamplingMeasurementService) ContextShare.getContext().getBean("samplingMeasurementService");
		}
		return samplingMeasurementService;
	}

	public Measurement getMeasurement() {
		if(measurement==null) {
			getSamplingMeasurementService().getMeasurement(this);
		}
		return measurement;
	}

	/**Do not call this method directly, please use the service insteed
	 * SamplingMeasurementService.setMeasurement(this, measurement);
	 */
	@Deprecated
	public void setMeasurement(Measurement measurement) {
		this.measurement=measurement;
	}

	@Override
	public int compareTo(SamplingMeasurementDto o) {
		if (o.equals(this)) return 0;
		int c = o.getSampling().compareTo(getSampling());
		if(c!=0)
			return c;
		return o.getAssay().compareTo(getAssay());
	}
}
