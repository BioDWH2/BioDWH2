package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

import java.util.List;

@GraphNodeLabel(MarkerDBGraphExporter.SEQUENCE_VARIANT_LABEL)
public class SequenceVariant {
    @GraphProperty(GraphExporter.ID_KEY)
    public Integer id;
    @GraphProperty(value = "variation", ignoreEmpty = true)
    public String variation;
    @GraphProperty(value = "position", ignoreEmpty = true)
    public String position;
    @JsonProperty("external_link")
    @GraphProperty(value = "external_link", ignoreEmpty = true)
    public String externalLink;
    @GraphProperty(value = "reference", ignoreEmpty = true)
    public Integer reference;
    @JsonProperty("sequence_variant_measurements")
    public List<SequenceVariantMeasurement> sequenceVariantMeasurements;
}
