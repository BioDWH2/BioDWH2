package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class DrugLabelsByGene {
    @Parsed(field = "Gene ID")
    public String geneId;
    @Parsed(field = "Gene Symbol")
    public String geneSymbol;
    @Parsed(field = "Label IDs")
    public String labelIds;
    @Parsed(field = "Label Names")
    public String labelNames;
}
