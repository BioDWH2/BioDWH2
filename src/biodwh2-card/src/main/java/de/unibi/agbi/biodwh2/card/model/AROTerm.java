package de.unibi.agbi.biodwh2.card.model;

import java.util.ArrayList;
import java.util.List;

public final class AROTerm {
    public String id;
    public String name;
    public String namespace;
    public String def;
    public String categoryAroAccession;
    public final List<String> isA = new ArrayList<>();
    public final List<String> synonyms = new ArrayList<>();
    public final List<String> xrefs = new ArrayList<>();
    public final List<Relationship> relationships = new ArrayList<>();

    public void mergeFrom(final AROTerm other) {
        if (other == null)
            return;
        if (id == null)
            id = other.id;
        if (name == null)
            name = other.name;
        if (namespace == null)
            namespace = other.namespace;
        if (def == null)
            def = other.def;
        if (categoryAroAccession == null)
            categoryAroAccession = other.categoryAroAccession;
        mergeUnique(isA, other.isA);
        mergeUnique(synonyms, other.synonyms);
        mergeUnique(xrefs, other.xrefs);
        mergeUniqueRelationships(relationships, other.relationships);
    }

    private void mergeUnique(final List<String> target, final List<String> source) {
        for (final String value : source) {
            if (value != null && !target.contains(value))
                target.add(value);
        }
    }

    private void mergeUniqueRelationships(final List<Relationship> target, final List<Relationship> source) {
        for (final Relationship value : source) {
            if (value != null && !containsRelationship(target, value))
                target.add(value);
        }
    }

    private boolean containsRelationship(final List<Relationship> relationships, final Relationship value) {
        for (final Relationship relationship : relationships) {
            if (relationship == null)
                continue;
            if (equals(relationship.name, value.name) && equals(relationship.targetId, value.targetId))
                return true;
        }
        return false;
    }

    private boolean equals(final String left, final String right) {
        if (left == null)
            return right == null;
        return left.equals(right);
    }

    public static final class Relationship {
        public String name;
        public String targetId;
    }
}

