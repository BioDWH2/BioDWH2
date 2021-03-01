package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * instance-frame ::= [ nl ]
 *         '[Instance]' nl
 *         id-Tag Instance-ID EOL
 *         { instance-frame-clause EOL }
 *
 * instance-frame-clause ::=
 *         is_anonymous-BT
 *         | name-TVP
 *         | namespace-Tag OBONamespace
 *         | alt_id-Tag ID
 *         | def-Tag QuotedString ws XrefList
 *         | comment-TVP
 *         | subset-Tag Subset-ID
 *         | synonym-Tag QuotedString ws SynonymScope [ ws SynonymType-ID ] ws XrefList
 *         | xref-Tag Xref
 *         | property_value-Tag Relation-ID ID
 *         | instance_of-Tag Class-ID
 *         | PropertyValueTagValue
 *         | relationship-Tag Rel-ID ws ID
 *         | created_by-TVP
 *         | creation_date-Tag ISO-8601-DateTime
 *         | is-obsolete-BT
 *         | replaced_by-Tag Instance-ID
 *         | consider-Tag ID
 */
@SuppressWarnings("unused")
public final class OboInstance extends OboEntry {
    OboInstance() {
        super("Instance");
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

    public String getCreatedBy() {
        return getFirst("created_by");
    }

    public String getCreationDate() {
        return getFirst("creation_date");
    }

    public Boolean isObsolete() {
        return getBooleanValue("is-obsolete");
    }
}
