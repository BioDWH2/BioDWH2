package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class GeneOntology {
    public static final String geneOntology = "http://www.geneontology.org/formats/oboInOwl#";

    public static final Property hasAlternativeId = ResourceFactory.createProperty(geneOntology + "hasAlternativeId");


    GeneOntology() {
    }
}
