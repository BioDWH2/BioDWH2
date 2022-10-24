package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class GroupStruct {
    public String title;
    public String description;
    @JacksonXmlProperty(isAttribute = true, localName = "group_id")
    public String groupId;
}
