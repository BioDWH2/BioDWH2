package de.unibi.agbi.biodwh2.iig.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.iig.IIGDataSource;
import de.unibi.agbi.biodwh2.iig.model.Ingredient;

import java.io.File;

public class IIGGraphExporter extends GraphExporter<IIGDataSource> {
    public static final String INGREDIENT_LABEL = "Ingredient";

    public IIGGraphExporter(final IIGDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        try {
            FileUtils.forEachZipEntry(new File(dataSource.resolveSourceFilePath(workspace, IIGUpdater.FILE_NAME)),
                                      ".csv", ((stream, entry) -> {
                        if ("IIR_OCOMM.csv".equals(entry.getName()))
                            FileUtils.openCsvWithHeader(stream, Ingredient.class, graph::addNodeFromModel);
                    }));
        } catch (Exception e) {
            throw new ExporterException("Failed to export '" + IIGUpdater.FILE_NAME + "'", e);
        }
        return true;
    }
}
