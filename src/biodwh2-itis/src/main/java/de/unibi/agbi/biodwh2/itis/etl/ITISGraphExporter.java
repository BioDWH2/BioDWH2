package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import de.unibi.agbi.biodwh2.itis.model.GeographicDivision;

import java.util.HashSet;
import java.util.Set;

public class ITISGraphExporter extends GraphExporter<ITISDataSource> {
    public ITISGraphExporter(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        createGeographicDivisionNodes(graph);

        return true;
    }

    private void createGeographicDivisionNodes(final Graph graph) {
        Set<String> uniqueDivisions = new HashSet<>();
        for (GeographicDivision division : dataSource.geographicDivisions)
            uniqueDivisions.add(division.value);
        for (String division : uniqueDivisions)
            graph.addNode("GeographicDivision", "id", division);
    }
}
