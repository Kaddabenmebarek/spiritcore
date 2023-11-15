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
import com.idorsia.research.spirit.core.dto.FavoriteStudyDto;
import com.idorsia.research.spirit.core.model.FavoriteStudy;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FavoriteStudyTest extends AbstractSpiritTest {

	
	private static final Integer FAVORITESTUDYID = 18;
	
	@Test
	public void aTestGet() {
		assertNotNull(getFavoriteStudyService().get(FAVORITESTUDYID));
	}

	@Test
	public void bTestList() {
		assertTrue(!CollectionUtils.isEmpty(getFavoriteStudyService().list()));
	}

	@Test
	public void cTestGetCount() {
		assertTrue(getFavoriteStudyService().getCount() > 0);
	}
	
	@Test
	public void dTestAddFavoriteStudy() {
		FavoriteStudy fs = new FavoriteStudy(getFavoriteStudyService().getSequence(Constants.FAVSTUDY_SEQUENCE_NAME), 21570, 11412);
		assertNotNull(getFavoriteStudyService().addFavoriteStudy(fs));
	}

	@Test
	public void eTestSaveOrUpdate() {
		FavoriteStudy fs = getLastInserted();
		fs.setUserId(11328);
		getFavoriteStudyService().saveOrUpdate(fs);
		assertTrue(getFavoriteStudyService().get(fs.getId()).getUserId() == 11328);
	}

	@Test
	public void fTestDelete() {
		int before = getFavoriteStudyService().getCount();
		getFavoriteStudyService().delete(getLastInserted().getId());
		assertTrue(before - getFavoriteStudyService().getCount() == 1);
	}
	
	@Test
	public void gtestDozerConversion() {
		FavoriteStudyDto fsDto = getDozerMapper().map(getFavoriteStudyService().get(FAVORITESTUDYID), FavoriteStudyDto.class,"favoriteStudyCustomMapping");
		assertNotNull(fsDto.getStudy());
		FavoriteStudy fs = getDozerMapper().map(fsDto, FavoriteStudy.class,"favoriteStudyCustomMapping");
		assertTrue(fs.getStudyId() == fsDto.getStudy().getId());
	}
	
	private FavoriteStudy getLastInserted() {
		List<FavoriteStudy> list = getFavoriteStudyService().list();
		Collections.sort(list, new Comparator<FavoriteStudy>() {
			public int compare(FavoriteStudy fs1, FavoriteStudy fs2) {
				return Integer.compare(fs2.getId(), fs1.getId());
			}
		});
		FavoriteStudy f = list.get(0);
		return f;
	}

}
