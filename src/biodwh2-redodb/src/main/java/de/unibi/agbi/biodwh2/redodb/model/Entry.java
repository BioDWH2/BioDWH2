package de.unibi.agbi.biodwh2.redodb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Drug", "Synonym", "Main Indications", "WHO", "Off-Patent", "Vitro", "Vivo", "Cases", "Obs.", "Trials", "Human",
        "Targets", "Date Update", "DrugBank", "PubChem", "PubMed"
})
public final class Entry {
    @JsonProperty("Drug")
    public String drug;
    @JsonProperty("Synonym")
    public String synonym;
    /**
     * Main uses, including off-label
     */
    @JsonProperty("Main Indications")
    public String mainIndications;
    /**
     * Drug is included in WHO Essential Medicines List
     */
    @JsonProperty("WHO")
    public String who;
    /**
     * Drug is off-patent in some countries
     */
    @JsonProperty("Off-Patent")
    public String offPatent;
    /**
     * Anticancer evidence exists in vitro
     */
    @JsonProperty("Vitro")
    public String vitro;
    /**
     * Anticancer evidence exists in vivo
     */
    @JsonProperty("Vivo")
    public String vivo;
    /**
     * Anticancer evidence from one or more case reports
     */
    @JsonProperty("Cases")
    public String cases;
    /**
     * One or more observational studies have shown an anticancer effect
     */
    @JsonProperty("Obs.")
    public String obs;
    /**
     * Drug has been included in one or more trials for anticancer effects
     */
    @JsonProperty("Trials")
    public String trials;
    /**
     * Data exists from cases, observational or clinical trials
     */
    @JsonProperty("Human")
    public String human;
    /**
     * Molecular targets address by this drug
     */
    @JsonProperty("Targets")
    public String targets;
    /**
     * Date of last update for this drug
     */
    @JsonProperty("Date Update")
    public String dateUpdate;
    @JsonProperty("DrugBank")
    public String drugBank;
    @JsonProperty("PubChem")
    public String pubChem;
    @JsonProperty("PubMed")
    public String pubMed;
}
