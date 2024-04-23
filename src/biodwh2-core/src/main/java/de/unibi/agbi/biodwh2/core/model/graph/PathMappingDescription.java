package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;
import java.util.Map;

public final class PathMappingDescription {
    @SuppressWarnings("unused")
    public enum EdgeType {
        UNKNOWN,
        DUMMY,
        TARGETS,
        INDICATES,
        CONTRAINDICATES,
        INDUCES,
        INTERACTS,
        TRANSCRIBES_TO,
        TRANSLATES_TO,
        INVESTIGATES,
        ASSOCIATED_WITH
    }

    private final String type;
    private Map<String, Object> additionalProperties;

    public PathMappingDescription(final EdgeType type) {
        this.type = type.name();
    }

    public PathMappingDescription(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(final String key, final Object value) {
        if (value == null)
            return;
        if (additionalProperties == null)
            additionalProperties = new HashMap<>();
        additionalProperties.put(key, value);
    }
}
