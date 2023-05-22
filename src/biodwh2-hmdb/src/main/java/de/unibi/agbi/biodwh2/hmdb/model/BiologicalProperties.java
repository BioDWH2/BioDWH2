package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class BiologicalProperties {
    @JacksonXmlElementWrapper(localName = "cellular_locations")
    public List<String> cellularLocations;
    @JacksonXmlElementWrapper(localName = "biospecimen_locations")
    public List<String> biospecimenLocations;
    @JacksonXmlElementWrapper(localName = "tissue_locations")
    public List<String> tissueLocations;
    @JacksonXmlElementWrapper(localName = "pathways")
    public List<Pathway> pathways;
}
