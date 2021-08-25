package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.json.NDJsonObjectMapper;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import de.unibi.agbi.biodwh2.opentargets.model.Disease;
import de.unibi.agbi.biodwh2.opentargets.model.Molecule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class OpenTargetsGraphExporter extends GraphExporter<OpenTargetsDataSource> {
    public static final String MOLECULE_LABEL = "Molecule";
    public static final String DISEASE_LABEL = "Disease";

    public OpenTargetsGraphExporter(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MOLECULE_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, "id", IndexDescription.Type.UNIQUE));
        exportMolecules(workspace, graph);
        exportDiseases(workspace, graph);
        return false;
    }

    private void exportMolecules(final Workspace workspace, final Graph graph) {
        // TODO: relationships, links, references, etc.
        for (final Molecule molecule : openJsonFile(workspace, OpenTargetsUpdater.MOLECULE_FILE_NAME, Molecule.class))
            graph.addNodeFromModel(molecule);
    }

    private <T> Iterable<T> openJsonFile(final Workspace workspace, final String fileName, final Class<T> type) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        final NDJsonObjectMapper mapper = new NDJsonObjectMapper();
        try {
            final Iterator<T> iterator = mapper.readValues(new FileInputStream(filePath), type);
            return () -> iterator;
        } catch (FileNotFoundException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        // TODO: ontology, ontology.sources, synonyms
        for (final Disease disease : openJsonFile(workspace, OpenTargetsUpdater.DISEASES_FILE_NAME, Disease.class))
            graph.addNodeFromModel(disease);
        for (final Disease disease : openJsonFile(workspace, OpenTargetsUpdater.DISEASES_FILE_NAME, Disease.class)) {
            final Node node = graph.findNode(DISEASE_LABEL, "id", disease.id);
            for (final String childId : disease.children) {
                final Node child = graph.findNode(DISEASE_LABEL, "id", childId);
                graph.addEdge(child, node, "CHILD_OF");
            }
        }
    }
}
