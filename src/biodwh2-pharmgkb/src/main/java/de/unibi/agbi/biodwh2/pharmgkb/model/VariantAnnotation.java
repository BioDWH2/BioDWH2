package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class VariantAnnotation {
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
    public String pmid;
    @Parsed(field = "Phenotype Category")
    @GraphArrayProperty(value = "phenotype_categories", arrayDelimiter = ", ")
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
    @Parsed(field = "Metabolizer types")
    @GraphProperty("metabolizer_types")
    public String metabolizerTypes;
    @Parsed(field = "isPlural")
    @GraphProperty("is_plural")
    public String isPlural;
    @Parsed(field = "Is/Is Not associated")
    @GraphProperty("is_isnot_associated")
    public String isIsNotAssociated;
    @Parsed(field = "Direction of effect")
    @GraphProperty("direction_of_effect")
    public String directionOfEffect;
    @Parsed(field = "PD/PK terms")
    @GraphProperty("pd_pk_terms")
    public String pdPkTerms;
    @Parsed(field = "Multiple drugs And/or")
    @GraphProperty("multiple_drugs_and_or")
    public String multipleDrugsAndOr;
    @Parsed(field = "Population types")
    @GraphProperty("population_types")
    public String populationTypes;
    @Parsed(field = "Population Phenotypes or diseases")
    @GraphProperty("population_phenotypes_or_diseases")
    public String populationPhenotypesOrDiseases;
    @Parsed(field = "Multiple phenotypes or diseases And/or")
    @GraphProperty("multiple_phenotypes_or_diseases_and_or")
    public String multiplePhenotypesOrDiseasesAndOr;
    @Parsed(field = "Comparison Allele(s) or Genotype(s)")
    @GraphProperty("comparison_alleles_or_genotypes")
    public String comparisonAllelesOrGenotypes;
    @Parsed(field = "Comparison Metabolizer types")
    @GraphProperty("comparison_metabolizer_types")
    public String comparisonMetabolizerTypes;
}
