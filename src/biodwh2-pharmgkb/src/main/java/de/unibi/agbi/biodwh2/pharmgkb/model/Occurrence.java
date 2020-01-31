package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Occurrence {
    @Parsed(field = "Source Type")
    public String source_type;
    @Parsed(field = "Source ID")
    public String source_id;
    @Parsed(field = "Source Name")
    public String source_name;
    @Parsed(field = "Object Type")
    public String object_type;
    @Parsed(field = "Object ID")
    public String object_id;
    @Parsed(field = "Object Name")
    public String object_name;
}