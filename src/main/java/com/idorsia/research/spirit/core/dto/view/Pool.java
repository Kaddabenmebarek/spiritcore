package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;

public class Pool implements SubjectSet, Serializable {
	
	private static final long serialVersionUID = 222008852845675069L;
	private Set<BiosampleDto> samples = new HashSet<>();

    public Pool() {
    }

    public Pool(Set<BiosampleDto> samples) {
        addBiosamples(samples);
    }

    public void addBiosample(BiosampleDto biosample) {
        if (biosample == null) return;
        samples.add(biosample);
    }

    public void addBiosamples(Collection<BiosampleDto> biosamples) {
        samples.addAll(biosamples);
        samples.remove(null);
    }

    @Override
    public Set<BiotypeMetadataValueDto> getMetadatas() {
        return null;
    }

    @Override
    public Set<ActionPatternsDto> getActionDefinitionPattern() {
        return null;
    }
    
    @Override
    public void addMetadata(BiotypeMetadataValueDto metaDefinition) {

    }

    @Override
    public void removeMetadata(BiotypeMetadataValueDto metaDefinition) {

    }

    @Override
    public boolean addActionDefinition(ActionPatternsDto actionDefinition) {
        return false;
    }

    @Override
    public boolean removeActionDefinition(ActionPatternsDto actionDefinition) {
        return false;
    }

    @Override
    public Set<BiosampleDto> getSubjects() {
        return new HashSet<>(samples);
    }

}
