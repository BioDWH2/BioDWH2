package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

import java.util.List;

@GraphNodeLabel("Concentration")
public class Concentration {
    @GraphProperty("biospecimen")
    public String biospecimen;
    @JsonProperty("concentration_value")
    @GraphProperty("concentration_value")
    public String concentrationValue;
    @JsonProperty("concentration_units")
    @GraphProperty("concentration_units")
    public String concentrationUnits;
    @JsonProperty("subject_age")
    @GraphProperty("subject_age")
    public String subjectAge;
    @JsonProperty("subject_sex")
    @GraphProperty("subject_sex")
    public String subjectSex;
    @JsonProperty("subject_condition")
    @GraphProperty("subject_condition")
    public String subjectCondition;
    @JsonProperty("patient_age")
    @GraphProperty("patient_age")
    public String patientAge;
    @JsonProperty("patient_sex")
    @GraphProperty("patient_sex")
    public String patientSex;
    @JsonProperty("patient_information")
    @GraphProperty("patient_information")
    public String patientInformation;
    @GraphProperty("comment")
    public String comment;
    @JacksonXmlElementWrapper(localName = "references")
    public List<Reference> references;
}
