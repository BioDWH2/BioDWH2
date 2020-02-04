package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Phenotype {
    @Parsed(field = "PharmGKB Accession Id")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Alternate Names")
    public String alternateNames;
    @Parsed(field = "Cross-references")
    public String crossReference;
    @Parsed(field = "External Vocabulary")
    public String externalVocabulary;
}
