package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class Faldo {
    public static final String Uri = "http://biohackathon.org/resource/faldo#";

    public static final Property location = ResourceFactory.createProperty(Uri + "location");
}
