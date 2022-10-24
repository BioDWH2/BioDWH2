package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpandedAccessInfoStruct {
    @JsonProperty(value = "expanded_access_type_individual")
    public YesNoEnum expandedAccessTypeIndividual;
    @JsonProperty(value = "expanded_access_type_intermediate")
    public YesNoEnum expandedAccessTypeIntermediate;
    @JsonProperty(value = "expanded_access_type_treatment")
    public YesNoEnum expandedAccessTypeTreatment;
}
