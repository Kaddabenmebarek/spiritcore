package com.idorsia.research.spirit.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actelion.research.util.ui.JExceptionDialog;
import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.FoodWaterDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.view.BiosampleQuery;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.dto.view.StudyActionResultQuery;
import com.idorsia.research.spirit.core.service.AssayResultService;
import com.idorsia.research.spirit.core.service.BiosampleCreationHelper;
import com.idorsia.research.spirit.core.service.BiosampleService;
import com.idorsia.research.spirit.core.service.SamplingService;
import com.idorsia.research.spirit.core.service.StudyActionResultService;

public class SpiritChangeHelper {
    private static SpiritChangeHelper instance ;

    private Set<StudyActionResult> resultsToDelete = new HashSet<>();
    private Set<StudyActionResult> resultsToTransform = new HashSet<>();
    private Set<StudyActionResult> resultsToAdd = new HashSet<>();
    private Set<StudyActionResult> resultsToQuestion = new HashSet<>();
    private Set<FoodWaterDto> fws = new HashSet<>();
    private Set<AssayResultDto> resultFromRemoveStudyToDelete = new HashSet<>();
    private Set<BiosampleDto> samplesToDelete = new HashSet<>();
    private Map<String, Set<StudyActionResultQuery>> levelsToCheck = new HashMap<>();
    private boolean samplesChecked = false;
    private Map<SamplingDto, SamplingDto> samplingToSynch = new HashMap<>();
    
    private StudyActionResultService studyActionResultService=(StudyActionResultService) ContextShare.getContext().getBean("studyActionResultService");
    private AssayResultService assayResultService=(AssayResultService) ContextShare.getContext().getBean("assayResultService");
    private BiosampleCreationHelper biosampleCreationHelper=(BiosampleCreationHelper) ContextShare.getContext().getBean("biosampleCreationHelper");
    private SamplingService samplingService = (SamplingService) ContextShare.getContext().getBean("samplingService");
    private BiosampleService biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");

    private SpiritChangeHelper() {

    }
    public static SpiritChangeHelper getInstance() {
        if (instance == null) instance = new SpiritChangeHelper();
        return instance;
    }

    public void clear() {
        clearSets();
        levelsToCheck.clear();
    }
    private void clearSets() {
        resultsToAdd.clear();
        resultsToDelete.clear();
        resultsToTransform.clear();
        resultsToDelete.addAll(samplesToDelete);
    }

    public void executeChanges() throws Exception {
    	performChecks();
        studyActionResultService.persistStudyActionResult(resultsToTransform);
        studyActionResultService.persistStudyActionResult(resultsToAdd);
        resultsToDelete.addAll(resultFromRemoveStudyToDelete);
        studyActionResultService.deleteStudyActionResult(resultsToDelete);
        clear();
    }

    public Set<StudyActionResult> getResultsToAdd() {
        return resultsToAdd;
    }

    public Set<StudyActionResult> getResultsToQuestion() {
        return resultsToQuestion;
    }

    public Set<StudyActionResult> getResultsToDelete() {
        return resultsToDelete;
    }
    public Set<StudyActionResult> getResultsToTransform() {
        return resultsToTransform;
    }

    public Map<String, Set<StudyActionResultQuery>> getLevelsToCheck() {
        return levelsToCheck;
    }

    public Set<FoodWaterDto> getFoodWater() {
		return fws;
	}
    
    public void addFoodWater(FoodWaterDto fw) {
    	fws.add(fw);
    }
    
    public Set<AssayResultDto> getResultFromRemoveStudyToDelete() {
		return resultFromRemoveStudyToDelete;
	}
    
    public void addResultFromRemoveStudyToDelete(AssayResultDto fw) {
    	resultFromRemoveStudyToDelete.add(fw);
    }

    public void addLevelToCheck(String key, StudyActionResultQuery query) {
        Set<StudyActionResultQuery> queries = levelsToCheck.get(key);
        if (queries==null) queries = new HashSet<>();
        queries.add(query);
        levelsToCheck.put(key, queries);
    }
    public void performChecks() {
        clearSets();
        samplesChecked = false;
        for(String key : levelsToCheck.keySet()) {
            Set<StudyActionResultQuery> queries = levelsToCheck.get(key);
            for (StudyActionResultQuery query : queries) {
                switch (key) {
                    case Constants.STUDY:
                        checkStudy(query);
                        break;
                    case Constants.END_PHASE:
                        checkEndPhase(query);
                        break;
                    case Constants.GROUP:
                        checkGroup(query);
                        break;
                    case Constants.GROUP_SIZE:
                        checkGroupSize(query);
                        break;
                    case Constants.SCHEDULE:
                        checkSchedule(query);
                        break;
                    default:
                        break;
                }

            }
        }
    }
    
