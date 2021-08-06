package de.unibi.agbi.biodwh2.kegg.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Reference")
public class Reference {
    @GraphProperty("pmid")
    public Integer pmid;
    public String remarks;
    @GraphProperty("authors")
    public String authors;
    @GraphProperty("title")
    public String title;
    @GraphProperty("journal")
    public String journal;
    @GraphProperty("doi")
    public String doi;
}
