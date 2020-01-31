package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Variant {
    @Parsed(field = "Variant ID")
    public String variant_id;
    @Parsed(field = "Variant Name")
    public String variant_name;
    @Parsed(field = "Gene IDs")
    public String gene_ids;
    @Parsed(field = "Gene Symbols")
    public String gene_symbols;
    @Parsed(field = "Location")
    public String location;
    @Parsed(field = "Variant Annotation count")
    public String variant_annotation_count;
    @Parsed(field = "Clinical Annotation count")
    public String clinical_annotation_count;
    @Parsed(field = "Level 1/2 Clinical Annotation count")
    public String level_12_clinical_annotation_count;
    @Parsed(field = "Guideline Annotation count")
    public String guideline_annotation_count;
    @Parsed(field = "Label Annotation count")
    public String label_annotation_count;
    @Parsed(field = "Synonyms")
    public String synonyms;
}
