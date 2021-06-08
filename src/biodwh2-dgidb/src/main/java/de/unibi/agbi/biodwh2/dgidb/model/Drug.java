package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

import java.util.Objects;

@GraphNodeLabel("Drug")
@JsonPropertyOrder({"drug_claim_name", "drug_name", "chembl_id", "drug_claim_source"})
public class Drug {
    @GraphProperty("claim_name")
    @JsonProperty("drug_claim_name")
    public String drugClaimName;
    @GraphProperty("name")
    @JsonProperty("drug_name")
    public String drugName;
    @GraphProperty("chembl_id")
    @JsonProperty("chembl_id")
    public String chemblId;
    @JsonProperty("drug_claim_source")
    public String drugClaimSource;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Drug drug = (Drug) o;
        return Objects.equals(drugClaimName, drug.drugClaimName) && Objects.equals(drugName, drug.drugName) &&
               Objects.equals(chemblId, drug.chemblId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drugClaimName, drugName, chemblId);
    }
}
