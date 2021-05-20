package de.unibi.agbi.biodwh2.pharmgkb.model.guideline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NodeLabels({"GuidelineAnnotation"})
public final class Guideline {
    public String objCls;
    @GraphProperty("id")
    public String id;
    @GraphProperty("name")
    public String name;
    @GraphProperty("cancer_genome")
    public Boolean cancerGenome;
    @GraphProperty("recommendation")
    public Boolean recommendation;
    @GraphProperty("pediatric")
    public Boolean pediatric;
    public List<CrossReference> crossReferences;
    public List<Citation> literature;
    @GraphProperty("descriptive_video_id")
    public String descriptiveVideoId;
    public String userId;
    @GraphProperty("source")
    public String source;
    @GraphProperty("version")
    public Integer version;
}
