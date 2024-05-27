package de.unibi.agbi.biodwh2.core.io.mitab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <a href="https://psicquic.github.io/MITAB26Format.html">https://psicquic.github.io/MITAB26Format.html</a>
 * <p>
 * The MITAB26 format is part of the PSI-MI 2.5 standard (1). It has been derived from the tabular format provided by
 * BioGrid. MITAB26 only describes binary interactions, one pair of interactors per row. Columns are separated by
 * tabulations. Tools allowing to manipulate this data format are available (2).
 * <p>
 * (1) http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&pubmedid=17925023
 * <p>
 * (2) http://www.psidev.info/groups/molecular-interactions (Tools section)
 * <p>
 * All the columns are mandatory.
 */
@JsonPropertyOrder({
        "interactorIdentifierA", "interactorIdentifierB", "interactorAlternativeIdentifierA",
        "interactorAlternativeIdentifierB", "interactorAliasesA", "interactorAliasesB", "interactionDetectionMethods",
        "firstAuthor", "publicationIdentifier", "interactorNCBITaxonomyIdentifierA",
        "interactorNCBITaxonomyIdentifierB", "interactionTypes", "sourceDatabases", "interactionIdentifiers",
        "confidenceScore", "expansion", "biologicalRoleA", "biologicalRoleB", "experimentalRoleA", "experimentalRoleB",
        "interactorTypeA", "interactorTypeB", "interactorXrefA", "interactorXrefB", "interactionXref",
        "interactorAnnotationsA", "interactorAnnotationsB", "interactionAnnotations",
        "hostOrganismNCBITaxonomyIdentifier", "interactionParameters", "creationDate", "updateDate",
        "interactorChecksumA", "interactorChecksumB", "interactionChecksum", "negative"
})
public class PsiMiTab26Entry {
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
    /**
     * Expansion. Model used to convert n-ary interactions into binary interactions for purpose of export in MITAB file.
     * The known expansions are none (if true binary interaction), spoke, matrix or bipartite.
     */
    @JsonProperty("expansion")
    public String expansion;
    /**
     * Biological role A, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(biological role name), separated by "|".
     */
    @JsonProperty("biologicalRoleA")
    public String biologicalRoleA;
    /**
     * Biological role B, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(biological role name), separated by "|".
     */
    @JsonProperty("biologicalRoleB")
    public String biologicalRoleB;
    /**
     * Experimental role A, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(experimental role name), separated by "|".
     */
    @JsonProperty("experimentalRoleA")
    public String experimentalRoleA;
    /**
     * Experimental role B, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(experimental role name), separated by "|".
     */
    @JsonProperty("experimentalRoleB")
    public String experimentalRoleB;
    /**
     * Interactor type A, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(interactor type name), separated by "|".
     */
    @JsonProperty("interactorTypeA")
    public String interactorTypeA;
    /**
     * Interactor type B, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
     * dataBaseName:identifier(interactor type name), separated by "|".
     */
    @JsonProperty("interactorTypeB")
    public String interactorTypeB;
    /**
     * Xref for interactor A, for example the gene ontology cross references associated. Representation as
     * databaseName:identifier(text). Multiple cross references separated by "|".
     */
    @JsonProperty("interactorXrefA")
    public String interactorXrefA;
    /**
     * Xref for interactor B, for example the gene ontology cross references associated. Representation as
     * databaseName:identifier(text). Multiple cross references separated by "|".
     */
    @JsonProperty("interactorXrefB")
    public String interactorXrefB;
    /**
     * Xref for the interaction, for example the gene ontology cross references associated (components, etc.) or OMIM
     * cross references. Representation as databaseName:identifier(text). Multiple cross references separated by "|".
     */
    @JsonProperty("interactionXref")
    public String interactionXref;
    /**
     * Annotations for interactor A, for example comments about this interactor. Representation as topic:text. Multiple
     * annotations separated by "|".
     */
    @JsonProperty("interactorAnnotationsA")
    public String interactorAnnotationsA;
    /**
     * Annotations for Interactor B, for example comments about this interactor. Representation as
     * databaseName:identifier(text). Multiple annotations separated by "|".
     */
    @JsonProperty("interactorAnnotationsB")
    public String interactorAnnotationsB;
    /**
     * Annotations for the interaction, for example comments about this interaction. Representation as topic:text.
     * Multiple annotations separated by "|".
     */
    @JsonProperty("interactionAnnotations")
    public String interactionAnnotations;
    /**
     * NCBI Taxonomy identifier for the host organism. Database name for NCBI taxid taken from the PSI-MI controlled
     * vocabulary, represented as databaseName:identifier (typicaly databaseName is set to 'taxid'). Multiple
     * identifiers separated by "|". Note: In this column, the databaseName:identifier(speciesName) notation is only
     * there for consistency. Currently no taxonomy identifiers other than NCBI taxid are anticipated, apart from the
     * use of -1 to indicate "in vitro", -2 to indicate "chemical synthesis", -3 indicates "unknown", -4 indicates "in
     * vivo" and -5 indicates "in silico".
     */
    @JsonProperty("hostOrganismNCBITaxonomyIdentifier")
    public String hostOrganismNCBITaxonomyIdentifier;
    /**
     * Parameters of the interaction, for example kinetics. Representation as type:value(text). The type can be taken
     * from the corresponding PSI-MI controlled vocabulary. Multiple parameters separated by "|".
     */
    @JsonProperty("interactionParameters")
    public String interactionParameters;
    /**
     * Creation date Representation as yyyy/mm/dd.
     */
    @JsonProperty("creationDate")
    public String creationDate;
    /**
     * Update date Representation as yyyy/mm/dd.
     */
    @JsonProperty("updateDate")
    public String updateDate;
    /**
     * Checksum for interactor A, for instance the ROGID of the interactor. Representation as databaseName:checksum
     */
    @JsonProperty("interactorChecksumA")
    public String interactorChecksumA;
    /**
     * Checksum for interactor B, for instance the ROGID of the interactor. Representation as databaseName:checksum
     */
    @JsonProperty("interactorChecksumB")
    public String interactorChecksumB;
    /**
     * Checksum for interaction, for instance the RIGID of the interaction. Representation as databaseName:checksum
     */
    @JsonProperty("interactionChecksum")
    public String interactionChecksum;
    /**
     * negative Boolean value to distinguish positive interactions (negative = false) from negative interactions
     * (negative = true)
     */
    @JsonProperty("negative")
    public String negative;
}
