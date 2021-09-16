package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@GraphNodeLabel("ClinicalAnnotationAllele")
public class ClinicalAnnotationAllele {
    @Parsed(field = "Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @Parsed(field = "Genotype/Allele")
    @GraphProperty("genotype_or_allele")
    public String genotypeAllele;
    @Parsed(field = "Annotation Text")
    @GraphProperty("text")
    public String annotationText;
    @Parsed(field = "Allele Function")
    @GraphProperty("allele_function")
    public String alleleFunction;
}
