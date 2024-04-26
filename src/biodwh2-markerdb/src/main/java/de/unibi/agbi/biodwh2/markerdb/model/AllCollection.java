package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement
@JsonIgnoreProperties({"to_s"})
public class AllCollection {
    public String version;
    public List<Protein> proteins;
    public List<Chemical> chemicals;
    public List<Karyotype> karyotypes;
    @JsonProperty("sequence_variants")
    public List<SequenceVariant> sequenceVariants;
}
