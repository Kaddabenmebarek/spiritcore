package com.idorsia.research.spirit.core.service;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actelion.research.business.ppg.formulation.PpgFormulationCompound;
import com.actelion.research.business.ppg.formulation.PpgTreatment;
import com.actelion.research.business.spi.formulation.SPIFormuCompound;
import com.actelion.research.business.spi.formulation.SpiFormuSubstance;
import com.actelion.research.business.spi.formulation.SpiFormuVehicle;
import com.actelion.research.business.spi.formulation.SpiFormulation;
import com.actelion.research.hts.datacenter.restapi.ppg.DAOPpgTreatment;
import com.actelion.research.util.Counter;
import com.actelion.research.util.ui.JExceptionDialog;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.dto.view.Disposal;
import com.idorsia.research.spirit.core.dto.view.Measurement;
import com.idorsia.research.spirit.core.dto.view.StudyAction;
import com.idorsia.research.spirit.core.dto.view.StudyActionResult;
import com.idorsia.research.spirit.core.dto.view.StudyRemoval;
import com.idorsia.research.spirit.core.dto.view.SubjectSet;
import com.idorsia.research.spirit.core.model.Study;
import com.idorsia.research.spirit.core.util.MiscUtils;

@Service
public class StudyActionService extends AbstractService implements Serializable {
	
	private static final long serialVersionUID = 7267351416006157223L;
	@Autowired
	NamedSamplingService namedSamplingService;
	@Autowired
	AssignmentService assignmentService;
	@Autowired
	AssayService assayService;
	@Autowired
	NamedTreatmentService namedTreatmentService;
	@Autowired
	StageService stageService;
	@Autowired
	ActionPatternsService actionPatternsService;
	
    public String getDescription(StudyAction studyAction) {
		switch (studyAction.getType()) {
		case DISEASE:
		case THERAPY:
			return getDescription((NamedTreatmentDto)studyAction);
		case DISPOSAL :
			return getDescription((Disposal)studyAction);
		case GROUPASSIGN :
			return getDescription((StageDto)studyAction);
		case MEASUREMENT :
			return getDescription((Measurement)studyAction);
		case REMOVE :
			return getDescription((StudyRemoval)studyAction);
		case SAMPLING :
			return getDescription((NamedSamplingDto)studyAction);
		default:
			break;
		};
		return null;
	}

	public String getDescription(Disposal disposal) {
		 return disposal.getSampling() == null ? "" : "(" + getDescription(disposal.getSampling()) + ")";
	}
	
	public String getDescription(StageDto stage) {
		return "Group Assignment for "+stage.getName();
	}
	
	public String getDescription(StudyRemoval studyRemoval) {
		return "Remove from study " + studyRemoval.getAssignment().getStage().getStudy().getStudyId();
	}
	
	public String getDescription(NamedSamplingDto namedSampling) {
		Counter<BiotypeDto> counter = new Counter<>();
		for(SamplingDto s: namedSampling.getSamplings()) {
			counter.increaseCounter(s.getBiotype());
		}
		StringBuilder sb = new StringBuilder();
		for (BiotypeDto b : counter.getKeys()) {
			if(sb.length()>0) sb.append(", ");
			sb.append(counter.getCount(b) + " "+b.getName());
		}
		return sb.toString();
	}
	
	public String getDescription(NamedTreatmentDto nt) {
		String result = "";
		if (nt.getSurgery() != null) result += nt.getSurgery() + " ";
		PpgTreatment ppgTreatment = nt.getPpgTreatment();
		if (ppgTreatment != null) {
			if (!ppgTreatment.isVehicle())
			for (PpgFormulationCompound p: ppgTreatment.getCompounds()) {
				result += " " + namedTreatmentService.getPpgCompoundDescription(p, ppgTreatment, nt.getStudy()) + " ";
			}
			if (ppgTreatment.getVehicle() != null) result += ppgTreatment.getVehicle().getName() + " " + ppgTreatment.getVolume() + (ppgTreatment.getVolumeUnit() == null ? "" : " " + ppgTreatment.getVolumeUnit());
		}
		SpiFormulation spiTreatment = nt.getSpiTreatment();
		if (spiTreatment != null) {
			try {
				if (spiTreatment.getFormulation_substances() != null)
					for (SpiFormuSubstance p : spiTreatment.getFormulation_substances()) {
						result += " " + namedTreatmentService.getSpiSubstanceDescription(p) + " ";
					}
				if (spiTreatment.getFormulation_compounds() != null)
					for (SPIFormuCompound p : spiTreatment.getFormulation_compounds()) {
						result += " " + namedTreatmentService.getSpiCompoundDescription(p) + " ";
					}
				if (spiTreatment.getFormulation_vehicles() != null) {
					if (spiTreatment.getFormulation_substances().isEmpty() && !spiTreatment.getFormulation_vehicles().isEmpty() && spiTreatment.getFormulation_vehicles().size() == 1) {
						result += spiTreatment.getFormulation_vehicles().get(0).getVehicle().getName() + " " + spiTreatment.getTotal_formulation_amount() + " " + spiTreatment.getTotal_formulation_unit();
					}else{
						for (SpiFormuVehicle p : spiTreatment.getFormulation_vehicles()) {
							result += p.getVehicle().getName() + " " + p.getConcentration_amount() + " " + p.getConcentration_unit();
						}

					}
				}

			}catch (Exception e) {
				JExceptionDialog.showError(e);
				e.printStackTrace();
			}
		}
		if ("".equals(result)) result = null;
		return result;
	}
	
