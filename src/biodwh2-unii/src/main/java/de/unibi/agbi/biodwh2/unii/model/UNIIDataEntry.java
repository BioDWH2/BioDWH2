package de.unibi.agbi.biodwh2.unii.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "UNII", "PT", "RN", "EC", "NCIT", "RXCUI", "PUBCHEM", "ITIS", "NCBI", "PLANTS", "GRIN", "MPNS", "INN_ID",
        "USAN_ID", "MF", "INCHIKEY", "SMILES", "INGREDIENT_TYPE", "UUID", "SUBSTANCE_TYPE", "DAILYMED"
})
@GraphNodeLabel("UNII")
public class UNIIDataEntry {
    @JsonProperty("UNII")
    @GraphProperty("id")
    public String unii;
    @JsonProperty("PT")
    @GraphProperty("preferred_term")
    public String pt;
    @JsonProperty("RN")
    @GraphProperty("cas")
    public String rn;
    /*
     * European Chemicals Agency registry number (formerly known EINECS)
     * https://echa.europa.eu/information-on-chemicals
     */
    @JsonProperty("EC")
    @GraphProperty("ec")
    public String ec;
    /*
     * NCI Thesaurus Concept Code
     * https://ncit.nci.nih.gov/ncitbrowser/
     */
    @JsonProperty("NCIT")
    @GraphProperty("ncit")
    public String ncit;
    /*
     * RXNORM Ingredient Concept Code
     * https://www.nlm.nih.gov/research/umls/rxnorm/
     */
    @JsonProperty("RXCUI")
    @GraphProperty("rx_cui")
    public String rxCui;
    @JsonProperty("PUBCHEM")
    @GraphProperty("pubchem_cid")
    public String pubchem;
    /*
     * Integrated Taxonomic Information System Taxonomic Serial Number (ITIS TSN)
     * https://www.itis.gov/
     */
    @JsonProperty("ITIS")
    public Long itis;
    /*
     * NCBI taxonomy organism ID
     * https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Root
     */
    @JsonProperty("NCBI")
    public String ncbi;
    /*
     * USDA PLANTS organism ID
     * https://plants.usda.gov/java/
     */
    @JsonProperty("PLANTS")
    public String plants;
    /*
     * USDA Agricultural Research Service Germplasm Resources Information Network (GRIN) nomen ID
     * https://npgsweb.ars-grin.gov/gringlobal/taxon/taxonomysearch.aspx
     */
    @JsonProperty("GRIN")
    @GraphProperty("usda_grin_nomen_id")
    public String grin;
    /*
     * Kew Gardens Medicinal Plants Name Service Record ID (includes source database prefix)
     * http://mpns.kew.org/mpns-portal/
     */
    @JsonProperty("MPNS")
    @GraphProperty("mpns")
    public String mpns;
    /*
     * Sequential number assigned by World Health Organization's International Nonproprietary Name (INN) Programme
     * http://www.who.int/medicines/services/inn/en/
     */
    @JsonProperty("INN_ID")
    @GraphProperty("inn_id")
    public String innId;
    @JsonProperty("USAN_ID")
    @GraphProperty("usan_id")
    public String usanId;
    /*
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
