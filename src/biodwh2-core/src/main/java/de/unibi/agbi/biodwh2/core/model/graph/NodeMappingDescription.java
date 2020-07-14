package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.model.IdentifierType;

import java.util.*;

public final class NodeMappingDescription {
    public enum NodeType {
        Dummy,
        Unknown,
        Gene,
        Drug,
        Compound,
        Disease,
        SideEffect,
        Variant,
        Haplotype,
        Pathway
    }

    public NodeType type;
    private final Map<IdentifierType, Set<String>> identifier;

    public NodeMappingDescription() {
        this(NodeType.Unknown);
    }

    public NodeMappingDescription(final NodeType type) {
        this.type = type;
        identifier = new HashMap<>();
    }

    public void addIdentifier(IdentifierType type, String value) {
        if (!identifier.containsKey(type))
            identifier.put(type, new HashSet<>());
        identifier.get(type).add(value);
    }

    public List<String> getIdentifiers() {
        List<String> result = new ArrayList<>();
        for (IdentifierType identifierType : identifier.keySet())
            for (String id : identifier.get(identifierType))
                result.add(identifierType.prefix + ":" + id);
        return result;
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
