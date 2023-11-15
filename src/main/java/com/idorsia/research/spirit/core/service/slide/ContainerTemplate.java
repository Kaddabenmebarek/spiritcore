package com.idorsia.research.spirit.core.service.slide;

import java.util.ArrayList;
import java.util.List;

public class ContainerTemplate {

	private int blocNo = 1;
	
	/**
	 * Describes the samples that should be in this container
	 */
	private List<SampleDescriptor> sampleDescriptors = new ArrayList<>();
	
	/**
	 * Describes the number of items for each staining (only for slides)
	 */
	private List<Duplicate> duplicates = new ArrayList<>();
	
	public static class Duplicate {
		private int nDuplicates;
		private String staining;
		private String sectionNo;
		
		public int getNDuplicates() {
			return nDuplicates;
		}
		public void setNDuplicates(int nDuplicates) {
			this.nDuplicates = nDuplicates;
		}
		public String getStaining() {
			return staining;
		}
		public void setStaining(String staining) {
			this.staining = staining;
		}
		public String getSectionNo() {
			return sectionNo;
		}
		public void setSectionNo(String sectionNo) {
			this.sectionNo = sectionNo;
		}
	}

	
	/**
	 * Gets list of duplicates->n, staining, sectionNo to be applied on this  template
	 * Only used for slides
	 * @return
	 */
	public List<Duplicate> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(List<Duplicate> duplicates) {
		this.duplicates = duplicates;
	}

	/**
	 * Gets the description of the sample that should be in the container 
	 * @return
	 */
	public List<SampleDescriptor> getSampleDescriptors() {
		return sampleDescriptors;
	}


	public void setSampleDescriptors(List<SampleDescriptor> samples) {
		this.sampleDescriptors = samples;
	}

	
	
	public int getBlocNo() {
		return blocNo;
	}

	public void setBlocNo(int blocNo) {
		this.blocNo = blocNo;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ContainerTemplate  x"+getDuplicates().size()+":");
		for (SampleDescriptor s : sampleDescriptors) {
			sb.append(" "+s.toString());
		}
		sb.append("]");
		return sb.toString();
	}

}
