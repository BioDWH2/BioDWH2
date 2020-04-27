package de.unibi.agbi.biodwh2.unii.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "UNII", "PT", "RN", "EC", "NCIT", "RXCUI", "PUBCHEM", "ITIS", "NCBI", "PLANTS", "GRIN", "MPNS", "INN_ID", "MF",
        "INCHIKEY", "SMILES", "INGREDIENT_TYPE"
})
public class UNIIDataEntry {
    @JsonProperty("UNII")
    public String unii;
    @JsonProperty("PT")
    public String pt;
    @JsonProperty("RN")
    public String rn;
    /*
     * European Chemicals Agency registry number (formerly known EINECS)
     * https://echa.europa.eu/information-on-chemicals
     */
    @JsonProperty("EC")
    public String ec;
    /*
     * NCI Thesaurus Concept Code
     * https://ncit.nci.nih.gov/ncitbrowser/
     */
    @JsonProperty("NCIT")
    public String ncit;
    /*
     * RXNORM Ingredient Concept Code
     * https://www.nlm.nih.gov/research/umls/rxnorm/
     */
    @JsonProperty("RXCUI")
    public String rxCui;
    @JsonProperty("PUBCHEM")
    public String pubchem;
    /*
     * Integrated Taxonomic Information System Taxonomic Serial Number (ITIS TSN)
     * https://www.itis.gov/
     */
    @JsonProperty("ITIS")
    public String itis;
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
    public String grin;
    /*
     * Kew Gardens Medicinal Plants Name Service Record ID (includes source database prefix)
     * http://mpns.kew.org/mpns-portal/
     */
    @JsonProperty("MPNS")
    public String mpns;
    /*
     * Sequential number assigned by World Health Organization's International Nonproprietary Name (INN) Programme
     * http://www.who.int/medicines/services/inn/en/
     */
    @JsonProperty("INN_ID")
    public String innId;
    /*
     * Molecular Formula - total atoms represented as a molecular structure in a UNII description
     */
    @JsonProperty("MF")
    public String mf;
    @JsonProperty("INCHIKEY")
    public String inchikey;
    @JsonProperty("SMILES")
    public String smiles;
    @JsonProperty("INGREDIENT_TYPE")
    public String ingredientType;
}
