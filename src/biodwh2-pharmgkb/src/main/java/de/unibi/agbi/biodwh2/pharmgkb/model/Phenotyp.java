package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Phenotyp {
    @Parsed(field = "PharmGKB Accession Id")
    public String pharmgkb_accession_id;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Alternate Names")
    public String alternate_names;
    @Parsed(field = "Cross-references")
    public String cross_reference;
    @Parsed(field = "External Vocabulary")
    public String external_vocabulary;
}
