package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
0: label
1 & 2: STITCH compound ids (flat/stereo, see above)
3: UMLS concept id as it was found on the label
4: MedDRA concept type (LLT = lowest level term, PT = preferred term; in a few cases the term is neither LLT nor PT)
5: UMLS concept id for MedDRA term
6: side effect name
*/
@JsonPropertyOrder({
        "label", "flat_compound_id", "stereo_compound_id", "umls_concept_id", "meddra_concept_type",
        "meddra_umls_concept_id", "side_effect_name"
})
public class SideEffect {
    @JsonProperty("label")
    public String label;
    @JsonProperty("flat_compound_id")
    public String flatCompoundId;
    @JsonProperty("stereo_compound_id")
    public String stereoCompoundId;
    @JsonProperty("umls_concept_id")
    public String umlsConceptId;
    @JsonProperty("meddra_concept_type")
    public String meddraConceptType;
    @JsonProperty("meddra_umls_concept_id")
    public String meddraUmlsConceptId;
    @JsonProperty("side_effect_name")
    public String sideEffectName;
}
