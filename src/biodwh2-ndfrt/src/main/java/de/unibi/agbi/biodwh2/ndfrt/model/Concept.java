package de.unibi.agbi.biodwh2.ndfrt.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

public class Concept {
    public static class DefiningConcept {
        @JacksonXmlText
        public String value;
    }

    public static class DefiningRoles {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "role")
        public List<DefiningRole> roles;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "roleGroup")
        public List<DefiningRoleGroup> roleGroups;
    }

    public static class DefiningRoleGroup {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "role")
        public List<DefiningRole> roles;
    }

    private static class NameValuePair {
        public String name;
        public String value;
    }

    public static class DefiningRole extends NameValuePair {
        public boolean some;
    }

    public static class Association extends NameValuePair {
        public List<NameValuePair> qualifiers;
    }

    public static class Property extends NameValuePair {
        public List<NameValuePair> qualifiers;
    }

    public String name;
    public String code;
    public String id;
    public String namespace;
    public String kind;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String primitive;
    @JacksonXmlElementWrapper(localName = "definingConcepts")
    @JacksonXmlProperty(localName = "concept")
    public List<DefiningConcept> definingConcepts;
    public DefiningRoles definingRoles;
    @JacksonXmlElementWrapper(localName = "properties")
    @JacksonXmlProperty(localName = "property")
    public List<Property> properties;
    @JacksonXmlElementWrapper(localName = "associations")
    @JacksonXmlProperty(localName = "association")
    public List<Association> associations;

    public boolean getPrimitive() {
        return primitive != null;
    }
}
