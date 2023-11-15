package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.actelion.research.business.ppg.formulation.PpgAnimal;
import com.actelion.research.business.ppg.formulation.PpgFormulation;
import com.actelion.research.business.ppg.formulation.PpgFormulationCompound;
import com.actelion.research.business.ppg.formulation.PpgFormulationRequest;
import com.actelion.research.business.ppg.formulation.PpgLabJournalReference;
import com.actelion.research.business.ppg.formulation.PpgTreatment;
import com.actelion.research.hts.datacenter.restapi.DAORest;
import com.actelion.research.hts.datacenter.restapi.ppg.DAOPpgTreatment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.StudyDto;
import com.idorsia.research.spirit.core.model.Administration;
import com.idorsia.research.uom.IdorsiaUnits;
import com.idorsia.research.uom.UnitSymbols;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

@Service
public class PpgTreatmentService implements Serializable {
	
	private static final long serialVersionUID = 49335823298347782L;
	@Autowired
	ActionPatternsService actionPatternService;
	@Autowired
	BiosampleService biosampleService;
	@Autowired
	StudyActionService studyActionService;

	public List<PpgTreatment> getTreatments(PpgTreatment ppgTreatment, boolean full) throws Exception {
        boolean enoughCriteria = true;
        List<String> criterias = new ArrayList<>();
        if (!full) {
            if (ppgTreatment.isVehicleAvailable() && ppgTreatment.getVehicle() == null) {
                enoughCriteria = false;
            }
            if (!ppgTreatment.isVehicle()) {
                for (PpgFormulationCompound compound : ppgTreatment.getCompounds()) {
                    if (compound.getActNumber() != null) {
                        criterias.add("act_no="+compound.getActNumber());
                        enoughCriteria = true;
                        break;
                    }
                    if (ppgTreatment.isDoseAvailable(true)) {
                    }
                    if (ppgTreatment.isVolumeAvailable(true)) {
                    }
                    if (ppgTreatment.isConcentrationAvailable(true)) {
                    }

                }
            }
        }
        if (full || enoughCriteria) {
            List<PpgTreatment> res = new ArrayList<>();
            try {
                List<PpgTreatment> treatments = DAOPpgTreatment.getTreatments(ppgTreatment);
                HashSet<PpgTreatment> treatmentsStripped = new HashSet<>();
                for (PpgTreatment treatment : treatments) {
                    treatment.setTreatmentInstanceId(-1);
                    if (!treatment.isVehicle()) {
                        for (PpgFormulationCompound compound : treatment.getCompounds()) {
                            compound.setContextId(-1);
                        }
                    }
                    treatmentsStripped.add(treatment);
                }
                res.addAll(new ArrayList<>(treatmentsStripped));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }
        return null;
    }

    public Set<Integer> getTreatmentInstanceIds(PpgTreatment ppgTreatment) throws Exception {
        Set<Integer> treatmentInstanceIds = new HashSet<>();
        boolean enoughCriteria = true;
        List<String> criterias = new ArrayList<>();

                for (PpgFormulationCompound compound : ppgTreatment.getCompounds()) {
                    if (compound.getActNumber() != null) {
                        criterias.add("act_no="+compound.getActNumber());
                        enoughCriteria = true;
                        break;
                    }
                }

        if (enoughCriteria) {
            try {
                List<PpgTreatment> treatments = DAOPpgTreatment.getTreatments(ppgTreatment);
                for (PpgTreatment treatment : treatments) {
                    treatmentInstanceIds.add(treatment.getTreatmentInstanceId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return treatmentInstanceIds;
    }


    public PpgTreatment getTreatment(int id) {
        PpgTreatment ppgTreatment = null;
        try {
            ppgTreatment = DAOPpgTreatment.getTreatment(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ppgTreatment;
    }

    public Quantity<?> getCalculatedAmount(int treatmentInstanceId, Double weight) {
        IdorsiaUnits idorsiaUnits = IdorsiaUnits.getInstance();
       	ComparableQuantity<?> quantity = weight==null ? null : Quantities.getQuantity(weight, (Unit<?>) idorsiaUnits.getUnit(UnitSymbols.G));
        Quantity<?> result = null;
        try {
            result = DAOPpgTreatment.getPosologyForTreatment(quantity, treatmentInstanceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public PpgTreatment addOrUpdateTreatment(PpgTreatment treatment, String username) throws Exception {
        Integer i = Constants.NEWTRANSIENTID;
        treatment.setPrototype(true);
        try {
            ResponseEntity<Integer> respone = DAOPpgTreatment.addOrUpdateTreatment(treatment, username, "spiritcore");
            i =  respone.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PpgTreatment treatment1 = getTreatment(i);
        if (i.intValue() <= Constants.NEWTRANSIENTID || treatment1 == null) throw new Exception("The treatment "+treatment+" couldn't be saved");
        return treatment1;
    }

    public List<Administration> persistAdministration(Collection<Administration> administrations, String user) throws Exception {
        List<Administration> res = new ArrayList<>();
        if(administrations==null || administrations.size()==0) return res;
        res = persistAdministrations(administrations, user);
        return res;
    }

    public List<Administration> persistAdministrations(Collection<Administration> administrations, String user) throws Exception {
        List<Administration> res = new ArrayList<>();
        for (Administration administration : administrations) {
            res.add(administration);
        }
        return res;
    }
    
    public String createRequestJSON(StudyDto study, List<NamedTreatmentDto> selectedValuesList, Collection<String> elns) {
        PpgFormulationRequest request = new PpgFormulationRequest();

        List<PpgLabJournalReference> labJournals = new ArrayList<>();
        for (String eln : elns) {
            labJournals.add(new PpgLabJournalReference(0, eln));

        }
        request.setLabJournals(labJournals);
        request.setStudyNumber(study.getStudyId());

        List<PpgFormulation> formulations = new ArrayList<>();
        for (NamedTreatmentDto namedTreatment : selectedValuesList) {
			PpgFormulation formulation = new PpgFormulation(studyActionService.getPpgTreatment(namedTreatment));
            HashSet<Integer> strainIds = new HashSet<>();
            formulation.setGender(null);
            for (ActionPatternsDto studyActionTreatment : actionPatternService.getStudyActionTreatment(actionPatternService.getByStudy(study.getId()))){
                if (namedTreatment.equals(studyActionTreatment.getAction())) {
                    for (BiosampleDto biosample : biosampleService.map(biosampleService.getByStudy(study.getId()))) {
                        if (biosample != null && Constants.ANIMAL.equals(biosample.getBiotype().getName())) {
                            Integer strainSample = biosampleService.getStrainId(biosample);
                            strainIds.add(strainSample);
                            String sex = biosampleService.getMetadataValue(biosample, "Sex");
                            if (formulation.getGender() == null) formulation.setGender(sex);
                            else if (sex != null && !formulation.getGender().equals(sex)) formulation.setGender("B");
                        }
                    }
                }
            }
            strainIds.remove(null);

            Set<PpgAnimal> animals = new HashSet<>();
            strainIds.forEach(strain -> animals.add(new PpgAnimal(strain)));
            formulation.setAnimalSet(animals);
            formulations.add(formulation);
        }
        request.setFormulations(formulations);

        String jsonRequest = null;
        try {
            jsonRequest = DAORest.mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonRequest;
    }
}
