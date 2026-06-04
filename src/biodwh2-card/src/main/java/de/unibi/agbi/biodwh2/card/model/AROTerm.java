package de.unibi.agbi.biodwh2.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class AROTerm {
    @JsonProperty("id")
    public String id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("namespace")
    public String namespace;

    @JsonProperty("def")
    public String def;

    @JsonProperty("category_aro_accession")
    public String categoryAroAccession;

    @JsonProperty("is_a")
    public final List<String> isA = new ArrayList<>();

    @JsonProperty("synonyms")
    public final List<String> synonyms = new ArrayList<>();

    @JsonProperty("xrefs")
    public final List<String> xrefs = new ArrayList<>();

    @JsonProperty("relationships")
    public final List<Relationship> relationships = new ArrayList<>();
    // Simplified model POJO: no merge/utility methods here. Merging is handled by the parser.

    public static final class Relationship {
        @JsonProperty("name")
        public String name;

        @JsonProperty("target_id")
        public String targetId;
    }
}

