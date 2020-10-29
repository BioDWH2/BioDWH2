package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("Drug")
public class Drug {
    @Parsed(field = "PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
    @GraphProperty("name")
    public String name;
    @Parsed(field = "Generic Names")
    public String genericNames;
    @Parsed(field = "Trade Names")
    public String tradeNames;
    @Parsed(field = "Brand Mixtures")
    public String brandMixtures;
    @Parsed(field = "Type")
    public String type;
    @Parsed(field = "Cross-references")
    public String crossReference;
    @Parsed(field = "SMILES")
    @GraphProperty("smiles")
    public String smiles;
    @Parsed(field = "InChI")
    @GraphProperty("inchi")
    public String inchi;
    @Parsed(field = "Dosing Guideline")
    public String dosingGuideline;
    @Parsed(field = "External Vocabulary")
    public String externalVocabulary;
    @Parsed(field = "Clinical Annotation Count")
    @GraphProperty("clinical_annotation_count")
    public Integer clinicalAnnotationCount;
    @Parsed(field = "Variant Annotation Count")
    @GraphProperty("variant_annotation_count")
    public Integer variantAnnotationCount;
    @Parsed(field = "Pathway Count")
    @GraphProperty("pathway_count")
    public Integer pathwayCount;
    @Parsed(field = "VIP Count")
    @GraphProperty("vip_count")
    public Integer vipCount;
    @Parsed(field = "Dosing Guideline Sources")
    public String dosingGuidelineSources;
    @Parsed(field = "Top Clinical Annotation Level")
    @GraphProperty("top_clinical_annotation_level")
    public String topClinicalAnnotationLevel;
    @Parsed(field = "Top FDA Label Testing Level")
    @GraphProperty("top_fda_label_testing_level")
    public String topFdaLabelTestingLevel;
    @Parsed(field = "Top Any Drug Label Testing Level")
    @GraphProperty("top_any_drug_label_testing_level")
    public String topAnyDrugLabelTestingLevel;
    @Parsed(field = "Label Has Dosing Info")
    @GraphProperty("label_has_dosing_info")
    public String labelHasDosingInfo;
    @Parsed(field = "Has Rx Annotation")
    @GraphProperty("has_rx_annotation")
    public String hasRxAnnotation;
    @Parsed(field = "RxNorm Identifiers")
    public String rxNormIdentifiers;
    @Parsed(field = "ATC Identifiers")
    public String atcIdentifiers;
    @Parsed(field = "PubChem Compound Identifiers")
    public String pubChemCompoundIdentifiers;
}
