package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class ControlledVocabulary extends UtilityClass {
    @GraphProperty("term")
    public String term;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
}
