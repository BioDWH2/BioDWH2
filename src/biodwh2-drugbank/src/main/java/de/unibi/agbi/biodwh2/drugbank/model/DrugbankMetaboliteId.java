package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.regex.Pattern;

public final class DrugbankMetaboliteId {
    private static final String[] PATTERN = {"DBMET[0-9]{5}"};
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public boolean primary;

    public boolean isValid() {
        if (value != null)
            for (String pattern : PATTERN)
                if (isPatternValid(pattern))
                    return true;
        return false;
    }

    private boolean isPatternValid(String pattern) {
        Pattern p = Pattern.compile(pattern);
        return p.matcher(value).matches();
    }
}
