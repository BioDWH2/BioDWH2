package de.unibi.agbi.biodwh2.unii.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "UNII", "Display Name", "RN", "EC", "NCIT", "RXCUI", "PUBCHEM", "SMSID", "EPA_CompTox", "CATALOGUE_OF_LIFE",
        "ITIS", "NCBI", "PLANTS", "POWO", "GRIN", "MPNS", "INN_ID", "USAN_ID", "MF", "INCHIKEY", "SMILES",
        "INGREDIENT_TYPE", "SUBSTANCE_TYPE", "UUID", "DAILYMED"
})
@GraphNodeLabel("UNII")
public class UNIIDataEntry {
    @JsonProperty("UNII")
    @GraphProperty("id")
    public String unii;
    @JsonProperty("Display Name")
    @GraphProperty("name")
    public String displayName;
    @JsonProperty("RN")
    @GraphProperty("cas")
    public String rn;
    /**
     * European Chemicals Agency registry number (formerly known EINECS)
     * <p>
     * https://echa.europa.eu/information-on-chemicals
     */
    @JsonProperty("EC")
    @GraphProperty("ec")
    public String ec;
    /**
     * NCI Thesaurus Concept Code
     * <p>
     * https://ncit.nci.nih.gov/ncitbrowser/
     */
    @JsonProperty("NCIT")
    @GraphProperty("ncit")
    public String ncit;
    /**
     * RXNORM Ingredient Concept Code
     * <p>
     * https://www.nlm.nih.gov/research/umls/rxnorm/
     */
    @JsonProperty("RXCUI")
    @GraphProperty("rx_cui")
    public String rxCui;
    @JsonProperty("PUBCHEM")
    @GraphProperty("pubchem_cid")
    public String pubchem;
    @JsonProperty("SMSID")
    @GraphProperty("smsid")
    public String smsId;
    @JsonProperty("EPA_CompTox")
    @GraphProperty("epa_comptox")
    public String epaCompTox;
    @JsonProperty("CATALOGUE_OF_LIFE")
    @GraphProperty("catalogue_of_life")
    public String catalogueOfLife;
    /**
     * Integrated Taxonomic Information System Taxonomic Serial Number (ITIS TSN)
     * <p>
     * https://www.itis.gov
     */
    @JsonProperty("ITIS")
    public Integer itis;
    /**
     * NCBI taxonomy organism ID
     * <p>
     * https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Root
     */
    @JsonProperty("NCBI")
    public String ncbi;
    /**
     * USDA PLANTS organism ID
     * <p>
     * https://plants.usda.gov/java/
     */
    @JsonProperty("PLANTS")
    public String plants;
    /**
     * Plants of the World Online
     * <p>
     * https://powo.science.kew.org
     */
    @JsonProperty("POWO")
    @GraphProperty("powo")
    public String powo;
    /**
     * USDA Agricultural Research Service Germplasm Resources Information Network (GRIN) nomen ID
     * <p>
     * https://npgsweb.ars-grin.gov/gringlobal/taxon/taxonomysearch.aspx
     */
    @JsonProperty("GRIN")
    @GraphProperty("usda_grin_nomen_id")
    public String grin;
    /**
     * Kew Gardens Medicinal Plants Name Service Record ID (includes source database prefix)
     * <p>
     * http://mpns.kew.org/mpns-portal/
     */
    @JsonProperty("MPNS")
    @GraphProperty("mpns")
    public String mpns;
    /**
     * Sequential number assigned by World Health Organization's International Nonproprietary Name (INN) Programme
     * <p>
     * http://www.who.int/medicines/services/inn/en/
     */
    @JsonProperty("INN_ID")
    @GraphProperty("inn_id")
    public String innId;
    @JsonProperty("USAN_ID")
    @GraphProperty("usan_id")
    public String usanId;
    /**
     * Molecular Formula - total atoms represented as a molecular structure in a UNII description
     */
    @JsonProperty("MF")
    @GraphProperty("molecular_formula")
    public String mf;
    @JsonProperty("INCHIKEY")
    @GraphProperty("inchi_key")
    public String inchikey;
    @JsonProperty("SMILES")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("INGREDIENT_TYPE")
    @GraphProperty("ingredient_type")
    public String ingredientType;
    @JsonProperty("UUID")
    @GraphProperty("uuid")
    public String uuid;
    @JsonProperty("SUBSTANCE_TYPE")
    @GraphProperty("substance_type")
    public String substanceType;
    @JsonProperty("DAILYMED")
    @GraphProperty("dailymed")
    public String dailymed;
}
