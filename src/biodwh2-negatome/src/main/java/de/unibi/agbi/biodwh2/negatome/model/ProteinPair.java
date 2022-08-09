package de.unibi.agbi.biodwh2.negatome.model;

/**
 * <pre>
 * - Manual: Manually annotated literature data describing the lack of protein interaction.
 *           High-throughput data are not included. The data is restricted only to mammalian proteins.
 *      - Manual-stringent: The Manual dataset filtered against the IntAct dataset
 *              - Manual-PFAM: PFAM domain pairs found in the Manual dataset filtered using iPFAM and 3did
 *
 * - PDB: Protein pairs that are members of at least one structural complex but do not interact directly.
 *        Organism of origin is not restricted
 *      - PDB-stringent: The PDB dataset filtered against the IntAct dataset
 *              - PDB-PFAM: Non-interacting PFAM domains found in the same structural complex filtered using iPFAM and 3did
 *
 * - Combined: A combined non-interacting Protein dataset of Manual and PDB
 *      - Combined-stringent: A combined stringent non-interacting Protein dataset
 *              - Combined PFAM: A combined non-interacting Protein domain dataset
 * </pre>
 */
public class ProteinPair {
    public String uniProtId1;
    public String uniProtId2;
    public Integer manualPmid;
    public String manualPmcid;
    public String manualEvidence;
    public String[] pdbCodes;
    public String pdbEvidence;
    public boolean isManual;
    public boolean isManualStringent;
    public boolean isPDB;
    public boolean isPDBStringent;
}
