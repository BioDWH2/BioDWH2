package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes a UniProtKB entry.
 */
public class Entry {
    @JacksonXmlProperty(isAttribute = true)
    public Integer version;
    @JacksonXmlProperty(isAttribute = true)
    public String created;
    @JacksonXmlProperty(isAttribute = true)
    public String modified;
    @JacksonXmlProperty(isAttribute = true)
    public String dataset;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> accession;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> name;
    public Protein protein;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Gene> gene;
    public Organism organism;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Organism> organismHost;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeneLocation> geneLocation;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Reference> reference;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Comment> comment;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DbReference> dbReference;
    public ProteinExistence proteinExistence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Keyword> keyword;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Feature> feature;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Evidence> evidence;
    public Sequence sequence;
}
