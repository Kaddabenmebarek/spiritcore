package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Study;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudyTest extends AbstractSpiritTest {

	private static final int STUDYID = 50;
	private final static String NEW_STUDYID = "S-02500";

	@Test
	public void aTestGet() {
		Study s = getStudyService().get(STUDYID);
		assertNotNull(s);
		assertTrue(s.getId() == STUDYID);
	}
	
	@Test
	public void bTestDozerConversion() {
		StudyDto sDto = getDozerMapper().map(getStudyService().get(STUDYID), StudyDto.class,"studyCustomMapping");
		assertNotNull(sDto.getStudyId());
	}
	
	@Test
	public void bTestGetStudyById() {
		Study s = getStudyService().getStudyById(STUDYID);
		assertNotNull(s);
		assertTrue(s.getId() == STUDYID);
	}
	
	@Test
	public void cTestNextStudyId() {
		String nextStudyId = getStudyService().getNextStudyId();
		assertNotNull(nextStudyId);
	}

	@Test
	public void dTestList() {
		List<Study> studies = getStudyService().list();
		assertNotNull(studies);
		assertTrue(!CollectionUtils.isEmpty(studies));
	}

	@Test
	public void eTestGetCount() {
		int nbStudies = getStudyService().getCount();
		assertTrue(nbStudies > 0);
	}
	
	@Test
	public void fTestAddStudy() {
		Study s = new Study();
		s.setStatus("ONGOING");
		s.setIvv("UnitTest2");
		s.setSynchroSamples(1);
		s.setStudyId(NEW_STUDYID);
		s.setId(getStudyService().getSequence(Constants.STUDY_SEQUENCE_NAME));
		assertNotNull(getStudyService().addStudy(s));
	}

	@Test
	public void gTestSaveOrUpdate() {
		Study s = getStudyService().getStudyByStudyId(NEW_STUDYID);
		s.setComments("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getStudyService().saveOrUpdate(s);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getStudyService().getStudyByStudyId(NEW_STUDYID).getComments()));
	}
	
	@Test
	public void hTestDelete() {
		int beforeNbStudies = getStudyService().getCount();
		Study s = getStudyService().getStudyByStudyId(NEW_STUDYID);
		try {
			getStudyService().delete(getStudyService().map(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int afterNbStudies = getStudyService().getCount();
		assertTrue(beforeNbStudies - afterNbStudies == 1);
	}

}
