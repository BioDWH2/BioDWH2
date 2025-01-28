package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class GeneticInteraction extends Interaction {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] interactionScore;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] phenotype;
}
