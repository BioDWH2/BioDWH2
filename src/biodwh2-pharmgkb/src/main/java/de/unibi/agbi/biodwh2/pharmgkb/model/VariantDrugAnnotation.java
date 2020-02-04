package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class VariantDrugAnnotation {
    @Parsed(field = "Annotation ID")
    public String annotation_id;
    @Parsed(field = "Variant")
    public String variant;
    @Parsed(field = "Gene")
    public String gene;
    @Parsed(field = "Chemical")
    public String chemical;
    @Parsed(field = "PMID")
    public String pmid;
    @Parsed(field = "Phenotype Category")
    public String phenotype_category;
    @Parsed(field = "Significance")
    public String significance;
    @Parsed(field = "Notes")
    public String notes;
    @Parsed(field = "Sentence")
    public String sentence;
    @Parsed(field = "StudyParameters")
    public String study_parameters;
    @Parsed(field = "Alleles")
    public String alleles;
    @Parsed(field = "Chromosome")
    public String chromosome;
}
