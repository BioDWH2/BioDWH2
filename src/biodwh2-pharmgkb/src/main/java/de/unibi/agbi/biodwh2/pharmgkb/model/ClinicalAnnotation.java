package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"ClinicalAnnotation"})
public class ClinicalAnnotation {
    @Parsed(field = "Genotype-Phenotype ID")
    @GraphProperty("id")
    public String genotypePhenotypeId;
    @Parsed(field = "Genotype")
    @GraphProperty("genotype")
    public String genotype;
    @Parsed(field = "Clinical Phenotype")
    @GraphProperty("clinical_phenotype")
    public String clinicalPhenotype;
}