    public List<BiosampleDto> getSynchronizedSamplesFromSampling() throws Exception {
		List<BiosampleDto> samplesToUpdate = new ArrayList<>();
    	//Check that there are no samples coming from this sampling
    	for(SamplingDto fromSampling : samplingToSynch.keySet()) {
    		SamplingDto toSampling = samplingToSynch.get(fromSampling);
	    	Set<BiosampleDto> samples = toSampling.getSamples();
			List<BiosampleDto> samplesToKeep = new ArrayList<>();
			if(samples.size()>0) {
				//Check that samples have not been modified
				for (BiosampleDto b : samples) {
					double score = samplingService.getMatchingScore(fromSampling, b);
					if(score<1) samplesToKeep.add(b);
					else samplesToUpdate.add(b);
				}
			}
			samplesToUpdate.addAll(samplesToKeep);
    	}
    	return samplesToUpdate;
    }

    public boolean synchronizedSamplesFromSampling() throws Exception {
    	for(SamplingDto fromSampling : samplingToSynch.keySet()) {
    		SamplingDto toSampling = samplingToSynch.get(fromSampling);
	    	Set<BiosampleDto> samples = toSampling.getSamples();
			List<BiosampleDto> samplesToUpdate = new ArrayList<>();
			List<BiosampleDto> samplesToKeep = new ArrayList<>();
			if(samples.size()>0) {
				//Check that samples have not been modified
				for (BiosampleDto b : samples) {
					double score = samplingService.getMatchingScore(fromSampling, b);
					if(score<1) samplesToKeep.add(b);
					else samplesToUpdate.add(b);
				}
			}
			samplesToUpdate.addAll(samplesToKeep);
			//Update the samples
			for (BiosampleDto b : samplesToUpdate) {
				if(!b.getBiotype().equals(toSampling.getBiotype())) throw new Exception("The biotype cannot be changed");
				samplingService.populate(toSampling, b);
			}

			//Update the containers
			BiosampleQuery q = new BiosampleQuery();
			q.setStudyIds(fromSampling.getNamedSampling().getStudy().getStudyId());
			q.setFilterNotInContainer(true);

			List<BiosampleDto> pool = biosampleService.queryBiosamples(q, null);
			pool.removeAll(samplesToUpdate);
			biosampleCreationHelper.assignContainers(pool, samplesToUpdate);
		}
		return true;
	}

    private void checkStudy(StudyActionResultQuery query){
        checkSampleChanges(query);
    }
    private void checkEndPhase(StudyActionResultQuery query) {
        checkSampleChanges(query);
    }
    private void checkGroup(StudyActionResultQuery query){
        checkSampleChanges(query);
    }
    private void checkGroupSize(StudyActionResultQuery query){
        checkSampleChanges(query);
    }
    private void checkSchedule(StudyActionResultQuery query){
        checkSampleChanges(query);
    }

    private void checkSampleChanges(StudyActionResultQuery query) {
        if (samplesChecked || query.getStudy()==null) return;
        List<BiosampleDto> toAdd = new ArrayList<>();
        List<BiosampleDto> toUpdate = new ArrayList<>();
        List<BiosampleDto> toQuestion = new ArrayList<>();
        List<BiosampleDto> toDelete = new ArrayList<>();

        try {
            biosampleCreationHelper.analyse(query.getStudy(), toAdd, toUpdate,toQuestion,toDelete);
            assayResultService.attachOrCreateStudyResultsToSamples(query.getStudy(),toDelete, null, null);
            assayResultService.attachOrCreateStudyResultsToSamples(query.getStudy(),toUpdate, null, null);
            assayResultService.attachOrCreateStudyResultsToSamples(query.getStudy(),toQuestion, null, null);

            resultsToAdd.addAll(toAdd);
            toUpdate.forEach(b->resultsToTransform.addAll(b.getResults()));
            resultsToTransform.addAll(toUpdate);
            toQuestion.forEach(b->resultsToQuestion.addAll(b.getResults()));
            resultsToQuestion.addAll(toQuestion);
            toDelete.forEach(b->resultsToDelete.addAll(b.getResults()));
            resultsToDelete.addAll(toDelete);
            samplesChecked = true;
        } catch (Exception e) {
            JExceptionDialog.showError(e);
            e.printStackTrace();
        }
    }

	public Set<BiosampleDto> getSamplesToDelete() {
		return samplesToDelete;
	}
	public void clearFoodWater() {
		fws.clear();
	}
	public void clearSamplingToSynch() {
		samplingToSynch.clear();
	}
	public void clearResultFromRemoveStudyToDelete() {
		resultFromRemoveStudyToDelete.clear();
	}
	public void addToSynchro(SamplingDto fromSampling, SamplingDto sampling) {
		samplingToSynch.put(fromSampling, sampling);
	}

}
