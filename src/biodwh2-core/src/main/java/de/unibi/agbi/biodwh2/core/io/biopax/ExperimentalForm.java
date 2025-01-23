package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class ExperimentalForm extends UtilityClass {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] experimentalFormDescription;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] experimentalFormEntity;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] experimentalFeature;
}
