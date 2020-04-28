package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.regex.Pattern;

public final class DrugbankMetaboliteId {
    private static final Pattern IdPattern = Pattern.compile("DBMET[0-9]{5}");

    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public boolean primary;

    public boolean isValid() {
        return value != null && IdPattern.matcher(value).matches();
    }
}
