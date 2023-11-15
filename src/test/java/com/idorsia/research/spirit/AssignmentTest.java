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
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.model.Assignment;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssignmentTest extends AbstractSpiritTest {
	
	private static final int ASSIGNMENTID=356433;
	private static int newId=0;

	@Test
	public void atestGet() {
		Assignment s = getAssignmentService().get(ASSIGNMENTID);
		assertNotNull(s);
		assertTrue(s.getId()==ASSIGNMENTID);
	}
	
	@Test
	public void atestGetAssignmentById() {
		Assignment s = getAssignmentService().getAssignmentById(ASSIGNMENTID);
		assertNotNull(s);
		assertTrue(s.getId()==ASSIGNMENTID);
	}
	
	@Test
	public void btestDozerConversion() {
		AssignmentDto aDto = getDozerMapper().map(getAssignmentService().get(ASSIGNMENTID), AssignmentDto.class,"assignmentCustomMapping");
		assertNotNull(aDto.getStage());
		assertNotNull(aDto.getBiosample());
		assertNotNull(aDto.getSubgroup());
		Assignment a = getDozerMapper().map(aDto, Assignment.class,"assignmentCustomMapping");
		assertTrue(a.getBiosampleId().equals(aDto.getBiosample().getId()));
		assertTrue(a.getStageId().equals(aDto.getStage().getId()));
		assertTrue(a.getSubgroupId().equals(aDto.getSubgroup().getId()));
	}
	
	@Test
	public void btestAddAssignment() {
		Assignment a = new Assignment();
		a.setStageId(233);
		a.setSubgroupId(390);
		a.setBiosampleId(5115);
		a.setNo(1);
		a.setId(getAssignmentService().getSequence(Constants.ASSIGNMENT_SEQUENCE_NAME));
		a.setCreDate(new Date());
		a.setCreUser("TEST");
		a.setUpdDate(new Date());
		a.setUpdUser("TEST");
		getAssignmentService().addAssignment(a);
		newId=a.getId();
		assertTrue(newId!=0);
	}
	
	@Test
	public void ctestGetAssignmentByBiosample() {
		List<Assignment> s = getAssignmentService().getAssignmentsByBiosample(5115);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssignmentByStage() {
		List<Assignment> s = getAssignmentService().getAssignmentsByStage(233);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssignmentBySubgroup() {
		List<Assignment> s = getAssignmentService().getAssignmentsBySubgroup(390);
		assertTrue(s.size()!=0);
	}
	
	@Test
	public void ctestGetAssignmentByBiosampleAndStage() {
		Assignment s = getAssignmentService().getAssignmentByBiosampleAndStage(5115,233);
		assertTrue(s.getId()==newId);
	}
	
	@Test
	public void ctestGetAssignmentByBiosampleAndSubgroup() {
		Assignment s = getAssignmentService().getAssignmentByBiosampleAndSubgroup(5115,390);
		assertTrue(s.getId()==newId);
	}

	@Test
	public void dtestSaveOrUpdate() {
		Assignment a = getAssignmentService().getAssignmentById(newId);
		assertNotNull(a);
		a.setName("Lorem ipsum dolor sit amet");
		getAssignmentService().saveOrUpdate(a);
		assertTrue("Lorem ipsum dolor sit amet".equals(getAssignmentService().getAssignmentById(newId).getName()));
	}
	
	@Test
	public void etestDelete() {
		Assignment s = getAssignmentService().getAssignmentById(newId);
		getAssignmentService().delete(getAssignmentService().map(s));
		assertNull(getAssignmentService().getAssignmentById(newId));
	}
}
