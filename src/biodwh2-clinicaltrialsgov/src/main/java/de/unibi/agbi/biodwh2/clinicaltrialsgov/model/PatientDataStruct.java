package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class PatientDataStruct {
    @JsonProperty(value = "sharing_ipd", required = true)
    public String sharingIpd;
    @JsonProperty(value = "ipd_description")
    public String ipdDescription;
    @JsonProperty(value = "ipd_info_type")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> ipdInfoType;
    @JsonProperty(value = "ipd_time_frame")
    public String ipdTimeFrame;
    @JsonProperty(value = "ipd_access_criteria")
    public String ipdAccessCriteria;
    @JsonProperty(value = "ipd_url")
    public String ipdUrl;
}
