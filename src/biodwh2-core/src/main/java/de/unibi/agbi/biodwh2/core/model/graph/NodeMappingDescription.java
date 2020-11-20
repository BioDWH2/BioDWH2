package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.model.IdentifierType;

import java.util.*;

public final class NodeMappingDescription {
    public enum NodeType {
        DUMMY,
        UNKNOWN,
        GENE,
        DRUG,
        COMPOUND,
        DISEASE,
        SIDE_EFFECT,
        VARIANT,
        HAPLOTYPE,
        PATHWAY
    }

    public NodeType type;
    private final Map<IdentifierType, Set<String>> identifier;
    private Set<String> identifierCache;
    private final Set<String> names;

    public NodeMappingDescription() {
        this(NodeType.UNKNOWN);
    }

    public NodeMappingDescription(final NodeType type) {
        this.type = type;
        identifier = new HashMap<>();
        names = new HashSet<>();
    }

    public void addName(final String name) {
        names.add(name);
    }

    public void addNames(final String... names) {
        this.names.addAll(Arrays.asList(names));
    }

    public void addNames(final Collection<String> names) {
        this.names.addAll(names);
    }

    public void addIdentifier(final IdentifierType type, final String value) {
        if (!identifier.containsKey(type))
            identifier.put(type, new HashSet<>());
        identifier.get(type).add(value);
        identifierCache = null;
    }

    public Set<String> getIdentifiers() {
        if (identifierCache != null)
            return identifierCache;
        identifierCache = new HashSet<>();
        for (final IdentifierType identifierType : identifier.keySet())
            for (final String id : identifier.get(identifierType))
                identifierCache.add(identifierType.prefix + ":" + id);
        return identifierCache;
    }

    public Set<String> getNames() {
        return new HashSet<>(names);
    }

    public boolean matches(final NodeMappingDescription other) {
        return other.type.equals(type) && matchesAnyIdentifier(other);
    }

    private boolean matchesAnyIdentifier(final NodeMappingDescription other) {
        for (final IdentifierType identifierType : identifier.keySet())
            if (isIdentifierTypeIntersecting(other, identifierType))
                return true;
        return false;
    }

    private boolean isIdentifierTypeIntersecting(final NodeMappingDescription other,
                                                 final IdentifierType identifierType) {
        if (other.identifier.containsKey(identifierType))
            for (final String id : identifier.get(identifierType))
                if (other.identifier.get(identifierType).contains(id))
                    return true;
        return false;
    }
}
