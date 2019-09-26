package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class UniProt {
    public static final String Uri = "https://purl.uniprot.org/core/";
    public static final Resource Gene = ResourceFactory.createResource(Uri + "Gene");
    public static final Resource Protein = ResourceFactory.createResource(Uri + "Protein");

    private UniProt() {
    }
}
