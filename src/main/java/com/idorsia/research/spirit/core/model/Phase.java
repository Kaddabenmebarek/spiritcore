package com.idorsia.research.spirit.core.model;

import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Phase implements Serializable, IObject{

	private static final long serialVersionUID = -2525103921502996211L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer stageId;
	private Long phase;
	private String label;
	
	public Phase() {
		super();
	}

	public Phase(Integer id, Integer stageId, Long phase, String label) {
		super();
		this.id = id;
		this.stageId = stageId;
		this.phase = phase;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStageId() {
		return stageId;
	}

	public void setStageId(Integer stageId) {
		this.stageId = stageId;
	}

	public Long getPhase() {
		return phase;
	}

	public void setPhase(Long phase) {
		this.phase = phase;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((phase == null) ? 0 : phase.hashCode());
		result = prime * result + ((stageId == null) ? 0 : stageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phase other = (Phase) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (phase == null) {
			if (other.phase != null)
				return false;
		} else if (!phase.equals(other.phase))
			return false;
		if (stageId == null) {
			if (other.stageId != null)
				return false;
		} else if (!stageId.equals(other.stageId))
			return false;
		return true;
	}

}
