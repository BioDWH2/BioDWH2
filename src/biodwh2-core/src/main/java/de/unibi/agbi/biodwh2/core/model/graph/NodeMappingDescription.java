package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.model.IdentifierType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class NodeMappingDescription {
    public enum NodeType {
        Unknown,
        Gene,
        Drug,
        Compound,
        Disease,
        SideEffect,
        Variant,
        Pathway
    }

    public NodeType type = NodeType.Unknown;
    public final Map<IdentifierType, Set<String>> identifier = new HashMap<>();

    public void addIdentifier(IdentifierType type, String value) {
        if (!identifier.containsKey(type))
            identifier.put(type, new HashSet<>());
        identifier.get(type).add(value);
    }

    public boolean matches(NodeMappingDescription other) {
        if (other.type.equals(type))
            for (IdentifierType identifierType : identifier.keySet())
                if (other.identifier.containsKey(identifierType)) {
                    Set<String> otherTypeIds = other.identifier.get(identifierType);
                    for (String id : identifier.get(identifierType))
                        if (otherTypeIds.contains(id))
                            return true;
                }
        return false;
    }
}
