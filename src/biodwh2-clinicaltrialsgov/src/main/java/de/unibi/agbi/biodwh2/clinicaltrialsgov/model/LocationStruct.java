package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class LocationStruct {
    public FacilityStruct facility;
    public String status;
    public ContactStruct contact;
    @JsonProperty(value = "contact_backup")
    public ContactStruct contactBackup;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<InvestigatorStruct> investigator;
}
