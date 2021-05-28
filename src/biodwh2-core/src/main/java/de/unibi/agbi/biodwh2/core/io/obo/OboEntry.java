package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * Shared OBO entry properties for Term, Typedef, and Instance
 */
public class OboEntry extends OboStructure {
    private final String type;

    OboEntry(final String type) {
        super();
        this.type = type;
    }

    public final String getType() {
        return type;
    }

    /**
     * cardinality 1
     *
     * @return ID (Class-ID for Term, Rel-ID for Typedef, Instance-ID for Instance)
     */
    public final String getId() {
        return getFirst("id");
    }

    /**
     * cardinality 0-1
     *
     * @return QuotedString ws XrefList
     */
    public final String getDef() {
        return getFirst("def");
    }

    /**
     * cardinality 0-1
     *
     * @return TVP
     */
    public final String getName() {
        return getFirst("name");
    }

    /**
     * cardinality 1
     *
     * @return OBONamespace
     */
    public final String getNamespace() {
        return getFirst("namespace");
    }

    /**
     * cardinality 0-1
     *
     * @return TVP
     */
    public final String getComment() {
        return getFirst("comment");
    }

    /**
     * cardinality 0-1
     *
     * @return TVP
     */
    public final String getCreatedBy() {
        return getFirst("created_by");
    }

    /**
     * cardinality 0-1
     *
     * @return ISO-8601-DateTime
     */
    public final String getCreationDate() {
        return getFirst("creation_date");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public final Boolean isAnonymous() {
        return getBooleanValue("is_anonymous");
    }

    /**
     * cardinality 0-1
     *
     * @return BT
     */
    public final Boolean isObsolete() {
        return getBooleanValue("is_obsolete");
    }

    /**
     * cardinality *
     *
     * @return Xref[]
     */
    public final String[] getXrefs() {
        return get("xref");
    }

    /**
     * cardinality *
     *
     * @return ID[]
     */
    public final String[] getAltIds() {
        return get("alt_id");
    }

    /**
     * cardinality *
     *
     * @return ID[] (Class-ID for Term, Rel-ID for Typedef, Instance-ID for Instance)
     */
    public final String[] replacedBy() {
        return get("replaced_by");
    }

    /**
     * cardinality *
     *
     * @return ID[]
     */
    public final String[] consider() {
        return get("consider");
    }

    /**
     * cardinality *
     *
     * @return (QuotedString ws SynonymScope [ ws SynonymType-ID ] ws XrefList)[]
     */
    public final String[] getSynonyms() {
        return get("synonym");
    }

    /**
     * cardinality *
     *
     * @return Subset-ID[]
     */
    public final String[] getSubsets() {
        return get("subset");
    }

    /**
     * cardinality *
     *
     * @return (Rel-ID ws ( QuotedString ws XSD-Type | ID))[]
     */
    public final String[] getPropertyValues() {
        return get("property_value");
    }
}
