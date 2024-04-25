package de.unibi.agbi.biodwh2.themarker.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfEntry;
import de.unibi.agbi.biodwh2.core.io.sdf.SdfReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.themarker.TheMarkerDataSource;
import de.unibi.agbi.biodwh2.themarker.model.Association;
import de.unibi.agbi.biodwh2.themarker.model.Disease;
import de.unibi.agbi.biodwh2.themarker.model.Drug;
import de.unibi.agbi.biodwh2.themarker.model.Marker;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class TheMarkerGraphExporter extends GraphExporter<TheMarkerDataSource> {
    public static final String DRUG_LABEL = "Drug";
    static final String DISEASE_LABEL = "Disease";
    public static final String MARKER_LABEL = "Marker";
    static final String ASSOCIATION_LABEL = "Association";

    public TheMarkerGraphExporter(final TheMarkerDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MARKER_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportDiseases(workspace, graph);
        exportDrugs(workspace, graph);
        exportMarker(workspace, graph);
        exportAssociations(workspace, graph);
        return false;
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, TheMarkerUpdater.DISEASES_FILE_NAME, Disease.class,
                                        (entry) -> exportDisease(graph, entry));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TheMarkerUpdater.DISEASES_FILE_NAME + "'", e);
        }
    }

    private void exportDisease(final Graph graph, final Disease entry) {
        String icd11 = StringUtils.stripStart(StringUtils.replace(entry.icd11, "ICD-11", ""), ": \t");
        if (StringUtils.isEmpty(icd11) || "N.A.".equalsIgnoreCase(icd11))
            graph.addNode(DISEASE_LABEL, ID_KEY, entry.id, "name", entry.name, "class", entry._class);
        else
            graph.addNode(DISEASE_LABEL, ID_KEY, entry.id, "name", entry.name, "icd11", entry.icd11, "class",
                          entry._class);
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        final var drugIdStructureMap = new HashMap<String, String>();
        try (final var reader = new SdfReader(
                FileUtils.openInput(workspace, dataSource, TheMarkerUpdater.DRUGS_SDF_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final SdfEntry entry : reader)
                drugIdStructureMap.put(entry.getTitle(), entry.getConnectionTable());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TheMarkerUpdater.DRUGS_SDF_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, TheMarkerUpdater.DRUGS_FILE_NAME, Drug.class,
                                        (entry) -> exportDrug(graph, drugIdStructureMap.get(entry.id), entry));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TheMarkerUpdater.DRUGS_FILE_NAME + "'", e);
        }
    }

    private void exportDrug(final Graph graph, final String structure, final Drug entry) {
        if (structure != null)
            graph.addNodeFromModel(entry, "structure", structure);
        else
            graph.addNodeFromModel(entry);
    }

    private void exportMarker(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, TheMarkerUpdater.MARKER_FILE_NAME, Marker.class,
                                        (entry) -> exportMarker(graph, entry));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TheMarkerUpdater.MARKER_FILE_NAME + "'", e);
        }
    }

    private void exportMarker(final Graph graph, final Marker entry) {
        graph.addNodeFromModel(entry);
    }

    private void exportAssociations(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, TheMarkerUpdater.ASSOCIATIONS_FILE_NAME,
                                        Association.class, (entry) -> {
                        // TODO
                    });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TheMarkerUpdater.ASSOCIATIONS_FILE_NAME + "'", e);
        }
    }
}
