package de.unibi.agbi.biodwh2.markerdb.model;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

import java.util.List;

@GraphNodeLabel(MarkerDBGraphExporter.KARYOTYPE_LABEL)
public class Karyotype {
    @GraphProperty(GraphExporter.ID_KEY)
    public Integer id;
    @GraphProperty("karyotype")
    public String karyotype;
    @GraphProperty("description")
    public String description;
    public List<Condition> conditions;
}
