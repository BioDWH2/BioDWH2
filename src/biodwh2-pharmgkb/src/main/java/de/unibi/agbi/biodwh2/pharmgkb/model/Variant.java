package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@NodeLabel("Variant")
public class Variant {
    @Parsed(field = "Variant ID")
    @GraphProperty("id")
    public String variantId;
    @Parsed(field = "Variant Name")
    @GraphProperty("name")
    public String variantName;
    @Parsed(field = "Gene IDs")
    @GraphArrayProperty(value = "gene_ids", arrayDelimiter = ",")
    public String geneIds;
    @Parsed(field = "Gene Symbols")
    @GraphArrayProperty(value = "gene_symbols", arrayDelimiter = ",")
    public String geneSymbols;
    @Parsed(field = "Location")
    @GraphProperty("location")
    public String location;
    @Parsed(field = "Variant Annotation count")
    @GraphProperty("variant_annotation_count")
    public Integer variantAnnotationCount;
    @Parsed(field = "Clinical Annotation count")
    @GraphProperty("clinical_annotation_count")
    public Integer clinicalAnnotationCount;
    @Parsed(field = "Level 1/2 Clinical Annotation count")
    @GraphProperty("level12_clinical_annotation_count")
    public Integer level12ClinicalAnnotationCount;
    @Parsed(field = "Guideline Annotation count")
    @GraphProperty("guideline_annotation_count")
    public Integer guidelineAnnotationCount;
    @Parsed(field = "Label Annotation count")
    @GraphProperty("label_annotation_count")
    public Integer labelAnnotationCount;
    @Parsed(field = "Synonyms")
    public String synonyms;
}
