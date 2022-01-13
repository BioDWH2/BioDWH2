package de.unibi.agbi.biodwh2.adrecs.etl;

import de.unibi.agbi.biodwh2.adrecs.ADReCSDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;

public class ADReCSGraphExporter extends GraphExporter<ADReCSDataSource> {
    public ADReCSGraphExporter(final ADReCSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Drug", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ADR", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ADR", "adrecs_id", false, IndexDescription.Type.UNIQUE));

        return true;
    }
}
