package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.service.SamplingService;

@Component
public class SamplingDto implements IObject, Comparable<SamplingDto>, Serializable{

	private static final long serialVersionUID = -605997941959210633L;
	@Autowired
	private SamplingService samplingService;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiotypeDto biotype;
	private NamedSamplingDto namedSampling;
	private Boolean weighingRequired;
	private SamplingDto parentSampling;
	private Integer locIndex;
	private Boolean commentsRequired;
	private ContainerType containerType;
	private Boolean lengthRequired;
	private String comments;
	private Double amount;
	private String sampleName;
	private Integer rowNumber;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Set<BiosampleDto> samples;
	private List<SamplingDto> children;
	private List<SamplingMeasurementDto> extraMeasurements;
	private List<SamplingParameterDto> parameters;

	public SamplingDto() {
	}
	
	public SamplingDto(SamplingDto sampling) {
		this.parameters=sampling.getParameters();
		this.biotype=sampling.getBiotype();
		this.weighingRequired=sampling.getWeighingRequired();
		this.locIndex=sampling.getLocIndex();
		this.commentsRequired=sampling.getCommentsRequired();
		this.containerType=sampling.getContainerType();
		this.lengthRequired=sampling.getLengthRequired();
		this.comments=sampling.getComments();
		this.amount=sampling.getAmount();
		this.sampleName=sampling.getSampleName();
		this.extraMeasurements=sampling.getExtraMeasurements();
		this.rowNumber=sampling.getRowNumber();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Deprecated
	public List<SamplingParameterDto> getParametersNoMapping() {
		return parameters;
	}
	
	public List<SamplingParameterDto> getParameters() {
		if(parameters==null) {
			getSamplingService().getParameters(this);
		}
		return parameters;
	}

	public void setParameters(List<SamplingParameterDto> parameters) {
		this.parameters = parameters;
	}

	public BiotypeDto getBiotype() {
		return biotype;
	}

	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}

	public NamedSamplingDto getNamedSampling() {
		return namedSampling;
	}

	public void setNamedSampling(NamedSamplingDto namedSampling) {
		this.namedSampling = namedSampling;
	}

	public Boolean getWeighingRequired() {
		return weighingRequired==Boolean.TRUE;
	}

	public void setWeighingRequired(Boolean weighingRequired) {
		this.weighingRequired = weighingRequired;
	}

	public SamplingDto getParentSampling() {
		return parentSampling;
	}

	public void setParentSampling(SamplingDto parentSampling) {
		this.parentSampling = parentSampling;
	}

	public Integer getLocIndex() {
		return locIndex;
	}

	public void setLocIndex(Integer locIndex) {
		this.locIndex = locIndex;
	}

	public Boolean getCommentsRequired() {
		return commentsRequired==Boolean.TRUE;
	}

	public void setCommentsRequired(Boolean commentsRequired) {
		this.commentsRequired = commentsRequired;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}

	public Boolean getLengthRequired() {
		return lengthRequired==Boolean.TRUE;
	}

	public void setLengthRequired(Boolean lengthRequired) {
		this.lengthRequired = lengthRequired;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public void setExtraMeasurements(List<SamplingMeasurementDto> extraMeasurements) {
		this.extraMeasurements = extraMeasurements;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	public void setSamples(Set<BiosampleDto> samples) {
		this.samples = samples;
	}

	public Set<BiosampleDto> getSamples() {
		if(samples == null) {
			getSamplingService().mapSamples(this);
		}
		return samples;
	}

	@Deprecated
	public Set<BiosampleDto> getSamplesNoMapping() {
		return samples;
	}
	
	public List<SamplingDto> getChildren() {
		if(children == null) {
			getSamplingService().mapChildren(this);
		}
		return children;
	}

	@Deprecated
	public List<SamplingDto> getChildrenNoMapping() {
		return children;
	}
	
	public void setChildren(List<SamplingDto> children) {
		this.children=children;
	}

	@Deprecated
	public List<SamplingMeasurementDto> getExtraMeasurementsNoMapping() {
		return extraMeasurements;
	}
	
	public List<SamplingMeasurementDto> getExtraMeasurements() {
		if(extraMeasurements==null) {
			getSamplingService().getExtraMeasurements(this);
		}
		return extraMeasurements;
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

	@Override
	public int compareTo(SamplingDto o) {
		int c = CompareUtils.compare(biotype, o.getBiotype());
		if(c!=0) return c;

		c = CompareUtils.compare(getSampleName(), o.getSampleName());
		if(c!=0) return c;

		c = CompareUtils.compare(getParameters(), o.getParameters());
		if(c!=0) return c;

		c = CompareUtils.compare(getComments(), o.getComments());
		if(c!=0) return c;

		c = CompareUtils.compare(getContainerType(), o.getContainerType());
		if(c!=0) return c;

		c = CompareUtils.compare(getLocIndex(), o.getLocIndex());
		if(c!=0) return c;
		
		c = CompareUtils.compare(getParentSampling(), o.getParentSampling());
		if(c!=0) return c;

		if(getId()>0 && o.getId()>0) {
			c = getId()-o.getId();
			if(c!=0) return c;
		}

		return 0;
	}

	public SamplingService getSamplingService() {
		if(samplingService == null) {
			samplingService = (SamplingService) ContextShare.getContext().getBean("samplingService");
		}
		return samplingService;
	}
	
	
	
}
