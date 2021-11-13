package de.unibi.agbi.biodwh2.cancerdrugsdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Product", "EMA", "FDA", "EN", "Other", "WHO", "Year", "Generic", "DrugBank ID", "ATC", "ChEMBL", "Indications",
        "Targets", "Last Update"
})
public class Entry {
    @JsonProperty("Product")
    public String product;
    @JsonProperty("EMA")
    public String emaApproval;
    @JsonProperty("FDA")
    public String fdaApproval;
    @JsonProperty("EN")
    public String europeanNationalApproval;
    @JsonProperty("Other")
    public String otherApproval;
    /**
     * Whether the medication is included in the WHO Model Lists of Essential Medicines (EML)
     */
    @JsonProperty("WHO")
    public String inWHOEML;
    @JsonProperty("Year")
    public String firstApprovalYear;
    @JsonProperty("Generic")
    public String generic;
    @JsonProperty("DrugBank ID")
    public String drugBankId;
    @JsonProperty("ATC")
    public String atc;
    @JsonProperty("ChEMBL")
    public String chEMBL;
    @JsonProperty("Indications")
    public String cancerIndications;
    @JsonProperty("Targets")
    public String geneTargets;
    @JsonProperty("Last Update")
    public String lastUpdate;
}
