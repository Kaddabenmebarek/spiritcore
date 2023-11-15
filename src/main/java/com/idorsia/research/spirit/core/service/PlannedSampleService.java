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
import com.idorsia.research.spirit.core.dao.PlannedSampleDao;
import com.idorsia.research.spirit.core.dto.AssignmentDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.PlannedSampleDto;
import com.idorsia.research.spirit.core.model.PlannedSample;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class PlannedSampleService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = 8979797904589819803L;

	@SuppressWarnings("unchecked")
	private static Map<Integer, PlannedSampleDto> idToPlannedSample = (Map<Integer, PlannedSampleDto>) getCacheMap(PlannedSampleDto.class);
	
	@Autowired
	private PlannedSampleDao plannedSampleDao;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private StageService stageService;
	
	public int addPlannedSample(PlannedSample plannedSample) {
		return plannedSampleDao.addPlannedSample(plannedSample);
	}
	
	public Integer saveOrUpdate(PlannedSample plannedSample) {
		return plannedSampleDao.saveOrUpdate(plannedSample);
	}

	public PlannedSample get(Integer id) {
		return plannedSampleDao.get(id);
	}
	
	public PlannedSample getPlannedSampleById(Integer id) {
		return plannedSampleDao.getPlannedSampleById(id);
	}
	
	public List<PlannedSample> getPlannedSamplesByStage(Integer stageId) {
		return plannedSampleDao.getPlannedSamplesByStage(stageId);
	}
	
	public List<PlannedSample> getPlannedSamplesByBiosample(Integer biosampleId) {
		return plannedSampleDao.getPlannedSamplesByBiosample(biosampleId);
	}
	
	public PlannedSample getPlannedSampleByStageAndBiosample(Integer stageId, Integer biosampleId) {
		return plannedSampleDao.getPlannedSampleByStageAndBiosample(stageId, biosampleId);
	}

	public PlannedSampleDao getPlannedSampleDao() {
		return plannedSampleDao;
	}

	public void setPlannedSampleDao(PlannedSampleDao plannedSampleDao) {
		this.plannedSampleDao = plannedSampleDao;
	}

	public List<PlannedSampleDto> map(List<PlannedSample> plannedSamples) {
		List<PlannedSampleDto> res = new ArrayList<PlannedSampleDto>();
		for(PlannedSample plannedSample : plannedSamples) {
			res.add(map(plannedSample));
		}
		return res;
	}
	
	public List<PlannedSampleDto> mapList(List<PlannedSample> plannedSamples) {
		List<PlannedSampleDto> res = new ArrayList<PlannedSampleDto>();
		for(PlannedSample plannedSample : plannedSamples) {
			res.add(map(plannedSample));
		}
		return res;
	}
	
	public PlannedSampleDto map(PlannedSample plannedSample) {
		if(plannedSample==null)
			return null;
		PlannedSampleDto plannedSampleDto = idToPlannedSample.get(plannedSample.getId());
		if(plannedSampleDto==null) {
			plannedSampleDto = dozerMapper.map(plannedSample,PlannedSampleDto.class,"plannedSampleCustomMapping");
			if(idToPlannedSample.get(plannedSample.getId())==null)
				idToPlannedSample.put(plannedSample.getId(), plannedSampleDto);
			else
				plannedSampleDto = idToPlannedSample.get(plannedSample.getId());
		}
		return plannedSampleDto;
	}
	
	@Transactional
	public void save(PlannedSampleDto plannedSample) throws Exception {
		save(plannedSample, false);
	}
	
	protected void save(PlannedSampleDto plannedSample, Boolean cross) throws Exception {
		try {
			if(!savedItems.contains(plannedSample)) {
				savedItems.add(plannedSample);
				if(plannedSample.getBiosample()!=null && plannedSample.getBiosample().getId()==Constants.NEWTRANSIENTID)biosampleService.save(plannedSample.getBiosample(), true);
				if(plannedSample.getStage().getId()==Constants.NEWTRANSIENTID)stageService.save(plannedSample.getStage(), true);
				plannedSample.setUpdDate(new Date());
				plannedSample.setUpdUser(UserUtil.getUsername());
				if(plannedSample.getId().equals(Constants.NEWTRANSIENTID)) {
					plannedSample.setCreDate(new Date());
					plannedSample.setCreUser(UserUtil.getUsername());
				}
				plannedSample.setId(saveOrUpdate(dozerMapper.map(plannedSample, PlannedSample.class, "plannedSampleCustomMapping")));
				idToPlannedSample.put(plannedSample.getId(), plannedSample);
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
	public void delete(PlannedSampleDto plannedSample) {
		delete(plannedSample, false);
	}
	
	protected void delete(PlannedSampleDto plannedSample, Boolean cross) {
		plannedSampleDao.delete(plannedSample.getId());
	}

	public void setBiosample(PlannedSampleDto plannedSample, BiosampleDto biosample) {
		if (biosample!=null) {
			setName(plannedSample, biosample.getSampleId());
    	} else {
    		setName(plannedSample, null);
    	} 
    }
	
    @SuppressWarnings("deprecation")
	public void setBiosample(PlannedSampleDto plannedSample, BiosampleDto biosample, boolean cross) {
        if (plannedSample.getBiosample()!=null){
            if (plannedSample.getBiosample().equals(biosample)) return;
            biosampleService.removePlannedSample(plannedSample.getBiosample(), plannedSample, true);
        }
        if (biosample!=null) {
            setName(plannedSample, biosample.getSampleId());
        } else {
            setName(plannedSample, null);
        }
        plannedSample.setBiosample(biosample);
    }

	@SuppressWarnings("deprecation")
	public void setName(PlannedSampleDto plannedSample, String name) {
        plannedSample.setName(name);
        handleBioSample(plannedSample, name);
	}
	
	 @SuppressWarnings("deprecation")
	private void handleBioSample(PlannedSampleDto plannedSample, String biosample) {
		 BiosampleDto sample = plannedSample.getBiosample();
		 if(sample!=null && sample.getSampleId().equals(biosample)) return;
		 if (sample!=null) biosampleService.removePlannedSample(sample, plannedSample, true);
		 if (biosample!=null) {
            if (plannedSample.getStage() != null) {
                for (AssignmentDto a : plannedSample.getStage().getAssignments()) {
                    if (a.getBiosample() != null && a.getBiosample().getSampleId().equals(biosample)) {
                        sample = a.getBiosample();
                        biosampleService.addPlannedSample(sample, plannedSample, true);
                        return;
                    }
                }
            }
            sample = biosampleService.map(biosampleService.getBiosampleBySampleId(biosample));
            //If it does not exist, create a new one
            if (sample == null) {
                sample = new BiosampleDto(null, biosample);
                try {
                    biosampleService.populateFromExternalDB(sample);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            biosampleService.addPlannedSample(sample, plannedSample, true);
            plannedSample.setBiosample(sample);
		 } else {
            setBiosample(plannedSample, null);
		 }
    }
		
	@Transactional 
	public void save(List<PlannedSampleDto> plannedSamples) {
		for(PlannedSampleDto plannedSample : plannedSamples) {
			try {				
				save(plannedSample, true);
			} catch(Exception e) {
				e.printStackTrace();
				AbstractService.clearTransient(false);
			}
		}
		AbstractService.clearSavedItem();
		AbstractService.clearTransient(true);
	}
}
