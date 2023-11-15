package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.actelion.research.osiris.util.ListHashMap;
import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.BarcodeType;
import com.idorsia.research.spirit.core.constants.ContainerType;
import com.idorsia.research.spirit.core.constants.HierarchyMode;
import com.idorsia.research.spirit.core.constants.InfoFormat;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.dto.view.Container;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.StudyAction;

@Service
public class BiosampleCreationHelper implements Serializable {
	
	private static final long serialVersionUID = 5632430054732489065L;
	@Autowired
	private BiosampleService biosampleService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private BarcodeService barcodeService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private NamedSamplingService namedSamplingService;
	
	public void analyse(StudyDto study, List<BiosampleDto> toAdd, List<BiosampleDto> toUpdate, List<BiosampleDto> toQuestionable, List<BiosampleDto> toDelete) throws Exception {
		//check existence of samples from sampling template
		List<BiosampleDto> allNeeded = processTemplateInStudy(study, null, null, null, null);

		////////////////////////////////////////

		//First filter: Find which ones must be deleted or created based on their status and their existence
		for (BiosampleDto b : allNeeded) {
			Date creDate = biosampleService.getCreationExecutionDateCalculated(b);
			if((creDate==null && studyService.getFirstDate(study)!=null) || biosampleService.isRemove(b.getTopParent(), study, creDate)||biosampleService.isDeadAt(b.getTopParent(), creDate)) {
				//Remove samples from animals marked as dead at this date
				if(b.getId()>0) {
					toDelete.add(b);
				}
				else {
					b.getTopParent().getChildren().remove(b);
				}
			} else {
				//Add other
				if(b.getId()<=0) {
					toAdd.add(b);
				}
			}
		}
		
		//update necropsy samples
		List<BiosampleDto> removeToAdd = new ArrayList<>();
		for(BiosampleDto b : toAdd) {
			SamplingDto s = b.getAttachedSampling();
			if(s.getNamedSampling().getNecropsy()) {
				List<BiosampleDto> samples = biosampleService.getChildSamples(b.getTopParent(), s);
				if((samples.size()==1 && !samples.get(0).equals(b)) || samples.size()>1){
					samples.remove(b);
					BiosampleDto sample = samples.get(0);
					sample.setInheritedPhase(b.getInheritedPhase());
					toUpdate.add(sample);
					removeToAdd.add(b);
				}
			}
		}
		toAdd.removeAll(removeToAdd);

		//Create Barcodes
		for (BiosampleDto b : toAdd) {
			if(b.getSampleId()==null || b.getSampleId().length()==0) {
				b.setSampleId(barcodeService.getNextId(b));
			}
		}

		////////////////////////////////////////
		//Check samples to be deleted
		for(BiosampleDto top: studyService.getSubjectsSorted(study)) {

			for(BiosampleDto sample: biosampleService.getHierarchy(top, HierarchyMode.ATTACHED_SAMPLES)) {
				if(!study.equals(sample.getStudy())) continue;
				if(sample.getInheritedPhase()==null) continue; //No Phase -> Skip
				if(sample.getAttachedSampling()==null) continue; //No sampling -> Skip (should not happen because we loop through attached samples)
				if(!allNeeded.contains(sample)) toDelete.add(sample);
			}
		}


		//Find samples that may have been moved.
		//find samples where the topSample may have been moved to a new actionGroup/subgroup: ie there is a sample to delete, matching a sample in toadd, with the same biotype, metadata
		ListHashMap<BiosampleDto, BiosampleDto> top2toAddSamples = new ListHashMap<>();
		for (BiosampleDto b : toAdd) {
			top2toAddSamples.add(b.getTopParent(), b);
		}
		for (BiosampleDto b : new ArrayList<>(toDelete)) {
			List<BiosampleDto> matches = new ArrayList<>();
			assert b!=null;
			assert b.getTopParent()!=null;
			if(top2toAddSamples.get(b.getTopParent())!=null) {
				for (BiosampleDto b2 : top2toAddSamples.get(b.getTopParent())) {

					if(!b.getAttachedSampling().equals(b2.getAttachedSampling())) continue;
					if(!b.getBiotype().equals(b2.getBiotype())) continue;
					if(!b.getInheritedPhase().equals(b2.getInheritedPhase())) continue;
					String cmt1 = biosampleService.getInfos(b, EnumSet.of(InfoFormat.METATADATA, InfoFormat.COMMENTS));
					String cmt2 = biosampleService.getInfos(b2, EnumSet.of(InfoFormat.METATADATA, InfoFormat.COMMENTS));
					if(!cmt1.equals(cmt2)) continue;
					matches.add(b2);
				}
			}
			if(matches.size()==1) {
				BiosampleDto match = matches.get(0);
				b.setInheritedPhase(match.getInheritedPhase());
				toAdd.remove(match);
				toDelete.remove(b);
				toUpdate.add(b);

				for (BiosampleDto child :  new ArrayList<>(match.getChildren())) {
					biosampleService.setParent(child, b);
				}

			} else if(matches.size()>1){
				//There are matches but this is not clear
				//Unresolvable problems
				toQuestionable.addAll(matches);
				toQuestionable.add(b);
			}

		}
		toAdd.removeAll(toQuestionable);
		toAdd.removeAll(toUpdate);
		toDelete.removeAll(toQuestionable);
		toDelete.removeAll(toUpdate);
	}
	
