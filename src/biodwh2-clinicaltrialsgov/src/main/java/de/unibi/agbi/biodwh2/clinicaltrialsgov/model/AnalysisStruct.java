package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class AnalysisStruct {
    @JsonProperty(value = "group_id_list", required = true)
    public AnalysisStruct.GroupIdList groupIdList;
    @JsonProperty(value = "groups_desc")
    public String groupsDesc;
    @JsonProperty(value = "non_inferiority_type")
    public NonInferiorityTypeEnum nonInferiorityType;
    @JsonProperty(value = "non_inferiority_desc")
    public String nonInferiorityDesc;
    @JsonProperty(value = "p_value")
    public String pValue;
    @JsonProperty(value = "p_value_desc")
    public String pValueDesc;
    public String method;
    @JsonProperty(value = "method_desc")
    public String methodDesc;
    @JsonProperty(value = "param_type")
    public String paramType;
    @JsonProperty(value = "param_value")
    public String paramValue;
    @JsonProperty(value = "dispersion_type")
    public AnalysisDispersionEnum dispersionType;
    @JsonProperty(value = "dispersion_value")
    public String dispersionValue;
    @JsonProperty(value = "ci_percent")
    public Float ciPercent;
    @JsonProperty(value = "ci_n_sides")
    public String ciNSides;
    @JsonProperty(value = "ci_lower_limit")
    public String ciLowerLimit;
    @JsonProperty(value = "ci_upper_limit")
    public String ciUpperLimit;
    @JsonProperty(value = "ci_upper_limit_na_comment")
    public String ciUpperLimitNaComment;
    @JsonProperty(value = "estimate_desc")
    public String estimateDesc;
    @JsonProperty(value = "other_analysis_desc")
    public String otherAnalysisDesc;

    public static class GroupIdList {
        @JsonProperty(value = "group_id", required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<String> groupId;
    }
}
