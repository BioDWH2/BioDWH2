package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public final class Polypeptide {
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String source;
    public String name;
    @JsonProperty("general-function")
    public String generalFunction;
    @JsonProperty("specific-function")
    public String specificFunction;
    @JsonProperty("gene-name")
    public String geneName;
    public String locus;
    @JsonProperty("cellular-location")
    public String cellularLocation;
    @JsonProperty("transmembrane-regions")
    public String transmembraneRegions;
    @JsonProperty("signal-regions")
    public String signalRegions;
    @JsonProperty("theoretical-pi")
    public String theoreticalPi;
    @JsonProperty("molecular-weight")
    public String molecularWeight;
    @JsonProperty("chromosome-location")
    public String chromosomeLocation;
    public Organism organism;
    @JsonProperty("external-identifiers")
    public ArrayList<PolypeptideExternalIdentifier> externalIdentifiers;
    public ArrayList<String> synonyms;
    @JsonProperty("amino-acid-sequence")
    public Sequence aminoAcidSequence;
    @JsonProperty("gene-sequence")
    public Sequence geneSequence;
    public ArrayList<Pfam> pfams;
    @JsonProperty("go-classifiers")
    public ArrayList<GoClassifier> goClassifiers;
}
