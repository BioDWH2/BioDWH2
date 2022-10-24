package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.math.BigInteger;

public class EventCountsStruct {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true, localName = "group_id")
    public String groupId;
    @JacksonXmlProperty(isAttribute = true, localName = "subjects_affected")
    public BigInteger subjectsAffected;
    @JacksonXmlProperty(isAttribute = true, localName = "subjects_at_risk")
    public BigInteger subjectsAtRisk;
    @JacksonXmlProperty(isAttribute = true)
    public BigInteger events;
}
