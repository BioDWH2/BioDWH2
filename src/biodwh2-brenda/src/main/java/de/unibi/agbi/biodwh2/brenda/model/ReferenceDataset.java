package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferenceDataset {
    @SuppressWarnings("unused")
    @JsonProperty("id")
    public String id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("authors")
    public String[] authors;
    @JsonProperty("journal")
    public String journal;
    @JsonProperty("year")
    public Integer year;
    @JsonProperty("pages")
    public String pages;
    @JsonProperty("vol")
    public String volume;
    @JsonProperty("pmid")
    public Long pmid; // TODO: should be Integer, however some PMIDs are malformed
}
