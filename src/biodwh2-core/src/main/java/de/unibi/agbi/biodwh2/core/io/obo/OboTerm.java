package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * term-frame ::= nl*
 *         '[Term]' nl
 *         id-Tag Class-ID EOL
 *         { term-frame-clause EOL }
 *
 * term-frame-clause ::=
 *         is_anonymous-BT
 *         | name-TVP
 *         | namespace-Tag OBONamespace
 *         | alt_id-Tag ID
 *         | def-Tag QuotedString ws XrefList
 *         | comment-TVP
 *         | subset-Tag Subset-ID
 *         | synonym-Tag QuotedString ws SynonymScope [ ws SynonymType-ID ] XrefList
 *         | xref-Tag Xref
 *         | builtin-BT
 *         | property_value-Tag Relation-ID ws ( QuotedString ws XSD-Type | ID )
 *         | is_a-Tag Class-ID
 *         | intersection_of-Tag Class-ID
 *         | intersection_of-Tag Relation-ID ws Class-ID
 *         | union_of-Tag Class-ID
 *         | equivalent_to-Tag Class-ID
 *         | disjoint_from-Tag Class-ID
 *         | relationship-Tag Relation-ID ws Class-ID
 *         | is_obsolete-BT
 *         | replaced_by-Tag Class-ID
 *         | consider-Tag ID
 *         | created_by-TVP
 *         | creation_date-Tag ISO-8601-DateTime
 */
@SuppressWarnings("unused")
public final class OboTerm extends OboEntry {
    OboTerm() {
        super("Term");
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

    public String getComment() {
        return getFirst("comment");
    }

    public Boolean builtin() {
        return getBooleanValue("builtin");
    }

    public Boolean isObsolete() {
        return getBooleanValue("is_obsolete");
    }

    public String getCreatedBy() {
        return getFirst("created_by");
    }

    public String getCreationDate() {
        return getFirst("creation_date");
    }
}
