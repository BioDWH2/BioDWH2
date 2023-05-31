package de.unibi.agbi.biodwh2.hmdb.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class MetaboliteStructure {
    @GraphProperty("structure")
    public String structure;
    @GraphProperty("jchem_acceptor_count")
    public Integer jchemAcceptorCount;
    @GraphProperty("jchem_atom_count")
    public Integer jchemAtomCount;
    @GraphProperty("jchem_average_polarizability")
    public String jchemAveragePolarizability;
    @GraphProperty("jchem_bioavailability")
    public String jchemBioavailability;
    @GraphProperty("jchem_donor_count")
    public Integer jchemDonorCount;
    @GraphProperty("jchem_formal_charge")
    public String jchemFormalCharge;
    @GraphProperty("jchem_ghose_filter")
    public String jchemGhoseFilter;
    @GraphProperty("jchem_iupac")
    public String jchemIupac;
    @GraphProperty("alogps_logp")
    public String alogpsLogp;
    @GraphProperty("jchem_logp")
    public String jchemLogp;
    @GraphProperty("alogps_logs")
    public String alogpsLogs;
    @GraphProperty("jchem_mddr_like_rule")
    public String jchemMddrLikeRule;
    @GraphProperty("jchem_number_of_rings")
    public String jchemNumberOfRings;
    @GraphProperty("jchem_physiological_charge")
    public String jchemPhysiologicalCharge;
    @GraphProperty("jchem_pka_strongest_acidic")
    public String jchemPkaStrongestAcidic;
    @GraphProperty("jchem_pka_strongest_basic")
    public String jchemPkaStrongestBasic;
    @GraphProperty("jchem_polar_surface_area")
    public String jchemPolarSurfaceArea;
    @GraphProperty("jchem_refractivity")
    public String jchemRefractivity;
    @GraphProperty("jchem_rotatable_bond_count")
    public Integer jchemRotatableBondCount;
    @GraphProperty("jchem_rule_of_five")
    public String jchemRuleOfFive;
    @GraphProperty("alogps_solubility")
    public String alogpsSolubility;
    @GraphProperty("jchem_traditional_iupac")
    public String jchemTraditionalIupac;
    @GraphProperty("jchem_veber_rule")
    public String jchemVeberRule;
}
