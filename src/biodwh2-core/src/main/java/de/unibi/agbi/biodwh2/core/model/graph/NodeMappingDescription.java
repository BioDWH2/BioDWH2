package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class NodeMappingDescription {
    @SuppressWarnings("unused")
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
        PATHWAY,
        TAXON,
        PUBLICATION,
        DRUG_LABEL,
        PROTEIN
    }

    private final String type;
    private final Map<String, Set<String>> identifier;
    private Set<String> identifierCache;
    private final Set<String> names;

    public NodeMappingDescription(final NodeType type) {
        this(type.name());
    }

    public NodeMappingDescription(final String type) {
        this.type = type;
        identifier = new HashMap<>();
        names = new HashSet<>();
    }

    public void addNames(final String... names) {
        if (names != null)
            for (final String name : names)
                addName(name);
    }

    public void addName(final String name) {
        if (StringUtils.isNotEmpty(name))
            names.add(name);
    }

    public void addNames(final Collection<String> names) {
        if (names != null)
            for (final String name : names)
                addName(name);
    }

    public void addIdentifier(final IdentifierType type, final Long longValue) {
        if (longValue != null)
            addIdentifier(type.prefix, longValue.toString());
    }

    public void addIdentifier(final String type, final String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (!identifier.containsKey(type))
                identifier.put(type, new HashSet<>());
            identifier.get(type).add(value);
            identifierCache = null;
        }
    }

    public void addIdentifier(final IdentifierType type, final String value) {
        if (StringUtils.isNotEmpty(value))
            addIdentifier(type.prefix, value);
    }

    public void addIdentifier(final IdentifierType type, final Integer intValue) {
        if (intValue != null)
            addIdentifier(type.prefix, intValue.toString());
    }

    public void addIdentifier(final String type, final Long longValue) {
        if (longValue != null)
            addIdentifier(type, longValue.toString());
    }

    public void addIdentifier(final String type, final Integer intValue) {
        if (intValue != null)
            addIdentifier(type, intValue.toString());
    }

    public Set<String> getIdentifiers() {
        if (identifierCache != null)
            return identifierCache;
        identifierCache = new HashSet<>();
        for (final String identifierType : identifier.keySet())
            for (final String id : identifier.get(identifierType))
                identifierCache.add(identifierType + ":" + id);
        return identifierCache;
    }

    public Set<String> getNames() {
        return new HashSet<>(names);
    }

    public boolean matches(final NodeMappingDescription other) {
        return other.type.equals(type) && matchesAnyIdentifier(other);
    }

    private boolean matchesAnyIdentifier(final NodeMappingDescription other) {
        for (final String identifierType : identifier.keySet())
            if (isIdentifierTypeIntersecting(other, identifierType))
                return true;
        return false;
    }

    private boolean isIdentifierTypeIntersecting(final NodeMappingDescription other, final String identifierType) {
        if (other.identifier.containsKey(identifierType))
            for (final String id : identifier.get(identifierType))
                if (other.identifier.get(identifierType).contains(id))
                    return true;
        return false;
    }

    public String getType() {
        return type;
    }
}
