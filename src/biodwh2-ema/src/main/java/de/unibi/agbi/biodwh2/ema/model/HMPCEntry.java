package de.unibi.agbi.biodwh2.ema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@JsonPropertyOrder({
        "Status", "Latin name of herbal substance", "Botanical name of plant",
        "English common name of herbal substance", "Combination", "Use", "Outcome", "Date added to the inventory",
        "Date added to the priority list", "First published", "Revision date", "URL"
})
@GraphNodeLabel("HMPCEntry")
public class HMPCEntry {
    @JsonProperty("Status")
    @GraphProperty("status")
    public String status;
    @JsonProperty("Latin name of herbal substance")
    @GraphProperty("latin_name_of_herbal_substance")
    public String latinNameOfHerbalSubstance;
    @JsonProperty("Botanical name of plant")
    @GraphArrayProperty(value = "botanical_names_of_plant", arrayDelimiter = "; ")
    public String botanicalNameOfPlant;
    @JsonProperty("English common name of herbal substance")
    @GraphProperty("english_common_name_of_herbal_substance")
    public String englishCommonNameOfHerbalSubstance;
    @JsonProperty("Combination")
    @GraphBooleanProperty(value = "combination", truthValue = "yes")
    public String combination;
    @JsonProperty("Use")
    @GraphArrayProperty(value = "uses", arrayDelimiter = ", ")
    public String use;
    @JsonProperty("Outcome")
    @GraphProperty("outcome")
    public String outcome;
    @JsonProperty("Date added to the inventory")
    @GraphProperty("date_added_to_the_inventory")
    public String dateAddedToTheInventory;
    @JsonProperty("Date added to the priority list")
    @GraphProperty("date_added_to_the_priority_list")
    public String dateAddedToThePriorityList;
    @JsonProperty("First published")
    @GraphProperty("first_published")
    public String firstPublished;
    @JsonProperty("Revision date")
    @GraphProperty("revision_date")
    public String revisionDate;
    @JsonProperty("URL")
    @GraphProperty("url")
    public String url;
}
