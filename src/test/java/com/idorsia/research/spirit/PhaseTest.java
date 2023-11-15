package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.model.Phase;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PhaseTest extends AbstractSpiritTest {
	
	private static final int PHASEID = 193400;

	@Test
	public void aTestGet() {
		Phase phase = getPhaseService().get(PHASEID);
		assertNotNull(phase);
		assertTrue(phase.getId() == PHASEID);
	}
	
	@Test
	public void bTestDozerConversion() {
		PhaseDto pDto = getDozerMapper().map(getPhaseService().get(PHASEID), PhaseDto.class,"phaseCustomMapping");
		assertNotNull(pDto.getStage());
		Phase p = getDozerMapper().map(pDto, Phase.class,"phaseCustomMapping");
		assertTrue(p.getStageId().equals(pDto.getStage().getId()));
	}

	@Test
	public void cTestList() {
		List<Phase> phases = getPhaseService().list();
		assertNotNull(phases);
		assertTrue(!CollectionUtils.isEmpty(phases));
	}

	@Test
	public void dTestGetCount() {
		int nbPhases = getPhaseService().getCount();
		assertTrue(nbPhases > 0);
	}
	
	@Test
	public void eTestAddPhase() {
		Phase p = new Phase();
		p.setPhase(Long.valueOf(0));
		p.setStageId(1995);
		p.setId(getPhaseService().getSequence(Constants.PHASE_SEQUENCE_NAME));
		assertNotNull(getPhaseService().addPhase(p));
	}

	@Test
	public void fTestSaveOrUpdate() {
		String label = "TestLabel";
		List<Phase> phases = getPhaseService().list();
		Collections.sort(phases, new Comparator<Phase>() {
			public int compare(Phase p1, Phase p2) {
				return Integer.compare(p2.getId(), p1.getId());
			}
		});
		Phase toUpdate = phases.get(0);
		toUpdate.setLabel(label);
		getPhaseService().saveOrUpdate(toUpdate);
		Phase p = getPhaseService().get(toUpdate.getId());
		assertTrue(p.getLabel().equals(label));
	}
	
	@Test
	public void gTestDelete() {
		List<Phase> phases = getPhaseService().list();
		Collections.sort(phases, new Comparator<Phase>() {
			public int compare(Phase p1, Phase p2) {
				return Integer.compare(p2.getId(), p1.getId());
			}
		});
		Phase toDelete = phases.get(0);
		try {
			getPhaseService().delete(getPhaseService().map(toDelete));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nbPhases = getPhaseService().getCount();
		assertTrue(phases.size() - nbPhases == 1);
	}

}
