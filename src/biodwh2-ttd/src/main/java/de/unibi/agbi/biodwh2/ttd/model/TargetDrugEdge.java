package de.unibi.agbi.biodwh2.ttd.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class TargetDrugEdge {
    @GraphArrayProperty(value = "moa", emptyPlaceholder = ".", arrayDelimiter = "; ")
    public String moa;
    @GraphProperty("activity")
    public String activity;
    @GraphProperty("highest_clinical_status")
    public String highestClinicalStatus;
}
