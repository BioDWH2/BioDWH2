package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

import java.util.Objects;

@NodeLabels({"Gene"})
@JsonPropertyOrder({"gene_claim_name", "gene_name", "entrez_id", "gene_claim_source"})
public class Gene {
    @GraphProperty("claim_name")
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @GraphProperty("name")
    @JsonProperty("gene_name")
    public String geneName;
    @GraphProperty("entrez_id")
    @JsonProperty("entrez_id")
    public String entrezId;
    @JsonProperty("gene_claim_source")
    public String geneClaimSource;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Gene gene = (Gene) o;
        return Objects.equals(geneClaimName, gene.geneClaimName) && Objects.equals(geneName, gene.geneName) &&
               Objects.equals(entrezId, gene.entrezId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geneClaimName, geneName, entrezId);
    }
}
