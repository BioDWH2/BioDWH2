package de.unibi.agbi.biodwh2.omim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Prefix", "MIM Number", "Preferred Title; symbol", "Alternative Title(s); symbol(s)",
        "Included Title(s); symbols"
})
public class MIMTitles {
    /**
     * Asterisk (*)  Gene
     * Plus (+)  Gene and phenotype, combined
     * Number Sign (#)  Phenotype, molecular basis known
     * Percent (%)  Phenotype or locus, molecular basis unknown
     * NULL (<null>)  Other, mainly phenotypes with suspected mendelian basis
     * Caret (^)  Entry has been removed from the database or moved to another entry
     */
    @JsonProperty("Prefix")
    public String prefix;
    @JsonProperty("MIM Number")
    public String mimNumber;
    @JsonProperty("Preferred Title; symbol")
    public String preferredTitle;
    @JsonProperty("Alternative Title(s); symbol(s)")
    public String alternativeTitle;
    @JsonProperty("Included Title(s); symbols")
    public String includedTitles;
}
