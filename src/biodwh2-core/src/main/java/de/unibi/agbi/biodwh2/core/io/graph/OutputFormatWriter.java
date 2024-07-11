package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

import java.nio.file.Path;

public abstract class OutputFormatWriter {
    public final void removeOldExport(final Workspace workspace, final DataSource dataSource) {
        final var path = workspace.getDataSourceDirectory(dataSource.getId()).resolve("intermediate." + getExtension());
        FileUtils.safeDelete(path);
    }

    public final void removeOldExport(final Workspace workspace, final String name) {
        final var path = workspace.getSourcesDirectory().resolve(name + "." + getExtension());
        FileUtils.safeDelete(path);
    }

    public final Path getOutputFilePath(final Workspace workspace, final String name) {
        return workspace.getSourcesDirectory().resolve(name + "." + getExtension());
    }

    public abstract String getId();

    public abstract String getExtension();

    public final boolean write(final Workspace workspace, final DataSource dataSource, final Graph graph) {
        final var path = workspace.getDataSourceDirectory(dataSource.getId()).resolve("intermediate." + getExtension());
        FileUtils.safeDelete(path);
        return write(path, graph);
    }

    public final boolean write(final Workspace workspace, final String name, final Graph graph) {
        return write(getOutputFilePath(workspace, name), graph);
    }

    public abstract boolean write(final Path outputFilePath, final Graph graph);
}
