package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.util.MiscUtils;

public class PlannedSampleDto implements IObject, Serializable{

	private static final long serialVersionUID = 6185853753007488548L;
	private Integer id = Constants.NEWTRANSIENTID;
	private BiosampleDto biosample;
	private String name;
	private StageDto stage;
	private Double weight;
	private String dataList;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private List<Double> dataListList = null;
	
	public PlannedSampleDto() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BiosampleDto getBiosample() {
		return biosample;
	}
	/**Do not call this method directly but call the service instead
	 * PlannedBiosampleService.setBiosample(this, biosample);
	 * @param biosample
	 */
	@Deprecated
	public void setBiosample(BiosampleDto biosample) {
		this.biosample = biosample;
	}
	public String getName() {
		return name;
	}
	/**Do not call this method directly but call the service instead 
	 *PlannedSampleService.setname(this, name);
	 */
	@Deprecated
	public void setName(String name) {
		this.name = name;
	}
	public StageDto getStage() {
		return stage;
	}
	public void setStage(StageDto stage) {
		this.stage = stage;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getDataList() {
		return dataList;
	}
	public List<Double> getDataListList() {
		if (dataListList == null) {
			dataListList = MiscUtils.deserializeDataList(dataList);
		}
		return dataListList;
	}
	public void setDataList(String dataList) {
		this.dataList = dataList;
		this.dataListList=null;
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

	/**
	 * @param dataListList
	 * use PlannedSampleService.deserializeDataList()
	 */
	@Deprecated
	public void setDataListList(List<Double> dataListList) {
		this.dataListList = dataListList;
	}
}
