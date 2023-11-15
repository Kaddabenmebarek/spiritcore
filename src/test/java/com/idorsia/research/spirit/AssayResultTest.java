package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.model.AssayResult;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssayResultTest extends AbstractSpiritTest {

	private static final int ASSAYRESULTID = 4017232;
	private static int newId=0;

	@Test
	public void atestGet() {
		AssayResult s = getAssayResultService().get(ASSAYRESULTID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYRESULTID);
	}
	
	@Test
	public void testDozerConversion() {
		AssayResultDto aDto = getDozerMapper().map(getAssayResultService().get(ASSAYRESULTID), AssayResultDto.class,"assayResultCustomMapping");
		assertNotNull(aDto.getStudy());
		assertNotNull(aDto.getAssay());
		AssayResult a = getDozerMapper().map(aDto, AssayResult.class,"assayResultCustomMapping");
		assertTrue(a.getStudyId().equals(aDto.getStudy().getId()));
		assertTrue(a.getAssayId().equals(aDto.getAssay().getId()));
	}
	
	@Test
	public void btestGetAssayResultById() {
		AssayResult s = getAssayResultService().getAssayResultById(ASSAYRESULTID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYRESULTID);
	}
	
	@Test
	public void btestAddAssayResult() {
		AssayResult a = new AssayResult();
		a.setAssayId(13030);
		a.setPhaseId(53);
		a.setBiosampleId(5115);
		a.setStudyId(50);
		a.setComments("TESSSSST");
		a.setQuality(1);
		a.setId(getAssayResultService().getSequence(Constants.ASSAY_RESULT_SEQUENCE_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		getAssayResultService().addAssayResult(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetAssayResultByBiosample() {
		List<AssayResult> s = getAssayResultService().getAssayResultsByBiosample(5115);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultByAssay() {
		List<AssayResult> s = getAssayResultService().getAssayResultsByAssay(13030);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultByPhase() {
		List<AssayResult> s = getAssayResultService().getAssayResultsByPhase(53);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultByStudy() {
		List<AssayResult> s = getAssayResultService().getAssayResultsByStudy(50);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultByComments() {
		List<AssayResult> s = getAssayResultService().getAssayResultsByComments("SSSSS");
		assertTrue(s.size()!=0);
	}
	

	@Test
	public void gtestSaveOrUpdate() {
		AssayResult a = getAssayResultService().getAssayResultById(newId);
		assertNotNull(a);
		a.setComments("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getAssayResultService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getAssayResultService().getAssayResultById(newId).getComments()));
	}
	
	@Test
	public void htestDelete() {
		AssayResult s = getAssayResultService().getAssayResultById(newId);
		getAssayResultService().delete(getAssayResultService().map(s));
		assertNull(getAssayResultService().getAssayResultById(newId));
	}

}
