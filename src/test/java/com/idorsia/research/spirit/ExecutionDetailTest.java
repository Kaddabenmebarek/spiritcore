package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.ExecutionDetailDto;
import com.idorsia.research.spirit.core.model.ExecutionDetail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecutionDetailTest extends AbstractSpiritTest {

	private static final int EXCDETAILID = 9730;
	private static final int PHASLNKID = 299380;
	private static final int ASSIGNMNTID = 141163;

	@Test
	public void aTestGet() {
		assertNotNull(getExecutionDetailService().get(EXCDETAILID));
	}

	@Test
	public void bTestDozerConversion() {
		ExecutionDetailDto eDto = getDozerMapper().map(getExecutionDetailService().get(EXCDETAILID), ExecutionDetailDto.class,"executionDetailCustomMapping");
		assertNotNull(eDto.getAssignment());
		ExecutionDetail e = getDozerMapper().map(eDto, ExecutionDetail.class,"executionDetailCustomMapping");
		assertTrue(e.getAssignmentId().equals(eDto.getAssignment().getId()));
	}
	
	@Test
	public void cTestGetByPhaseAndAssignment() {
		assertNotNull(getExecutionDetailService().getByPhaseAndAssignment(279320, 350087));
	}

	@Test
	public void dTestList() {
		assertTrue(!getExecutionDetailService().list().isEmpty());
	}

	@Test
	public void eTestGetCount() {
		assertTrue(getExecutionDetailService().getCount() > 0);
	}

	@Test
	public void fTestAddExecutionDetail() {
		ExecutionDetail ed = new ExecutionDetail();
		ed.setId(getExecutionDetailService().getSequence(Constants.STUDY_EXECUTION_DETAILS_SEQ_NAME));
		ed.setAssignmentId(347550);
		ed.setPhaselinkId(299380);
		assertNotNull(getExecutionDetailService().addExecutionDetail(ed));
	}

	@Test
	public void gTestSaveOrUpdate() {
		ExecutionDetail ed = getLastInserted();
		ed.setCreUser("benmeka1");
		getExecutionDetailService().saveOrUpdate(ed);
		assertTrue(getExecutionDetailService().getByPhaseAndAssignment(PHASLNKID, ASSIGNMNTID).getCreUser().equals("benmeka1"));

	}

	@Test
	public void hTestDelete() {
		int before = getExecutionDetailService().getCount();
		getExecutionDetailService().delete(getExecutionDetailService().map(getLastInserted()));
		assertTrue(before - getExecutionDetailService().getCount() == 1);
	}
	
	private ExecutionDetail getLastInserted() {
		List<ExecutionDetail> list = getExecutionDetailService().list();
		Collections.sort(list, new Comparator<ExecutionDetail>() {
			public int compare(ExecutionDetail s1, ExecutionDetail s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		ExecutionDetail a = list.get(0);
		return a;
	}

}
