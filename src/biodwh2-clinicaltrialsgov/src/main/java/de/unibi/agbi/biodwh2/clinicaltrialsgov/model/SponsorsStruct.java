package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class SponsorsStruct {
    @JsonProperty(value = "lead_sponsor", required = true)
    public SponsorStruct leadSponsor;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<SponsorStruct> collaborator;
}
