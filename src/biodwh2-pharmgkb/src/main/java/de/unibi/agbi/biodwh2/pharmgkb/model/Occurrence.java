package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("Occurrence")
public class Occurrence {
    @Parsed(field = "Source Type")
    @GraphProperty("source_type")
    public String sourceType;
    @Parsed(field = "Source ID")
    @GraphProperty("source_id")
    public String sourceId;
    @Parsed(field = "Source Name")
    @GraphProperty("source_name")
    public String sourceName;
    @Parsed(field = "Object Type")
    @GraphProperty("object_type")
    public String objectType;
    @Parsed(field = "Object ID")
    @GraphProperty("object_id")
    public String objectId;
    @Parsed(field = "Object Name")
    @GraphProperty("object_name")
    public String objectName;
}