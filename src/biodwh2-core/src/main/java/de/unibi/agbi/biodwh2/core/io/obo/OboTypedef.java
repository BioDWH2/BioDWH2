package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * OBO Typedef definition
 * <p/>
 * http://purl.obolibrary.org/obo/oboformat/spec.html#3.4
 */
public final class OboTypedef extends OboEntry {
    OboTypedef() {
        super("Typedef");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isMetadataTag() {
        return getBooleanValue("is_metadata_tag");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isCyclic() {
        return getBooleanValue("is_cyclic");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isReflexive() {
        return getBooleanValue("is_reflexive");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isSymmetric() {
        return getBooleanValue("is_symmetric");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isAntiSymmetric() {
        return getBooleanValue("is_anti_symmetric");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isTransitive() {
        return getBooleanValue("is_transitive");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isFunctional() {
        return getBooleanValue("is_functional");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isInverseFunctional() {
        return getBooleanValue("is_inverse_functional");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public Boolean isClassLevel() {
        return getBooleanValue("is_class_level");
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
     * @return Rel-ID[]
     */
    public String[] isA() {
        return get("is_a");
    }

    /**
     * cardinality *
     *
     * @return Rel-ID[]
     */
    public String[] equivalentTo() {
        return get("equivalent_to");
    }

    /**
     * cardinality *
     *
     * @return Rel-ID[]
     */
    public String[] disjointFrom() {
        return get("disjoint_from");
    }

    /**
     * cardinality 0, 2-*
     *
     * @return Rel-ID[]
     */
    public String[] unionOf() {
        return get("union_of");
    }

    /**
     * cardinality 0, 2-*
     *
     * @return Rel-ID[]
     */
    public String[] intersectionOf() {
        return get("intersection_of");
    }

    /**
     * cardinality *
     * <p>
     * disjoint_over-Tag Rel-ID
     *
     * @return Rel-ID[]
     */
    public String[] inverseOf() {
        return get("inverse_of");
    }

    /**
     * cardinality *
     *
     * @return Rel-ID[]
     */
    public String[] transitiveOver() {
        return get("transitive_over");
    }

    /**
     * cardinality *
     *
     * @return Rel-ID[]
     */
    public String[] disjointOver() {
        return get("disjoint_over");
    }

    /**
     * cardinality 0-1
     *
     * @return Class-ID
     */
    public String getDomain() {
        return getFirst("domain");
    }

    /**
     * cardinality 0-1
     *
     * @return Class-ID
     */
    public String getRange() {
        return getFirst("range");
    }

    /**
     * cardinality *
     *
     * @return (Rel-ID ws Rel-ID)[]
     */
    public String[] getRelationships() {
        return get("relationship");
    }

    /**
     * cardinality *
     *
     * @return (Rel-ID ws Rel-ID)[]
     */
    public String[] holdsOverChain() {
        return get("holds_over_chain");
    }

    /**
     * cardinality *
     *
     * @return (Rel-ID ws Rel-ID)[]
     */
    public String[] equivalentToChain() {
        return get("equivalent_to_chain");
    }

    /**
     * cardinality *
     *
     * @return (QuotedString ws XrefList)[]
     */
    public String[] expandAssertionTo() {
        return get("expand_assertion_to");
    }

    /**
     * cardinality *
     *
     * @return (QuotedString ws XrefList)[]
     */
    public String[] expandExpressionTo() {
        return get("expand_expression_to");
    }
}
