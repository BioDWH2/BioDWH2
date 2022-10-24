package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class IdInfoStruct {
    @JsonProperty(value = "org_study_id")
    public String orgStudyId;
    @JsonProperty(value = "secondary_id")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> secondaryId;
    @JsonProperty(value = "nct_id", required = true)
    public String nctId;
    @JsonProperty(value = "nct_alias")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> nctAlias;
}
