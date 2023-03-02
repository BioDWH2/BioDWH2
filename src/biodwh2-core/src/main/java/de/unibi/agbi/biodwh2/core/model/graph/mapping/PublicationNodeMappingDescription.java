package de.unibi.agbi.biodwh2.core.model.graph.mapping;

import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;

import java.util.HashMap;
import java.util.Map;

public class PublicationNodeMappingDescription extends NodeMappingDescription {
    public String doi;
    /**
     * PubMed ID
     */
    public Integer pubmedId;
    /**
     * PubMed Central ID
     */
    public String pmcId;

    public PublicationNodeMappingDescription() {
        super(NodeType.PUBLICATION);
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        final Map<String, Object> result = new HashMap<>();
        if (pmcId != null)
            result.put("pmcid", pmcId);
        if (pubmedId != null)
            result.put("pmid", pubmedId);
        if (doi != null)
            result.put("doi", doi);
        return result;
    }
}
