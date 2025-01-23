package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Evidence extends UtilityClass {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] confidence;
    public ResourceRef evidenceCode;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] experimentalForm;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
}
