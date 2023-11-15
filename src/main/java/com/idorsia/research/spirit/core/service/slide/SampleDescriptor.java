package com.idorsia.research.spirit.core.service.slide;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.service.ContainerService;
import com.idorsia.research.spirit.core.util.MiscUtils;

public class SampleDescriptor implements Comparable<SampleDescriptor>, Serializable, Cloneable {

	private static final long serialVersionUID = 7897746904331019542L;
	private static ContainerService containerService = (ContainerService) ContextShare.getContext().getBean("containerService");
	private static BiosampleService biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");
	private BiotypeDto biotype;
	private String name;
	private String parameters;
	private String comments;
	private int animalNo = 1;
	private Integer blocNo = null;
	private transient ContainerType containerType = null;
	
	/**
	 * Constructor for a default Sampling
	 */
	public SampleDescriptor() {}	

	@Override
	public SampleDescriptor clone() {
		try {
			SampleDescriptor clone = (SampleDescriptor) super.clone();
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates a Sampling based on the metadata from the biosample
	 * @param b
	 */
	public SampleDescriptor(int animalNo, BiosampleDto b) {
		this.animalNo = animalNo;
		this.biotype = b.getBiotype();
		this.containerType = b.getContainerType();
		this.blocNo = b.getContainer()==null? null: containerService.getBlocNo(b.getContainer());

		Map<Integer, String> map = new HashMap<>();
		for (BiotypeMetadataDto bm : biotype.getMetadatas()) {
			String m = biosampleService.getMetadataValue(b, bm);
			if(m!=null) {
				map.put(bm.getId(), m);
			}
		}
		
		name = b.getBiotype().getNameLabel()==null? null: b.getLocalId();
		parameters = MiscUtils.serializeIntegerMap(map);
		comments = b.getComments();
	}
	
	public BiotypeDto getBiotype() {
		return biotype;
	}
	public void setBiotype(BiotypeDto biotype) {
		this.biotype = biotype;
	}
	protected String getParameters() {
		return parameters;
	}

	protected String getComments() {
		return comments;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		Collection<String> values = MiscUtils.deserializeIntegerMap(parameters).values();
		return biotype.getName() + (name!=null? " " + name: "") + (values.size()>0? ": " + MiscUtils.flatten(values): "") + comments != null ? comments : "";
	}
	

	
	public BiosampleDto createCompatibleBiosample() {
		BiosampleDto biosample = new BiosampleDto(biotype);
		
		biosample.setLocalId(name);
		
		//parameters
		Map<Integer, String> map = MiscUtils.deserializeIntegerMap(parameters);
		for (BiotypeMetadataDto m : biotype.getMetadatas()) {
			String data = map.get(m.getId());
			if(data!=null) {
				biosampleService.setMetadataValue(biosample, m, data);
			}
		}

		biosample.setComments(comments);
		
		return biosample;
	}
	
	@Override
	public int compareTo(SampleDescriptor o) {

		
		int c = CompareUtils.compare(biotype, o.biotype);
		if(c!=0) return c;
				
		c = CompareUtils.compare(name, o.name);
		if(c!=0) return c;

		c = CompareUtils.compare(getParameters(), o.getParameters());
		if(c!=0) return c;

		c = CompareUtils.compare(getComments(), o.getComments());
		if(c!=0) return c;
			
		c = CompareUtils.compare(getBlocNo(), o.getBlocNo());
		if(c!=0) return c;

		return c;
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((SampleDescriptor)obj)==0;
	}		
		
	public int getAnimalNo() {
		return animalNo;
	}
	
	public void setAnimalNo(int animalNo) {
		this.animalNo = animalNo;
	}

	public ContainerType getContainerType() {
		return containerType;
	}
	
	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}

	/**
	 * Check if this sample is compatible to allow the generation of slides with this biosample
	 * ie, the name and params must be identicals
	 * does not check container (Cassette/bottle/...)
	 * @param biosample
	 * @param checkSample
	 * @return
	 */
	public boolean isBiosampleCompatible(BiosampleDto biosample) {

		//check biotype
		if (biosample.getBiotype() == null || !biosample.getBiotype().equals(getBiotype()))
			return false;
		Map<Integer, String> map = MiscUtils.deserializeIntegerMap(parameters);
		
		//check name
		if(biosample.getBiotype().getNameLabel()!=null) {			
			if(CompareUtils.compare(biosample.getLocalId(), getName())!=0)
				return false;
		}
		//Check metadata
		for (BiotypeMetadataDto m : getBiotype().getMetadatas()) {
			if(m.getName().equals(Constants.STAINING)) continue;
			String data = map.get(m.getId());
			String me = biosampleService.getMetadataValue(biosample, m);
			if(data==null || data.length()==0) {
				if((me!=null && me.trim().length()>0)) {
					return false;								
				}
			} else {
				if((me==null || !data.equalsIgnoreCase(me))) {
					return false;
				}
			}
		}

		//Check comments
		String sampleComments = biosample.getComments();
		if (sampleComments != null && sampleComments.equals(getComments()) == false)
			return false;
		
		return true;
	}

	public Integer getBlocNo() {
		return blocNo;
	}
//
//	public void setBlocNo(String blocNo) {
//		this.blocNo = blocNo;
//	}
	
		
}
