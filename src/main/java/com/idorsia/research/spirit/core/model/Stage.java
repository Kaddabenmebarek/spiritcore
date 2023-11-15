package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;
import com.idorsia.research.spirit.core.service.StageService;

public class Stage implements Serializable, IObject, Comparable<Stage> {

	private static final long serialVersionUID = 2977821124426169652L;
	@Autowired
	private StageService stageService;
	
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer studyId;
	private Integer dynamic;
	private Integer biotypeId;
	private String name;
	private Integer nextId;
	private Date startDate;
	private Long offsetOfD0;
	private Long offsetFromPreviousstage;
	private Integer duration;
	private String startingDay;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public Stage() {
		super();
	}

	public Stage(Integer id, Integer studyId, Integer dynamic) {
		super();
		this.id = id;
		this.studyId = studyId;
		this.dynamic = dynamic;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getDynamic() {
		return dynamic;
	}

	public void setDynamic(Integer dynamic) {
		this.dynamic = dynamic;
	}

	public Integer getBiotypeId() {
		return biotypeId;
	}

	public void setBiotypeId(Integer biotypeId) {
		this.biotypeId = biotypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNextId() {
		return nextId;
	}

	public void setNextId(Integer nextId) {
		this.nextId = nextId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Long getOffsetOfD0() {
		return offsetOfD0;
	}

	public void setOffsetOfD0(Long offsetOfDO) {
		this.offsetOfD0 = offsetOfDO;
	}

	public Long getOffsetFromPreviousstage() {
		return offsetFromPreviousstage;
	}

	public void setOffsetFromPreviousstage(Long offsetFromPreviousstage) {
		this.offsetFromPreviousstage = offsetFromPreviousstage;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getStartingDay() {
		return startingDay;
	}

	public void setStartingDay(String startingDay) {
		this.startingDay = startingDay;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biotypeId == null) ? 0 : biotypeId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((dynamic == null) ? 0 : dynamic.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nextId == null) ? 0 : nextId.hashCode());
		result = prime * result + ((offsetFromPreviousstage == null) ? 0 : offsetFromPreviousstage.hashCode());
		result = prime * result + ((offsetOfD0 == null) ? 0 : offsetOfD0.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((startingDay == null) ? 0 : startingDay.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
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
		Stage other = (Stage) obj;
		if (biotypeId == null) {
			if (other.biotypeId != null)
				return false;
		} else if (!biotypeId.equals(other.biotypeId))
			return false;
		if (creDate == null) {
			if (other.creDate != null)
				return false;
		} else if (!creDate.equals(other.creDate))
			return false;
		if (creUser == null) {
			if (other.creUser != null)
				return false;
		} else if (!creUser.equals(other.creUser))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (dynamic == null) {
			if (other.dynamic != null)
				return false;
		} else if (!dynamic.equals(other.dynamic))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nextId == null) {
			if (other.nextId != null)
				return false;
		} else if (!nextId.equals(other.nextId))
			return false;
		if (offsetFromPreviousstage == null) {
			if (other.offsetFromPreviousstage != null)
				return false;
		} else if (!offsetFromPreviousstage.equals(other.offsetFromPreviousstage))
			return false;
		if (offsetOfD0 == null) {
			if (other.offsetOfD0 != null)
				return false;
		} else if (!offsetOfD0.equals(other.offsetOfD0))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (startingDay == null) {
			if (other.startingDay != null)
				return false;
		} else if (!startingDay.equals(other.startingDay))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Stage o) {
		if(o==null) return -1;
		if (o.equals(this)) return 0;
		if (!studyId.equals(o.getStudyId())) {
			return studyId.compareTo(o.getStudyId());
		}
		Integer nextStage = o.getNextId();
		if (nextStage == null) return -1;
		if (this.getNextId() == null) return 1;
		if (nextStage.equals(id)) return 1;
		return compareTo(getStageService().get(o.getNextId()));
	}

	public StageService getStageService() {
		if(stageService == null) {
			stageService = (StageService) ContextShare.getContext().getBean("stageService");
		}
		return stageService;
	}
}
