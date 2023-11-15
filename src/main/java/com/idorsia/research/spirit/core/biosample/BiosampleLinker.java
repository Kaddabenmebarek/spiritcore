package com.idorsia.research.spirit.core.biosample;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.DataType;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.util.ThreadUtils;

public class BiosampleLinker implements Comparable<BiosampleLinker> {

	public static enum LinkerType {
		SAMPLEID,
		SAMPLENAME,
		METADATA,
		COMMENTS,
		CONTAINERID
	}

	private String label;
	private final LinkerType type;
	private final BiotypeDto hierarchyBiotype;
	private final BiotypeMetadataDto aggregatedMetadata;
	private BiosampleService biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");
	
	private static Set<BiosampleLinker> syncSet = Collections.synchronizedSet(new TreeSet<>());

	/** Cannot be null if type=metadata*/
	private final BiotypeMetadataDto biotypeMetadata;

	/** Only used for the label (for amount and name), not for the actual linking*/
	private final BiotypeDto biotypeForLabel;


	public enum LinkerMethod {
		/** No container, no sampleId, no links */
		DIRECT_LINKS,
		/** No container, no sampleId, include links */
		ALL_LINKS,
		/** No container, no sampleId, include links */
		INDIRECT_LINKS;

	}

	public static Set<BiosampleLinker> getLinkers(Collection<BiosampleDto> biosamples, LinkerMethod method) {
		syncSet.clear();
		Set<BiosampleLinker> res = new TreeSet<>();
		for (BiosampleDto b : biosamples) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						syncSet.addAll(getLinkers(b, method));
					}catch(Exception e) {
						e.printStackTrace();
					}finally {
						ThreadUtils.removeThread(this);
					}							
				}
			};
			ThreadUtils.start(t);
		}
		ThreadUtils.waitProcess();
		res = new TreeSet<>(syncSet);
		syncSet.clear();
		return res;
	}


	/**
	 * Gets all available linkers
	 * @param biosample
	 * @param method
	 * @return
	 */
	public static Set<BiosampleLinker> getLinkers(BiosampleDto biosample, LinkerMethod method) {
		BiosampleService biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");
		Set<BiosampleLinker> res = new TreeSet<>();
		if(biosample==null || biosample.getBiotype()==null) return res;

		boolean followLinks = method==LinkerMethod.ALL_LINKS || method==LinkerMethod.INDIRECT_LINKS;

		if(followLinks) {
			//Create aggregated links from this biosample
			for(BiotypeMetadataDto m1: biosample.getBiotype().getMetadatas()) {
				BiosampleDto b = m1.getDatatype()==DataType.BIOSAMPLE? biosampleService.getMetadataBiosample(biosample, m1): null;
				if(b!=null && b.getBiotype()!=null) {
					res.add(new BiosampleLinker(m1, LinkerType.SAMPLEID, b.getBiotype()));

					if(b.getBiotype().getNameLabel()!=null && (b.getLocalId()!=null  && b.getLocalId().length()>0) && !b.getBiotype().getIsHidden()) {
						res.add(new BiosampleLinker(m1, LinkerType.SAMPLENAME, b.getBiotype()));
					}
					for(BiotypeMetadataDto m2: b.getBiotype().getMetadatas()) {
						String s = biosampleService.getMetadataValue(b, m2);
						if(s!=null && s.length()>0) {
							res.add(new BiosampleLinker(m1, m2));
						}
					}
					if(b.getComments()!=null && b.getComments().length()>0) {
						res.add(new BiosampleLinker(m1, LinkerType.COMMENTS, b.getBiotype()));
					}

				}
			}

			//Create hierarchical links from this biosample
			BiosampleDto b2 = biosample.getParent();
			int count = 0;
			while(b2!=null && count++<5) {
				if(b2.getBiotype()!=null && !b2.getBiotype().equals(biosample.getBiotype())) {
					if(!b2.getBiotype().getHideSampleId()) {
						res.add(new BiosampleLinker(b2.getBiotype(), LinkerType.SAMPLEID));
					}
					if(b2.getBiotype().getNameLabel()!=null && b2.getLocalId()!=null && b2.getLocalId().length()>0) {
						res.add(new BiosampleLinker(b2.getBiotype(), LinkerType.SAMPLENAME));
					}					
					for(BiotypeMetadataDto m2: b2.getBiotype().getMetadatas()) {
						String s = biosampleService.getMetadataValue(b2, m2);
						if(s!=null && s.length()>0) {
							res.add(new BiosampleLinker(b2.getBiotype(), m2));
						}
					}
					if(b2.getComments()!=null && b2.getComments().length()>0) {
						res.add(new BiosampleLinker(b2.getBiotype(), LinkerType.COMMENTS));
					}
				}

				b2 = b2.getParent();
			}

		}

		if(method==LinkerMethod.ALL_LINKS || method==LinkerMethod.DIRECT_LINKS) {
			//Create direct links from this biosample
			if(followLinks) {
				res.add(new BiosampleLinker(biosample.getBiotype(), LinkerType.SAMPLEID));
			} else {
				res.add(new BiosampleLinker(LinkerType.SAMPLEID, biosample.getBiotype()));
			}


			if(biosample.getBiotype().getNameLabel()!=null && biosample.getLocalId()!=null) {
				if(followLinks) {
					res.add(new BiosampleLinker(biosample.getBiotype(), LinkerType.SAMPLENAME));
				} else {
					res.add(new BiosampleLinker(LinkerType.SAMPLENAME, biosample.getBiotype()));
				}
			}			
			//Retrieve metadata in the appropriate order
			for(BiotypeMetadataDto bm: biosample.getBiotype().getMetadatas()) {
				String value = biosampleService.getMetadataValue(biosample, bm);
				if(value!=null && value.length()>0) {
					if(followLinks) {
						if(bm.getDatatype()==DataType.BIOSAMPLE && biosampleService.getMetadataBiosample(biosample, bm)==null) {
							res.add(new BiosampleLinker(biosample.getBiotype(), bm));
						}
					} else {
						res.add(new BiosampleLinker(bm));
					}
				}
			}
			if(biosample.getComments()!=null) {
				if(followLinks) {
					res.add(new BiosampleLinker(biosample.getBiotype(), LinkerType.COMMENTS));
				} else {
					res.add(new BiosampleLinker(LinkerType.COMMENTS, biosample.getBiotype()));
				}
			}
		}

		return res;
	}



	/**
	 * Create a linker to an aggregated data (ex cell->plasmid.metadata)
	 */
	public BiosampleLinker(BiotypeMetadataDto aggregatedMetadata, BiotypeMetadataDto biotypeMetadata) {
		if(aggregatedMetadata==null) throw new IllegalArgumentException("The aggregatedMetadata cannot be null");
		if(biotypeMetadata==null) throw new IllegalArgumentException("The biotypeMetadata cannot be null");
		this.hierarchyBiotype = null;
		this.aggregatedMetadata = aggregatedMetadata;
		this.type = LinkerType.METADATA;
		this.biotypeMetadata = biotypeMetadata;
		this.biotypeForLabel = biotypeMetadata.getBiotype();
	}


	/**
	 * Create a linker to an aggregated data (ex cell->plasmid.comments)
	 */
	public BiosampleLinker(BiotypeMetadataDto aggregatedMetadata, LinkerType type) {
		this(aggregatedMetadata, type, null);
	}

	/**
	 * Create a linker to an aggregated data (ex cell->plasmid.comments)
	 */
	public BiosampleLinker(BiotypeMetadataDto aggregatedMetadata, LinkerType type, BiotypeDto typeForLabel) {
		if(aggregatedMetadata==null) throw new IllegalArgumentException("aggregatedMetadata cannot be null");
		this.hierarchyBiotype = null;
		this.aggregatedMetadata = aggregatedMetadata;
		this.type = type;
		this.biotypeMetadata = null;
		this.biotypeForLabel = typeForLabel;
	}


	/**
	 * Create a linker to an parent data (ex cell->cell line.metadata)
	 */
	public BiosampleLinker(BiotypeDto hierarchyBiotype, BiotypeMetadataDto biotypeMetadata) {
		if(hierarchyBiotype==null) throw new IllegalArgumentException("hierarchyBiotype cannot be null");
		if(biotypeMetadata==null) throw new IllegalArgumentException("biotypeMetadata cannot be null");
		this.hierarchyBiotype = hierarchyBiotype;
		this.aggregatedMetadata = null;
		this.type = LinkerType.METADATA;
		this.biotypeMetadata = biotypeMetadata;
		this.biotypeForLabel = hierarchyBiotype;
	}

	/**
	 * Create a linker to an parent data (ex cell->cell line.comments)
	 */
	public BiosampleLinker(BiotypeDto hierarchyBiotype, LinkerType type) {
		if(type==LinkerType.METADATA || type==null) throw new IllegalArgumentException("The type cannot be METADATA or null");
		this.hierarchyBiotype = hierarchyBiotype;
		this.aggregatedMetadata = null;
		this.type = type;
		this.biotypeMetadata = null;
		this.biotypeForLabel = hierarchyBiotype;
	}

	/**
	 * Create a linker to a non metadata attribute (ex cell.comments).
	 * Only to be used within queries
	 */
	public BiosampleLinker(LinkerType type) {
		this(type, null);
	}

	/**
	 * Create a linker to a non metadata attribute (ex cell.comments)
	 * The biotypeForLabel should be given to display the header correctly.
	 */
	public BiosampleLinker(LinkerType type, BiotypeDto biotypeForLabel) {
		this.hierarchyBiotype = null;
		this.aggregatedMetadata = null;
		this.type = type;
		this.biotypeMetadata = null;
		this.biotypeForLabel = biotypeForLabel;
	}
	/**
	 * Create a linker to a metadata attribute (ex cell.metadata)
	 */
	public BiosampleLinker(BiotypeMetadataDto biotypeMetadata) {
		this.hierarchyBiotype = null;
		this.aggregatedMetadata = null;
		this.type = LinkerType.METADATA;
		this.biotypeMetadata = biotypeMetadata;
		this.biotypeForLabel = biotypeMetadata==null? null: biotypeMetadata.getBiotype();
	}

	public boolean isLinked() {
		return hierarchyBiotype != null || aggregatedMetadata != null;
	}

	/**
	 * Not null, to link to a parent of the given biotype
	 */
	public BiotypeDto getHierarchyBiotype() {
		return hierarchyBiotype;
	}

	public LinkerType getType() {
		return type;
	}

	public BiotypeMetadataDto getBiotypeMetadata() {
		return biotypeMetadata;
	}

	/**
	 * returns the expected biotype linked to this display.
	 */
	public BiotypeDto getBiotypeForLabel() {
		return biotypeForLabel;
	}
	/**
	 * Returns the linked biosample
	 */
	public BiosampleDto getLinked(BiosampleDto biosample) {
		if(biosample==null) return null;
		if(!isLinked()) return biosample;

		BiosampleDto b2 = biosample;

		if(aggregatedMetadata!=null) {
			//Aggregation
			if(b2.getBiotype()!=null && b2.getBiotype().equals(aggregatedMetadata.getBiotype())) {
				return biosampleService.getMetadataBiosample(b2, aggregatedMetadata);
			}
		} else if(hierarchyBiotype!=null) {
			//Parent
			int count = 0;
			while(b2!=null && count++<5) {
				if(hierarchyBiotype.equals(b2.getBiotype())) {
					return b2;
				}
				b2 = b2.getParent();
			}
		}
		return null;
	}

	/**
	 * to link to an other biosample through the metadata
	 */
	public BiotypeMetadataDto getAggregatedMetadata() {
		return aggregatedMetadata;
	}

	public String getValue(BiosampleDto b) {
		b = getLinked(b);
		if(b==null) return null;

		switch(type) {
		case SAMPLEID: return b.getSampleId();
		case SAMPLENAME: return b.getLocalId();
		case METADATA: return b.getBiotype()==null || biotypeMetadata==null || !b.getBiotype().equals(biotypeMetadata.getBiotype())? 
				null: biosampleService.getMetadataValue(b, biotypeMetadata);
		case COMMENTS: return b.getComments();
		case CONTAINERID: return b.getContainerId();
		default: return "??"+type;
		}
	}

	public boolean setValue(BiosampleDto b, String value) {
		b = getLinked(b);
		if(b==null) return false;

		switch(type) {
		case SAMPLEID:
			b.setSampleId(value);
			return true;
		case SAMPLENAME:
			b.setLocalId(value);
			return true;			
		case METADATA:
			if(!b.getBiotype().equals(biotypeMetadata.getBiotype()) || biosampleService.getMetadataValue(b, biotypeMetadata)==null) return false;
			biosampleService.setMetadata(b, biotypeMetadata, value);
			return true;
		case COMMENTS:
			b.setComments(value);
			return true;
		case CONTAINERID:
			biosampleService.setContainerId(b, value);
			return true;
		default: return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o==this) return true;
		if(!(o instanceof BiosampleLinker)) return false;
		return getLabel().equals(((BiosampleLinker)o).getLabel());
	}

	/**
	 * The order should be
	 * parentType1, parentType2, self, selfName, selfMetadata, selfToAgregratedMetadata, selfComments, selfContainer
	 */
	@Override
	public int compareTo(BiosampleLinker l) {
		//linker with HierarchyBiotype are first
		if(getHierarchyBiotype()!=null && l.getHierarchyBiotype()!=null) {
			int c = CompareUtils.compare(getHierarchyBiotype(), l.getHierarchyBiotype());
			if(c!=0) return c;
		} else if(getHierarchyBiotype()!=null && l.getHierarchyBiotype()==null) {
			return -1;
		} else if(getHierarchyBiotype()==null && l.getHierarchyBiotype()!=null) {
			return 1;
		}

		//Compare the next link
		BiotypeMetadataDto toCompare1 = getAggregatedMetadata()!=null? getAggregatedMetadata(): getBiotypeMetadata();
		BiotypeMetadataDto toCompare2 = l.getAggregatedMetadata()!=null? l.getAggregatedMetadata(): l.getBiotypeMetadata();
		if(toCompare1!=null && toCompare2!=null) {
			int c = toCompare1.getIdx() - toCompare2.getIdx();
			if(c!=0) return c;
		}

		//Compare type / and second type if needed
		if(getType()==LinkerType.METADATA && l.getType()==LinkerType.METADATA) {
			BiotypeMetadataDto toCompare1_2 = getAggregatedMetadata()!=null? getBiotypeMetadata(): null;
			BiotypeMetadataDto toCompare2_2 = l.getAggregatedMetadata()!=null? l.getBiotypeMetadata(): null;
			return CompareUtils.compare(toCompare1_2, toCompare2_2);
		} else {
			return getType().compareTo(l.getType());
		}

	}


	/**
	 * Returns the label (one line), used in selectors
	 */
	@Override
	public String toString() {
		return getLabel().replace("\n", ".");
	}

	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}

	private static String getLabel(LinkerType type, BiotypeDto biotypeForLabel) {
		switch(type) {
		case SAMPLEID: return  "SampleId";
		case SAMPLENAME: return  biotypeForLabel==null? "SampleName" :biotypeForLabel.getNameLabel();
		case COMMENTS: return "Comments";
		default: return "??"+type;
		}
	}


	/**
	 * Returns the label, used as unique identifier: biotypeOrMetadata\nMetadataOrType[.MetadataOrType]
	 * @return
	 */
	public String getLabel() {
		//if(label==null) {
			if(aggregatedMetadata!=null) {
				if(biotypeMetadata!=null) {
					label = aggregatedMetadata.getBiotype().getName() + "\n" + aggregatedMetadata.getName() + "." + biotypeMetadata.getName();
				} else if(type==LinkerType.SAMPLEID) {
					//Careful, make sure that the label is the same as in the other case (or the linker equality will fail)
					label = aggregatedMetadata.getBiotype().getName() + "\n" + aggregatedMetadata.getName();
				} else {
					label = aggregatedMetadata.getBiotype().getName() + "\n" + aggregatedMetadata.getName() + "." + getLabel(type, biotypeForLabel);
				}

			} else if(hierarchyBiotype!=null) {
				if(biotypeMetadata!=null) {
					label = hierarchyBiotype.getName()+ "\n" + biotypeMetadata.getName();
				} else {
					label = hierarchyBiotype.getName()+ "\n" + getLabel(type, hierarchyBiotype);
				}
			} else if(biotypeForLabel!=null) {
				if(biotypeMetadata!=null) {
					label = biotypeForLabel.getName()+ "\n" + biotypeMetadata.getName();
				} else {
					label = biotypeForLabel.getName()+ "\n" + getLabel(type, biotypeForLabel);
				}
			} else {
				if(biotypeMetadata!=null) {
					label = biotypeMetadata.getName();
				} else {
					label = getLabel(type, null);
				}

			}
		//}
		return label;
	}

	/**
	 * Returns the label in a readable format
	 * @return
	 */
	public String getLabelShort() {
		String prefix = aggregatedMetadata==null || aggregatedMetadata.getName()==null?"": aggregatedMetadata.getName() + ".";
		switch(type) {
		case SAMPLEID: return   prefix + "SampleId";
		case SAMPLENAME: return  prefix + (biotypeForLabel==null? "SampleName" :biotypeForLabel.getNameLabel());
		case COMMENTS: return prefix + "Comments";
		case CONTAINERID: return prefix + "ContainerId";
		default:
			if(biotypeMetadata!=null) return prefix + biotypeMetadata.getName();
			return "??";
		}
	}

}
