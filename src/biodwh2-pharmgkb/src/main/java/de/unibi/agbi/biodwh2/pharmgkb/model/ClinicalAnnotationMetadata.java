package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class ClinicalAnnotationMetadata {
    @Parsed(field = "Clinical Annotation ID")
    public String clinical_annotation_id;
    @Parsed(field = "Location")
    public String location;
    @Parsed(field = "Gene")
    public String gene;
    @Parsed(field = "Level of Evidence")
    public String level_of_evidence;
    @Parsed(field = "Clinical Annotation Types")
    public String clinical_annotation_types;
    @Parsed(field = "Genotype-Phenotype IDs")
    public String genotype_phenotypes_id;
    @Parsed(field = "Annotation Text")
    public String annotation_text;
    @Parsed(field = "Variant Annotations IDs")
    public String variant_annotations_id;
    @Parsed(field = "Variant Annotations")
    public String variant_annotation;
    @Parsed(field = "PMIDs")
    public String pmids;
    @Parsed(field = "Evidence Count")
    public String evidence_count;
    @Parsed(field = "Related Chemicals")
    public String related_chemicals;
    @Parsed(field = "Related Diseases")
    public String related_diseases;
    @Parsed(field = "Race")
    public String race;
    @Parsed(field = "Chromosome")
    public String chromosome;

}
