package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

import java.util.List;

@GraphNodeLabel(HMDBGraphExporter.ONTOLOGY_TERM_LABEL)
public class OntologyTerm {
    @GraphProperty("term")
    public String term;
    @GraphProperty("definition")
    public String definition;
    @JsonProperty("parent_id")
    @GraphProperty("parent_id")
    public Integer parentId;
    @GraphProperty("level")
    public Integer level;
    @GraphProperty("type")
    public String type;
    @JacksonXmlElementWrapper(localName = "synonyms")
    @GraphProperty(value = "synonyms", transformation = ValueTransformation.COLLECTION_TO_STRING_ARRAY)
    public List<String> synonyms;
    @JacksonXmlElementWrapper(localName = "descendants")
    public List<OntologyTerm> descendants;
}
