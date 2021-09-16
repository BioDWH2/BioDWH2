package de.unibi.agbi.biodwh2.core.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubAsset {
    @JsonProperty("url")
    public String url;
    @JsonProperty("browser_download_url")
    public String browserDownloadUrl;
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("label")
    public String label;
    @JsonProperty("state")
    public String state;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("content_type")
    public String contentType;
    @JsonProperty("size")
    public Integer size;
    @JsonProperty("download_count")
    public Integer downloadCount;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
    @JsonProperty("uploader")
    public GithubUser uploader;
}
