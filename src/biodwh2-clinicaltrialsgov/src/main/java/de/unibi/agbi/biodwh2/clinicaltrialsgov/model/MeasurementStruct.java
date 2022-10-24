package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class MeasurementStruct {
    @JacksonXmlText
    public String valueText;
    @JacksonXmlProperty(isAttribute = true, localName = "group_id")
    public String groupId;
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    public String valueAttr;
    @JacksonXmlProperty(isAttribute = true, localName = "spread")
    public String spread;
    @JacksonXmlProperty(isAttribute = true, localName = "lower_limit")
    public String lowerLimit;
    @JacksonXmlProperty(isAttribute = true, localName = "upper_limit")
    public String upperLimit;
}
