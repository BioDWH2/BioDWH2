package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Enzyme {
    /**
     * Unique identifier of the enzyme. For enzymes, the Enzyme Commission number (EC Number) given by the IUBMB
     * (International Union of Biochemistry and Molecular Biology), classes of enzymes und subclasses based on the
     * reaction they catalyze. For spontaneous, non-enzymatic reactions the id is `spontaneous`.
     */
    @JsonProperty("id")
    public String id;
    /**
     * Information about when EC number has been deleted or transferred.
     */
    @JsonProperty("history")
    public String history;
    /**
     * The recommended or accepted name of an enzyme as given by the IUBMB.
     */
    @JsonProperty("recommended_name")
    public String recommendedName;
    /**
     * The systematic name fields provides a chemically more precise name and is usually based on the substrate or, in
     * the case of a bimolecular reaction, of the two substrates separated by a colon, and the nature of the reaction.
     */
    @JsonProperty("systematic_name")
    public String systematicName;
    /**
     * Synonyms comprise names by which an enzyme is known. The names are found in other databases or in the literature,
     * abbreviations, names of commercially available products or gene names.
     */
    @JsonProperty("synonyms")
    public TextDataset[] synonyms;
    /**
     * Actual or possible application in the fields of i.e. pharmacology, medicine, biofuel production, analysis,
     * biotechnology etc. are described.
     */
    @JsonProperty("application")
    public TextDataset[] application;
    /**
     * According to the type of reaction, the enzymes catalyze, enzymes are classified into 7 main classes,
     * oxidoreductases, transferases, hydrolases, lyases, isomerases, ligases, and translocases with several
     * subcategories, i.e. addition, elimination, carboxylation etc.
     */
    @JsonProperty("reaction_type")
    public TextDataset[] reactionType;
    /**
     * Information on the X-ray structure of an enzyme and the procedure and conditions of the crystallization are
     * described.
     */
    @JsonProperty("crystallization")
    public Dataset[] crystallization;
    /**
     * Information on the renaturation, refolding and reactivation procedures of enzymes after denaturation processes.
     */
    @JsonProperty("purification")
    public Dataset[] purification;
    /**
     * Information on the renaturation, refolding and reactivation procedures of enzymes after denaturation processes.
     */
    @JsonProperty("renatured")
    public Dataset[] renatured;
    /**
     * General information on enzyme stability.
     */
    @JsonProperty("general_stability")
    public Dataset[] generalStability;
    /**
     * The influence of oxidating agents on enzyme stability.
     */
    @JsonProperty("oxidation_stability")
    public Dataset[] oxidationStability;
    /**
     * The influence of storage duration and conditions such as temperature, pH, additives etc. on the enzyme stability
     * and activity.
     */
    @JsonProperty("storage_stability")
    public Dataset[] storageStability;
    /**
     * The reaction as defined by the IUBMB (International Union of Biochemistry and Molecular Biology).
     */
    @JsonProperty("reaction")
    public TextDataset[] reaction;
    /**
     * Substrates and products in the metabolism of the cell in vivo, under physiological conditions. A natural
     * substrate and/or product is only given if it is specified in the literature reference.
     */
    @JsonProperty("natural_substrates_products")
    public TextDataset[] naturalSubstratesProducts;
    /**
     * All natural or synthetic substrates and products are listed here (not in stoichiometric quantities). In cases
     * with unclear product formation only a '?' as a dummy is given. The reversibility of the reaction is given here in
     * {...} r=reversible, ir=irreversible.
     */
    @JsonProperty("substrates_products")
    public TextDataset[] substratesProducts;
    /**
     * The turnover number (kcat) is given in the unit 1/s. Each value is connected to a substrate name in {...}.
     */
    @JsonProperty("turnover_number")
    public NumericDataset[] turnoverNumber;
    /**
     * The Michaelis-Menten value of the enzyme in mM. Each value is connected to a substrate name.
     */
    @JsonProperty("km_value")
    public NumericDataset[] kmValue;
    /**
     * The pH where the enzymes activity is at its maximum.
     */
    @JsonProperty("ph_optimum")
    public NumericDataset[] phOptimum;
    /**
     * pH range in which the enzyme is active.
     */
    @JsonProperty("ph_range")
    public NumericDataset[] phRange;
    /**
     * This field can either give a range or a single value in which the enzyme has its optimal stability.
     */
    @JsonProperty("ph_stability")
    public NumericDataset[] phStability;
    /**
     * The specific activity of the enzyme in µmol/min/mg.
     */
    @JsonProperty("specific_activity")
    public NumericDataset[] specificActivity;
    /**
     * The temperature where the enzymes activity is at its maximum in °C.
     */
    @JsonProperty("temperature_optimum")
    public NumericDataset[] temperatureOptimum;
    /**
     * Temperature range in which the enzyme is active in °C.
     */
    @JsonProperty("temperature_range")
    public NumericDataset[] temperatureRange;
    /**
     * The temperature stability is given in °C. This field can either give a range or a single value in which the
     * enzyme is stable.
     */
    @JsonProperty("temperature_stability")
    public NumericDataset[] temperatureStability;
    /**
     * The molecular weight of the holoenzyme. For monomeric enzymes the MW is identical to the value given for
     * subunits.
     */
    @JsonProperty("molecular_weight")
    public NumericDataset[] molecularWeight;
    /**
     * Isoelectric point of the enzyme, the pH value at which the protein carries no net electrical charge.
     */
    @JsonProperty("pi_value")
    public NumericDataset[] piValue;
    /**
     * The inhibition constant in mM. Each value is connected to an inhibitor in {...}
     */
    @JsonProperty("ki_value")
    public NumericDataset[] kiValue;
    /**
     * The half maximal inhibitory concentration of a compund in mM. Each value is connected to an inhibitor in {...}.
     */
    @JsonProperty("ic50_value")
    public NumericDataset[] ic50Value;
    /**
     * The catalytic efficiency of the enzyme in mM/s. Each value is connected to a substrate in {...}.
     */
    @JsonProperty("kcat_km_value")
    public NumericDataset[] kcatKmValue;
    /**
     * The intracellular localization of the enzyme.
     */
    @JsonProperty("localization")
    public TextDataset[] localization;
    /**
     * For multicellular organisms the tissues used for isolation of the enzyme or the tissue in which the enzyme is
     * present. Cell-lines can also be a source of enzymes.
     */
    @JsonProperty("source_tissue")
    public TextDataset[] sourceTissue;
    /**
     * Compounds that have positive effects on the activity of the enzyme, except metal ions and cofactors.
     */
    @JsonProperty("activating_compound")
    public TextDataset[] activatingCompound;
    /**
     * Compounds found to be inhibitory to the enzyme.
     */
    @JsonProperty("inhibitor")
    public TextDataset[] inhibitor;
    /**
     * The ions or salts that have activating effects, or are closely bound to the enzyme.
     */
    @JsonProperty("metals_ions")
    public TextDataset[] metalsIons;
    /**
     * This field describes covalent and general enzyme modifications following protein biosynthesis.
     */
    @JsonProperty("posttranslational_modification")
    public TextDataset[] posttranslationalModification;
    /**
     * The tertiary structure of the active enzyme is described. It can be active as a monomer, dimer, trimer etc. The
     * stoichiometry of the subunits is given.
     */
    @JsonProperty("subunits")
    public TextDataset[] subunits;
    /**
     * All compounds which participate in the reaction, which are loosely-bound to the protein and are required for the
     * enzyme activity, whereas prosthetic groups are tightly-bound to the enzyme.
     */
    @JsonProperty("cofactor")
    public TextDataset[] cofactor;
    /**
     * The properties of modified enzyme proteins are described. The modification could be i.e. natural occurring
     * mutations, site-directed mutagenesis, deletions etc. In most cases the amino acid exchanges are given.
     */
    @JsonProperty("protein_variants")
    public TextDataset[] proteinVariants;
    /**
     * Information on expression and cloning procedures and systems are given and in which organism or cell culture an
     * enzyme is expressed in.
     */
    @JsonProperty("cloned")
    public TextDataset[] cloned;
    /**
     * The enzyme stability in presence of organic solvents is described.
     */
    @JsonProperty("organic_solvent_stability")
    public TextDataset[] organicSolventStability;
    /**
     * This information field describes the effect of compounds and/or conditions on the expression of enzymes leading
     * to an up- or down-regulation.
     */
    @JsonProperty("expression")
    public TextDataset[] expression;
    /**
     * This field contains more general information on the role of an enzyme in the metabolism, the physiological
     * function, a possible malfunction, or evolutionary aspects.
     */
    @JsonProperty("general_information")
    public TextDataset[] generalInformation;
    /**
     * Protein/gene accessions associated with the data field.
     */
    @JsonProperty("protein")
    public Map<Integer, ProteinDataset> proteins;
    /**
     * The references associated with the data field.
     */
    @JsonProperty("reference")
    public Map<Integer, ReferenceDataset> references;
}
