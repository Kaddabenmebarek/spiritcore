package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataDao;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadata;
import com.idorsia.research.spirit.core.util.Cache;
import com.idorsia.research.spirit.core.util.MiscUtils;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class BiotypeMetadataService extends AbstractService implements Serializable {

	private static final long serialVersionUID = 8179203321443275679L;

	@SuppressWarnings("unchecked")
	private static Map<Integer, BiotypeMetadataDto> idToBiotypeMetadata = (Map<Integer, BiotypeMetadataDto>) getCacheMap(
			BiotypeMetadataDto.class);

	@Autowired
	private BiotypeMetadataDao biotypeMetadataDao;
	@Autowired
	private BiotypeService biotypeService;

	public BiotypeMetadata get(Integer id) {
		return biotypeMetadataDao.get(id);
	}

	public List<BiotypeMetadata> getByBiotype(int biotypeId) {
		return biotypeMetadataDao.getByBiotype(biotypeId);
	}

	public List<BiotypeMetadata> list() {
		return biotypeMetadataDao.list();
	}

	public int getCount() {
		return biotypeMetadataDao.getCount();
	}

	public Integer saveOrUpdate(BiotypeMetadata biotypeMetadata) {
		return biotypeMetadataDao.saveOrUpdate(biotypeMetadata);
	}

	public int addBiotypeMetadata(BiotypeMetadata biotypeMetadata) {
		return biotypeMetadataDao.addBiotypeMetadata(biotypeMetadata);
	}

	public BiotypeMetadataDao getBiotypeMetadataDao() {
		return biotypeMetadataDao;
	}

	public void setBiotypeMetadataDao(BiotypeMetadataDao biotypeMetadataDao) {
		this.biotypeMetadataDao = biotypeMetadataDao;
	}

	public List<BiotypeMetadataDto> map(List<BiotypeMetadata> biotypeMetadatas) {
		List<BiotypeMetadataDto> res = new ArrayList<BiotypeMetadataDto>();
		for (BiotypeMetadata biotypeMetadata : biotypeMetadatas) {
			res.add(map(biotypeMetadata));
		}
		return res;
	}

	public synchronized BiotypeMetadataDto map(BiotypeMetadata biotypeMetadata) {
		BiotypeMetadataDto biotypeMetadataDto = idToBiotypeMetadata.get(biotypeMetadata.getId());
		if(biotypeMetadata.getId().equals(2765) && biotypeMetadataDto==null)
			System.out.println();
		if (biotypeMetadataDto == null) {
			biotypeMetadataDto = dozerMapper.map(biotypeMetadata, BiotypeMetadataDto.class,
					"biotypeMetadataCustomMapping");
			if (idToBiotypeMetadata.get(biotypeMetadata.getId()) == null)
				idToBiotypeMetadata.put(biotypeMetadata.getId(), biotypeMetadataDto);
		}
		return biotypeMetadataDto;
	}

	public String extractUnit(BiotypeMetadataDto bm) {
		int index = bm.getName().indexOf("[");
		int index2 = bm.getName().indexOf("]");

		if (index > 0 && index < index2) {
			return bm.getName().substring(index + 1, index2);
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getAutoCompletionFields(BiotypeMetadataDto metadataType, StudyDto study) {
		if (metadataType == null || metadataType.getId() <= 0)
			return new TreeSet<String>();
		// Use Cache
		String key = "biotype_autocompletion_" + metadataType.getId() + "_" + (study == null ? "" : study.getId());
		Set<String> res = (Set<String>) Cache.getInstance().get(key);
		if (res == null) {
			res = new TreeSet<String>(CompareUtils.STRING_COMPARATOR);
			res.addAll(biotypeService.getAutoCompletionFields(metadataType, study));
			Cache.getInstance().add(key, res, 60);
		}
		return res;
	}

	public List<String> extractChoices(BiotypeMetadataDto metadata) {
		return MiscUtils.splitChoices(metadata.getParameters());
	}

	public int countRelations(BiotypeMetadataDto biotypeMetadata) {
		if (biotypeMetadata == null || biotypeMetadata.getId() <= 0)
			return 0;
		int id = biotypeMetadata.getId();
		return biotypeMetadataDao.countRelations(id);
	}

	@Transactional
	public void save(BiotypeMetadataDto metadata) throws Exception {
		save(metadata, false);
	}

	@Transactional
	public void save(Collection<BiotypeMetadataDto> metadatas) {
			for (BiotypeMetadataDto metadata : metadatas) {
				try {					
					save(metadata, true);
				} catch(Exception e) {
					e.printStackTrace();
					AbstractService.clearTransient(false);
				}
			}
			AbstractService.clearSavedItem();
			AbstractService.clearTransient(true);
	}

	protected void save(BiotypeMetadataDto metadata, Boolean cross) throws Exception {
		try {
			if (metadata != null && !savedItems.contains(metadata)) {
				savedItems.add(metadata);
				if (metadata.getBiotype() != null && metadata.getBiotype().getId() == Constants.NEWTRANSIENTID)
					biotypeService.save(metadata.getBiotype(), true);
				metadata.setUpdDate(new Date());
				metadata.setUpdUser(UserUtil.getUsername());
				if(metadata.getId().equals(Constants.NEWTRANSIENTID)) {
					metadata.setCreDate(new Date());
					metadata.setCreUser(UserUtil.getUsername());
				}
				metadata.setId(
						saveOrUpdate(dozerMapper.map(metadata, BiotypeMetadata.class, "biotypeMetadataCustomMapping")));
				idToBiotypeMetadata.put(metadata.getId(), metadata);
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if (!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@Transactional
	public void delete(BiotypeMetadataDto metadata) {
		delete(metadata, false);
	}
	
	public void delete(BiotypeMetadataDto metadata, Boolean cross) {
		biotypeMetadataDao.delete(metadata.getId());
	}

}
