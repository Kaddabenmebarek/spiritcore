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
import com.idorsia.research.spirit.core.dto.ResultAssignmentDto;
import com.idorsia.research.spirit.core.model.ResultAssignment;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResultAssignmentTest extends AbstractSpiritTest {

	private static final int RESULTASSIGNMENTID = 49;
	private static int newId=0;

	@Test
	public void atestGet() {
		ResultAssignment s = getResultAssignmentService().get(RESULTASSIGNMENTID);
		assertNotNull(s);
		assertTrue(s.getId() == RESULTASSIGNMENTID);
	}
	
	@Test
	public void atestDozerConversion() {
		ResultAssignmentDto aDto = getDozerMapper().map(getResultAssignmentService().get(RESULTASSIGNMENTID), ResultAssignmentDto.class, "resultAssignmentCustomMapping");
		assertNotNull(aDto.getAssignment());
		assertNotNull(aDto.getAssayResult());
		ResultAssignment a = getDozerMapper().map(aDto, ResultAssignment.class,"resultAssignmentCustomMapping");
		assertTrue(a.getAssayResultId().equals(aDto.getAssayResult().getId()));
		assertTrue(a.getAssignmentId().equals(aDto.getAssignment().getId()));
	}
	
	@Test
	public void btestGetAssayResultById() {
		ResultAssignment s = getResultAssignmentService().getResultAssignmentById(RESULTASSIGNMENTID);
		assertNotNull(s);
		assertTrue(s.getId() == RESULTASSIGNMENTID);
	}
	
	@Test
	public void btestAddAssayResult() {
		ResultAssignment a = new ResultAssignment();
		a.setAssayResultId(4017232);
		a.setAssignmentId(141872);
		a.setId(getResultAssignmentService().getSequence(Constants.RESULT_ASSIGNMENT_SEQ_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		getResultAssignmentService().addAssayResult(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetAssayResultByAssignment() {
		List<ResultAssignment> s = getResultAssignmentService().getResultAssignmentByAssignment(141872);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssayResultByAssignmentAndAssay() {
		ResultAssignment s = getResultAssignmentService().getResultAssignmentByAssignmentAndAssay(141872, 13030);
		assertNotNull(s);
	}
	
	@Test
	public void gtestSaveOrUpdate() {
		ResultAssignment a = getResultAssignmentService().getResultAssignmentById(newId);
		assertNotNull(a);
		a.setUpdUser("TEST2");
		getResultAssignmentService().saveOrUpdate(a);
		assertTrue("TEST2".equals(getResultAssignmentService().getResultAssignmentById(newId).getUpdUser()));
	}
	
	@Test
	public void htestDelete() {
		ResultAssignment s = getResultAssignmentService().getResultAssignmentById(newId);
		getResultAssignmentService().delete(getResultAssignmentService().map(s));
		assertNull(getResultAssignmentService().getResultAssignmentById(newId));
	}

}
