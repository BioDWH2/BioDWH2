package de.unibi.agbi.biodwh2.ndfrt.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;

public class Property {
    public String name;
    public String code;
    public String id;
    public String namespace;
    public String range;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String containsIndex;
    public List<String> pickList;

    public boolean getContainsIndex() {
        return containsIndex != null;
    }
}
