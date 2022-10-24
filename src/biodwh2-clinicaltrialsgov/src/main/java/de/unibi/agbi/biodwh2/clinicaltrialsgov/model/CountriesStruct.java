package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class CountriesStruct {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> country;
}
