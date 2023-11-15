package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dao.SamplingDao;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataBiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementAttributeDto;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementDto;
import com.idorsia.research.spirit.core.dto.SamplingParameterDto;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.model.Biosample;
import com.idorsia.research.spirit.core.model.Sampling;
import com.idorsia.research.spirit.core.model.SamplingMeasurement;
import com.idorsia.research.spirit.core.model.SamplingParameter;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class SamplingService extends AbstractService implements Serializable {

	private static final long serialVersionUID = -2003829661445566706L;
	@SuppressWarnings("unchecked")
	private static Map<Integer, SamplingDto> idToSampling = (Map<Integer, SamplingDto>) getCacheMap(SamplingDto.class);
	@Autowired
	private SamplingDao samplingDao;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private NamedSamplingService namedSamplingService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private SamplingMeasurementService samplingMeasurementService;
	@Autowired
	private SamplingParameterService samplingParameterService;
	@Autowired
	private AssayService assayService;

	public Sampling get(Integer id) {
		return samplingDao.get(id);
	}

	public List<Sampling> getbyNamedSampling(Integer namedSamplingId) {
		return samplingDao.getByNamedSampling(namedSamplingId);
	}

	public List<Sampling> getByParent(Integer parentId) {
		return samplingDao.getByParent(parentId);
	}

	public List<Sampling> list() {
		return samplingDao.list();
	}

	public int getCount() {
		return samplingDao.getCount();
	}

	public Integer saveOrUpdate(Sampling sampling) {
		return samplingDao.saveOrUpdate(sampling);
	}

	public int addSampling(Sampling sampling) {
		return samplingDao.addSampling(sampling);
	}

	public SamplingDao getSamplingDao() {
		return samplingDao;
	}

	public void setSamplingDao(SamplingDao samplingDao) {
		this.samplingDao = samplingDao;
	}

	public List<SamplingDto> map(List<Sampling> samplings) {
		List<SamplingDto> res = new ArrayList<SamplingDto>();
		for(Sampling sampling : samplings) {
			res.add(map(sampling));
		}
		return res;
	}
	
	
	public SamplingDto map(Sampling sampling) {
		SamplingDto samplingDto = idToSampling.get(sampling.getId());
		if(samplingDto==null) {
			samplingDto = dozerMapper.map(sampling,SamplingDto.class,"samplingCustomMapping");
			if(idToSampling.get(sampling.getId())==null)
				idToSampling.put(sampling.getId(), samplingDto);
			else
				samplingDto=idToSampling.get(sampling.getId());
		}
		return samplingDto;
	}
	
	@Transactional
	public void save(SamplingDto sampling) throws Exception {
		save(sampling, false);
	}
	
	@SuppressWarnings("deprecation")
	protected void save(SamplingDto sampling, Boolean cross) throws Exception {
		try {
			if(sampling!=null && !savedItems.contains(sampling)) {
				savedItems.add(sampling);
				if(sampling.getId()!=Constants.NEWTRANSIENTID)
					deleteChildren(sampling);
				if(sampling.getNamedSampling().getId()==Constants.NEWTRANSIENTID)
					namedSamplingService.save(sampling.getNamedSampling(), true);
				sampling.setUpdDate(new Date());
				sampling.setUpdUser(UserUtil.getUsername());
				if(sampling.getId().equals(Constants.NEWTRANSIENTID)) {
					sampling.setCreDate(new Date());
					sampling.setCreUser(UserUtil.getUsername());
				}
				sampling.setId(saveOrUpdate(dozerMapper.map(sampling, Sampling.class, "samplingCustomMapping")));
				idToSampling.put(sampling.getId(), sampling);
				if(sampling.getSamplesNoMapping()!=null)
					for(BiosampleDto biosample : sampling.getSamples()) {
						biosampleService.save(biosample, true);
					}
				if(sampling.getExtraMeasurementsNoMapping()!=null)
					for(SamplingMeasurementDto sm : sampling.getExtraMeasurements()) {
						samplingMeasurementService.save(sm, true);
					}
				if(sampling.getParametersNoMapping()!=null)
					for(SamplingParameterDto sp : sampling.getParameters()) {
						samplingParameterService.save(sp, true);
					}
				if(sampling.getChildrenNoMapping()!=null)
					for(SamplingDto child : sampling.getChildren()) {
						save(child, true);
					}
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

	@SuppressWarnings("deprecation")
	private void deleteChildren(SamplingDto sampling) throws Exception {
		if(sampling.getSamplesNoMapping()!=null) {
			for(Biosample b : biosampleService.getBySampling(sampling.getId())) {
				Boolean found = false;
				for(BiosampleDto child : sampling.getSamples()){
					if(b.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					biosampleService.delete(biosampleService.map(b), true);
				}
			}
		}
		if(sampling.getChildrenNoMapping()!=null) {
			for(Sampling s : getByParent(sampling.getId())) {
				Boolean found = false;
				for(SamplingDto child : sampling.getChildren()){
					if(s.getId().equals(child.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					delete(map(s), true);
				}
			}
		}
		if(sampling.getExtraMeasurementsNoMapping()!=null) {
			for(SamplingMeasurement s : samplingMeasurementService.getSamplingMeasurementBySampling(sampling.getId())) {
				Boolean found = false;
				for(SamplingMeasurementDto sm : sampling.getExtraMeasurements()){
					if(s.getId().equals(sm.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					samplingMeasurementService.delete(samplingMeasurementService.map(s), true);
				}
			}
		}
		if(sampling.getParametersNoMapping()!=null) {
			for(SamplingParameter s : samplingParameterService.getSamplingParameterBySampling(sampling.getId())) {
				Boolean found = false;
				for(SamplingParameterDto sm : sampling.getParameters()){
					if(s.getId().equals(sm.getId())) {
						found=true;
						break;
					}
				}
				if(!found) {
					samplingParameterService.delete(samplingParameterService.map(s), true);
				}
			}
		}
	}

	@Transactional
	public void delete(SamplingDto sampling) throws Exception {
		delete(sampling, false);
	}
	
	protected void delete(SamplingDto sampling, Boolean cross) throws Exception {
		samplingDao.delete(sampling.getId());
	}

	public SamplingDto getSamplingDto(Integer id) {
		return map(get(id));
	}

	public List<SamplingDto> getTopSamplings(List<SamplingDto> samplings) {
		List<SamplingDto> res = new ArrayList<>();
		for (SamplingDto s : samplings) {
			if(s.getParentSampling()==null) res.add(s);
		}
		return res;
	}
	
	public List<SamplingDto> getTopSamplings(NamedSamplingDto namedSampling) {
		return getTopSamplings(namedSampling.getSamplings());
	}
	
	public BiosampleDto createCompatibleBiosample(SamplingDto sampling) {
		BiosampleDto biosample = new BiosampleDto();
		biosampleService.setBiotype(biosample, sampling.getBiotype());
		biosampleService.setContainerType(biosample, sampling.getContainerType());
		biosample.setContainerIndex(sampling.getLocIndex());
		biosample.setLocalId(sampling.getSampleName());
		//parameters
		for (BiotypeMetadataDto m : sampling.getBiotype().getMetadatas()) {
			String data = getMetadata(sampling, m);
			if(data!=null) {
				BiotypeMetadataBiosampleDto metadata = new BiotypeMetadataBiosampleDto();
				metadata.setBiosample(biosample);
				metadata.setMetadata(m);
				metadata.setValue(data);
				biosampleService.setMetadata(biosample, metadata); 
			}
		}
		return biosample;
	}
	
	public String getMetadata(SamplingDto sampling, BiotypeMetadataDto metadata) {
		for(SamplingParameterDto sm : sampling.getParameters()) {
			if(sm.getBiotypemetadata().equals(metadata)) {
				return sm.getValue();
			}
		}
		return null;
	}

	public String getDetailsShort(SamplingDto sampling) {
		return (sampling.getSampleName()!=null? sampling.getSampleName(): sampling.getBiotype().getName());
	}


	public String getDetailsLong(SamplingDto sampling) {
		String values = getMetadataValues(sampling);
		String sampleName = sampling.getSampleName();
		String comments = sampling.getComments();
		return
				(sampleName!=null && sampleName.length()>0? sampleName: sampling.getBiotype().getName()) +
				(values.length()>0? ": " + values: "") +
				(comments!=null && comments.length()>0? " "+comments:"");
	}
	
	public Object getDetailsComplement(SamplingDto sampling) {
		String values = getMetadataValues(sampling);
		return
				(values.length()>0? values: "") +
				(sampling.getComments()!=null? " "+sampling.getComments():"");
	}
	
	public String getMetadataValues(SamplingDto sampling) {
		StringBuilder sb = new StringBuilder();
		if(sampling.getBiotype()!=null) {
			for(BiotypeMetadataDto bm: sampling.getBiotype().getMetadatas()) {
				String s = getMetadata(sampling, bm);
				if(s!=null && s.length()>0) {
					if(sb.length()>0) sb.append("; ");
					sb.append(s);
				}
			}
		}
		return sb.toString();
	}
	
	public boolean hasMeasurements(SamplingDto sampling) {
		return sampling.getWeighingRequired() || sampling.getCommentsRequired() || sampling.getLengthRequired() || sampling.getExtraMeasurements().size()>0;
	}

	public String getDetailsWithMeasurements(SamplingDto s) {
		String values = getMetadataValues(s);

		return "<b>" + s.getBiotype().getName() + "</b>" +
		(s.getSampleName()!=null && s.getSampleName().length()>0? " "+s.getSampleName(): "") +
		(values.length()>0? ": " + values: "") +
		(s.getComments()!=null && s.getComments().length()>0? " " + s.getComments(): "") +
		(s.getContainerType()!=null?" <b style='color:#777777'>["+s.getContainerType().getName()+ 
				(s.getLocIndex()!=null && s.getContainerType().isMultiple()? " " + s.getLocIndex():"") + "]</b>":"") +
		(s.getAmount()!=null && s.getBiotype().getAmountUnit()!=null? " "+s.getAmount()+ s.getBiotype().getAmountUnit().getUnit():"") +
		(hasMeasurements(s)? ("<span style='color:#0000AA'><b> [" +
				(s.getWeighingRequired()?"w":"") +
				(s.getLengthRequired()?"l":"") +
				(s.getCommentsRequired()?"o":"") +
				(s.getExtraMeasurements().size()>0?""+s.getExtraMeasurements().size():"") +
				"]</b></span>") : "");
	}
	
	public void remove(SamplingDto sampling) {
		for (SamplingDto s : sampling.getChildren()) {
			s.setParentSampling(sampling.getParentSampling());
		}

		//remove the link from the parent
		if(sampling.getParentSampling()!=null) {
			sampling.getParentSampling().getChildren().remove(sampling);
		}

		//remove the attached samplings
		for(BiosampleDto b : sampling.getSamples())
			b.setAttachedSampling(null);
		sampling.getSamples().clear();
		sampling.setParentSampling(null);
	}

	public double getMatchingScore(SamplingDto sampling, BiosampleDto biosample) {
		if(biosample==null || biosample.getBiotype()==null || !biosample.getBiotype().equals(sampling.getBiotype())) return 0;
		int n = 0;
		int total = 0;

		//Check name
		if(biosample.getBiotype().getNameLabel()!=null) {
			total+=6;
			if(sampling.getSampleName()!=null && sampling.getSampleName().length()>0) {
				if(sampling.getSampleName().equalsIgnoreCase(biosample.getLocalId())) n+=6;
			} else {
				if(biosample.getLocalId()==null || biosample.getLocalId().length()==0) n+=6;
			}
		}

		//Check metadata
		for (BiotypeMetadataDto m : sampling.getBiotype().getMetadatas()) {
			String data = getMetadata(sampling, m);
			String val = biosampleService.getMetadataValue(biosample, m);
			total+=2;
			if(data!=null && data.length()>0) {
				if(val!=null && data.equalsIgnoreCase(val)) {
					n+=2;
				}
			} else {
				if( val==null || val.length()==0) {
					n+=2;
				}
			}
		}

		//Check comments
		total++;
		if(biosample.getComments()!=null) {
			if(biosample.getComments().equals(biosample.getComments())) n++;
		} else {
			if(biosample.getComments()==null || biosample.getComments().length()==0) n++;
		}

		//Check container
		total++;
		if(biosample.getContainerType()==biosample.getContainerType()) n++;

		total++;
		if(sampling.getLocIndex()!=null) {
			if(biosample.getContainer()!=null && CompareUtils.equals(containerService.getBlocNo(biosample.getContainer()), sampling.getLocIndex())) n++;
		} else {
			if(biosample.getContainer()==null || containerService.getBlocNo(biosample.getContainer())==null) n++;
		}

		return ((double)n) / total;
	}

	public void populate(SamplingDto sampling, BiosampleDto biosample) {
		if(sampling.getBiotype()==null) return;

		if(biosample.getBiotype()!=null && !biosample.getBiotype().equals(sampling.getBiotype())) {
			throw new IllegalArgumentException("The biotype cannot be changed, the metadata cannot be updated");
		}

		//name
		if(sampling.getBiotype().getNameLabel()!=null) {
			biosample.setLocalId(sampling.getSampleName());
		}

		//parameters
		for (BiotypeMetadataDto m : sampling.getBiotype().getMetadatas()) {
			String data = getMetadata(sampling, m);
			if(data!=null) biosampleService.setMetadataValue(biosample, m, data);
		}

		//amount
		if(sampling.getBiotype().getAmountUnit()!=null) {
			biosample.setAmount(sampling.getAmount());
		}

		//Comments
		biosample.setComments(sampling.getComments());

		//If the biosample had a multiple container and we set a container which is not, we
		biosampleService.setContainerId(biosample, null);
		biosampleService.setContainerType(biosample, sampling.getContainerType());
		biosample.setContainerIndex(sampling.getLocIndex());
	}

	public SamplingDto clone(SamplingDto sampling) {
		SamplingDto clone = new SamplingDto();
		clone.setParameters(sampling.getParameters());
		clone.setAmount(sampling.getAmount());
		clone.setBiotype(sampling.getBiotype());
		clone.setLocIndex(sampling.getLocIndex());
		clone.setComments(sampling.getComments());
		clone.setCommentsRequired(sampling.getCommentsRequired());
		clone.setContainerType(sampling.getContainerType());
		clone.setLengthRequired(sampling.getLengthRequired());
		clone.setSampleName(sampling.getSampleName());
		clone.setWeighingRequired(sampling.getWeighingRequired());
		clone.setExtraMeasurements(sampling.getExtraMeasurements());
		return clone;
	}

	public void mapSamples(SamplingDto sampling) {
		List<BiosampleDto> samplings = biosampleService.map(biosampleService.getBySampling(sampling.getId()));
		if(samplings != null) {			
			sampling.setSamples(new HashSet<BiosampleDto>(samplings));
		}
	}

	public void mapChildren(SamplingDto sampling) {
		List<SamplingDto> children = new ArrayList<>();
		for(Sampling child : getByParent(sampling.getId())) {
			children.add(map(child));
		}
		Collections.sort(children);
		sampling.setChildren(children);
	}

	public void getExtraMeasurements(SamplingDto samplingDto) {
		samplingDto.setExtraMeasurements(samplingMeasurementService.map(samplingMeasurementService.getSamplingMeasurementBySampling(samplingDto.getId())));
		Collections.sort(samplingDto.getExtraMeasurements());
	}
	
	public void getParameters(SamplingDto samplingDto) {
		samplingDto.setParameters(samplingParameterService.map(samplingParameterService.getSamplingParameterBySampling(samplingDto.getId())));
		Collections.sort(samplingDto.getParameters());
	}

	public List<Measurement> getMeasurements(SamplingDto sampling) {
		List<Measurement> res = new ArrayList<>();
		for(SamplingMeasurementDto s : sampling.getExtraMeasurements())
			res.add(s.getMeasurement());
		return res;
	}
	
	public boolean remove(SamplingDto sampling, Measurement measurement) {
		for(SamplingMeasurementDto samplingMeasurement : sampling.getExtraMeasurements()) {
			if(samplingMeasurement.getMeasurement().equals(measurement)) {
				sampling.getExtraMeasurements().remove(samplingMeasurement);
				return true;
			}
		}
		return false;
	}

	public void addMeasurement(SamplingDto sampling, Measurement measurement) {
		SamplingMeasurementDto sm = new SamplingMeasurementDto();
		sm.setSampling(sampling);
		AssayDto assay = measurement.getAssay();
		sm.setAssay(assay);
		int i=0;
		for(String s : Arrays.asList(measurement.getParameters())) {
			SamplingMeasurementAttributeDto sma = new SamplingMeasurementAttributeDto();
			sma.setSamplingMeasurement(sm);
			sma.setAssayAttribute(assayService.getInputAttributes(assay).get(i));
			sma.setValue(s);
			sm.getAttributes().add(sma);
		}
		sampling.getExtraMeasurements().add(sm);
	}

	public void addProperty(SamplingDto sampling, BiotypeMetadataDto metadata, String value) {
		for(SamplingParameterDto sp : sampling.getParameters()) {
			if(sp.getBiotypemetadata().equals(metadata)) {
				sp.setValue(value);
				return;
			}
		}
		SamplingParameterDto sp = new SamplingParameterDto();
		sp.setBiotypemetadata(metadata);
		sp.setSampling(sampling);
		sp.setValue(value);
		sampling.getParameters().add(sp);
	}

	public void setMeasurementfromList(SamplingDto sampling, List<SamplingMeasurementDto> extraMeasurements) {
		List<SamplingMeasurementDto> measurements = new ArrayList<>();
		for(SamplingMeasurementDto em : extraMeasurements) {
			boolean found = false;
			for(SamplingMeasurementDto sm : sampling.getExtraMeasurements()) {
				if(sm.getMeasurement().equals(em.getMeasurement())) {
					measurements.add(sm);
					found=true;
					break;
				}
			}
			if(!found) {
				em.setSampling(sampling);
				measurements.add(em);
			}
		}
		sampling.setExtraMeasurements(measurements);
	}
}
