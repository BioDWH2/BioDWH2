package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Describes the names for the protein and parts thereof. Equivalent to the flat file DE-line.
 */
public class Protein {
    public Protein.RecommendedName recommendedName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Protein.AlternativeName> alternativeName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Protein.SubmittedName> submittedName;
    public EvidencedString allergenName;
    public EvidencedString biotechName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> cdAntigenName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> innName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Protein.Domain> domain;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Protein.Component> component;

    public static class AlternativeName {
        public EvidencedString fullName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> shortName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> ecNumber;
    }

    public static class Component {
        public Protein.RecommendedName recommendedName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Protein.AlternativeName> alternativeName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Protein.SubmittedName> submittedName;
        public EvidencedString allergenName;
        public EvidencedString biotechName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> cdAntigenName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> innName;
    }

    public static class Domain {
        public Protein.RecommendedName recommendedName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Protein.AlternativeName> alternativeName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Protein.SubmittedName> submittedName;
        public EvidencedString allergenName;
        public EvidencedString biotechName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> cdAntigenName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> innName;
    }

    public static class RecommendedName {
        public EvidencedString fullName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> shortName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> ecNumber;
    }

    public static class SubmittedName {
        public EvidencedString fullName;
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<EvidencedString> ecNumber;
    }
}
