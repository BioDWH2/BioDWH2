package de.unibi.agbi.biodwh2.markerdb.model;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

@GraphNodeLabel(MarkerDBGraphExporter.KARYOTYPE_LABEL)
public class KaryotypeSimple extends ConditionSimple {
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty("karyotype")
    public String karyotype;
    @GraphProperty("description")
    public String description;
}
