package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Concentration {
    public String biospecimen;
    @JsonProperty("concentration_value")
    public String concentrationValue;
    @JsonProperty("concentration_units")
    public String concentrationUnits;
    @JsonProperty("subject_age")
    public String subjectAge;
    @JsonProperty("subject_sex")
    public String subjectSex;
    @JsonProperty("subject_condition")
    public String subjectCondition;
    @JsonProperty("patient_age")
    public String patientAge;
    @JsonProperty("patient_sex")
    public String patientSex;
    @JsonProperty("patient_information")
    public String patientInformation;
    public String comment;
    @JacksonXmlElementWrapper(localName = "references")
    public List<Reference> references;
}
