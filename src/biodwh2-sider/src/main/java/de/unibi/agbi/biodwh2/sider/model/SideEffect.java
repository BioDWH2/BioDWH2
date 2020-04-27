package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/*
0: label
1 & 2: STITCH compound ids (flat/stereo, see above)
3: UMLS concept id as it was found on the label
4: MedDRA concept type (LLT = lowest level term, PT = preferred term; in a few cases the term is neither LLT nor PT)
5: UMLS concept id for MedDRA term
6: side effect name
*/
@JsonPropertyOrder(value = {
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

    public String getConceptId() {
        return meddraUmlsConceptId != null ? meddraUmlsConceptId : umlsConceptId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, stereoCompoundId, meddraUmlsConceptId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SideEffect other = (SideEffect) obj;
        return label.equals(other.label) && stereoCompoundId.equals(other.stereoCompoundId) && Objects.equals(
                umlsConceptId, other.umlsConceptId) && Objects.equals(meddraUmlsConceptId, other.meddraUmlsConceptId);
    }
}
