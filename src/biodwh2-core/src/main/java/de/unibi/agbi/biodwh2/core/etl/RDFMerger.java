package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RDFMerger extends Merger {
    @Override
    public final boolean merge(final Workspace workspace, final List<DataSource> dataSources,
                               final String outputFilePath) throws MergerException {
        try (PrintWriter writer = new PrintWriter(outputFilePath)) {
            for (DataSource dataSource : dataSources)
                appendDataSource(workspace, dataSource, writer);
        } catch (IOException e) {
            throw new MergerException("Failed to merge RDF graphs", e);
        }
        return true;
    }

    private void appendDataSource(final Workspace workspace, final DataSource dataSource,
                                  final PrintWriter writer) throws IOException {
        try (LineIterator iterator = getRdfFileIterator(workspace, dataSource)) {
            iterator.forEachRemaining(writer::println);
            dataSource.getMetadata().mergeSuccessful = true;
        }
    }

    private LineIterator getRdfFileIterator(final Workspace workspace, final DataSource dataSource) throws IOException {
        final String filePath = dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.RDF_TURTLE);
        return FileUtils.lineIterator(new File(filePath), StandardCharsets.UTF_8.name());
    }
}
