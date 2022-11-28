package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class EventCountsStruct {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true, localName = "group_id")
    public String groupId;
    @JacksonXmlProperty(isAttribute = true, localName = "subjects_affected")
    public Integer subjectsAffected;
    @JacksonXmlProperty(isAttribute = true, localName = "subjects_at_risk")
    public Integer subjectsAtRisk;
    @JacksonXmlProperty(isAttribute = true)
    public Integer events;
}
