package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ExperimentalFactorOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final Pattern VERSION_PATTERN = Pattern.compile("v([0-9]+)\\.([0-9]+)\\.([0-9]+)");
    private static final String FILE_NAME = "efo.obo";

    @Override
    public String getId() {
        return "ExperimentalFactorOntology";
    }

    @Override
    public String getLicense() {
        return License.APACHE_2_0.getName();
    }

    @Override
    public String getFullName() {
        return "Experimental Factor Ontology (EFO)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EFOMappingDescriber(this);
    }

    @Override
    protected String getDownloadUrl() {
        return "http://www.ebi.ac.uk/efo/" + FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = VERSION_PATTERN.matcher(dataVersion);
        return matcher.find() ? new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                            Integer.parseInt(matcher.group(3))) : null;
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    private static class EFOMappingDescriber extends MappingDescriber {
        private Long diseaseNodeId;

        public EFOMappingDescriber(final DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
            if (diseaseNodeId == null)
                diseaseNodeId = graph.findNode("ExperimentalFactorOntology_Term", GraphExporter.ID_KEY, "EFO:0000408")
                                     .getId();
            if (OntologyGraphExporter.TERM_LABEL.equals(localMappingLabel)) {
                if (isNodeChildOfDisease(graph, node.getId())) {
                    final NodeMappingDescription description = new NodeMappingDescription(
                            NodeMappingDescription.NodeType.DISEASE);
                    final String id = node.getProperty(GraphExporter.ID_KEY);
                    if (id != null) {
                        final String[] idParts = StringUtils.split(id, ':');
                        if ("EFO".equals(idParts[0]))
                            description.addIdentifier(IdentifierType.EFO, idParts[1]);
                        else if ("Orphanet".equals(idParts[0]))
                            description.addIdentifier(IdentifierType.ORPHANET, idParts[1]);
                        else if ("MONDO".equals(idParts[0]))
                            description.addIdentifier(IdentifierType.MONDO, idParts[1]);
                        else if ("DOID".equals(idParts[0]))
                            description.addIdentifier("DOID", idParts[1]);
                        else if ("HP".equals(idParts[0]))
                            description.addIdentifier("HP", idParts[1]);
                        else if ("NCIT".equals(idParts[0]))
                            description.addIdentifier("NCIT", idParts[1]);
                    }
                    // TODO: xrefs
                    description.addName(node.getProperty("name"));
                    return new NodeMappingDescription[]{description};
                }
            }
            return null;
        }

        private boolean isNodeChildOfDisease(final Graph graph, final long nodeId) {
            for (final long parentId : graph.getAdjacentNodeIdsForEdgeLabel(nodeId, "ExperimentalFactorOntology_IS_A",
                                                                            EdgeDirection.FORWARD))
                if (diseaseNodeId == parentId || isNodeChildOfDisease(graph, parentId))
                    return true;
            return false;
        }

        @Override
        public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
            return null;
        }

        @Override
        protected String[] getNodeMappingLabels() {
            return new String[]{OntologyGraphExporter.TERM_LABEL};
        }

        @Override
        protected PathMapping[] getEdgePathMappings() {
            return new PathMapping[0];
        }
    }
}
