package de.unibi.agbi.biodwh2.hprd.model;

import java.util.HashMap;
import java.util.Map;

public class Gene {
    public final String id;
    public String name;
    public String symbol;
    public Integer entrezGeneId;
    public Integer omimId;
    public String[] swissProtIds;
    public final Map<String, Transcript> transcripts = new HashMap<>();
    public Long nodeId;

    public Gene(final String id) {
        this.id = id;
    }
}
