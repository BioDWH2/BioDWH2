package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Describes the subcellular location and optionally the topology and orientation of a molecule.
 */
public class SubcellularLocation {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> location;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> topology;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> orientation;
}
