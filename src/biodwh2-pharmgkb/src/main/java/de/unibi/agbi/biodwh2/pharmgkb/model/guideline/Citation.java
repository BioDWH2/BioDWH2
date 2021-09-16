package de.unibi.agbi.biodwh2.pharmgkb.model.guideline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Citation {
    public Integer id;
    public String resourceId;
    public Integer version;
    public String objCls;
    public String type;
    public String title;
    public String journal;
    public Integer year;
    public Integer month;
    public String page;
    public String volume;
    public String summary;
    public List<String> authors;
    public String pubDate;
    public List<String> meshDiseases;
    public List<String> meshTerms;
    public Boolean pediatric;
    public Boolean pgkbPublication;
    public Boolean nonHuman;
    public Boolean hasKeyword;
    public List<CrossReference> crossReferences;
}
