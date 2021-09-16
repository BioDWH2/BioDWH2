package de.unibi.agbi.biodwh2.core.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubUser {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("login")
    public String login;
    @JsonProperty("avatar_url")
    public String avatarUrl;
    @JsonProperty("gravatar_id")
    public String gravatarId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("type")
    public String type;
    @JsonProperty("site_admin")
    public Boolean siteAdmin;
    @JsonProperty("name")
    public String name;
    @JsonProperty("company")
    public String company;
    @JsonProperty("blog")
    public String blog;
    @JsonProperty("location")
    public String location;
    @JsonProperty("email")
    public String email;
    @JsonProperty("hireable")
    public Boolean hireable;
    @JsonProperty("bio")
    public String bio;
    @JsonProperty("twitter_username")
    public String twitterUsername;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
}
