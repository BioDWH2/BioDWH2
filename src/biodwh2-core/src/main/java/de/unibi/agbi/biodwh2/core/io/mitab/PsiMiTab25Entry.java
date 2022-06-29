package de.unibi.agbi.biodwh2.core.io.mitab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <a href="https://psicquic.github.io/MITAB25Format.html">https://psicquic.github.io/MITAB25Format.html</a>
 * <p>
 * The MITAB25 format is part of the PSI-MI 2.5 standard (1). It has been derived from the tabular format provided by
 * BioGrid. MITAB25 only describes binary interactions, one pair of interactors per row. Columns are separated by
 * tabulations. Tools allowing to manipulate this data format are available (2).
 * <p>
 * (1) http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&pubmedid=17925023
 * <p>
 * (2) http://www.psidev.info/groups/molecular-interactions (Tools section)
 * <p>
 * All the columns are mandatory.
 */
@SuppressWarnings("unused")
@JsonPropertyOrder({
        "interactorIdentifierA", "interactorIdentifierB", "interactorAlternativeIdentifierA",
        "interactorAlternativeIdentifierB", "interactorAliasesA", "interactorAliasesB", "interactionDetectionMethods",
        "firstAuthor", "publicationIdentifier", "interactorNCBITaxonomyIdentifierA",
        "interactorNCBITaxonomyIdentifierB", "interactionTypes", "sourceDatabases", "interactionIdentifiers",
        "confidenceScore"
})
public class PsiMiTab25Entry {
    /**
     * Unique identifier for interactor A, represented as databaseName:ac, where databaseName is the name of the
     * corresponding database as defined in the PSI-MI controlled vocabulary, and ac is the unique primary identifier of
     * the molecule in the database. Identifiers from multiple databases can be separated by "|". It is recommended that
     * proteins be identified by stable identifiers such as their UniProtKB or RefSeq accession number.
     */
    @JsonProperty("interactorIdentifierA")
    public String interactorIdentifierA;
    /**
     * Unique identifier for interactor B.
     */
    @JsonProperty("interactorIdentifierB")
    public String interactorIdentifierB;
    /**
     * Alternative identifier for interactor A, for example the official gene symbol as defined by a recognised
     * nomenclature committee. Representation as databaseName:identifier. Multiple identifiers separated by "|".
     */
    @JsonProperty("interactorAlternativeIdentifierA")
    public String interactorAlternativeIdentifierA;
    /**
     * Alternative identifier for interactor B.
     */
    @JsonProperty("interactorAlternativeIdentifierB")
    public String interactorAlternativeIdentifierB;
    /**
     * Aliases for A, separated by "|". Representation as databaseName:identifier. Multiple identifiers separated by
     * "|".
     */
    @JsonProperty("interactorAliasesA")
    public String interactorAliasesA;
    /**
     * Aliases for B.
     */
    @JsonProperty("interactorAliasesB")
    public String interactorAliasesB;
    /**
     * Interaction detection methods, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * darabaseName:identifier(methodName), separated by "|".
     */
    @JsonProperty("interactionDetectionMethods")
    public String interactionDetectionMethods;
    /**
     * First author surname(s) of the publication(s) in which this interaction has been shown, optionally followed by
     * additional indicators, e.g. "Doe-2005-a". Separated by "|".
     */
    @JsonProperty("firstAuthor")
    public String firstAuthor;
    /**
     * Identifier of the publication in which this interaction has been shown. Database name taken from the PSI-MI
     * controlled vocabulary, represented as databaseName:identifier. Multiple identifiers separated by "|".
     */
    @JsonProperty("publicationIdentifier")
    public String publicationIdentifier;
    /**
     * NCBI Taxonomy identifier for interactor A. Database name for NCBI taxid taken from the PSI-MI controlled
     * vocabulary, represented as databaseName:identifier (typicaly databaseName is set to 'taxid'). Multiple
     * identifiers separated by "|". Note: In this column, the databaseName:identifier(speciesName) notation is only
     * there for consistency. Currently no taxonomy identifiers other than NCBI taxid are anticipated, apart from the
     * use of -1 to indicate "in vitro", -2 to indicate "chemical synthesis", -3 indicates "unknown", -4 indicates "in
     * vivo" and -5 indicates "in silico".
     */
    @JsonProperty("interactorNCBITaxonomyIdentifierA")
    public String interactorNCBITaxonomyIdentifierA;
    /**
     * NCBI Taxonomy identifier for interactor B.
     */
    @JsonProperty("interactorNCBITaxonomyIdentifierB")
    public String interactorNCBITaxonomyIdentifierB;
    /**
     * Interaction types, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * dataBaseName:identifier(interactionType), separated by "|".
     */
    @JsonProperty("interactionTypes")
    public String interactionTypes;
    /**
     * Source databases and identifiers, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * databaseName:identifier(sourceName). Multiple source databases can be separated by "|".
     */
    @JsonProperty("sourceDatabases")
    public String sourceDatabases;
    /**
     * Interaction identifier(s) in the corresponding source database, represented by databaseName:identifier
     */
    @JsonProperty("interactionIdentifiers")
    public String interactionIdentifiers;
    /**
     * Confidence score. Denoted as scoreType:value. There are many different types of confidence score, but so far no
     * controlled vocabulary. Thus the only current recommendation is to use score types consistently within one source.
     * Multiple scores separated by "|".
     */
    @JsonProperty("confidenceScore")
    public String confidenceScore;
}
