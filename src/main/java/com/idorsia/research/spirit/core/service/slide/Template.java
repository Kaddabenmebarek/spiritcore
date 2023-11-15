package com.idorsia.research.spirit.core.service.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.idorsia.research.spirit.core.constants.ContainerType;

public class Template {

	private List<ContainerTemplate> containerTemplates = new ArrayList<ContainerTemplate>();

	private ContainerType containerType;
	
	public Template(ContainerType containerType) {
		this.containerType = containerType;
	}
	
	public ContainerType getContainerType() {
		return containerType;
	}
	
	public int getNAnimals() {
		int maxAnimal = 0;
		for (ContainerTemplate containerTemplate : containerTemplates) {
			for (SampleDescriptor sample : containerTemplate.getSampleDescriptors()) {
				maxAnimal = Math.max(maxAnimal, sample.getAnimalNo()+1);
			}			
		}
		return maxAnimal;
	}
	
	public String getSuggestedName() {
		StringBuilder sb = new StringBuilder();
		for (ContainerTemplate containerTemplate : containerTemplates) {
			if(containerTemplate.getSampleDescriptors().size()==0) continue;
			sb.append("[");
//			sb.append(slide.getActions().size()>1? slide.getActions().size()+"::":"");
			
			boolean first = true;
			for (SampleDescriptor s : containerTemplate.getSampleDescriptors()) {
				if(first) first=false; else sb.append(", ");
				String st = s.getName()==null?"": s.getName();
				if(st.length()==0) st = s.getBiotype().getName();
				if(st.indexOf(';')>=0) st = st.substring(0, st.indexOf(';'));
				if(st.indexOf('/')>=0) st = st.substring(st.indexOf('/')+1);
				sb.append(st);
			}
//			if(slide.getStaining()!=null && slide.getStaining().length()>0) sb.append(":"+slide.getStaining());
			sb.append("] ");
		}
		
		return sb.toString().trim();
	}


//	public void setName(String name) {
//		this.name = name;
//	}
//
//
//	public String getCreUser() {
//		return creUser;
//	}
//
//
//	public void setCreUser(String creUser) {
//		this.creUser = creUser;
//	}
//
//
//	public Date getCreDate() {
//		return creDate;
//	}
//
//
//	public void setCreDate(Date creDate) {
//		this.creDate = creDate;
//	}


	public List<ContainerTemplate> getContainerTemplates() {
		return containerTemplates;
	}


	public void setContainerTemplates(List<ContainerTemplate> containerTemplate) {
		this.containerTemplates = containerTemplate;
	}
	
	@Override
	public String toString() {
		return getSuggestedName();
	}
	
	/**
	 * Returns true if 2 slides contains the same item (which is not allowed)
	 * @return
	 */
	public boolean testDuplicates() {
		Set<SampleDescriptor> all = new TreeSet<SampleDescriptor>();
		for (ContainerTemplate containerTemplate : containerTemplates) {
			for (SampleDescriptor s : containerTemplate.getSampleDescriptors()) {
				if(all.contains(s)) {
					return true;
				}
				all.add(s);
			}			
		}		
		return false; 
	}
	
}
