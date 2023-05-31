package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

import java.util.List;

public class ProteinProperties {
    @JsonProperty("residue_number")
    @GraphProperty("residue_number")
    public Integer residueNumber;
    @JsonProperty("molecular_weight")
    @GraphProperty("molecular_weight")
    public String molecularWeight;
    @JsonProperty("theoretical_pi")
    @GraphProperty("theoretical_pi")
    public String theoreticalPi;
    @JacksonXmlElementWrapper(localName = "pfams")
    public List<Pfam> pfams;
    /**
     * Never used, so type is unknown
     */
    @JacksonXmlElementWrapper(localName = "transmembrane_regions")
    @GraphProperty(value = "transmembrane_regions", transformation = ValueTransformation.COLLECTION_TO_ARRAY)
    public List<String> transmembraneRegions;
    /**
     * Never used, so type is unknown
     */
    @JacksonXmlElementWrapper(localName = "signal_regions")
    @GraphProperty(value = "signal_regions", transformation = ValueTransformation.COLLECTION_TO_ARRAY)
    public List<String> signalRegions;
    @JsonProperty("polypeptide_sequence")
    @GraphProperty("polypeptide_sequence")
    public String sequence;
}
