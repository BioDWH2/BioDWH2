package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@JsonPropertyOrder({
        "tsn", "unit_ind1", "unit_name1", "unit_ind2", "unit_name2", "unit_ind3", "unit_name3", "unit_ind4",
        "unit_name4", "unnamed_taxon_ind", "usage", "unaccept_reason", "credibility_rtng", "completeness_rtng",
        "currency_rating", "phylo_sort_seq", "initial_time_stamp", "parent_tsn", "taxon_author_id", "hybrid_author_id",
        "kingdom_id", "rank_id", "update_date", "uncertain_prnt_ind", "name_usage", "complete_name"
})
@NodeLabels("Taxon")
public class TaxonomicUnit {
    @JsonProperty("tsn")
    @GraphProperty("id")
    public Integer tsn;
    @JsonProperty("unit_ind1")
    public String unitInd1;
    @JsonProperty("unit_name1")
    @GraphProperty("unit_name1")
    public String name1;
    @JsonProperty("unit_ind2")
    public String unitInd2;
    @JsonProperty("unit_name2")
    @GraphProperty("unit_name2")
    public String name2;
    @JsonProperty("unit_ind3")
    public String unitInd3;
    @JsonProperty("unit_name3")
    @GraphProperty("unit_name3")
    public String name3;
    @JsonProperty("unit_ind4")
    public String unitInd4;
    @JsonProperty("unit_name4")
    @GraphProperty("unit_name4")
    public String name4;
    @JsonProperty("unnamed_taxon_ind")
    public String unnamedTaxonInd;
    @JsonProperty("usage")
    @GraphProperty("usage")
    public String usage;
    @JsonProperty("unaccept_reason")
    @GraphProperty("unaccept_reason")
    public String unacceptReason;
    @JsonProperty("credibility_rtng")
    @GraphProperty("credibility")
    public String credibilityRtng;
    @JsonProperty("completeness_rtng")
    @GraphProperty("completeness")
    public String completenessRtng;
    @JsonProperty("currency_rating")
    @GraphProperty("currency")
    public String currencyRating;
    @JsonProperty("phylo_sort_seq")
    public int phyloSortSeq;
    @JsonProperty("initial_time_stamp")
    public String initialTimeStamp;
    @JsonProperty("parent_tsn")
    public int parentTsn;
    @JsonProperty("taxon_author_id")
    public int taxonAuthorId;
    @JsonProperty("hybrid_author_id")
    public int hybridAuthorId;
    @JsonProperty("kingdom_id")
    @GraphProperty("kingdom_id")
    public int kingdomId;
    @JsonProperty("rank_id")
    @GraphProperty("rank_id")
    public int rankId;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("uncertain_prnt_ind")
    public String uncertainPrntInd;
    @JsonProperty("name_usage")
    @GraphProperty("name_usage")
    public String nameUsage;
    @JsonProperty("complete_name")
    @GraphProperty("name")
    public String completeName;
}
