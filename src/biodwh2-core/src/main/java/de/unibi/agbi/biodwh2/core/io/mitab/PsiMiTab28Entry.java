package de.unibi.agbi.biodwh2.core.io.mitab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <a href="https://psicquic.github.io/MITAB28Format.html">https://psicquic.github.io/MITAB28Format.html</a>
 * <p>
 * The MITAB28 format is an extention of the PSI-MI 2.5 (1), 2.6 and 2.7 standards (2) and is suited to capture causal
 * interactions among biological entities. It was initially derived from the tabular format provided by BioGrid and the
 * newest columns are derived from Signor's CausalTAB format (3). MITAB28 describes binary interactions, one pair of
 * interactors per row. Columns are separated by tabulations. Tools allowing to manipulate this data format are
 * available (4).
 * <p>
 * (1) http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&pubmedid=17925023
 * <p>
 * (2) https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3977660
 * <p>
 * (3) https://signor.uniroma2.it/scripts/causalTabInfo.php
 * <p>
 * (4) http://www.psidev.info/groups/molecular-interactions (Tools section)
 * <p>
 * Empty columns should be represented with '-' to keep track of the columns.
 */
@SuppressWarnings("unused")
@JsonPropertyOrder({
        "interactorIdentifierA", "interactorIdentifierB", "interactorAlternativeIdentifierA",
        "interactorAlternativeIdentifierB", "interactorAliasesA", "interactorAliasesB", "interactionDetectionMethods",
        "firstAuthor", "publicationIdentifier", "interactorNCBITaxonomyIdentifierA",
        "interactorNCBITaxonomyIdentifierB", "interactionTypes", "sourceDatabases", "interactionIdentifiers",
        "confidenceScore", "complexExpansion", "biologicalRoleA", "biologicalRoleB", "experimentalRoleA",
        "experimentalRoleB", "interactorTypeA", "interactorTypeB", "interactorXrefA", "interactorXrefB",
        "interactionXref", "interactorAnnotationsA", "interactorAnnotationsB", "interactionAnnotations",
        "hostOrganismNCBITaxonomyIdentifier", "interactionParameters", "creationDate", "updateDate",
        "interactorChecksumA", "interactorChecksumB", "interactionChecksum", "negative", "interactorFeaturesA",
        "interactorFeaturesB", "interactorStoichiometryA", "interactorStoichiometryB",
        "interactorParticipantIdentificationMethodA", "interactorParticipantIdentificationMethodB",
        "interactorBiologicalEffectA", "interactorBiologicalEffectB", "causalRegulatoryMechanism", "causalStatement"
})
public class PsiMiTab28Entry {
    /**
     * Unique identifier for interactor A, represented as databaseName:identifier, where databaseName is the name of the
     * corresponding database as defined in the PSI-MI controlled vocabulary, and identifier is the unique primary
     * identifier of the molecule in the database. Even though identifiers from multiple databases can be separated by
     * "|", it is recommended to give only one identifier in this column. It is recommended that proteins be identified
     * by stable identifiers such as their UniProtKB or RefSeq accession number. Small molecules should have Chebi
     * identifiers, nucleic acids should have embl/ddbj/genbank identifiers and gene should have entrez gene/locuslink,
     * ensembl, or ensemblGenome identifiers. This column should never be empty ('-') except for describing
     * intra-molecular interactions or auto-catalysis. Ex: uniprotkb:P12346
     */
    @JsonProperty("interactorIdentifierA")
    public String interactorIdentifierA;
    /**
     * Unique identifier for interactor B.
     */
    @JsonProperty("interactorIdentifierB")
    public String interactorIdentifierB;
    /**
     * Alternative identifier for interactor A, represented as databaseName:ac, where databaseName is the name of the
     * corresponding database as defined in the PSI-MI controlled vocabulary, and ac is the primary identifier of the
     * molecule in the database. Multiple identifiers separated by "|". It is recommended to only give database
     * identifiers in this column. Other cross references for interactor A such as GO xrefs should be moved to the
     * column 'Interactor xrefs A' and interactor names such as gene names should be moved to the column 'Alias A'. Ex:
     * refseq:NP_001013128|ensembl:ENSRNOP00000012946
     */
    @JsonProperty("interactorAlternativeIdentifierA")
    public String interactorAlternativeIdentifierA;
    /**
     * Alternative identifier for interactor B.
     */
    @JsonProperty("interactorAlternativeIdentifierB")
    public String interactorAlternativeIdentifierB;
    /**
     * Aliases for A, separated by "|". Representation as databaseName:name(alias type), where databaseName is the name
     * of the corresponding database as defined in the PSI-MI controlled vocabulary, name is the alias name and alias
     * type is the name of the corresponding alias type as defined in the PSI-MI controlled vocabulary. In the absence
     * of databaseName, one can use unknown. Multiple names separated by "|". In parenthesis, 'display_short' and
     * 'display_long' are used to describe what name can be used for network display. Ex: uniprotkb:Tf(gene
     * name)|uniprotkb:Serotransferrin(recommended name)|uniprotkb:Tf(display_short)
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
     * databaseName:identifier(methodName), separated by "|". As the detection methods are taken from the PSI-MI
     * ontology, the database name is 'psi-mi'. Interaction detection method is recommended by MIMIx and can be used for
     * scoring interactions so it is recommended to always give this information. It is also recommended to give one
     * interaction detection method per MITAB line (for clustering purposes). Ex: psi-mi:"MI:0006"(anti bait
     * coimmunoprecipitation)
     */
    @JsonProperty("interactionDetectionMethods")
    public String interactionDetectionMethods;
    /**
     * First author: surname(s) of the publication(s) followed by 'et al.' and the publication year in parenthesis, e.g.
     * Ciferri et al.(2005). Separated by "|".
     */
    @JsonProperty("firstAuthor")
    public String firstAuthor;
    /**
     * Identifier of the publication in which this interaction has been shown. Database name taken from the PSI-MI
     * controlled vocabulary, represented as databaseName:identifier. Multiple identifiers separated by "|". The
     * publication identifier is used for clustering interactions from different data providers in PSICQUIC and can be
     * used for scoring interactions so it is recommended to always give this information. It is recommended to give one
     * pubmed id per MITAB line and IMEx ids can be added. Ex: pubmed:16980971|imex:IM-1
     */
    @JsonProperty("publicationIdentifier")
    public String publicationIdentifier;
    /**
     * NCBI Taxonomy identifier for interactor A, represented as taxid:identifier(organism name) where the identifier is
     * the taxon id of the organism and organism name can either be the common name or scientific name. Even though
     * multiple identifiers can be separated by "|", it is recommended to have one organism per interactor per MITAB
     * line. If both scientific name and common name are given, they should be represented with : taxid:id1(common
     * name1)|taxid:id1(scientific name1). Note: Currently no taxonomy identifiers other than NCBI taxid are
     * anticipated, apart from the use of -2 to indicate "chemical synthesis" and -3 indicates "unknown". It is
     * recommended to always give this information for proteins and genes. For small molecules and nucleic acids, this
     * information should be given when available but can be left empty ('-') if not relevant.
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
     * dataBaseName:identifier(interactionType), separated by "|". As the interaction types are taken from the PSI-MI
     * ontology, the database name is 'psi-mi'. Interaction type can be used for scoring interactions so it is
     * recommended to always give this information. It is also recommended to give one interaction type per MITAB line
     * (for clustering purposes). Ex: psi-mi:"MI:0914"(association)
     */
    @JsonProperty("interactionTypes")
    public String interactionTypes;
    /**
     * Source databases and identifiers, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * databaseName:identifier(sourceName). As the detection methods are taken from the PSI-MI ontology, the database
     * name is 'psi-mi'. Multiple source databases can be separated by "|". When the interaction has been imported and
     * reported by different sources, it is recommended to give the original source plus the source that currently
     * reports the interaction. Ex: psi-mi:"MI:0469"(intact)|psi-mi:"MI:0923"(irefindex)
     */
    @JsonProperty("sourceDatabases")
    public String sourceDatabases;
    /**
     * Interaction identifier(s) in the corresponding source database, represented by databaseName:identifier. It is
     * recommended to always give a unique identifier per interaction (binary and n-ary) so in case of complexes which
     * have been expanded, it would be possible to retrieve and re-build the original complex based on the interaction
     * identifier. IrefIndex IDs go into the interaction checksum field. Other interaction references such as GO
     * references go into the interaction xref field. Ex: intact:EBI-1547321|imex:IM-11865-3
     */
    @JsonProperty("interactionIdentifiers")
    public String interactionIdentifiers;
    /**
     * Confidence score: Denoted as scoreType:value where scoreType is taken from the corresponding PSI-MI controlled
     * vocabulary. Multiple scores separated by "|". Ex: author-score:0.60|author-score:high|intact-miscore:0.36784992
     */
    @JsonProperty("confidenceScore")
    public String confidenceScore;
    /**
     * Complex expansion: Model used to convert n-ary interactions into binary interactions for purpose of export in
     * MITAB file. It should be represented as databaseName:identifier(expansion name) and taken from the corresponding
     * PSI-MI controlled vocabulary. It is recommended to always give this information when n-ary interactions have been
     * expanded. In case of true binary interactions, this column should be empty ('-'). Ex: psi-mi:"MI:1060"(spoke
     * expansion)
     */
    @JsonProperty("complexExpansion")
    public String complexExpansion;
    /**
     * Biological role A, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * dataBaseName:identifier(biological role name), separated by "|". As the biological roles are taken from the
     * PSI-MI ontology, the database name is 'psi-mi'. Biological roles are recommended by MIMIx so it is recommended to
     * always give this information. When the participant A does not have any specific biological roles, the term
     * 'unspecified role' (MI:0499) should be used. Ex: psi-mi:"MI:0684"(ancillary)
     */
    @JsonProperty("biologicalRoleA")
    public String biologicalRoleA;
    /**
     * Biological role B.
     */
    @JsonProperty("biologicalRoleB")
    public String biologicalRoleB;
    /**
     * Experimental role A, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * dataBaseName:identifier(experimental role name), separated by "|". As the experimental roles are taken from the
     * PSI-MI ontology, the database name is 'psi-mi'. Experimental roles are recommended by MIMIx so it is recommended
     * to always give this information. When the participant A does not have any specific experimental roles, the term
     * 'unspecified role' (MI:0499) should be used. Ex:psi-mi:"MI:0496"(bait)
     */
    @JsonProperty("experimentalRoleA")
    public String experimentalRoleA;
    /**
     * Experimental role B.
     */
    @JsonProperty("experimentalRoleB")
    public String experimentalRoleB;
    /**
     * Interactor type A, taken from the corresponding PSI-MI controlled vocabulary, and represented as
     * dataBaseName:identifier(interactor type name), separated by "|". As the interactor types are taken from the
     * PSI-MI ontology, the database name is 'psi-mi'. It is recommended to always give this information as it can be
     * useful for recognizing protein-protein, small molecule-proteins, nucleic acids-proteins and gene-proteins
     * interactions. Ex: psi-mi:"MI:0326"(protein)
     */
    @JsonProperty("interactorTypeA")
    public String interactorTypeA;
    /**
     * Interactor type B.
     */
    @JsonProperty("interactorTypeB")
    public String interactorTypeB;
    /**
     * Xref for interactor A, represented as databaseName:ac(text), where databaseName is the name of the corresponding
     * database as defined in the PSI-MI controlled vocabulary, and ac is the primary accession of the molecule in the
     * database. For example the gene ontology cross references associated. The text can be used to describe the
     * qualifier type of the cross reference (see the corresponding PSI-MI controlled vocabulary) or could be used to
     * give the name of the GO term in case of cross references to ontology databases. Multiple cross references
     * separated by "|". This column aims at adding more information to describe the interactor A but cannot be used to
     * identify the interactor A. If some sequence database accessions are ambiguous (Ex : uniprot secondary accessions
     * that are shared between different uniprot entries and so cannot be used as identifiers of interactor A), it is
     * possible to report them in this column. Ex: go:"GO:0003824"(catalytic activity)
     */
    @JsonProperty("interactorXrefA")
    public String interactorXrefA;
    /**
     * Xref for interactor B.
     */
    @JsonProperty("interactorXrefB")
    public String interactorXrefB;
    /**
     * Xref for the interaction, represented as databaseName:ac(text), where databaseName is the name of the
     * corresponding database as defined in the PSI-MI controlled vocabulary, and ac is the primary accession in the
     * database. For example the gene ontology cross references associated (components, etc.) or OMIM cross references.
     * Multiple cross references separated by "|". The text can be used to describe the qualifier type of the cross
     * reference (see the corresponding PSI-MI controlled vocabulary) or could be used to give the name of the GO term
     * in case of cross references to ontology databases. Note, that this field is not meant not store interaction
     * accessions or interaction checksums in this field. Ex: go:"GO:0005643"(nuclear pore)
     */
    @JsonProperty("interactionXref")
    public String interactionXref;
    /**
     * Annotations for interactor A, represented as topic:"text", where topic is the name of the topic as defined in the
     * PSI-MI controlled vocabulary and text is free text associated with the topic (linebreak and other MITAB reserved
     * characters should be properly escaped, replaced and/or removed). For example comments about this interactor :
     * comment:"sequence not available in uniprotKb". The text is optional and only a topic could be given, e.g.
     * anti-bacterial. Multiple annotations separated by "|".
     */
    @JsonProperty("interactorAnnotationsA")
    public String interactorAnnotationsA;
    /**
     * Annotations for Interactor B.
     */
    @JsonProperty("interactorAnnotationsB")
    public String interactorAnnotationsB;
    /**
     * Annotations for the interaction, represented as topic:"text", where topic is the name of the topic as defined in
     * the PSI-MI controlled vocabulary and text is free text associated with the topic (linebreak and other MITAB
     * reserved characters should be properly escaped, replaced and/or removed). For example figure legends : figure
     * legend:"Supp Tables 1 and 2". The text is optional and only a topic could be given. This column would also be
     * used for tagging interactions and in such a case, topics for tags are also defined in the PSI-MI controlled
     * vocabulary except for the complex expansion tags that have their own column. Ex: internally-curated Multiple
     * annotations separated by "|".
     */
    @JsonProperty("interactionAnnotations")
    public String interactionAnnotations;
    /**
     * NCBI Taxonomy identifier for the host organism. represented as taxid:identifier(organism name) where the
     * identifier is the taxon id of the organism and organism name can either be the common name or scientific name.
     * Multiple identifiers can be separated by "|". Cells and tissues cannot be described in this column. If both
     * scientific name and common name are given, they should be represented with : taxid:id1(common
     * name1)|taxid:id1(scientific name1). Note: Currently no taxonomy identifiers other than NCBI taxid are
     * anticipated, apart from the use of -1 to indicate "in vitro", -2 to indicate "chemical synthesis", -3 indicates
     * "unknown", -4 indicates "in vivo" and -5 indicates "in silico".
     */
    @JsonProperty("hostOrganismNCBITaxonomyIdentifier")
    public String hostOrganismNCBITaxonomyIdentifier;
    /**
     * Parameters of the interaction, for example kinetics. Representation as type:value(text). The type can be taken
     * from the corresponding PSI-MI controlled vocabulary. Multiple parameters separated by "|". Ex: kd:"4.0x2^5 ~0.3"
     */
    @JsonProperty("interactionParameters")
    public String interactionParameters;
    /**
     * Creation date: when the curation of the publication started. Representation as yyyy/mm/dd. This field is
     * equivalent to the release date in the PSI-XML schema. Ex:2010/10/17
     */
    @JsonProperty("creationDate")
    public String creationDate;
    /**
     * Update date: when the interaction was updated for the last time. Ex:2011/12/13 Representation as yyyy/mm/dd. This
     * field does not have any equivalence in PSI-XML (could be the release date as well) and helps to keep track of
     * changes in curated data.
     */
    @JsonProperty("updateDate")
    public String updateDate;
    /**
     * Checksum for interactor A, for instance the ROGID of the interactor which takes into consideration both the
     * sequence and the organism of the interactor. Representation as methodName:checksum where methodName is the name
     * of the method used to create the checksum. It is recommended to give the ROGID and CROGID for proteins and the
     * standard Inchi key for small molecules. Ex:
     * rogid:UcdngwpTSS6hG/pvQGgpp40u67I9606|crogid:UcdngwpTSS6hG/pvQGgpp40u67I9606
     */
    @JsonProperty("interactorChecksumA")
    public String interactorChecksumA;
    /**
     * Checksum for interactor B.
     */
    @JsonProperty("interactorChecksumB")
    public String interactorChecksumB;
    /**
     * Checksum for interaction, for instance the RIGID of the interaction. Representation as methodName:checksum where
     * methodName is the name of the method used to create the checksum. Ex: rigid:"+++94o2VtVJcuk6jD3H2JZXaVYc"
     */
    @JsonProperty("interactionChecksum")
    public String interactionChecksum;
    /**
     * negative: Boolean value to distinguish positive interactions (false) from negative interactions (true). A
     * molecular interaction between A and B entities is considered as negative (true) when an isoform of A
     * interacts/binds to B and not A itself. For that particular isoform of A that interacts with entity B, in the
     * corresponding binary interaction row, the negative value should be set to false. By default, if the column is
     * empty ('-'), the negative value is considered to be false (positive interaction). Ex: true
     */
    @JsonProperty("negative")
    public String negative;
    /**
     * Feature(s) for interactor A: describe features for participant A such as binding sites, PTMs, tags, etc.
     * Represented as feature_type:range(text), where feature_type is the feature type as described in the PSI-MI
     * controlled vocabulary. For the PTMs, the MI ontology terms are obsolete and the PSI-MOD ontology should be used
     * instead. The text can be used for feature type names, feature names, interpro cross references, etc. For instance
     * : sufficient to bind:27-195,201-133 (IPR000785). The use of the following characters is allowed to describe a
     * range position : ‘?’ (undetermined position), ‘n’ (n terminal range), ‘c’ (c-terminal range), ‘>x’ (greater than
     * x), ‘<’ (less than x), ‘x1..x1’ (fuzzy range position Ex : 5..5-9..10). The character '-' is used to separate
     * start position(s) from end position(s). Multiple features separated by '|'. Multiple ranges per feature separated
     * by ','. However, It is not possible to represent linked features/ranges. Ex: gst tag:n-n(n-terminal
     * region)|sufficient to bind:23-45. or binding site:23..24-46,33-33
     */
    @JsonProperty("interactorFeaturesA")
    public String interactorFeaturesA;
    /**
     * Feature(s) for interactor B.
     */
    @JsonProperty("interactorFeaturesB")
    public String interactorFeaturesB;
    /**
     * Stoichiometry for interactor A: A numerical value describing the count of instance of the molecule participating
     * in the interaction. If no stoichiometry is available for interactor A, the column should be empty ('-'),
     * otherwise a positive Integer value should be given. Several specific cases should be taken into consideration :
     * in case of auto-catalysis, only one interactor is given and the stoichiometry should be 1. In case of homodimers,
     * homotrimers, etc., the stoichiometry of one interactor should be 0 and the stoichiometry of the other should be a
     * valid positive Integer. In case of homo-oligomer, the stoichiometry of both interactors should be 0. Example: for
     * self interactors e.g. a kinase occluding its kinase domain by an internal phospho-tyrosine/SH2 domain
     * interaction, only Interactor A column will show the molecule accession number with the stoichiometry 1. The
     * columns for Interactor B will be empty. Ex: 4
     */
    @JsonProperty("interactorStoichiometryA")
    public String interactorStoichiometryA;
    /**
     * Stoichiometry for interactor B.
     */
    @JsonProperty("interactorStoichiometryB")
    public String interactorStoichiometryB;
    /**
     * Participant identification method for interactor A: taken from the corresponding PSI-MI controlled Vocabulary,
     * and represented as databaseName:identifier(methodName), separated by "|". As the identification methods are taken
     * from the PSI-MI ontology, the database name is 'psi-mi'. Participant detection method is recommended by MIMIx so
     * it is recommended to always give this information. Ex: psi-mi:"MI:0102"(sequence tag identification)
     */
    @JsonProperty("interactorParticipantIdentificationMethodA")
    public String interactorParticipantIdentificationMethodA;
    /**
     * Participant identification method for interactor B.
     */
    @JsonProperty("interactorParticipantIdentificationMethodB")
    public String interactorParticipantIdentificationMethodB;
    /**
     * Biological effect of interactor A: The GO term associated with the molecular function of interactor A that is
     * responsible for its regulatory activity. Represented as dataBaseName:identifier(biological effect name),
     * separated by "|". As the biological effect is taken from the GO ontology, the database name should be 'go'. Also,
     * it is recommended that at most one GO xref is given as a biological effect. Ex: go:"GO:0016301"(kinase activity)
     */
    @JsonProperty("interactorBiologicalEffectA")
    public String interactorBiologicalEffectA;
    /**
     * Biological effect of interactor B.
     */
    @JsonProperty("interactorBiologicalEffectB")
    public String interactorBiologicalEffectB;
    /**
     * Causal regulatory mechanism: This column describes the regulatory mechanism of indirect causal interactions,
     * where the entity A is not immediately upstream of the entity B. It is taken from the corresponding PSI-MI
     * controlled vocabulary and represented as dataBaseName:identifier(regulatoryMechanism), separated by "|". As the
     * causal regulatory mechanism is taken from the PSI-MI ontology, the database name is 'psi-mi'. When the
     * ‘Interaction Types’ column is equal to the psi-mi term: psi-mi:"MI:2286"(functional association), then the causal
     * regulatory mechanism must be absolutely filled - otherwise it can be empty. Ex: psi-mi:"MI:2247"(transcriptional
     * regulation)
     */
    @JsonProperty("causalRegulatoryMechanism")
    public String causalRegulatoryMechanism;
    /**
     * Causal statement: This column describes the effect of modulator entity A on a modulated entity B. It is taken
     * from the corresponding PSI-MI controlled vocabulary and represented as dataBaseName:identifier(causalStatement),
     * separated by "|". As the causal statement is taken from the PSI-MI ontology, the database name is 'psi-mi'. Ex:
     * psi-mi:"MI:2240"(down regulates)
     */
    @JsonProperty("causalStatement")
    public String causalStatement;
}
