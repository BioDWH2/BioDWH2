package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Drug {
    @Parsed(field = "PharmGKB Accession Id")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
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
    public String smiles;
    @Parsed(field = "InChI")
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
