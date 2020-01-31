package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class ClinicalVariants {
    @Parsed(field = "variant")
    public String variant;
    @Parsed(field = "gene")
    public String gene;
    @Parsed(field = "type")
    public String type;
    @Parsed(field = "level of evidence")
    public String level_of_evidence;
    @Parsed(field = "chemicals")
    public String chemicals;
    @Parsed(field = "phenotypes")
    public String phenotypes;
}
