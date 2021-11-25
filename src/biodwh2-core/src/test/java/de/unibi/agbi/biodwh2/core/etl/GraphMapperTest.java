package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphMapperTest {
    private TestDataSource dataSource;

    @BeforeEach
    void setup() {
        dataSource = new TestDataSource();
    }

    @Test
    void mapNode() throws IOException {
        dataSource.mappingDescriber = new TestMappingDescriber(dataSource) {
            @Override
            public NodeMappingDescription[] describe(final Graph graph, final Node node,
                                                     final String localMappingLabel) {
                if ("Drug".equals(localMappingLabel)) {
                    final NodeMappingDescription description = new NodeMappingDescription(
                            NodeMappingDescription.NodeType.DRUG);
                    description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id"));
                    description.addName(node.getProperty("name"));
                    return new NodeMappingDescription[]{description};
                }
                return null;
            }

            @Override
            protected String[] getNodeMappingLabels() {
                return new String[]{"Drug"};
            }
        };
        final Graph graph = Graph.createTempGraph();
        graph.addNode(dataSource.getId() + "_Drug", "id", "D4693", "name", "TestDrug");
        new GraphMapper().mapGraph(graph, new DataSource[]{dataSource}, false, 0);
        final List<Node> nodes = new ArrayList<>();
        for (final Node node : graph.findNodes(NodeMappingDescription.NodeType.DRUG.name()))
            nodes.add(node);
        assertEquals(1, nodes.size());
        assertArrayEquals(new String[]{"Dummy:D4693"}, nodes.get(0).<Collection<String>>getProperty("ids").toArray());
        assertArrayEquals(new String[]{"TestDrug"}, nodes.get(0).<Collection<String>>getProperty("names").toArray());
    }

    @Test
    void mapNodes() throws IOException {
        dataSource.mappingDescriber = new TestMappingDescriber(dataSource) {
            @Override
            public NodeMappingDescription[] describe(final Graph graph, final Node node,
                                                     final String localMappingLabel) {
                if ("Drug".equals(localMappingLabel)) {
                    final NodeMappingDescription description = new NodeMappingDescription(
                            NodeMappingDescription.NodeType.DRUG);
                    description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id"));
                    description.addIdentifier("Shared", node.<String>getProperty("id2"));
                    description.addName(node.getProperty("name"));
                    return new NodeMappingDescription[]{description};
                }
                return null;
            }

            @Override
            protected String[] getNodeMappingLabels() {
                return new String[]{"Drug"};
            }
        };
        final Graph graph = Graph.createTempGraph();
        int sharedIndex = 0;
        for (int i = 0; i < 1000; i++) {
            if (i % 100 == 0)
                sharedIndex++;
            graph.addNode(dataSource.getId() + "_Drug", "id", "D" + i, "name", "Drug" + i, "id2", "S" + sharedIndex);
        }
        new GraphMapper().mapGraph(graph, new DataSource[]{dataSource}, false, 0);
        final List<Node> nodes = new ArrayList<>();
        for (final Node node : graph.findNodes(NodeMappingDescription.NodeType.DRUG.name()))
            nodes.add(node);
        assertEquals(10, nodes.size());
        for (final Node node : nodes) {
            final Collection<String> ids = node.getProperty("ids");
            final Collection<String> names = node.getProperty("names");
            assertNotNull(ids);
            assertNotNull(names);
            final String sharedId = ids.stream().filter(id -> id.startsWith("Shared")).findFirst().get();
            final int sharedIdNumber = Integer.parseInt(sharedId.split(":")[1].substring(1));
            for (int i = 0; i < 100; i++) {
                final int drugIndex = (sharedIdNumber - 1) * 100 + i;
                assertTrue(ids.contains("Dummy:D" + drugIndex));
                assertTrue(names.contains("Drug" + drugIndex));
            }
        }
    }

    private static class TestMappingDescriber extends MappingDescriber {
        public TestMappingDescriber(final DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
            return new NodeMappingDescription[0];
        }

        @Override
        public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
            return null;
        }

        @Override
        protected String[] getNodeMappingLabels() {
            return new String[0];
        }

        @Override
        protected PathMapping[] getEdgePathMappings() {
            return new PathMapping[0];
        }
    }

    private static class TestDataSource extends DataSource {
        public GraphExporter<TestDataSource> graphExporter;
        public MappingDescriber mappingDescriber;

        @Override
        public String getId() {
            return "GraphMapperTest";
        }

        @Override
        public DevelopmentState getDevelopmentState() {
            return null;
        }

        @Override
        protected Updater<? extends DataSource> getUpdater() {
            return null;
        }

        @Override
        protected Parser<? extends DataSource> getParser() {
            return null;
        }

        @Override
        protected GraphExporter<? extends DataSource> getGraphExporter() {
            return graphExporter;
        }

        @Override
        public MappingDescriber getMappingDescriber() {
            return mappingDescriber;
        }

        @Override
        protected void unloadData() {
        }
    }
}