package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;
import de.unibi.agbi.biodwh2.pharmgkb.etl.PharmGKBGraphExporter;

@NodeLabels({"VariantAnnotation", "VariantPhenotypeAnnotation"})
public class VariantPhenotypeAnnotation {
    @Parsed(field = "Variant Annotation ID")
    @GraphProperty("id")
    public Integer annotationId;
    @Parsed(field = "Variant/Haplotypes")
    public String variantHaplotypes;
    @Parsed(field = "Gene")
    public String gene;
    @Parsed(field = "Drug(s)")
    public String drugs;
    @Parsed(field = "PMID")
    @GraphProperty("pmid")
    public String pmid;
    @Parsed(field = "Phenotype Category")
    @GraphArrayProperty(value = "phenotype_categories", arrayDelimiter = ",", quotedArrayElements = true)
    public String phenotypeCategory;
    @Parsed(field = "Significance")
    @GraphProperty("significance")
    public String significance;
    @Parsed(field = "Notes")
    @GraphProperty("notes")
    public String notes;
    @Parsed(field = "Sentence")
    @GraphProperty("sentence")
    public String sentence;
    @Parsed(field = "Alleles")
    @GraphProperty("alleles")
    public String alleles;
    @Parsed(field = "Specialty Population")
    @GraphProperty("specialty_population")
    public String specialtyPopulation;
}
