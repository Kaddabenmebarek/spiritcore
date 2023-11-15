package com.idorsia.research.spirit.core.model;

import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class FavoriteStudy implements Serializable, IObject {

	private static final long serialVersionUID = 8489630930015717887L;

	private Integer id = Constants.NEWTRANSIENTID;
	private int userId;
	private int studyId;

	public FavoriteStudy() {
		super();
	}

	public FavoriteStudy(int id, int studyId, int userId) {
		this.id = id;
		this.studyId = studyId;
		this.userId=userId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + studyId;
		result = prime * result + userId;
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
		FavoriteStudy other = (FavoriteStudy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (studyId != other.studyId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}


}
