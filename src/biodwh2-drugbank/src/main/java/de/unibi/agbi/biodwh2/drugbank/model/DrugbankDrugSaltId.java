package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.regex.Pattern;

public final class DrugbankDrugSaltId {
    private static final Pattern[] IdPattern = {
            Pattern.compile("DB[0-9]{5}"), Pattern.compile("DBSALT[0-9]{6}"), Pattern.compile("APRD[0-9]{5}"),
            Pattern.compile("BIOD[0-9]{5}"), Pattern.compile("BTD[0-9]{5}"), Pattern.compile("EXPT[0-9]{5}"),
            Pattern.compile("NUTR[0-9]{5}")
    };
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public boolean primary;

    public boolean isValid() {
        if (value != null)
            for (Pattern pattern : IdPattern)
                if (pattern.matcher(value).matches())
                    return true;
        return false;
    }
}
