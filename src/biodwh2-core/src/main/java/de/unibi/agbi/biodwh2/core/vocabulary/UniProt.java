package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class UniProt {
    public static final String Uri = "https://purl.uniprot.org/core/";
    public static final Resource Gene = ResourceFactory.createResource(Uri + "Gene");
    public static final Resource Protein = ResourceFactory.createResource(Uri + "Protein");

    public static final Property Status = ResourceFactory.createProperty(Uri + "Status");
    public static final Property name = ResourceFactory.createProperty(Uri + "name");
    public static final Property alias = ResourceFactory.createProperty(Uri + "alias");
    public static final Property locusName = ResourceFactory.createProperty(Uri + "locusName");
    public static final Property subcellularLocation = ResourceFactory.createProperty(Uri + "Subcellular_Location");
    public static final Property familyMembershipStatement = ResourceFactory.createProperty(Uri + "Family_Membership_Statement");
    public static final Property created = ResourceFactory.createProperty(Uri + "created");
    public static final Property date = ResourceFactory.createProperty(Uri + "date");
    public static final Property journal = ResourceFactory.createProperty(Uri + "Journal");


    private UniProt() {
    }
}
