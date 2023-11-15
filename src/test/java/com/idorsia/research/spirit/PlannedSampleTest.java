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
import com.idorsia.research.spirit.core.dto.PlannedSampleDto;
import com.idorsia.research.spirit.core.model.PlannedSample;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlannedSampleTest extends AbstractSpiritTest {

	private static final int PLANNEDSAMPLEID = 1573261;
	private static int newId=0;

	@Test
	public void atestGet() {
		PlannedSample s = getPlannedSampleService().get(PLANNEDSAMPLEID);
		assertNotNull(s);
		assertTrue(s.getId() == PLANNEDSAMPLEID);
	}
	
	@Test
	public void atestDozerConversion() {
		PlannedSampleDto aDto = getDozerMapper().map(getPlannedSampleService().get(PLANNEDSAMPLEID), PlannedSampleDto.class,"plannedSampleCustomMapping");
		assertNotNull(aDto.getBiosample());
		assertNotNull(aDto.getStage());
		PlannedSample a = getDozerMapper().map(aDto, PlannedSample.class,"plannedSampleCustomMapping");
		assertTrue(a.getBiosampleId().equals(aDto.getBiosample().getId()));
		assertTrue(a.getStageId().equals(aDto.getStage().getId()));
	}
	
	@Test
	public void btestGetPlannedSampleById() {
		PlannedSample s = getPlannedSampleService().getPlannedSampleById(PLANNEDSAMPLEID);
		assertNotNull(s);
		assertTrue(s.getId() == PLANNEDSAMPLEID);
	}
	
	@Test
	public void btestAddPlannedSample() {
		PlannedSample a = new PlannedSample();
		a.setBiosampleId(2050);
		a.setStageId(233);
		a.setName("TESSSSST");
		a.setId(getPlannedSampleService().getSequence(Constants.PLANNED_SAMPLE_SEQUENCE_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		getPlannedSampleService().addPlannedSample(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetPlannedSampleByBiosample() {
		List<PlannedSample> s = getPlannedSampleService().getPlannedSamplesByBiosample(2050);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetPlannedSampleByStage() {
		List<PlannedSample> s = getPlannedSampleService().getPlannedSamplesByStage(233);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetPlannedSampleByComments() {
		PlannedSample s = getPlannedSampleService().getPlannedSampleByStageAndBiosample(233, 2050);
		assertTrue(s.getId().equals(newId));
	}
	

	@Test
	public void gtestSaveOrUpdate() {
		PlannedSample a = getPlannedSampleService().getPlannedSampleById(newId);
		assertNotNull(a);
		a.setName("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getPlannedSampleService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getPlannedSampleService().getPlannedSampleById(newId).getName()));
	}
	
	@Test
	public void htestDelete() {
		PlannedSample s = getPlannedSampleService().getPlannedSampleById(newId);
		getPlannedSampleService().delete(getPlannedSampleService().map(s));
		assertNull(getPlannedSampleService().getPlannedSampleById(newId));
	}
}
