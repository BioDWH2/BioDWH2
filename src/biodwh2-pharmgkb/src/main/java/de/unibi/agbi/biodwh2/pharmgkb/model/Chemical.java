package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Chemical {

    @Parsed(field = "PharmGKB Accession Id")
    public String pharmgkb_accession_id;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Generic Names")
    public String generic_names;
    @Parsed(field = "Trade Names")
    public String trade_names;
    @Parsed(field = "Brand Mixtures")
    public String brand_mixtures;
    @Parsed(field = "Type")
    public String type;
    @Parsed(field = "Cross-references")
    public String cross_reference;
    @Parsed(field = "SMILES")
    public String smiles;
    @Parsed(field = "InChI")
    public String inchi;
    @Parsed(field = "Dosing Guideline")
    public String dosing_guideline;
    @Parsed(field = "External Vocabulary")
    public String external_vocabulary;
    @Parsed(field = "Clinical Annotation Count")
    public String clinical_annotation_count;
    @Parsed(field = "Variant Annotation Count")
    public String variant_annotation_count;
    @Parsed(field = "Pathway Count")
    public String pathway_count;
    @Parsed(field = "VIP Count")
    public String vip_count;
    @Parsed(field = "Dosing Guideline Sources")
    public String dosing_guideline_sources;
    @Parsed(field = "Top Clinical Annotation Level")
    public String top_clinical_annotation_level;
    @Parsed(field = "Top FDA Label Testing Level")
    public String top_fda_label_testing_level;
    @Parsed(field = "Top Any Drug Label Testing Level")
    public String top_any_drug_label_testing_level;
    @Parsed(field = "Label Has Dosing Info")
    public String label_has_dosing_info;
    @Parsed(field = "Has Rx Annotation")
    public String has_rx_annotation;
    @Parsed(field = "RxNorm Identifiers")
    public String rx_norm_identifiers;
    @Parsed(field = "ATC Identifiers")
    public String atc_identifiers;
    @Parsed(field = "PubChem Compound Identifiers")
    public String pub_chem_compound_identifiers;

}
