package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class BindingFeature extends EntityFeature {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] bindsTo;
    @GraphProperty("intra_molecular")
    public Boolean intraMolecular;
}
