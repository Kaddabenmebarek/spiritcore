package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.model.AssayAttribute;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssayAttributeTest extends AbstractSpiritTest {

	private static final int ASSAYATTRIBUTEID = 13080;
	private static final int ASSAYID = 13027;
	private static final String ASSAYATTRIBUTENAME = "TESSST";
	private static Integer newId = 0;
	
	@Test
	public void atestGet() {
		AssayAttribute s = getAssayAttributeService().get(ASSAYATTRIBUTEID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYATTRIBUTEID);
	}
	
	@Test
	public void atestGetAssayById() {
		AssayAttribute s = getAssayAttributeService().getAssayAttributeById(ASSAYATTRIBUTEID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYATTRIBUTEID);
	}
	
	@Test
	public void btestDozerConversion() {
		AssayAttributeDto aDto = getDozerMapper().map(getAssayAttributeService().getAssayAttributeById(ASSAYATTRIBUTEID), AssayAttributeDto.class,"assayAttributeCustomMapping");
		assertNotNull(aDto.getAssay());
		AssayAttribute a = getDozerMapper().map(aDto, AssayAttribute.class,"assayAttributeCustomMapping");
		assertTrue(a.getAssayId().equals(aDto.getAssay().getId()));
	}
	
	@Test
	public void btestAddAssayAttribute() {
		AssayAttribute a = new AssayAttribute();
		a.setDatatype("NUMERICAL");
		a.setAssayId(ASSAYID);
		a.setName(ASSAYATTRIBUTENAME);
		a.setIdx(2);
		a.setId(getAssayAttributeService().getSequence(Constants.ASSAY_ATTRIBUTE_SEQUENCE_NAME));
		a.setOutputType("INPUT");
		a.setRequired(1);
		a.setParameters("TEST,OK,NULL");
		getAssayAttributeService().addAssayAttribute(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetAssayByName() {
		List<AssayAttribute> s = getAssayAttributeService().getAssayAttributesByName(ASSAYATTRIBUTENAME);
		assertNotNull(s);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetAssayByAssay() {
		List<AssayAttribute> s = getAssayAttributeService().getAssayAttributesByAssay(ASSAYID);
		assertNotNull(s);
		assertTrue(s.size()>0);
	}
	
	@Test
	public void ctestGetAssayByAssayAndName() {
		AssayAttribute s = getAssayAttributeService().getAssayAttributesByAssayAndName(ASSAYID, ASSAYATTRIBUTENAME);
		assertNotNull(s);
	}
	

	@Test
	public void dtestList() {
		List<AssayAttribute> assayAttributes = getAssayAttributeService().list();
		assertNotNull(assayAttributes);
		assertTrue(!CollectionUtils.isEmpty(assayAttributes));
	}

	@Test
	public void etestGetCount() {
		int nbAttributes = getAssayAttributeService().getCount();
		assertTrue(nbAttributes > 0);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		AssayAttribute a = getAssayAttributeService().getAssayAttributeById(newId);
		a.setParameters("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getAssayAttributeService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getAssayAttributeService().getAssayAttributeById(newId).getParameters()));
	}
	
	@Test
	public void htestDelete() {
		AssayAttribute s = getAssayAttributeService().getAssayAttributeById(newId);
		getAssayAttributeService().delete(getAssayAttributeService().map(s));
		assertNull(getAssayAttributeService().getAssayAttributeById(newId));
	}

}
