package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("ClinicalVariant")
public class ClinicalVariant {
    @Parsed(field = "variant")
    public String variant;
    @Parsed(field = "gene")
    public String gene;
    @Parsed(field = "type")
    @GraphProperty("type")
    public String type;
    @Parsed(field = "level of evidence")
    @GraphProperty("level_of_evidence")
    public String levelOfEvidence;
    @Parsed(field = "chemicals")
    public String chemicals;
    @Parsed(field = "phenotypes")
    public String phenotypes;
}
