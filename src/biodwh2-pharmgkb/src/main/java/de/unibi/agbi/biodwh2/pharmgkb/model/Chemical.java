package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("Chemical")
public class Chemical {
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
    public String clinicalAnnotationCount;
    @Parsed(field = "Variant Annotation Count")
    public String variantAnnotationCount;
    @Parsed(field = "Pathway Count")
    public String pathwayCount;
    @Parsed(field = "VIP Count")
    public String vipCount;
    @Parsed(field = "Dosing Guideline Sources")
    public String dosingGuidelineSources;
    @Parsed(field = "Top Clinical Annotation Level")
    public String topClinicalAnnotationLevel;
    @Parsed(field = "Top FDA Label Testing Level")
    public String topFdaLabelTestingLevel;
    @Parsed(field = "Top Any Drug Label Testing Level")
    public String topAnyDrugLabelTestingLevel;
    @Parsed(field = "Label Has Dosing Info")
    public String labelHasDosingInfo;
    @Parsed(field = "Has Rx Annotation")
    public String hasRxAnnotation;
    @Parsed(field = "RxNorm Identifiers")
    public String rxNormIdentifiers;
    @Parsed(field = "ATC Identifiers")
    public String atcIdentifiers;
    @Parsed(field = "PubChem Compound Identifiers")
    public String pubChemCompoundIdentifiers;
}
