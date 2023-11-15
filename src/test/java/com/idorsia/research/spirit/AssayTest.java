package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Assay;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssayTest extends AbstractSpiritTest {

	private static final int ASSAYID = 13330;
	private static final String ASSAYNAME = "Observation";
	private static final String NEWTESTNAME = "AssayTest";

	@Test
	public void atestGet() {
		Assay s = getAssayService().get(ASSAYID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYID);
	}
	
	@Test
	public void btestGetAssayById() {
		Assay s = getAssayService().getAssayById(ASSAYID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYID);
	}
	
	@Test
	public void ctestGetAssayByName() {
		Assay s = getAssayService().getAssayByName(ASSAYNAME);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYID);
	}
	

	@Test
	public void dtestList() {
		List<Assay> assays = getAssayService().list();
		assertNotNull(assays);
		assertTrue(!CollectionUtils.isEmpty(assays));
	}

	@Test
	public void etestGetCount() {
		int nbAssays = getAssayService().getCount();
		assertTrue(nbAssays> 0);
	}
	
	@Test
	public void ftestAddAssay() {
		Assay a = new Assay();
		a.setCategory("PHYSIOLOGY");
		a.setName(NEWTESTNAME);
		a.setDescription("TEST");
		a.setId(getAssayService().getSequence(Constants.ASSAY_SEQUENCE_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		assertNotNull(getAssayService().addAssay(a));
	}

	@Test
	public void gtestSaveOrUpdate() {
		Assay a = getAssayService().getAssayByName(NEWTESTNAME);
		a.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getAssayService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getAssayService().getAssayByName(NEWTESTNAME).getDescription()));
	}
	
	@Test
	public void htestDelete() {
		int beforeNbAssay = getAssayService().getCount();
		Assay s = getAssayService().getAssayByName(NEWTESTNAME);
		getAssayService().delete(getAssayService().map(s));
		int afterNbAssay = getAssayService().getCount();
		assertTrue(beforeNbAssay - afterNbAssay == 1);
	}
}
