package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Patent"})
public final class Patent {
    @GraphProperty("number")
    public String number;
    @GraphProperty("country")
    public String country;
    @GraphProperty("approved")
    public String approved;
    @GraphProperty("expires")
    public String expires;
    @JsonProperty("pediatric-extension")
    @GraphProperty("pediatric_extension")
    public boolean pediatricExtension;
}
