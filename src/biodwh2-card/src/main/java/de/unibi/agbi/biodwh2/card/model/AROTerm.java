package de.unibi.agbi.biodwh2.card.model;

import java.util.ArrayList;
import java.util.List;

public final class AROTerm {
    public String id;
    public String name;
    public String namespace;
    public String def;
    public final List<String> isA = new ArrayList<>();
    public final List<String> synonyms = new ArrayList<>();
    public final List<String> xrefs = new ArrayList<>();

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
        mergeUnique(isA, other.isA);
        mergeUnique(synonyms, other.synonyms);
        mergeUnique(xrefs, other.xrefs);
    }

    private void mergeUnique(final List<String> target, final List<String> source) {
        for (final String value : source) {
            if (value != null && !target.contains(value))
                target.add(value);
        }
    }
}

