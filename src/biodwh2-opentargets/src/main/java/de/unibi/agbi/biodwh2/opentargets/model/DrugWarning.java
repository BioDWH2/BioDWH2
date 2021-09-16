package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsGraphExporter;

@SuppressWarnings("unused")
@GraphNodeLabel(OpenTargetsGraphExporter.DRUG_WARNING_LABEL)
public class DrugWarning {
    @JsonProperty("id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("meddraSocCode")
    @GraphProperty("meddra_soc_code")
    public Integer meddraSocCode;
    @JsonProperty("year")
    @GraphProperty("year")
    public Integer year;
    @JsonProperty("toxicityClass")
    @GraphProperty("toxicity_class")
    public String toxicityClass;
    @JsonProperty("chemblIds")
    public String[] chemblIds;
    @JsonProperty("country")
    @GraphProperty("country")
    public String country;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("warningType")
    @GraphProperty("warning_type")
    public String warningType;
    @JsonProperty("references")
    public Reference[] references;

    public static class Reference {
        @JsonProperty("ref_id")
        public String refId;
        @JsonProperty("ref_type")
        public String refType;
        @JsonProperty("ref_url")
        public String refUrl;
    }
}
