package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Biomarkers {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "gene")
    public List<GeneSimple> genes;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "karyotype")
    public List<KaryotypeSimple> karyotypes;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "protein")
    public List<ProteinSimple> proteins;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "chemical")
    public List<ChemicalSimple> chemicals;
}
