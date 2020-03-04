package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;

public class NDFRTGraphExporter extends GraphExporter<NDFRTDataSource> {
    @Override
    protected Graph exportGraph(NDFRTDataSource dataSource) {
        return new Graph();
    }
}
