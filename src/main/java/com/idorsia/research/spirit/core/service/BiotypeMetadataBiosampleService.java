package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.BiotypeMetadataBiosampleDao;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataBiosampleDto;
import com.idorsia.research.spirit.core.model.BiotypeMetadataBiosample;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class BiotypeMetadataBiosampleService extends AbstractService implements Serializable{

	private static final long serialVersionUID = 4426544299481180987L;

	@Autowired
	private BiotypeMetadataBiosampleDao biotypeMetadataBiosampleDao;
	
	@SuppressWarnings("unchecked")
	private static Map<Integer, BiotypeMetadataBiosampleDto> idToBiotypeMetadataBiosample = (Map<Integer, BiotypeMetadataBiosampleDto>) getCacheMap(BiotypeMetadataBiosampleDto.class);

	public int addBiotypeMetadataBiosample(BiotypeMetadataBiosample biotypeMetadataBiosample) {
		return biotypeMetadataBiosampleDao.addBiotypeMetadataBiosample(biotypeMetadataBiosample);
	}

	public Integer saveOrUpdate(BiotypeMetadataBiosample biotypeMetadataBiosample) {
		return biotypeMetadataBiosampleDao.saveOrUpdate(biotypeMetadataBiosample);
	}

	public BiotypeMetadataBiosample get(Integer id) {
		return biotypeMetadataBiosampleDao.get(id);
	}

	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleById(int id) {
		return biotypeMetadataBiosampleDao.getBiotypeMetadataBiosampleById(id);
	}

	public BiotypeMetadataBiosample getBiotypeMetadataBiosampleByBiosampleAndMetadata(Integer biosampleId, Integer biotypeMetadataId) {
		return biotypeMetadataBiosampleDao.getBiotypeMetadataBiosampleByBiosampleAndMetadata(biosampleId, biotypeMetadataId);
	}

	public List<BiotypeMetadataBiosample> getBiotypeMetadataBiosamplesByBiosample(Integer biosampleId){
		if(biosampleId.equals(Constants.NEWTRANSIENTID))
			return new ArrayList<>();
		return biotypeMetadataBiosampleDao.getBiotypeMetadataBiosamplesByBiosample(biosampleId);
	}

	public List<BiotypeMetadataBiosampleDto> map(List<BiotypeMetadataBiosample> biotypeMetadataBiosamples) {
		List<BiotypeMetadataBiosampleDto>  res = new ArrayList<BiotypeMetadataBiosampleDto>();
		for(BiotypeMetadataBiosample biotypeMetadataBiosample : biotypeMetadataBiosamples) {
			res.add(map(biotypeMetadataBiosample));
		}
		return res;
	}
	
	public BiotypeMetadataBiosampleDto map(BiotypeMetadataBiosample biotypeMetadataBiosample) {
		BiotypeMetadataBiosampleDto biotypeMetadataBiosampleDto = idToBiotypeMetadataBiosample.get(biotypeMetadataBiosample.getId());
		if(biotypeMetadataBiosampleDto==null) {
			biotypeMetadataBiosampleDto = dozerMapper.map(biotypeMetadataBiosample, BiotypeMetadataBiosampleDto.class,"biotypeMetadataBiosampleCustomMapping");
			if(idToBiotypeMetadataBiosample.get(biotypeMetadataBiosample.getId())==null)
				idToBiotypeMetadataBiosample.put(biotypeMetadataBiosample.getId(), biotypeMetadataBiosampleDto);
			else
				biotypeMetadataBiosampleDto = idToBiotypeMetadataBiosample.get(biotypeMetadataBiosample.getId());
		}
		return biotypeMetadataBiosampleDto;
	}

	@Transactional
	public void save(BiotypeMetadataBiosampleDto metadata) {
		save(metadata, false);
	}
	
	protected void save(BiotypeMetadataBiosampleDto metadata, Boolean cross) {
		try {
			if(!savedItems.contains(metadata)) {
				savedItems.add(metadata);
				metadata.setUpdDate(new Date());
				metadata.setUpdUser(UserUtil.getUsername());
				if(metadata.getId().equals(Constants.NEWTRANSIENTID)) {
					metadata.setCreDate(new Date());
					metadata.setCreUser(UserUtil.getUsername());
				}
				metadata.setId(saveOrUpdate(dozerMapper.map(metadata, BiotypeMetadataBiosample.class, "biotypeMetadataBiosampleCustomMapping")));
			}
		}catch(Exception e) {
			if (!cross) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
			throw e;
		}finally{
			if(!cross) {
				AbstractService.clearSavedItem();
				AbstractService.clearTransient(true);
			}
		}
	}

	@Transactional
	public void delete(BiotypeMetadataBiosampleDto metadata) {
		delete(metadata, false);
	}
	
	protected void delete(BiotypeMetadataBiosampleDto metadata, Boolean cross) {
		biotypeMetadataBiosampleDao.delete(metadata.getId());
	}

	@SuppressWarnings("deprecation")
	public List<BiotypeMetadataBiosampleDto> getMetadataByBiosamples(List<BiosampleDto> biosamples) {
		List<Integer> ids = getIds(biosamples);
		List<Integer> idsTemp = new ArrayList<>();
		List<BiotypeMetadataBiosampleDto> res = new ArrayList<>();
		int i=0;
		int j=0;
		for(BiosampleDto b : biosamples) {
			b.setMetadatas(new ArrayList<>());
		}
		while(ids.size()>i) {
			while(j%1000<999 && ids.size()>i) {
				Integer id = ids.get(i);
				if(id!=Constants.NEWTRANSIENTID) {
					idsTemp.add(id);
					j++;
				}
				i++;
			}
			j=0;
			if(idsTemp.size()>0)
				res.addAll(map(biotypeMetadataBiosampleDao.getMetadataByBiosamples(idsTemp)));
			System.out.println("get metadatas for "+idsTemp.size()+" samples");
			idsTemp.clear();
		}
		for(BiotypeMetadataBiosampleDto bmb : res) {
			bmb.getBiosample().getMetadatas().add(bmb);
		}
		return res;
	}

}
