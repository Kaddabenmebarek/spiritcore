package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.InfoFormat;
import com.idorsia.research.spirit.core.constants.InfoSize;
import com.idorsia.research.spirit.core.constants.Status;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.GroupDto;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Amount;
import com.idorsia.research.spirit.core.dto.view.Container;
import com.idorsia.research.spirit.core.util.MiscUtils;

@Service
public class ContainerService implements Serializable {

	private static final long serialVersionUID = 1843912116562198052L;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private PhaseService phaseService;
	
	/**
	 * The Container is formatter like: {prefix}{ID}[-BlocNo][-Suffix]
	 *
	 * @return
	 */
	public Integer getBlocNo(Container container) {
		if(container==null) return null;
		return getBlocNo(container.getContainerType(), container.getContainerId());
	}

	public Integer getBlocNo(ContainerType containerType, String containerId) {
		if(containerId==null) return null;
		if(containerType!=null && containerType.getBlocNoPrefix()==null) return null;

		int ind1 = containerId.indexOf(Constants.BLOC_SEPARATOR, 4);
		if(ind1<0) return null;

		int ind2 = containerId.indexOf(Constants.BLOC_SEPARATOR, ind1+1);
		if(ind2<0) ind2 = containerId.length();

		String blocNo = containerId.substring(ind1+1, ind2);

		try {
			return Integer.parseInt(blocNo);
		} catch(Exception e) {
			return null;
		}
	}

	public int getPos(Container container) {
		for(BiosampleDto b: getBiosamples(container)) {
			return b.getLocationPos();
		}
		return -1;
	}

	public void setPos(Container container, int pos) {
		for(BiosampleDto b: getBiosamples(container)) {
			b.setLocationPos(pos);
			b.setScannedPosition(null);
		}
	}

	public LocationDto getLocation(Container container) {
		BiosampleDto b = getFirstBiosample(container);
		return b==null? null: b.getLocation();
	}

	public void setLocation(Container container, LocationDto location) {
		if(getLocation(container)!=null) {
			getLocation(container).getBiosamples().removeAll(getBiosamples(container));
		}
		for(BiosampleDto b: getBiosamples(container)) {
			biosampleService.setLocation(b, location);
		}
		if(getLocation(container)!=null) {
			getLocation(container).getBiosamples().addAll(getBiosamples(container));
		}
	}
	public String getContainerOrBiosampleId(Container container) {
		if(container.getContainerId()!=null && container.getContainerId().length()>0) return container.getContainerId();
		if(getBiosamples(container).size()==0) return "";
		BiosampleDto b = getBiosamples(container).iterator().next();
		return b.getSampleId()==null?"": b.getSampleId();
	}

	@SuppressWarnings("deprecation")
	public Set<BiosampleDto> getBiosamples(Container container) {
		if(container.getBiosamples()==null) {
			container.setBiosamples(new TreeSet<BiosampleDto>((o1,o2)-> {
				if(o1==null && o2==null) return 0;
				if(o1==null) return 1; //Null at the end
				if(o2==null) return -1;
				int c = CompareUtils.compare(o1.getContainerIndex(), o2.getContainerIndex());
				if(c!=0) return c;
				c = CompareUtils.compare(o1.getSampleId(), o2.getSampleId());
				if(c!=0) return c;
				return o1.getId()-o2.getId();
			}));
			if(container.getContainerId()!=null && container.getContainerId().length()>0 && 
					(container.getContainerType()!=null && container.getContainerType().isMultiple())) {
				container.getBiosamples().addAll(biosampleService.map(biosampleService.getBiosampleByContainerId(container.getContainerId())));
			}
			if(container.getCreatedFor()!=null) {
				container.getBiosamples().add(container.getCreatedFor());
			}
		}
		return container.getBiosamples();
	}

	public void removeBiosample(Container container, BiosampleDto b) {
		if(getBiosamples(container)!=null)
			getBiosamples(container).remove(b);
	}

	public void addBiosample(Container container, BiosampleDto b) {
		if(getBiosamples(container)!=null)
			getBiosamples(container).add(b);
	}

	public BiosampleDto getFirstBiosample(Container container) {
		if(container.getCreatedFor()!=null) 
			return container.getCreatedFor();
		return getBiosamples(container).size()>0? getBiosamples(container).iterator().next(): null;
	}

	public String getType(Container container) {
		Container c = container;

		Set<String> types = biosampleService.getTypes(getBiosamples(c));
		if(types.size()==1) return types.iterator().next();
		return MiscUtils.extractCommonPrefix(types);
	}

