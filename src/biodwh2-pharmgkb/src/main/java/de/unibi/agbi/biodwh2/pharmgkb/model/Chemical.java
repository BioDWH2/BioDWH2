package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("Chemical")
@JsonPropertyOrder({
        "PharmGKB Accession Id", "Name", "Generic Names", "Trade Names", "Brand Mixtures", "Type", "Cross-references",
        "SMILES", "InChI", "Dosing Guideline", "External Vocabulary", "Clinical Annotation Count",
        "Variant Annotation Count", "Pathway Count", "VIP Count", "Dosing Guideline Sources",
        "Top Clinical Annotation Level", "Top FDA Label Testing Level", "Top Any Drug Label Testing Level",
        "Label Has Dosing Info", "Has Rx Annotation", "RxNorm Identifiers", "ATC Identifiers",
        "PubChem Compound Identifiers"
})
public class Chemical {
    @JsonProperty("PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @JsonProperty("Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Generic Names")
    @GraphArrayProperty(value = "generic_names", arrayDelimiter = ", ", quotedArrayElements = true)
    public String genericNames;
    @JsonProperty("Trade Names")
    @GraphArrayProperty(value = "trade_names", arrayDelimiter = ", ", quotedArrayElements = true)
    public String tradeNames;
    @JsonProperty("Brand Mixtures")
    @GraphArrayProperty(value = "brand_mixtures", arrayDelimiter = ", ", quotedArrayElements = true)
    public String brandMixtures;
    @JsonProperty("Type")
    @GraphArrayProperty(value = "types", arrayDelimiter = ", ", quotedArrayElements = true)
    public String type;
    @JsonProperty("Cross-references")
    @GraphArrayProperty(value = "cross_references", arrayDelimiter = ", ", quotedArrayElements = true)
    public String crossReferences;
    @JsonProperty("SMILES")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("InChI")
    @GraphProperty("inchi")
    public String inchi;
    @JsonProperty("Dosing Guideline")
    @GraphBooleanProperty(value = "dosing_guideline", truthValue = "yes")
    public String dosingGuideline;
    @JsonProperty("External Vocabulary")
    public String externalVocabulary;
    @JsonProperty("Clinical Annotation Count")
    @GraphProperty("clinical_annotation_count")
    public Integer clinicalAnnotationCount;
    @JsonProperty("Variant Annotation Count")
    @GraphProperty("variant_annotation_count")
    public Integer variantAnnotationCount;
    @JsonProperty("Pathway Count")
    @GraphProperty("pathway_count")
    public Integer pathwayCount;
    @JsonProperty("VIP Count")
    @GraphProperty("vip_count")
    public Integer vipCount;
    @JsonProperty("Dosing Guideline Sources")
    @GraphArrayProperty(value = "dosing_guideline_sources", arrayDelimiter = ", ", quotedArrayElements = true)
    public String dosingGuidelineSources;
    @JsonProperty("Top Clinical Annotation Level")
    @GraphProperty("top_clinical_annotation_level")
    public String topClinicalAnnotationLevel;
    @JsonProperty("Top FDA Label Testing Level")
    @GraphProperty("top_fda_label_testing_level")
    public String topFdaLabelTestingLevel;
    @JsonProperty("Top Any Drug Label Testing Level")
    @GraphProperty("top_any_drug_label_testing_level")
    public String topAnyDrugLabelTestingLevel;
    @JsonProperty("Label Has Dosing Info")
    @GraphProperty("label_has_dosing_info")
    public String labelHasDosingInfo;
    @JsonProperty("Has Rx Annotation")
    @GraphProperty("has_rx_annotation")
    public String hasRxAnnotation;
    @JsonProperty("RxNorm Identifiers")
    @GraphArrayProperty(value = "rxnorm_identifiers", arrayDelimiter = ", ", quotedArrayElements = true)
    public String rxNormIdentifiers;
    @JsonProperty("ATC Identifiers")
    @GraphArrayProperty(value = "atc_identifiers", arrayDelimiter = ", ", quotedArrayElements = true)
    public String atcIdentifiers;
    @JsonProperty("PubChem Compound Identifiers")
    @GraphArrayProperty(value = "pubchem_compound_identifiers", arrayDelimiter = ", ", quotedArrayElements = true)
    public String pubChemCompoundIdentifiers;
}
