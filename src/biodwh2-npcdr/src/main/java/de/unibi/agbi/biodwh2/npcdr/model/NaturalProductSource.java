package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "NP ID", "NP Name", "Superkingdom or Kingdom Name", "Superkingdom or Kingdom ID", "Phylum Name", "Phylum ID",
        "Class Name", "Class ID", "Order Name", "Order ID", "Family Name", "Family ID", "Genus Name", "Genus ID",
        "Species Name", "Species ID"
})
public class NaturalProductSource {
    @JsonProperty("NP ID")
    public String naturalProductId;
    @JsonProperty("NP Name")
    public String naturalProductName;
    @JsonProperty("Superkingdom or Kingdom Name")
    public String superkingdomOrKingdomName;
    @JsonProperty("Superkingdom or Kingdom ID")
    public String superkingdomOrKingdomId;
    @JsonProperty("Phylum Name")
    public String phylumName;
    @JsonProperty("Phylum ID")
    public String phylumId;
    @JsonProperty("Class Name")
    public String className;
    @JsonProperty("Class ID")
    public String classId;
    @JsonProperty("Order Name")
    public String orderName;
    @JsonProperty("Order ID")
    public String orderId;
    @JsonProperty("Family Name")
    public String familyName;
    @JsonProperty("Family ID")
    public String familyId;
    @JsonProperty("Genus Name")
    public String genusName;
    @JsonProperty("Genus ID")
    public String genusId;
    @JsonProperty("Species Name")
    public String speciesName;
    @JsonProperty("Species ID")
    public String speciesId;
}