	public String getBlocDescription(Container container) {
		Container c = container;
		Set<String> lines = new LinkedHashSet<>();
		for (BiosampleDto b : getBiosamples(c)) {
			lines.add(biosampleService.getInfos(b, EnumSet.of(InfoFormat.SAMPLENAME, InfoFormat.BIOTYPE, InfoFormat.METATADATA, InfoFormat.COMMENTS), InfoSize.ONELINE));
		}
		StringBuilder sb = new StringBuilder();
		sb.append(c.getContainerType().getBlocNoPrefix()==null?"": c.getContainerType().getBlocNoPrefix() + getBlocNo(c) + "\n");
		sb.append(MiscUtils.flatten(lines, "\n"));
		return sb.toString();
	}

	/**
	 * Return a generic print Label (used in SpiritWeb)
	 * If the study is blinded, the actionGroup is shown as blinded
	 * @return
	 */
	public String getPrintLabel(Container container) {
		return getPrintStudyLabel(container, null) + "\n" + getPrintMetadataLabel(container);
	}


	public String getPrintStudyLabel(Container container, String user) {
		StringBuilder sb = new StringBuilder();
		StudyDto study = biosampleService.getStudy(getBiosamples(container));
		if(study!=null) {
			//Add the study
			sb.append(study.getStudyId() + "\n");

			//Add the actionGroup
			GroupDto g = biosampleService.getCreationGroup(getBiosamples(container));
			if(g!=null) {
				sb.append("Gr." + groupService.getShortName(g));
				//Add the phase
				PhaseDto phase = biosampleService.getPhase(getBiosamples(container));
				if(phase!=null) {
					sb.append(" / " +phaseService.getShortName(phase));
				}
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	public String getPrintMetadataLabel(Container container) {
		StudyDto study = biosampleService.getStudy(getBiosamples(container));
		return getPrintMetadataLabel(container, study==null? InfoSize.EXPANDED: InfoSize.CUSTOM);
	}

	/**
	 * Infos how they will be printed on the label (without the study)
	 * @return
	 */
	public String getPrintMetadataLabel(Container container, InfoSize infoSize) {
		String res;
		ContainerType containerType = container.getContainerType();
		if(containerType==ContainerType.BOTTLE) {
			//Bottle
			res = biosampleService.getInfos(getBiosamples(container), EnumSet.of(InfoFormat.TOPIDNAMES), infoSize)+"\n";
			Integer blocNo = getBlocNo(container);
			if(blocNo!=null && containerType.getBlocNoPrefix()!=null) res += containerType.getBlocNoPrefix() + blocNo;
		} else if(containerType==ContainerType.CAGE) {
			//Cage
			//This is obsolate shouldn't happen
			res = "\n";
		} else if(containerType!=null && containerType.isMultiple()) {
			//Slide, Cassette
			res = biosampleService.getInfos(getBiosamples(container), EnumSet.of(InfoFormat.TOPIDNAMES), infoSize)+"\n";

			String info = biosampleService.getInfos(getBiosamples(container), EnumSet.of(InfoFormat.SAMPLENAME, InfoFormat.PARENT_SAMPLENAME), infoSize);
			if(info.length()>0) {
				res+= info + "\n";
			}

			Integer blocNo = getBlocNo(container);
			if(blocNo!=null && containerType.getBlocNoPrefix()!=null) {
				res += containerType.getBlocNoPrefix() + blocNo + "\n";
			}

		} else {
			//Default Label
			res = biosampleService.getInfos(getBiosamples(container), EnumSet.of(
					InfoFormat.SAMPLEID,
					InfoFormat.TOPIDNAMES,
					InfoFormat.SAMPLENAME,
					InfoFormat.METATADATA,
					InfoFormat.PARENT_METATADATA,
					InfoFormat.PARENT_SAMPLENAME,
					InfoFormat.COMMENTS,
					InfoFormat.AMOUNT), infoSize);
		}
		return res;
	}

	/**
	 * Returns the groups of the contained biosamples
	 * @return
	 */
	public Set<GroupDto> getGroups(Container container) {
		return biosampleService.getGroups(getBiosamples(container));
	}

	/**
	 * Returns the actionGroup (if all samples share the same actionGroup) or null
	 * @return
	 */
	public GroupDto getGroup(Container container) {
		return biosampleService.getCreationGroup(getBiosamples(container));
	}

	public PhaseDto getPhase(Container container) {
		return biosampleService.getPhase(getBiosamples(container));
	}

	public Date getPhaseDate(Container container) {
		PhaseDto phase = biosampleService.getPhase(getBiosamples(container));
		if (phase!=null) {
			Set<Date> dates = new HashSet<>();
			for (BiosampleDto b : getBiosamples(container)) {
				Date date = biosampleService.getDate(b, phase, true);
				if (date!=null) 
					dates.add(date);
			}
			return dates.size()==1 ? dates.iterator().next() : null;
		}
		return null;
	}

	public Date getCreationDate(Container container) {
		for (BiosampleDto b : getBiosamples(container)) {
			Date date = biosampleService.getCreationExecutionDate(b);
			if (date!=null) 
				return date;
		}
		return null;
	}
	
	public StudyDto getStudy(Container container) {
		return biosampleService.getStudy(getBiosamples(container));
	}

	public Status getStatus(Container container) {
		return biosampleService.getStatus(getBiosamples(container));
	}

	public String getTopParents(Container container) {
		StringBuilder sb = new StringBuilder();
		Set<BiosampleDto> tops = biosampleService.getTopParents(getBiosamples(container));
		for(BiosampleDto top: tops) {
			if(sb.length()>0) sb.append(" ");
			sb.append(top.getSampleId());
		}
		return sb.toString();
	}

	public BiosampleDto getTopParent(Container container) {
		Set<BiosampleDto> topParents = biosampleService.getTopParents(getBiosamples(container));
		if (topParents.size() == 1) 
			return topParents.iterator().next();
		return null;
	}
	
	public String getContainerPrefix(Container container) {
		int index = container.getContainerId().lastIndexOf('-');
		if(index>5) return container.getContainerId().substring(0, index);
		else return container.getContainerId();
	}


	public Set<LocationDto> getLocations(Collection<Container> containers) {
		if(containers==null) return null;
		Set<LocationDto> res = new java.util.HashSet<>();
		for (Container c : containers) {
			if(c!=null) res.add(getLocation(c));
		}
		return res;
	}

	public Set<Integer> getPoses(Collection<Container> containers) {
		if(containers==null) return null;
		Set<Integer> res = new java.util.HashSet<>();
		for (Container c : containers) {
			if(getLocation(c)!=null && getPos(c)>=0) res.add(getPos(c));
		}
		return res;
	}

	public Set<String> getScannedPoses(Collection<Container> containers) {
		if(containers==null) return null;
		Set<String> res = new java.util.HashSet<>();
		for (Container c : containers) {
			if(getScannedPosition(c)!=null) res.add(getScannedPosition(c));
		}
		return res;
	}

	public Set<String> getContainerIds(Collection<Container> containers) {
		if(containers==null) return null;
		Set<String> res = new java.util.HashSet<>();
		for (Container c : containers) {
			res.add(c.getContainerId());
		}
		return res;
	}

	public Set<ContainerType> getContainerTypes(Collection<Container> containers) {
		if(containers==null) return null;
		Set<ContainerType> res = new java.util.HashSet<>();
		for (Container c : containers) {
			res.add(c.getContainerType());
		}
		return res;
	}

	public List<BiosampleDto> getBiosamples(Collection<Container> containers) {
		return getBiosamples(containers, false);
	}

	public List<BiosampleDto> getBiosamples(Collection<Container> containers, boolean createFakeBiosamplesForEmptyContainer) {
		if(containers==null) return null;
		List<BiosampleDto> res = new ArrayList<>();
		for (Container c : containers) {
			if(c==null) continue;
			if(getBiosamples(c).size()==0) {
				if(createFakeBiosamplesForEmptyContainer && getBiosamples(c).size()==0) {
					BiosampleDto b = new BiosampleDto();
					biosampleService.setContainer(b, c);
					res.add(b);
				}
			} else {
				res.addAll(getBiosamples(c));
			}
		}
		return res;
	}


	public Amount getAmount(Container container) {
		BiosampleDto b = getFirstBiosample(container);
		return b==null? null: biosampleService.getAmountAndUnit(b);
	}

	public void setAmount(Container container, Double volume) {
		for (BiosampleDto b : getBiosamples(container)) {
			b.setAmount(volume);
		}
	}

	public Map<String, Container> mapContainerId(Collection<Container> containers){
		Map<String, Container> res = new HashMap<>();

		for (Container container : containers) {
			if(container.getContainerId()!=null) res.put(container.getContainerId(), container);
		}
		return res;
	}

	public boolean isAllWithScannedPositions(Collection<Container> containers) {
		for (Container c : containers) {
			if(getScannedPosition(c)==null) return false;
		}
		return true;
	}

	public String getMetadata(Container container, String name) {
		Set<String> res = new HashSet<>();
		for (BiosampleDto b : getBiosamples(container)) {
			String metadata = biosampleService.getMetadataValue(b, name);
			if(metadata!=null && metadata.length()>0) res.add(metadata);
		}
		if(res.size()==1) return res.iterator().next();
		return "";
	}



	public boolean isEmpty(Container container) {
		for (BiosampleDto b : getBiosamples(container)) {
			if(b.getBiotype()!=null) 
				return false;
		}
		return true;
	}


	public int getRow(Container container) {
		LocationDto location = getLocation(container);
		if(location==null) return 0;
		return locationService.getRow(location, getPos(container));
	}

	public int getCol(Container container) {
		LocationDto location = getLocation(container);
		if(location==null) return 0;
		return locationService.getCol(location, getPos(container));
	}

	public String getScannedPosition(Container container) {
		BiosampleDto b = getFirstBiosample(container);
		return b==null? null: b.getScannedPosition();
	}
}
