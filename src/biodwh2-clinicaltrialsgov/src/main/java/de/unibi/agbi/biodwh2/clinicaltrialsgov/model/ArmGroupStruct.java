package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArmGroupStruct {
    @JsonProperty(value = "arm_group_label", required = true)
    public String armGroupLabel;
    @JsonProperty(value = "arm_group_type")
    public String armGroupType;
    public String description;
}
