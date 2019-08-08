package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public final class Target extends Interactant {
    @JacksonXmlProperty(isAttribute = true)
    public int position;
}
