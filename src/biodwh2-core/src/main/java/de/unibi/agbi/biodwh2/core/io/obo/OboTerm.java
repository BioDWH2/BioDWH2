package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * OBO Term definition
 * <p/>
 * http://purl.obolibrary.org/obo/oboformat/spec.html#3.3
 */
public final class OboTerm extends OboEntry {
    OboTerm() {
        super("Term");
    }

    /**
     * cardinality *
     *
     * @return BT[]
     */
    public Boolean[] builtin() {
        return getBooleanValues("builtin");
    }

    /**
     * cardinality *
     *
     * @return Class-ID[]
     */
    public String[] isA() {
        return get("is_a");
    }

    /**
     * cardinality *
     *
     * @return Class-ID[]
     */
    public String[] equivalentTo() {
        return get("equivalent_to");
    }

    /**
     * cardinality *
     *
     * @return Class-ID[]
     */
    public String[] disjointFrom() {
        return get("disjoint_from");
    }

    /**
     * cardinality 0, 2-*
     *
     * @return Class-ID[]
     */
    public String[] unionOf() {
        return get("union_of");
    }

    /**
     * cardinality 0, 2-*
     *
     * @return (Class - ID | Rel - ID ws Class - ID)[]
     */
    public String[] intersectionOf() {
        return get("intersection_of");
    }

    /**
     * cardinality *
     *
     * @return (Rel - ID ws ( QuotedString ws XSD - Type | ID))[]
     */
    public String[] getPropertyValues() {
        return get("property_value");
    }

    /**
     * cardinality *
     *
     * @return (Rel - ID ws Class - ID)[]
     */
    public String[] getRelationships() {
        return get("relationship");
    }
}
