package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRGraphExporter;

@JsonPropertyOrder({
        "Cell Line ID", "Cell Line Name", "Accession ID", "Cell Line Description", "Cell Line Synonyms", "Disease",
        "Species"
})
@GraphNodeLabel(NPCDRGraphExporter.CELL_LINE_LABEL)
public class CellLine {
    @JsonProperty("Cell Line ID")
    @GraphProperty(GraphExporter.ID_KEY)
    public String id;
    @JsonProperty("Cell Line Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Accession ID")
    @GraphProperty(value = "accession", emptyPlaceholder = ".")
    public String accession;
    @JsonProperty("Cell Line Description")
    @GraphProperty(value = "description", emptyPlaceholder = ".")
    public String description;
    @JsonProperty("Cell Line Synonyms")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String synonyms;
    @JsonProperty("Disease")
    @GraphProperty(value = "disease", emptyPlaceholder = ".")
    public String disease;
    @JsonProperty("Species")
    @GraphProperty(value = "species", emptyPlaceholder = ".")
    public String species;
}
