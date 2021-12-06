package de.unibi.agbi.biodwh2.omim.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.omim.OMIMDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OMIMGraphExporter extends GraphExporter<OMIMDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OMIMGraphExporter.class);
    static final String GENE_LABEL = "Gene";
    static final String PHENOTYPE_LABEL = "Phenotype";

    public OMIMGraphExporter(final OMIMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "mim_number", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, "mim_number", IndexDescription.Type.UNIQUE));
        return false;
    }
}
