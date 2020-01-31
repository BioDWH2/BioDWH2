package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class DrugLabelsByGene {
    @Parsed(field = "Gene ID")
    public String gene_id;
    @Parsed(field = "Gene Symbol")
    public String gene_symbol;
    @Parsed(field = "Label IDs")
    public String label_ids;
    @Parsed(field = "Label Names")
    public String label_names;
}
