package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;

@JsonPropertyOrder({"id", "nct_id", "document_type", "has_protocol", "has_icf", "has_sap", "document_date", "url"})
public class ProvidedDocument {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("document_type")
    public String documentType;
    @GraphBooleanProperty(value = "has_protocol", truthValue = "t")
    public String hasProtocol;
    @GraphBooleanProperty(value = "has_icf", truthValue = "t")
    public String hasIcf;
    @GraphBooleanProperty(value = "has_sap", truthValue = "t")
    public String hasSap;
    @JsonProperty("document_date")
    public String documentDate;
    @JsonProperty("url")
    public String url;
}
