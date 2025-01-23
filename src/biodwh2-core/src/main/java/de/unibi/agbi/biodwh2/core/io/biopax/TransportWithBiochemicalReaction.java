package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

/**
 * Due to a lack of multiple inheritance, all properties from {@link Transport} and {@link BiochemicalReaction} are
 * defined here.
 */
public class TransportWithBiochemicalReaction extends Conversion {
    @GraphProperty("ec_number")
    public String eCNumber;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] deltaG;
    /**
     * Datatype is officially float, but to prevent float representation errors, string is used.
     */
    @GraphProperty("deltaH")
    public String deltaH;
    /**
     * Datatype is officially float, but to prevent float representation errors, string is used.
     */
    @GraphProperty("deltaS")
    public String deltaS;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] kEQ;
}
