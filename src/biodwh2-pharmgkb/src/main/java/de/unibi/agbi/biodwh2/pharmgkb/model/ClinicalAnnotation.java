package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class ClinicalAnnotation {
    @Parsed(field = "Genotype-Phenotype ID")
    public String genotype_phenotype_id;
    @Parsed(field = "Genotype")
    public String genotype;
    @Parsed(field = "Clinical Phenotype")
    public String clinical_phenotype;


}
