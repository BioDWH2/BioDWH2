package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Xref extends UtilityClass {
    @JsonProperty("ID")
    public String xrefId;
    public String db;
    public String dbVersion;
    public String id;
    public String idVersion;
}