	public List<BiosampleDto> processTemplateInStudy(StudyDto study, NamedSamplingDto ns, Collection<PhaseDto> phases, ContainerType containerFilter, List<BiosampleDto> animalFilters) {
		assert study!=null;
		List<BiosampleDto> biosamples = new ArrayList<>();
		if(phases==null) phases = new ArrayList<>(studyService.getPhases(study));

		LoggerFactory.getLogger(BiosampleCreationHelper.class).debug("processTemplateInStudy for "+study+" n="+studyService.getParticipants(study).size());

		for(BiosampleDto animal: studyService.getSubjectsSorted(study)) {
			//filter by animals?
			if(animalFilters!=null && !animalFilters.contains(animal)) continue;

			for (PhaseDto phase : phases) {
				//Find the applicable sampling
				List<NamedSamplingDto> applicableSampling = new ArrayList<>();
				for (StudyAction studyAction : biosampleService.getStudyActions(animal, phase)) {
					if (studyAction instanceof NamedSamplingDto)
						if (ns==null || ns.equals(studyAction)) {
							applicableSampling.add((NamedSamplingDto)studyAction);
					}
					if (studyAction instanceof Disposal) {
						NamedSamplingDto necropsySampling = ((Disposal) studyAction).getSampling();
						if (necropsySampling != null && ((ns==null || ns.equals(necropsySampling)))) {
							applicableSampling.add(necropsySampling);
						}
					}
				}
				for (NamedSamplingDto namedSampling : applicableSampling) {
					LoggerFactory.getLogger(BiosampleCreationHelper.class).debug("Apply " + animal +  " " + phase+" "+namedSampling);
					for (SamplingDto topSampling : namedSamplingService.getTopSamplings(namedSampling)) {
						retrieveOrCreateSamplesRec(phase, animal, topSampling, biosamples);
					}
				}

			}
		}

		//Filter Samples by container and sort them
		List<BiosampleDto> sortedSamples = new ArrayList<>();
		for (BiosampleDto biosample : biosamples) {
			if(containerFilter!=null && !containerFilter.equals(biosample.getContainerType())) continue;
			sortedSamples.add(biosample);
		}
		Collections.sort(sortedSamples);

		//Assign Containers
		List<BiosampleDto> sampleToAssignContainers = new ArrayList<>();
		for (BiosampleDto b : sortedSamples) {
			if(b.getContainerType()!=null) sampleToAssignContainers.add(b);
		}
		LoggerFactory.getLogger(BiosampleCreationHelper.class).debug("processTemplateInStudy for "+study+" n="+sortedSamples.size()+" sampleToAssignContainers="+sampleToAssignContainers.size());
		assignContainers(biosamples, sampleToAssignContainers);

		return sortedSamples;
	}
	
	public List<BiosampleDto> processTemplate(PhaseDto phase, NamedSamplingDto ns, Collection<BiosampleDto> parents, boolean generateContainers, boolean isInStudy) throws Exception {

		//Create samples
		List<BiosampleDto> biosamples = new ArrayList<>();
		for (BiosampleDto parent : parents) {
			for (SamplingDto topSampling : samplingService.getTopSamplings(ns.getSamplings())) {
				retrieveOrCreateSamplesRec(phase, parent, topSampling, biosamples);
			}
		}

		//generateContainers
		if(generateContainers) {
			assignContainers(biosamples, biosamples);
		}

		//Remove link to Sampling (we don't want the linkage outside study)
		if(!isInStudy)
			for (BiosampleDto biosample : biosamples) {
				biosample.setAttachedSampling(null);
			}
		return biosamples;
	}
	
