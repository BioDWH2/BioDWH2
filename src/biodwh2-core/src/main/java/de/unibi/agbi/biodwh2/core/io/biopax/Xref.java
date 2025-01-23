package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class Xref extends UtilityClass {
    @GraphProperty("db")
    public String db;
    @GraphProperty("db_version")
    public String dbVersion;
    @GraphProperty("id")
    public String id;
    @GraphProperty("id_version")
    public String idVersion;
}
