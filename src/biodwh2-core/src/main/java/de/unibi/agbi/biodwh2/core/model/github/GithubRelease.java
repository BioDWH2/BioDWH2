package de.unibi.agbi.biodwh2.core.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRelease {
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("assets_url")
    public String assetsUrl;
    @JsonProperty("tarball_url")
    public String tarballUrl;
    @JsonProperty("zipball_url")
    public String zipballUrl;
    @JsonProperty("upload_url")
    public String uploadUrl;
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("tag_name")
    public String tagName;
    @JsonProperty("name")
    public String name;
    @JsonProperty("body")
    public String body;
    @JsonProperty("target_commitish")
    public String targetCommitish;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("published_at")
    public String publishedAt;
    @JsonProperty("draft")
    public Boolean draft;
    @JsonProperty("prerelease")
    public Boolean preRelease;
    @JsonProperty("assets")
    public List<GithubAsset> assets;
    @JsonProperty("author")
    public GithubUser author;
}