	private void retrieveOrCreateSamplesRec(PhaseDto phase, BiosampleDto parent, SamplingDto sampling, List<BiosampleDto> res) {
		boolean isNecropsy = sampling.getNamedSampling()!=null && sampling.getNamedSampling().getNecropsy();

		PhaseDto phaseOfSample;
		if(phase==null) {
			phaseOfSample = parent.getInheritedPhase();
		} else {
			PhaseDto endPhase = isNecropsy? biosampleService.getTerminationPhasePlanned(parent.getTopParent()): null;
			phaseOfSample = isNecropsy && endPhase!=null? endPhase: phase;
		}

		//Find compatible biosamples
		List<BiosampleDto> compatibles = new ArrayList<>();
		if(phase!=null) {
			for (BiosampleDto biosample : parent.getChildren()) {
				if(res.contains(biosample)) continue;
				if(!phaseOfSample.equals(biosample.getInheritedPhase())) continue;
				if(!sampling.equals(biosample.getAttachedSampling())) continue;

				//We found a compatible one
				compatibles.add(biosample);
			}
		}

		if(compatibles.size()==0) {
			//Create a compatible biosample
			BiosampleDto created = samplingService.createCompatibleBiosample(sampling);
			created.setAttachedSampling(sampling);
			biosampleService.setParent(created, parent);
			parent.getChildren().add(created);
			created.setStudy((phase==null || phase.getStage().getStudy() ==null ) ? parent.getStudy() : phase.getStage().getStudy());
			created.setInheritedPhase(phaseOfSample);
			compatibles.add(created);
		}

		BiosampleDto b = compatibles.get(0);
		res.add(b);
		for (SamplingDto s : sampling.getChildren()) {
			retrieveOrCreateSamplesRec(phase, b, s, res);
		}
	}
	
	public void assignContainers(Collection<BiosampleDto> biosamplesWithExistingContainers, Collection<BiosampleDto> biosamplesToAssign) {
		Map<String, Container> key2Containers = new HashMap<>();
		Map<String, String> map2prefix = new HashMap<>();

		//Map existing multiple containers
		for (BiosampleDto b : biosamplesWithExistingContainers) {
			SamplingDto s = b.getAttachedSampling();
			if(s==null || s.getContainerType()==null) continue;

			if(b.getContainer()!=null && b.getContainerId()!=null && b.getContainerType()!=null && b.getContainerType().isMultiple()) {
				//The container is already saved, map it in order to potentially reuse it
				String key = b.getContainerType()+"_"+b.getInheritedPhase() + "_" + b.getTopParent().getSampleId() + "_"+ s.getContainerType() + "_" + s.getLocIndex();
				key2Containers.put(key, b.getContainer());
			}
		}

		//Create new containers
		for (BiosampleDto b : biosamplesToAssign) {
			SamplingDto s = b.getAttachedSampling();
			if(s==null) continue;

			if(s.getContainerType()==null) {
				//No container -> unset
				biosampleService.setContainer(b, null);
			} else if(!s.getContainerType().isMultiple() || s.getLocIndex()==null) {
				//Container but no bloc, assign a new container type
				if(b.getContainerType()!=s.getContainerType()) {
					biosampleService.setContainer(b, new Container(s.getContainerType()));
				}
			} else if(b.getContainer()!=null && b.getContainerType()==s.getContainerType() && CompareUtils.equals(containerService.getBlocNo(b.getContainer()), s.getLocIndex())) {
				//Already done
			} else {
				//The container is a new multiple container, check if we have a container already to add it into.
				//If there is a container, we add it
				//If not, we create it, and add it into

				String key = b.getContainerType()+"_"+b.getInheritedPhase() + "_" + b.getTopParent().getSampleId() + "_"+ s.getContainerType() + "_" + s.getLocIndex();
				Container container = key2Containers.get(key);
				if(container==null) {
					String prefix;
					if(s.getContainerType().getBarcodeType()== BarcodeType.GENERATE) {
						String key2 = b.getContainerType()+"_"+b.getTopParent().getSampleId() + "_" + s.getContainerType();
						prefix = map2prefix.get(key2);
						if(prefix==null) {
							prefix = barcodeService.getNextId(s.getContainerType());
							map2prefix.put(key2, prefix);
						}
					} else {
						prefix = b.getSampleId();
					}
					String containerId = prefix + "-" + s.getLocIndex();
					container = new Container(s.getContainerType(), containerId);
					key2Containers.put(key, container);
				}
				biosampleService.setContainer(b, container);
			}
		}
	}
}
