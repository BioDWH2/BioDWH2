package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

public class MEDRTGraphExporter extends GraphExporter {
    @Override
    protected Graph exportGraph(DataSource dataSource) {
        return new Graph();
    }
}
