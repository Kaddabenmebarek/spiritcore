package com.idorsia.research.spirit.core.dto.view;

import java.util.Set;

import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;

public interface SubjectSet {
	Set<BiotypeMetadataValueDto> getMetadatas();
    Set<ActionPatternsDto> getActionDefinitionPattern();
    void addMetadata(BiotypeMetadataValueDto metaDefinition);
    void removeMetadata(BiotypeMetadataValueDto metaDefinition);
    boolean addActionDefinition(ActionPatternsDto actionDefinition);
    boolean removeActionDefinition(ActionPatternsDto actionDefinition);
    Set<BiosampleDto> getSubjects();
}
