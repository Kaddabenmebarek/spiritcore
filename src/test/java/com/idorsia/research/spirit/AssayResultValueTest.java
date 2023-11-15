package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.AssayResultValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssayResultValueTest extends AbstractSpiritTest {

	private static final int ASSAYRESULTVALUEID = 4080;
	private static int newId=0;

	@Test
	public void atestGet() {
		AssayResultValue s = getAssayResultValueService().get(ASSAYRESULTVALUEID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYRESULTVALUEID);
	}
	
	@Test
	public void atestGetAssayResultValueById() {
		AssayResultValue s = getAssayResultValueService().getAssayResultValueById(ASSAYRESULTVALUEID);
		assertNotNull(s);
		assertTrue(s.getId() == ASSAYRESULTVALUEID);
	}
	
	@Test
	public void btestAddAssayResultValue() {
		AssayResultValue a = new AssayResultValue();
		a.setAssayResultId(4016792);
		a.setDocumentId(545);
		a.setAssayAttributeId(13261);
		a.setTextValue("TESSSSST");
		a.setId(getAssayResultValueService().getSequence(Constants.ASSAY_RESULT_VALUE_SEQUENCE_NAME));
		getAssayResultValueService().addAssayResultValue(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetAssayResultValueByAssayResult() {
		List<AssayResultValue> s = getAssayResultValueService().getAssayResultValuesByAssayResult(4016792);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultValueByDocument() {
		List<AssayResultValue> s = getAssayResultValueService().getAssayResultValuesByDocument(545);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultValueByAssayResultAndAssayParameter() {
		AssayResultValue s = getAssayResultValueService().getAssayResultsByAssayResultAndAssayAttribute(4016792, 13261);
		assertTrue(s.getId()==newId);
	}

	@Test
	public void gtestSaveOrUpdate() {
		AssayResultValue a = getAssayResultValueService().getAssayResultValueById(newId);
		assertNotNull(a);
		a.setTextValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		getAssayResultValueService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.".equals(getAssayResultValueService().getAssayResultValueById(newId).getTextValue()));
	}
	
	@Test
	public void htestDelete() {
		AssayResultValue s = getAssayResultValueService().getAssayResultValueById(newId);
		getAssayResultValueService().delete(s.getId());
		assertNull(getAssayResultValueService().getAssayResultValueById(newId));
	}

}
