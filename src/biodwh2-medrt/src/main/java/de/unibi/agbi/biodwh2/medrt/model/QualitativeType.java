package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@GraphNodeLabel("QualitativeDefinition")
public final class QualitativeType {
    @GraphProperty("type")
    public String type;
    @GraphProperty("name")
    public String name;
    public String namespace;
}
