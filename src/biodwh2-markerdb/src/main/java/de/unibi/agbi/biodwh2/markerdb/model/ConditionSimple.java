package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionSimple {
    @JsonProperty("biomarker_type")
    public String biomarkerType;
    public String conditions;
    @JsonProperty("indication_types")
    public String indicationTypes;
    // The following are only included in ChemicalSimple and ProteinSimple
    public String concentration;
    public String age;
    public String sex;
    public String biofluid;
    public String citation;
}
