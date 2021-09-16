package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes different types of general annotations. Equivalent to the flat file CC-line.
 */
public class Comment {
    public Molecule molecule;
    public Comment.Absorption absorption;
    public Comment.Kinetics kinetics;
    public Comment.PhDependence phDependence;
    public Comment.RedoxPotential redoxPotential;
    public Comment.TemperatureDependence temperatureDependence;
    public Reaction reaction;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PhysiologicalReaction> physiologicalReaction;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Cofactor> cofactor;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<SubcellularLocation> subcellularLocation;
    public Comment.Conflict conflict;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Comment.Link> link;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Event> event;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Isoform> isoform;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Interactant> interactant;
    public Boolean organismsDiffer = false;
    public Integer experiments;
    public Comment.Disease disease;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Location> location;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> text;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String locationType;
    @JacksonXmlProperty(isAttribute = true)
    public String name;
    @JacksonXmlProperty(isAttribute = true)
    public Float mass;
    @JacksonXmlProperty(isAttribute = true)
    public String error;
    @JacksonXmlProperty(isAttribute = true)
    public String method;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;

    public static class Absorption {
        public EvidencedString max;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> text;
    }

    public static class Conflict {
        public Comment.Conflict.Sequence sequence;
        @JacksonXmlProperty(isAttribute = true)
        public String type;
        @JacksonXmlProperty(isAttribute = true)
        public String ref;

        public static class Sequence {
            @JacksonXmlProperty(isAttribute = true)
            public String resource;
            @JacksonXmlProperty(isAttribute = true)
            public String id;
            @JacksonXmlProperty(isAttribute = true)
            public Integer version;
        }
    }

    public static class Disease {
        public String name;
        public String acronym;
        public String description;
        public DbReference dbReference;
        @JacksonXmlProperty(isAttribute = true)
        public String id;
    }

    public static class Kinetics {
        @JsonProperty("")
        @JacksonXmlProperty(localName = "KM")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> km;
        @JacksonXmlProperty(localName = "Vmax")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> vmax;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> text;
    }

    public static class Link {
        public String uri;
    }

    public static class PhDependence {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> text;
    }

    public static class RedoxPotential {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> text;
    }

    public static class TemperatureDependence {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> text;
    }
}
