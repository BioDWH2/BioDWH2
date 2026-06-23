package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.ReconExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

public class ChEBIReconExporter extends ReconExporter<ChEBIDataSource> {
    public ChEBIReconExporter(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getReconVersion() {
        return 1;
    }

    @Override
    public void recon(final Workspace workspace, final Graph graph) {
    }
}
