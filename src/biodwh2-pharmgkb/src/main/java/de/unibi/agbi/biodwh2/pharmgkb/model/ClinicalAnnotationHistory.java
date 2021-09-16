package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class ClinicalAnnotationHistory {
    @Parsed(field = "Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @Parsed(field = "Date (YYYY-MM-DD)")
    public String date;
    @Parsed(field = "Type")
    public String type;
    @Parsed(field = "Comment")
    public String comment;
}
