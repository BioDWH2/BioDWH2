package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class ClinicalAnnotationEvidence {
    @Parsed(field = "Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @Parsed(field = "Evidence ID")
    public String evidenceId;
    @Parsed(field = "Evidence Type")
    public String evidenceType;
    @Parsed(field = "Evidence URL")
    public String evidenceUrl;
    @Parsed(field = "PMID")
    public String pmid;
    @Parsed(field = "Summary")
    public String summary;
    @Parsed(field = "Score")
    public String score;
}
