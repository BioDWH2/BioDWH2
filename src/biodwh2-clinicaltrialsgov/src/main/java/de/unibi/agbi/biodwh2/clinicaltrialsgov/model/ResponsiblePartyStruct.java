package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponsiblePartyStruct {
    @JsonProperty(value = "name_title")
    public String nameTitle;
    public String organization;
    @JsonProperty(value = "responsible_party_type")
    public ResponsiblePartyTypeEnum responsiblePartyType;
    @JsonProperty(value = "investigator_affiliation")
    public String investigatorAffiliation;
    @JsonProperty(value = "investigator_full_name")
    public String investigatorFullName;
    @JsonProperty(value = "investigator_title")
    public String investigatorTitle;
}
