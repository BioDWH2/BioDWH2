package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.herb.etl.HerbGraphExporter;

@JsonPropertyOrder({
        "Herb_ID", "Herb_pinyin_name", "Herb_cn_name", "Herb_en_name", "Herb_latin_name", "Properties", "Meridians",
        "UsePart", "Function", "Indication", "Toxicity", "Clinical_manifestations", "Therapeutic_en_class",
        "Therapeutic_cn_class", "TCMID_id", "TCM_ID_id", "SymMap_id", "TCMSP_id"
})
@GraphNodeLabel(HerbGraphExporter.HERB_LABEL)
public class Herb {
    @JsonProperty("Herb_ID")
    @GraphProperty("id")
    public String herbId;
    @JsonProperty("Herb_pinyin_name")
    @GraphProperty(value = "pinyin_name", emptyPlaceholder = "NA")
    public String herbPinyinName;
    @JsonProperty("Herb_cn_name")
    @GraphProperty(value = "cn_name", emptyPlaceholder = "NA")
    public String herbCnName;
    @JsonProperty("Herb_en_name")
    @GraphProperty(value = "en_name", emptyPlaceholder = "NA")
    public String herbEnName;
    @JsonProperty("Herb_latin_name")
    @GraphProperty(value = "latin_name", emptyPlaceholder = "NA")
    public String herbLatinName;
    @JsonProperty("Properties")
    @GraphProperty(value = "properties", emptyPlaceholder = "NA")
    public String properties;
    @JsonProperty("Meridians")
    @GraphProperty(value = "meridians", emptyPlaceholder = "NA")
    public String meridians;
    @JsonProperty("UsePart")
    @GraphProperty(value = "use_part", emptyPlaceholder = "NA")
    public String usePart;
    @JsonProperty("Function")
    @GraphProperty(value = "function", emptyPlaceholder = "NA")
    public String function;
    @JsonProperty("Indication")
    @GraphProperty(value = "indication", emptyPlaceholder = "NA")
    public String indication;
    @JsonProperty("Toxicity")
    @GraphProperty(value = "toxicity", emptyPlaceholder = "NA")
    public String toxicity;
    @JsonProperty("Clinical_manifestations")
    @GraphProperty(value = "clinical_manifestations", emptyPlaceholder = "NA")
    public String clinicalManifestations;
    @JsonProperty("Therapeutic_en_class")
    @GraphProperty(value = "therapeutic_en_class", emptyPlaceholder = "NA")
    public String therapeuticEnClass;
    @JsonProperty("Therapeutic_cn_class")
    @GraphProperty(value = "therapeutic_cn_class", emptyPlaceholder = "NA")
    public String therapeuticCnClass;
    @JsonProperty("TCMID_id")
    @GraphProperty(value = "tcmid_class", emptyPlaceholder = "NA")
    public String tcmidId;
    @JsonProperty("TCM_ID_id")
    @GraphProperty(value = "tcm_id_class", emptyPlaceholder = "NA")
    public String tcmIdId;
    @JsonProperty("SymMap_id")
    @GraphProperty(value = "symmap_class", emptyPlaceholder = "NA")
    public String symMapId;
    @JsonProperty("TCMSP_id")
    @GraphProperty(value = "tcmsp_id", emptyPlaceholder = "NA")
    public String tcmspId;
}
