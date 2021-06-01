package de.unibi.agbi.biodwh2.hpo.model;

/**
 * Level of evidence supporting an annotation
 */
public enum EvidenceCode {
    /**
     * Annotations that have been extracted by parsing the Clinical Features sections of the omim.txt file are assigned
     * the evidence code IEA (inferred from electronic annotation). Please note that you need to contact OMIM in order
     * to reuse these annotations in other software products.
     */
    IEA,
    /**
     * Published clinical study. This should be used for information extracted from articles in the medical literature.
     * Generally, annotations of this type will include the PubMed id of the published study in the DB_Reference field.
     */
    PCS,
    /**
     * Individual clinical experience. This may be appropriate for disorders with a limited amount of published data.
     * This must be accompanied by an entry in the DB:Reference field denoting the individual or center performing the
     * annotation together with an identifier.
     */
    ICE,
    /**
     * Traceable author statement. Usually reviews or disease entries (e.g. OMIM) that only refers to the original
     * publication.
     */
    TAS
}
