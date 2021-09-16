package de.unibi.agbi.biodwh2.core.io.obo;

/**
 * OBO Instance definition
 * <p/>
 * http://purl.obolibrary.org/obo/oboformat/spec.html#3.5
 */
public final class OboInstance extends OboEntry {
    OboInstance() {
        super("Instance");
    }

    /**
     * cardinality 0-1
     *
     * @return Class-ID[]
     */
    public String instanceOf() {
        return getFirst("instance_of");
    }
}
