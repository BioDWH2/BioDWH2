package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class MeasureCountStruct {
    @JacksonXmlText
    public String valueText;
    @JacksonXmlProperty(isAttribute = true, localName = "group_id")
    public String groupId;
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    public String valueAttr;
}
