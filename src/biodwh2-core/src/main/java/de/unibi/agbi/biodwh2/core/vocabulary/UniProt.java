package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class UniProt {
    public static final String Uri = "https://purl.uniprot.org/core/";
    public static final Resource Gene = ResourceFactory.createResource(Uri + "Gene");
    public static final Resource Protein = ResourceFactory.createResource(Uri + "Protein");

    //TODO
    public static final Property Status = ResourceFactory.createProperty(Uri + "Status");
    public static final Property name = ResourceFactory.createProperty(Uri + "name");
    public static final Property alias = ResourceFactory.createProperty(Uri + "alias");
    public static final Property locusName = ResourceFactory.createProperty(Uri + "locus");


    private UniProt() {
    }
}
