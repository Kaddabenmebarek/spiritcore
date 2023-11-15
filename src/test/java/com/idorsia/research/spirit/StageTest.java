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
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.model.Stage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StageTest extends AbstractSpiritTest {
	
	private static final int STAGEID = 240;

	@Test
	public void aTestGet() {
		Stage stage = getStageService().get(STAGEID);
		assertNotNull(stage);
		assertTrue(stage.getId() == STAGEID);
	}
	
	@Test
	public void bTestDozerConversion() {
		StageDto sDto = getDozerMapper().map(getStageService().get(STAGEID), StageDto.class,"stageCustomMapping");
		assertNotNull(sDto.getStudy());
		Stage s = getDozerMapper().map(sDto, Stage.class,"stageCustomMapping");
		assertTrue(s.getStudyId().equals(sDto.getStudy().getId()));
	}
	
	@Test
	public void cTestGetStageById() {
		Stage stage = getStageService().getStageById(STAGEID);
		assertNotNull(stage);
		assertTrue(stage.getId() == STAGEID);
	}

	@Test
	public void dTestList() {
		List<Stage> stages = getStageService().list();
		assertNotNull(stages);
		assertTrue(!CollectionUtils.isEmpty(stages));
	}

	@Test
	public void eTestGetCount() {
		int nbStatges = getStageService().getCount();
		assertTrue(nbStatges > 0);
	}
	
	@Test
	public void fTestAddStage() {
		Stage s = new Stage();
		s.setStudyId(21350);
		s.setBiotypeId(50);
		s.setName("Test Stage");
		s.setDynamic(1);
		s.setId(getStageService().getSequence(Constants.STUDY_STAGE_SEQUENCE_NAME));
		assertNotNull(getStageService().addStage(s));
	}

	@Test
	public void gTestSaveOrUpdate() {
		List<Stage> stages = getStageService().getStagesByStudyId(21350);
		Stage s = null;
		for(Stage stage : stages) {
			if(stage.getName().equals("Test Stage")) s = stage;
		}
		s.setDynamic(0);
		try {
			getStageService().saveOrUpdate(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(s.getDynamic()==0);
	}
	
	@Test
	public void hTestDelete() {
		List<Stage> stages = getStageService().list();
		Collections.sort(stages, new Comparator<Stage>() {
			public int compare(Stage s1, Stage s2) {
				return Integer.compare(s2.getId(), s1.getId());
			}
		});
		Stage toDelete = stages.get(0);
		try {
			getStageService().delete(getStageService().map(toDelete));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nbStages = getStageService().getCount();
		assertTrue(stages.size() - nbStages == 1);
	}

}
