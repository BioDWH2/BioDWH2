package de.unibi.agbi.biodwh2.expasy.hamap.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.expasy.UniRuleDataClass;
import de.unibi.agbi.biodwh2.expasy.UniRuleEntry;
import de.unibi.agbi.biodwh2.expasy.UniRuleReader;
import de.unibi.agbi.biodwh2.expasy.hamap.HamapDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HamapGraphExporter extends GraphExporter<HamapDataSource> {
    static final String ACCESSION_KEY = "accession";

    public HamapGraphExporter(final HamapDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Rule", ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        try (final UniRuleReader reader = new UniRuleReader(
                FileUtils.openInput(workspace, dataSource, HamapUpdater.HAMAP_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final UniRuleEntry entry : reader)
                exportRuleEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportRuleEntry(final Graph graph, final UniRuleEntry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Rule");
        builder.withProperty(ACCESSION_KEY, entry.accession);
        if (entry.secondaryAccessions != null && entry.secondaryAccessions.length > 0)
            builder.withProperty("secondary_accessions", entry.secondaryAccessions);
        builder.withProperty("data_classes", Arrays.stream(entry.dataClasses).map(UniRuleDataClass::getValue)
                                                   .toArray(String[]::new));
        // TODO
        builder.build();
    }
}
