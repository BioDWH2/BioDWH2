package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProvidedDocumentStruct {
    @JsonProperty(value = "document_type")
    public String documentType;
    @JsonProperty(value = "document_has_protocol")
    public String documentHasProtocol;
    @JsonProperty(value = "document_has_icf")
    public String documentHasIcf;
    @JsonProperty(value = "document_has_sap")
    public String documentHasSap;
    @JsonProperty(value = "document_date")
    public String documentDate;
    @JsonProperty(value = "document_url")
    public String documentUrl;
}
