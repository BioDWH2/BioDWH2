package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.model.graph.*;

public abstract class MappingDescriber {
    public abstract NodeMappingDescription describe(final Graph graph, final Node node);

    public abstract EdgeMappingDescription describe(final Graph graph, final Edge edge);
}
