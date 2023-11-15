package com.idorsia.research.spirit.core.dao;

import java.util.Date;
import java.util.List;

import com.actelion.research.security.entity.User;
import com.idorsia.research.spirit.core.dto.view.StudyQuery;
import com.idorsia.research.spirit.core.model.Study;

public interface StudyDao {

	public Study get(Integer id);
	
	public Study getStudyById(int id);
	
	public Study getStudyByStudyId(String studyId);
	
	public void changeOwner(Study study, String updUser, String creUser);

	public List<Study> getRecentStudies(User user);

	public List<Study> list();

	public int getCount();
	
	public Integer saveOrUpdate(Study study);

	public int addStudy(Study study);
	
	public void delete(int studyId);

	public List<String> getStudyIds();

	public List<Study> queryStudies(StudyQuery q, User user) throws Exception;

	public List<Study> getStudyAfterDate(Date date);

	public List<Study> getStudyTargetedByBiosampleAfterDate(Date date);

	public List<Study> getStudyTargetedByResultAfterDate(Date date);

}
