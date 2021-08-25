package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsGraphExporter;

@GraphNodeLabel(OpenTargetsGraphExporter.DISEASE_LABEL)
public class Disease {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("code")
    @GraphProperty("code")
    public String code;
    @JsonProperty("dbXRefs")
    @GraphProperty("db_xrefs")
    public String[] dbXRefs;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("directLocationIds")
    @GraphProperty("direct_location_ids")
    public String[] directLocationIds;
    @JsonProperty("indirectLocationIds")
    @GraphProperty("indirect_location_ids")
    public String[] indirectLocationIds;
    @JsonProperty("obsoleteTerms")
    @GraphProperty("obsolete_terms")
    public String[] obsoleteTerms;
    @JsonProperty("parents")
    public String[] parents;
    @JsonProperty("ancestors")
    public String[] ancestors;
    @JsonProperty("children")
    public String[] children;
    @JsonProperty("descendants")
    public String[] descendants;
    @JsonProperty("sko")
    @GraphProperty("sko")
    public String[] sko;
    @JsonProperty("therapeuticAreas")
    @GraphProperty("therapeutic_areas")
    public String[] therapeuticAreas;
    @JsonProperty("synonyms")
    public Synonyms synonyms;
    @JsonProperty("ontology")
    public Ontology ontology;

    public static class Synonyms {
        @JsonProperty("hasBroadSynonym")
        @GraphProperty("has_broad_synonym")
        public String[] hasBroadSynonym;
        @JsonProperty("hasExactSynonym")
        @GraphProperty("has_exact_synonym")
        public String[] hasExactSynonym;
        @JsonProperty("hasNarrowSynonym")
        @GraphProperty("has_narrow_synonym")
        public String[] hasNarrowSynonym;
        @JsonProperty("hasRelatedSynonym")
        @GraphProperty("has_related_synonym")
        public String[] hasRelatedSynonym;
    }

    public static class Ontology {
        @JsonProperty("isTherapeuticArea")
        @GraphProperty("is_therapeutic_area")
        public Boolean isTherapeuticArea;
        @JsonProperty("leaf")
        @GraphProperty("leaf")
        public Boolean leaf;
        @JsonProperty("sources")
        public Source source;
    }

    public static class Source {
        @JsonProperty("name")
        @GraphProperty("name")
        public String name;
        @JsonProperty("url")
        @GraphProperty("url")
        public String url;
    }
}
