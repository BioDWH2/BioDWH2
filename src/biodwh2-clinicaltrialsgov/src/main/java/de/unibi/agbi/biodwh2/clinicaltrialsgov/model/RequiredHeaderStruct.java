package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequiredHeaderStruct {
    @JsonProperty(value = "download_date", required = true)
    public String downloadDate;
    @JsonProperty(value = "link_text", required = true)
    public String linkText;
    @JsonProperty(required = true)
    public String url;
}
