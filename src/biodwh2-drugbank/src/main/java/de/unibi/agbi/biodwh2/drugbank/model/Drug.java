package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public final class Drug {
    @JacksonXmlProperty(isAttribute = true)
    public String created;
    @JacksonXmlProperty(isAttribute = true)
    public String updated;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JsonProperty("drugbank-id")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<DrugbankDrugSaltId> drugbankIds;
    public String name;
    public String description;
    @JsonProperty("cas-number")
    public String casNumber;
    public String unii;
    @JsonProperty("average-mass")
    public float averageMass;
    @JsonProperty("monoisotopic-mass")
    public float monoisotopicMass;
    public State state;
    public ArrayList<Group> groups;
    @JsonProperty("general-references")
    public ReferenceList generalReferences;
    @JsonProperty("synthesis-reference")
    public String synthesisReference;
    public String indication;
    public String pharmacodynamics;
    @JsonProperty("mechanism-of-action")
    public String mechanismOfAction;
    public String toxicity;
    public String metabolism;
    public String absorption;
    @JsonProperty("half-life")
    public String halfLife;
    @JsonProperty("protein-binding")
    public String proteinBinding;
    @JsonProperty("route-of-elimination")
    public String routeOfElimination;
    @JsonProperty("volume-of-distribution")
    public String volumeOfDistribution;
    public String clearance;
    public Classification classification;
    public ArrayList<Salt> salts;
    public ArrayList<Synonym> synonyms;
    public ArrayList<Product> products;
    @JsonProperty("international-brands")
    public ArrayList<InternationalBrand> internationalBrands;
    public ArrayList<Mixture> mixtures;
    public ArrayList<Packager> packagers;
    public ArrayList<Manufacturer> manufacturers;
    public ArrayList<Price> prices;
    public ArrayList<Category> categories;
    @JsonProperty("affected-organisms")
    public ArrayList<String> affectedOrganisms;
    public ArrayList<Dosage> dosages;
    @JsonProperty("atc-codes")
    public ArrayList<AtcCode> atcCodes;
    @JsonProperty("ahfs-codes")
    public ArrayList<String> ahfsCodes;
    @JsonProperty("pdb-entries")
    public ArrayList<String> pdbEntries;
    @JsonProperty("fda-label")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> fdaLabel;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> msds;
    public ArrayList<Patent> patents;
    @JsonProperty("food-interactions")
    public ArrayList<String> foodInteractions;
    @JsonProperty("drug-interactions")
    public ArrayList<DrugInteraction> drugInteractions;
    public ArrayList<Sequence> sequences;
    @JsonProperty("calculated-properties")
    public ArrayList<CalculatedProperty> calculatedProperties;
    @JsonProperty("experimental-properties")
    public ArrayList<ExperimentalProperty> experimentalProperties;
    @JsonProperty("external-identifiers")
    public ArrayList<ExternalIdentifier> externalIdentifiers;
    @JsonProperty("external-links")
    public ArrayList<ExternalLink> externalLinks;
    public ArrayList<Pathway> pathways;
    public ArrayList<Reaction> reactions;
    @JsonProperty("snp-effects")
    public ArrayList<SnpEffect> snpEffects;
    @JsonProperty("snp-adverse-drug-reactions")
    public ArrayList<SnpAdverseDrugReaction> snpAdverseDrugReactions;
    public ArrayList<Target> targets;
    public ArrayList<Enzyme> enzymes;
    public ArrayList<Carrier> carriers;
    public ArrayList<Transporter> transporters;
}
