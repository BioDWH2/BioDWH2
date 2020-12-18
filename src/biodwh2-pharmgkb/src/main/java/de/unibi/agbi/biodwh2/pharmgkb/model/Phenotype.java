package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;
import de.unibi.agbi.biodwh2.pharmgkb.etl.PharmGKBGraphExporter;

@NodeLabels("Phenotype")
public class Phenotype {
    @Parsed(field = "PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
    @GraphProperty("name")
    public String name;
    @Parsed(field = "Alternate Names")
    @GraphArrayProperty(value = "alternate_names", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String alternateNames;
    @Parsed(field = "Cross-references")
    @GraphArrayProperty(value = "cross_references", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String crossReferences;
    @Parsed(field = "External Vocabulary")
    @GraphArrayProperty(value = "external_vocabulary", arrayDelimiter = PharmGKBGraphExporter.QUOTED_ARRAY_DELIMITER)
    public String externalVocabulary;
}