    public Set<StudyActionResult> getActionResults(Disposal disposal, SubjectSet subjectSet, Study study, StageDto stage, Collection<PhaseDto> phases) {
        Set<StudyActionResult> samples = new HashSet<>();
        if (disposal.getSampling() != null) samples.addAll(namedSamplingService.getActionResults(disposal.getSampling(), subjectSet, study, stage, phases));
        return null;
    }
    
    public Color getColor(StudyRemoval studyRemoval) {
		return assignmentService.getGroup(studyRemoval.getAssignment()).getColor();
	}
    
    public static Set<Integer> getTestIds(Collection<Measurement> col) {
		Set<Integer> ids = new HashSet<>();
		for (Measurement m : col) {
			ids.add(m.getTestId());
		}
		return ids;
	}
    
    public boolean isMeasureWeight(Measurement m) {
		return m.getAssay() != null && Constants.WEIGHING_TESTNAME.equals(m.getAssay().getName());
	}

	public boolean isMeasureFood(Measurement m) {
		return m.getAssay() != null && Constants.FOODWATER_TESTNAME.equals(m.getAssay().getName());
	}

	public boolean isMeasureWater(Measurement m) {
		return m.getAssay() != null && Constants.FOODWATER_TESTNAME.equals(m.getAssay().getName());
	}
	
	public String getMeasurementAbbreviations(Measurement m) {
		AssayDto t = m.getAssay();
		if (isMeasureFood(m)) {
			return "F";
		}
		if (isMeasureWater(m)) {
			return "O";
		}
		if (isMeasureWeight(m)) {
			return "w";
		}
		return t==null || t.getName()==null || t.getName().length()==0? "M" : t.getName().substring(0, 1);
	}
	
	public Set<AssayDto> getTests(Collection<Measurement> col) {
		Set<AssayDto> ids = new HashSet<>();
		for (Measurement m : col) {
			if(m.getAssay()!=null) ids.add(m.getAssay());
		}
		return ids;
	}
	
	public String getDescription(Measurement m) {
		String params=getParametersString(m);
		return (m.getAssay()==null?"?": m.getAssay().getName()) +  (params==null || params.length()==0? "": " (" + params +")" );
	}
	
	public String getParametersString(Measurement m) {
		return MiscUtils.unsplit(m.getParameters(), ", ");
	}
	
	public PpgTreatment getPpgTreatment(NamedTreatmentDto nt) {
		List<PpgTreatment> treatments;
		try {
			treatments = DAOPpgTreatment.getTreatmentsByIds(Collections.singletonList(nt.getPpgtreatmentInstanceId()));
			if(treatments.size()>0) {
				return treatments.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StudyAction map(Integer id, String actionType, String parameters, Integer stageId) {
		StudyAction action = null;
		if(actionType != null) {
			switch (actionType) {
	            case Constants.DISEASE:
	            case Constants.THERAPY:
	            	action = namedTreatmentService.getNamedTreatmentDto(id);
	                break;
	            case Constants.MEASUREMENT:
	                Measurement measurement = new Measurement(assayService.getAssayDto(id), MiscUtils.deserializeStrings(parameters).toArray(new String[0]));
	                action = measurement;
	                break;
	            case Constants.SAMPLING:
	            	action = namedSamplingService.getNamedSamplingDto(id);
	                break;
	            case Constants.DISPOSAL:
	            	action = new Disposal(id!=null && id!=0?namedSamplingService.getNamedSamplingDto(id):null);
	                break;
	            case Constants.GROUPASSIGN:
	            	action = stageService.getStageDto(stageId);
	                break;
	            default:
					break;
			};
		}
		return action;
	}
	
	public Integer getSequence(String actionSequenceName) {
		return null;
	}

	@Transactional
	public void save(StudyAction action) throws Exception {
		save(action, false);
	}
	
	public void save(StudyAction action, boolean cross) throws Exception {
		try{
			if(action != null) {
				switch (action.getType()) {
		            case DISEASE:
		            case THERAPY:
		            	namedTreatmentService.save((NamedTreatmentDto)action, true);
		                break;
		            case MEASUREMENT:
		            	new Measurement((Measurement) action);
		                break;
		            case SAMPLING:
		            	namedSamplingService.save((NamedSamplingDto)action, true);
		                break;
		            case DISPOSAL:
		            	if(((Disposal)action).getSampling()!=null)
		            		namedSamplingService.save(((Disposal)action).getSampling(), true);
		                break;
		            case GROUPASSIGN:
		            	stageService.save((StageDto)action, true);
		                break;
		            default:
						break;
				};
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

}
