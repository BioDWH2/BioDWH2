package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

/**
 * Describes the source of the sequence according to the citation. Equivalent to the flat file RC-line.
 */
public class SourceData {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<StrainOrPlasmidOrTransposonOrTissue> strain;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<StrainOrPlasmidOrTransposonOrTissue> plasmid;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<StrainOrPlasmidOrTransposonOrTissue> transposon;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<StrainOrPlasmidOrTransposonOrTissue> tissue;

    public static class StrainOrPlasmidOrTransposonOrTissue {
        @JacksonXmlText
        public String value;
        @JacksonXmlProperty(isAttribute = true)
        public String evidence;
    }
}
