package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class StudyDocsStruct {
    @JsonProperty(value = "study_doc", required = true)
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<StudyDocStruct> studyDoc;
}
