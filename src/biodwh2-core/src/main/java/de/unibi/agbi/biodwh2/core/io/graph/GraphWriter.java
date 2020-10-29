package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

public abstract class GraphWriter {
    public abstract boolean write(final Workspace workspace, final DataSource dataSource, final Graph graph);
}
