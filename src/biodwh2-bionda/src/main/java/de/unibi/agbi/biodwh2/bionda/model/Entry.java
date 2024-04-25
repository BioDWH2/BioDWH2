package de.unibi.agbi.biodwh2.bionda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Row", "Disease", "Disease_id", "PMID", "Citations", "Journal", "Author", "Date", "Sentence",
        "Sentence_unnormalized", "Marker", "Marker_id", "UniProt_protein_name", "UniProt_entry", "Paper_type",
        "Mentions_in_PMID", "TPs", "FPs", "FNs", "TNs", "Score"
})
public class Entry {
    @JsonProperty("Row")
    public Integer row;
    @JsonProperty("Disease")
    public String disease;
    @JsonProperty("Disease_id")
    public String diseaseId;
    @JsonProperty("PMID")
    public String pmid;
    @JsonProperty("Citations")
    public Integer citations;
    @JsonProperty("Journal")
    public String journal;
    @JsonProperty("Author")
    public String author;
    @JsonProperty("Date")
    public String date;
    @JsonProperty("Sentence")
    public String sentence;
    @JsonProperty("Sentence_unnormalized")
    public String sentenceUnnormalized;
    @JsonProperty("Marker")
    public String marker;
    @JsonProperty("Marker_id")
    public String markerId;
    @JsonProperty("UniProt_protein_name")
    public String uniProtProteinName;
    @JsonProperty("UniProt_entry")
    public String uniProtEntry;
    @JsonProperty("Paper_type")
    public String paperType;
    @JsonProperty("Mentions_in_PMID")
    public Integer mentionsInPMID;
    @JsonProperty("TPs")
    public Integer truePositives;
    @JsonProperty("FPs")
    public Integer falsePositives;
    @JsonProperty("FNs")
    public Integer falseNegatives;
    @JsonProperty("TNs")
    public Integer trueNegatives;
    @JsonProperty("Score")
    public String score;
}
