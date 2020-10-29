package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
1 & 2: STITCH compound ids (flat/stereo, see above)
3: UMLS concept id as it was found on the label
4: "placebo" if the info comes from placebo administration, "" otherwise
5: a description of the frequency: for example "postmarketing", "rare", "infrequent", "frequent", "common", or an exact percentage
6: a lower bound on the frequency
7: an upper bound on the frequency
8: MedDRA concept type (LLT = lowest level term, PT = preferred term; in a few cases the term is neither LLT nor PT)
9: UMLS concept id for MedDRA term
10: side effect name
 */
@JsonPropertyOrder({
        "flat_compound_id", "stereo_compound_id", "umls_concept_id", "placebo", "frequency", "frequency_lower_bound",
        "frequency_upper_bound", "meddra_concept_type", "meddra_umls_concept_id", "side_effect_name"
})
public class Frequency {
    @JsonProperty("flat_compound_id")
    public String flatCompoundId;
    @JsonProperty("stereo_compound_id")
    public String stereoCompoundId;
    @JsonProperty("umls_concept_id")
    public String umlsConceptId;
    @JsonProperty("placebo")
    public String placebo;
    @JsonProperty("frequency")
    public String frequency;
    @JsonProperty("frequency_lower_bound")
    public String frequencyLowerBound;
    @JsonProperty("frequency_upper_bound")
    public String frequencyUpperBound;
    @JsonProperty("meddra_concept_type")
    public String meddraConceptType;
    @JsonProperty("meddra_umls_concept_id")
    public String meddraUmlsConceptId;
    @JsonProperty("side_effect_name")
    public String sideEffectName;
}
