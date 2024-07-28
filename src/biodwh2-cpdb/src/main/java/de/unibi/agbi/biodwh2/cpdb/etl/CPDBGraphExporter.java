package de.unibi.agbi.biodwh2.cpdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MIGraphExporter;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.cpdb.CPDBDataSource;

import java.io.IOException;
import java.io.InputStream;

public class CPDBGraphExporter extends MIGraphExporter<CPDBDataSource> {
    public CPDBGraphExporter(final CPDBDataSource dataSource) {
        super(dataSource, MIFormat.Xml25);
    }

    @Override
    protected void exportFiles(final Workspace workspace,
                               final ExportCallback<InputStream> callback) throws IOException {
        try (final InputStream stream = FileUtils.openGzip(workspace, dataSource, CPDBUpdater.PPI_PSI_MI_FILE_NAME)) {
            callback.accept(stream);
        }
    }
}
