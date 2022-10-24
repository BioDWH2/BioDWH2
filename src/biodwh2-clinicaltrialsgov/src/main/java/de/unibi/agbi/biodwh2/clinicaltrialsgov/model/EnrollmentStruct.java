package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.math.BigInteger;

public class EnrollmentStruct {
    @JacksonXmlText
    public BigInteger value;
    @JacksonXmlProperty(isAttribute = true)
    public ActualEnum type;
}
