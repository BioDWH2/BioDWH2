package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.etl.GraphMapper;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class NodeMappingDescription {
    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public enum NodeType {
        ADVERSE_EVENT,
        ANATOMY,
        CLINICAL_TRIAL,
        COMPOUND,
        DIPLOTYPE,
        DISEASE,
        DRUG,
        DRUG_LABEL,
        DUMMY,
        GENE,
        GENE_VARIANT,
        GENOTYPE,
        HAPLOTYPE,
        METABOLITE,
        PATHWAY,
        PHENOTYPE,
        PHYTOCHEMICAL,
        PRODRUG,
        PROTEIN,
        PROTEIN_DOMAIN,
        PUBLICATION,
        RNA,
        SYMPTOM,
        TAXON,
        UNKNOWN,
        VARIANT
    }

    private static final Logger LOGGER = LogManager.getLogger(GraphMapper.class);
    private static final Map<IdentifierType, Set<Class<?>>> loggedIdentifierMismatchTypes = new HashMap<>();

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

    public <T extends Collection<String>> void addNames(final T names) {
        if (names != null)
            for (final String name : names)
                addName(name);
    }

    public void addIdentifier(final IdentifierType type, final Collection<?> ids) {
        if (ids != null) {
            for (final Object id : ids) {
                checkIdentifierRecommendedTypeIfAvailable(type, id);
                addIdentifierWithoutChecks(type.prefix, id.toString());
            }
        }
    }

    private void checkIdentifierRecommendedTypeIfAvailable(final IdentifierType type, final Object id) {
        if (id != null && type.expectedType != null && !type.expectedType.isAssignableFrom(id.getClass())) {
            final Set<Class<?>> loggedTypes = loggedIdentifierMismatchTypes.computeIfAbsent(type, k -> new HashSet<>());
            if (loggedTypes.contains(id.getClass()))
                return;
            loggedTypes.add(id.getClass());
            if (LOGGER.isWarnEnabled())
                LOGGER.warn(
                        "Identifier type '" + type + "' recommends java type '" + type.expectedType + "' but the id '" +
                        id + "' is of type '" + id.getClass() + "' (logged only once)");
        }
    }

    private void addIdentifierWithoutChecks(final String type, final String id) {
        identifier.computeIfAbsent(type, k -> new HashSet<>()).add(id);
        identifierCache = null;
    }

    public void addIdentifier(final IdentifierType type, final Long id) {
        if (id != null) {
            checkIdentifierRecommendedTypeIfAvailable(type, id);
            addIdentifierWithoutChecks(type.prefix, id.toString());
        }
    }

    public void addIdentifier(final String type, final String id) {
        if (StringUtils.isNotEmpty(id))
            addIdentifierWithoutChecks(type, id);
    }

    public void addIdentifier(final IdentifierType type, final String id) {
        if (StringUtils.isNotEmpty(id)) {
            checkIdentifierRecommendedTypeIfAvailable(type, id);
            addIdentifierWithoutChecks(type.prefix, id);
        }
    }

    public void addIdentifier(final IdentifierType type, final Integer id) {
        if (id != null) {
            checkIdentifierRecommendedTypeIfAvailable(type, id);
            addIdentifierWithoutChecks(type.prefix, id.toString());
        }
    }

    public void addIdentifier(final String type, final Long id) {
        if (id != null)
            addIdentifierWithoutChecks(type, id.toString());
    }

    public void addIdentifier(final String type, final Integer id) {
        if (id != null)
            addIdentifierWithoutChecks(type, id.toString());
    }

    public boolean hasIdentifiers() {
        return !identifier.isEmpty();
    }

    public Set<String> getIdentifiers() {
        if (identifierCache != null)
            return identifierCache;
        identifierCache = new HashSet<>();
        for (final String identifierType : identifier.keySet())
            for (final String id : identifier.get(identifierType))
                identifierCache.add(identifierType + ':' + id);
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

    public Map<String, Object> getAdditionalProperties() {
        return null;
    }
}
