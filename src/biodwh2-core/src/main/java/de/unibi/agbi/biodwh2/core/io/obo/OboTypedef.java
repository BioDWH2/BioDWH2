package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * typedef-frame ::= [ nl ]
 *         '[Typedef]' nl
 *         id-Tag Relation-ID EOL
 *         { typedef-frame-clause EOL }
 *
 * typedef-frame-clause ::=
 *         is_anonymous-BT
 *         | name-TVP
 *         | namespace-Tag OBONamespace
 *         | alt_id-Tag ID
 *         | def-Tag QuotedString ws XrefList
 *         | comment-TVP
 *         | subset-Tag Subset-ID
 *         | synonym-Tag QuotedString ws SynonymScope [ ws SynonymType-ID ] ws XrefList
 *         | xref-Tag Xref
 *         | property_value-Tag Relation-ID ws ( QuotedString ws XSD-Type | ID )
 *         | domain-Tag Class-ID
 *         | range-Tag Class-ID
 *         | builtin-BT
 *         | holds_over_chain-Tag Relation-ID ws Relation-ID
 *         | is_anti_symmetric-BT
 *         | is_cyclic-BT
 *         | is_reflexive-BT
 *         | is_symmetric-BT
 *         | is_transitive-BT
 *         | is_functional-BT
 *         | is_inverse_functional-BT
 *         | is_a-Tag Rel-ID
 *         | intersection_of-Tag Rel-ID
 *         | union_of-Tag Rel-ID
 *         | equivalent_to-Tag Rel-ID
 *         | disjoint_from-Tag Rel-ID
 *         | inverse_of-Tag Rel-ID
 *         | transitive_over-Tag Relation-ID
 *         | equivalent_to_chain-Tag Relation-ID ws Relation-ID
 *         | disjoint_over-Tag Rel-ID
 *         | relationship-Tag Rel-ID Rel-ID
 *         | is-obsolete-BT
 *         | replaced_by-Tag Rel-ID
 *         | consider-Tag ID
 *         | created_by-TVP
 *         | creation_date-Tag ISO-8601-DateTime
 *         | expand_assertion_to-Tag QuotedString ws XrefList
 *         | expand_expression_to-Tag QuotedString ws XrefList
 *         | is_metadata_tag-BT
 *         | is_class_level-BT
 */
@SuppressWarnings("unused")
public final class OboTypedef extends OboEntry {
    OboTypedef() {
        super("Typedef");
    }

    public String getId() {
        return getFirst("id");
    }

    public Boolean isAnonymous() {
        return getBooleanValue("is_anonymous");
    }

    public String getName() {
        return getFirst("name");
    }

    public String getNamespace() {
        return getFirst("namespace");
    }

    public Boolean isObsolete() {
        return getBooleanValue("is-obsolete");
    }

    public String getCreatedBy() {
        return getFirst("created_by");
    }

    public String getCreationDate() {
        return getFirst("creation_date");
    }
}
