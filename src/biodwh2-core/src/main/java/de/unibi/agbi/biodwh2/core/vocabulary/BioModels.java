package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class BioModels {
    public static final String sbmlRdf = "http://identifiers.org/biomodels.vocabulary/";
    public static final String bqbiol = "http://biomodels.net/biology-qualifiers/";
    public static final String bqmodel = "http://biomodels.net/model-qualifiers/";

    public static final Property name = ResourceFactory.createProperty(sbmlRdf + "name");

    BioModels(){
    }
}
