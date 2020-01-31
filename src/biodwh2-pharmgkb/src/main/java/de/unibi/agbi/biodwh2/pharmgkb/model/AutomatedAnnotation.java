package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class AutomatedAnnotation {
    @Parsed(field = "Chemical ID")
    public String chemical_id;
    @Parsed(field = "Chemical Name")
    public String chemical_name;
    @Parsed(field = "Chemical in Text")
    public String chemical_in_text;
    @Parsed(field = "Variation ID")
    public String variation_id;
    @Parsed(field = "Variation Name")
    public String variation_name;
    @Parsed(field = "Variation Type")
    public String variation_type;
    @Parsed(field = "Variation in Text")
    public String variation_in_text;
    @Parsed(field = "Gene IDs")
    public String gene_ids;
    @Parsed(field = "Gene Symbols")
    public String gene_symbols;
    @Parsed(field = "Gene in Text")
    public String gene_in_text;
    @Parsed(field = "Literature ID")
    public String literature_id;
    @Parsed(field = "PMID")
    public String pmid;
    @Parsed(field = "Literature Title")
    public String literature_title;
    @Parsed(field = "Publication Year")
    public String publication_year;
    @Parsed(field = "Journal")
    public String journal;
    @Parsed(field = "Sentence")
    public String sentences;
    @Parsed(field = "Source")
    public String source;

}
