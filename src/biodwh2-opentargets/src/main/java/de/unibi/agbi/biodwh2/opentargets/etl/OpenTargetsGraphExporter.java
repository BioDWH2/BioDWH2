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
import de.unibi.agbi.biodwh2.opentargets.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class OpenTargetsGraphExporter extends GraphExporter<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsGraphExporter.class);
    public static final String MOLECULE_LABEL = "Molecule";
    public static final String DISEASE_LABEL = "Disease";
    public static final String REFERENCE_LABEL = "Reference";
    public static final String MECHANISM_OF_ACTION_LABEL = "MechanismOfAction";
    public static final String DRUG_WARNING_LABEL = "DrugWarning";
    public static final String INDICATION_LABEL = "Indication";
    public static final String PATHWAY_LABEL = "Pathway";
    public static final String INDICATES_LABEL = "INDICATES";
    public static final String HAS_REFERENCE_LABEL = "HAS_REFERENCE";

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
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "id", IndexDescription.Type.NON_UNIQUE));
        exportMolecules(workspace, graph);
        exportDiseases(workspace, graph);
        exportMechanismsOfAction(workspace, graph);
        exportDrugWarnings(workspace, graph);
        exportIndications(workspace, graph);
        exportPathways(workspace, graph);
        return true;
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

    private void exportMechanismsOfAction(final Workspace workspace, final Graph graph) {
        for (final MechanismOfAction mechanism : openJsonFile(workspace,
                                                              OpenTargetsUpdater.MECHANISM_OF_ACTION_FILE_NAME,
                                                              MechanismOfAction.class)) {
            final Node node = graph.addNode(MECHANISM_OF_ACTION_LABEL, "action_type", mechanism.actionType,
                                            "mechanism_of_action", mechanism.mechanismOfAction);
            for (final String chemblId : mechanism.chemblIds) {
                final Node moleculeNode = graph.findNode(MOLECULE_LABEL, "id", chemblId);
                graph.addEdge(moleculeNode, node, "HAS_MECHANISM_OF_ACTION");
            }
            // TODO: targets
            for (final MechanismOfAction.Reference reference : mechanism.references) {
                if (reference.ids.length == reference.urls.length) {
                    for (int i = 0; i < reference.ids.length; i++) {
                        final Node referenceNode = getOrCreateReference(graph, reference.source, reference.ids[i],
                                                                        reference.urls[i]);
                        graph.addEdge(node, referenceNode, HAS_REFERENCE_LABEL);
                    }
                } else if (reference.ids.length > 0 && reference.urls.length == 0) {
                    for (final String id : reference.ids)
                        graph.addEdge(node, getOrCreateReference(graph, reference.source, id, null),
                                      HAS_REFERENCE_LABEL);
                } else if (reference.ids.length == 0) {
                    for (final String url : reference.urls)
                        graph.addEdge(node, getOrCreateReference(graph, reference.source, null, url),
                                      HAS_REFERENCE_LABEL);
                } else if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Failed to add reference with uneven ids and urls " + reference);
            }
        }
    }

    private Node getOrCreateReference(final Graph graph, final String source, final String id, final String url) {
        Node node = null;
        if (id != null)
            node = graph.findNode(REFERENCE_LABEL, "id", id, "source", source);
        else if (url != null)
            node = graph.findNode(REFERENCE_LABEL, "url", url, "source", source);
        if (node == null) {
            if (id != null && url != null)
                node = graph.addNode(REFERENCE_LABEL, "id", id, "source", source, "url", url);
            else if (id != null)
                node = graph.addNode(REFERENCE_LABEL, "id", id, "source", source);
            else if (url != null)
                node = graph.addNode(REFERENCE_LABEL, "url", url, "source", source);
        }
        return node;
    }

    private void exportDrugWarnings(final Workspace workspace, final Graph graph) {
        for (final DrugWarning warning : openJsonFile(workspace, OpenTargetsUpdater.DRUG_WARNINGS_FILE_NAME,
                                                      DrugWarning.class)) {
            final Node node = graph.addNodeFromModel(warning);
            for (final DrugWarning.Reference reference : warning.references)
                graph.addEdge(node, getOrCreateReference(graph, reference.refType, reference.refId, reference.refUrl),
                              HAS_REFERENCE_LABEL);
        }
    }

    private void exportIndications(final Workspace workspace, final Graph graph) {
        for (final Indication indication : openJsonFile(workspace, OpenTargetsUpdater.INDICATION_FILE_NAME,
                                                        Indication.class)) {
            final Node moleculeNode = graph.findNode(MOLECULE_LABEL, "id", indication.id);
            final List<String> approvedIndications = Arrays.asList(indication.approvedIndications);
            for (final Indication.Entry entry : indication.indications) {
                final Node diseaseNode = graph.findNode(DISEASE_LABEL, "id", entry.disease);
                final Node indicationNode = graph.addNode(INDICATION_LABEL, "approved",
                                                          approvedIndications.contains(entry.disease), "efo_name",
                                                          entry.efoName, "max_phase", entry.maxPhaseForIndication);
                graph.addEdge(moleculeNode, indicationNode, INDICATES_LABEL);
                graph.addEdge(indicationNode, diseaseNode, INDICATES_LABEL);
                for (final Indication.Reference reference : entry.references)
                    for (final String id : reference.ids)
                        graph.addEdge(indicationNode, getOrCreateReference(graph, reference.source, id, null),
                                      HAS_REFERENCE_LABEL);
            }
        }
    }

    private void exportPathways(final Workspace workspace, final Graph graph) {
        for (final Reactome reactome : openJsonFile(workspace, OpenTargetsUpdater.REACTOME_FILE_NAME, Reactome.class))
            graph.addNodeFromModel(reactome);
        for (final Reactome reactome : openJsonFile(workspace, OpenTargetsUpdater.REACTOME_FILE_NAME, Reactome.class)) {
            final Node node = graph.findNode(PATHWAY_LABEL, "id", reactome.id);
            for (final String parentId : reactome.parents)
                graph.addEdge(node, graph.findNode(PATHWAY_LABEL, "id", parentId), "CHILD_OF");
        }
        // TODO: path?
    }
}
