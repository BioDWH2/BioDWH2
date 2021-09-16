package de.unibi.agbi.biodwh2.core.model.graph.migration;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphMigratorTest {
    @Test
    void peekVersion() throws IOException {
        final Graph tempGraph = Graph.createTempGraph();
        tempGraph.close();
        final Integer version = GraphMigrator.peekVersion(tempGraph.getFilePath());
        assertNotNull(version);
        assertEquals(Graph.VERSION, version);
    }
}