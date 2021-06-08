package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("AutomatedAnnotation")
public class AutomatedAnnotation {
    @Parsed(field = "Chemical ID")
    public String chemicalId;
    @Parsed(field = "Chemical Name")
    public String chemicalName;
    @Parsed(field = "Chemical in Text")
    @GraphProperty("chemical_in_text")
    public String chemicalInText;
    @Parsed(field = "Variation ID")
    public String variationId;
    @Parsed(field = "Variation Name")
    public String variationName;
    @Parsed(field = "Variation Type")
    public String variationType;
    @Parsed(field = "Variation in Text")
    @GraphProperty("variation_in_text")
    public String variationInText;
    @Parsed(field = "Gene IDs")
    public String geneIds;
    @Parsed(field = "Gene Symbols")
    public String geneSymbols;
    @Parsed(field = "Gene in Text")
    @GraphProperty("gene_in_text")
    public String geneInText;
    @Parsed(field = "Literature ID")
    public String literatureId;
    @Parsed(field = "PMID")
    public String pmid;
    @Parsed(field = "Literature Title")
    public String literatureTitle;
    @Parsed(field = "Publication Year")
    public Integer publicationYear;
    @Parsed(field = "Journal")
    @GraphProperty("journal")
    public String journal;
    @Parsed(field = "Sentence")
    @GraphProperty("sentences")
    public String sentences;
    @Parsed(field = "Source")
    @GraphProperty("source")
    public String source;
}
