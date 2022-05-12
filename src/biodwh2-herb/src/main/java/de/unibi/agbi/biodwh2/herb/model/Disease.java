package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Disease_id", "DisGENet_disease_id", "Disease_name", "Disease_type", "DiseaseClass_MeSH",
        "DiseaseClassName_MeSH", "HPO_ClassId", "HPO_ClassName", "DO_ClassId", "DO_ClassName", "UMLS_SemanticTypeId",
        "UMLS_SemanticTypeName"
})
@GraphNodeLabel("Disease")
public class Disease {
    @JsonProperty("Disease_id")
    @GraphProperty("id")
    public String diseaseId;
    @JsonProperty("DisGENet_disease_id")
    @GraphProperty("disgenet_id")
    public String disGeNetDiseaseId;
    @JsonProperty("Disease_name")
    @GraphProperty("name")
    public String diseaseName;
    @JsonProperty("Disease_type")
    @GraphProperty("type")
    public String diseaseType;
    @JsonProperty("DiseaseClass_MeSH")
    @GraphProperty("mesh_class")
    public String diseaseClassMeSH;
    @JsonProperty("DiseaseClassName_MeSH")
    @GraphProperty("mesh_class_name")
    public String diseaseClassNameMeSH;
    @JsonProperty("HPO_ClassId")
    @GraphProperty("hpo_class_id")
    public String hpoClassId;
    @JsonProperty("HPO_ClassName")
    @GraphProperty("hpo_class_name")
    public String hpoClassName;
    @JsonProperty("DO_ClassId")
    @GraphProperty("do_class_id")
    public String doClassId;
    @JsonProperty("DO_ClassName")
    @GraphProperty("do_class_name")
    public String doClassName;
    @JsonProperty("UMLS_SemanticTypeId")
    @GraphProperty("umls_semantic_type_id")
    public String umlsSemanticTypeId;
    @JsonProperty("UMLS_SemanticTypeName")
    @GraphProperty("umls_semantic_type_name")
    public String umlsSemanticTypeName;
}
