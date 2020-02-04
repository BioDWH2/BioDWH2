package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Occurrence {
    @Parsed(field = "Source Type")
    public String sourceType;
    @Parsed(field = "Source ID")
    public String sourceId;
    @Parsed(field = "Source Name")
    public String sourceName;
    @Parsed(field = "Object Type")
    public String objectType;
    @Parsed(field = "Object ID")
    public String objectId;
    @Parsed(field = "Object Name")
    public String objectName;
}