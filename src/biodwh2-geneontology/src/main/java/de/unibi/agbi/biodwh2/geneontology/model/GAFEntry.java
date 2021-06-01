package de.unibi.agbi.biodwh2.geneontology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DB", "DB Object ID", "DB Object Symbol", "Qualifier", "GO ID", "DB:Reference", "Evidence Code",
        "With (or) From", "Aspect", "DB Object Name", "DB Object Synonym", "DB Object Type", "Taxon", "Date",
        "Assigned By", "Annotation Extension", "Gene Product Form ID"
})
public class GAFEntry {
    /**
     * required, 1
     */
    @JsonProperty("DB")
    public String db;
    /**
     * required, 1
     */
    @JsonProperty("DB Object ID")
    public String dbObjectId;
    /**
     * required, 1
     */
    @JsonProperty("DB Object Symbol")
    public String dbObjectSymbol;
    /**
     * required, 1 or 2
     */
    @JsonProperty("Qualifier")
    public String qualifier;
    /**
     * required, 1
     */
    @JsonProperty("GO ID")
    public String goId;
    /**
     * required, 1 or greater
     */
    @JsonProperty("DB:Reference")
    public String dbReference;
    /**
     * required, 1
     */
    @JsonProperty("Evidence Code")
    public String evidenceCode;
    /**
     * optional, 0 or greater
     */
    @JsonProperty("With (or) From")
    public String withOrFrom;
    /**
     * required, 1
     */
    @JsonProperty("Aspect")
    public String aspect;
    /**
     * optional, 0 or 1
     */
    @JsonProperty("DB Object Name")
    public String dbObjectName;
    /**
     * optional, 0 or greater
     */
    @JsonProperty("DB Object Synonym")
    public String dbObjectSynonym;
    /**
     * required, 1
     */
    @JsonProperty("DB Object Type")
    public String dbObjectType;
    /**
     * required, 1 or 2
     */
    @JsonProperty("Taxon")
    public String taxon;
    /**
     * required, 1
     */
    @JsonProperty("Date")
    public String date;
    /**
     * required, 1
     */
    @JsonProperty("Assigned By")
    public String assignedBy;
    /**
     * optional, 0 or greater
     */
    @JsonProperty("Annotation Extension")
    public String annotationExtension;
    /**
     * optional, 0 or 1
     */
    @JsonProperty("Gene Product Form ID")
    public String geneProductFormId;
}
