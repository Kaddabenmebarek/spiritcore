package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataBiosampleDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadataBiosample;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BiotypeMetadataBiosampleTest extends AbstractSpiritTest {

	private static final int BIOTYPEMETADATABIOSAMPLEID = 210837;
	private static int newId=0;

	@Test
	public void atestGet() {
		BiotypeMetadataBiosample s = getBiotypeMetadataBiosampleService().get(BIOTYPEMETADATABIOSAMPLEID);
		assertNotNull(s);
		assertTrue(s.getId() == BIOTYPEMETADATABIOSAMPLEID);
	}
	
	@Test
	public void atestDozerConversion() {
		BiotypeMetadataBiosampleDto aDto = getDozerMapper().map(getBiotypeMetadataBiosampleService().get(BIOTYPEMETADATABIOSAMPLEID), BiotypeMetadataBiosampleDto.class,"biotypeMetadataBiosampleCustomMapping");
		assertNotNull(aDto.getBiosample());
		assertNotNull(aDto.getMetadata());
		BiotypeMetadataBiosample a = getDozerMapper().map(aDto, BiotypeMetadataBiosample.class,"biotypeMetadataBiosampleCustomMapping");
		assertTrue(a.getBiosampleId().equals(aDto.getBiosample().getId()));
		assertTrue(a.getMetadataId().equals(aDto.getMetadata().getId()));
	}
	
	@Test
	public void btestAddBiotypeMetadataBiosample() {
		BiotypeMetadataBiosample a = new BiotypeMetadataBiosample();
		a.setBiosampleId(2050);
		a.setMetadataId(2187);
		a.setValue("TESSSSST");
		a.setId(getBiotypeMetadataBiosampleService().getSequence(Constants.BIOTYPE_METADATA_BIOSAMPLE_SEQUENCE_NAME));
		getBiotypeMetadataBiosampleService().addBiotypeMetadataBiosample(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetBiotypeMetadataBiosampleByBiosample() {
		List<BiotypeMetadataBiosample> s = getBiotypeMetadataBiosampleService().getBiotypeMetadataBiosamplesByBiosample(2050);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetBiotypeMetadataBiosampleByStage() {
		BiotypeMetadataBiosample s = getBiotypeMetadataBiosampleService().getBiotypeMetadataBiosampleByBiosampleAndMetadata(2050, 2187);
		assertNotNull(s);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		BiotypeMetadataBiosample a = getBiotypeMetadataBiosampleService().get(newId);
		assertNotNull(a);
		a.setValue("Test");
		getBiotypeMetadataBiosampleService().saveOrUpdate(a);
		assertTrue("Test".equals(getBiotypeMetadataBiosampleService().get(newId).getValue()));
	}
	
	@Test
	public void htestDelete() {
		BiotypeMetadataBiosample s = getBiotypeMetadataBiosampleService().get(newId);
		getBiotypeMetadataBiosampleService().delete(getBiotypeMetadataBiosampleService().map(s));
		assertNull(getBiotypeMetadataBiosampleService().getBiotypeMetadataBiosampleById(newId));
	}
}
