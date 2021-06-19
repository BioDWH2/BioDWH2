package de.unibi.agbi.biodwh2.geneontology.model;

public enum EvidenceCode {
    // Experimental evidence codes
    /**
     * Inferred from Experiment
     **/
    EXP,
    /**
     * Inferred from Direct Assay
     **/
    IDA,
    /**
     * Inferred from Physical Interaction
     **/
    IPI,
    /**
     * Inferred from Mutant Phenotype
     **/
    IMP,
    /**
     * Inferred from Genetic Interaction
     **/
    IGI,
    /**
     * Inferred from Expression Pattern
     **/
    IEP,
    /**
     * Inferred from High Throughput Experiment
     **/
    HTP,
    /**
     * Inferred from High Throughput Direct Assay
     **/
    HDA,
    /**
     * Inferred from High Throughput Mutant Phenotype
     **/
    HMP,
    /**
     * Inferred from High Throughput Genetic Interaction
     **/
    HGI,
    /**
     * Inferred from High Throughput Expression Pattern
     **/
    HEP,
    // Phylogenetically-inferred annotations
    /**
     * Inferred from Biological aspect of Ancestor
     **/
    IBA,
    /**
     * Inferred from Biological aspect of Descendant
     **/
    IBD,
    /**
     * Inferred from Key Residues
     **/
    IKR,
    /**
     * Inferred from Rapid Divergence
     **/
    IRD,
    // Computational analysis evidence codes
    /**
     * Inferred from Sequence or structural Similarity
     **/
    ISS,
    /**
     * Inferred from Sequence Orthology
     **/
    ISO,
    /**
     * Inferred from Sequence Alignment
     **/
    ISA,
    /**
     * Inferred from Sequence Model
     **/
    ISM,
    /**
     * Inferred from Genomic Context
     **/
    IGC,
    /**
     * Inferred from Reviewed Computational Analysis
     **/
    RCA,
    // Author statement evidence codes
    /**
     * Traceable Author Statement
     **/
    TAS,
    /**
     * Non-traceable Author Statement
     **/
    NAS,
    // Curator statement evidence codes
    /**
     * Inferred by Curator
     **/
    IC,
    /**
     * No biological Data available
     **/
    ND,
    // Electronic annotation evidence code
    /**
     * Inferred from Electronic Annotation
     */
    IEA
}
