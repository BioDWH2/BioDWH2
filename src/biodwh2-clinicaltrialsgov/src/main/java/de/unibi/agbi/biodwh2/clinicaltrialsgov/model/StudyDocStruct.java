package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudyDocStruct {
    @JsonProperty(value = "doc_id")
    public String docId;
    @JsonProperty(value = "doc_type")
    public String docType;
    @JsonProperty(value = "doc_url")
    public String docUrl;
    @JsonProperty(value = "doc_comment")
    public String docComment;
}
