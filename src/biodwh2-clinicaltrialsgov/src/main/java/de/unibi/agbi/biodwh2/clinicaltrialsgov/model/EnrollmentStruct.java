package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class EnrollmentStruct {
    @JacksonXmlText
    public Integer value;
    @JacksonXmlProperty(isAttribute = true)
    public ActualEnum type;
}
