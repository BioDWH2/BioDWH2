package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"ClinicalAnnotationMetadata"})
public class ClinicalAnnotationMetadata {
    @Parsed(field = "Clinical Annotation ID")
    @GraphProperty("id")
    public String clinicalAnnotationId;
    @Parsed(field = "Location")
    @GraphProperty("location")
    public String location;
    @Parsed(field = "Gene")
    public String gene;
    @Parsed(field = "Level of Evidence")
    @GraphProperty("levelOfEvidence")
    public String levelOfEvidence;
    @Parsed(field = "Clinical Annotation Types")
    public String clinicalAnnotationTypes;
    @Parsed(field = "Genotype-Phenotype IDs")
    public String genotypePhenotypesId;
    @Parsed(field = "Annotation Text")
    @GraphProperty("annotationText")
    public String annotationText;
    @Parsed(field = "Variant Annotations IDs")
    public String variantAnnotationsId;
    @Parsed(field = "Variant Annotations")
    public String variantAnnotation;
    @Parsed(field = "PMIDs")
    public String pmids;
    @Parsed(field = "Evidence Count")
    public String evidenceCount;
    @Parsed(field = "Related Chemicals")
    public String relatedChemicals;
    @Parsed(field = "Related Diseases")
    public String relatedDiseases;
    @Parsed(field = "Race")
    @GraphProperty("race")
    public String race;
    @Parsed(field = "Chromosome")
    @GraphProperty("chromosome")
    public String chromosome;

}
