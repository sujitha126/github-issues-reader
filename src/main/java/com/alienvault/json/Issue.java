package com.alienvault.json;

import com.alienvault.mapper.DateSerializer;
import com.alienvault.mapper.IssueDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonDeserialize(using = IssueDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "issues")
public class Issue {

    @JsonProperty
    private long id;

    @JsonProperty
    private String state;

    @JsonProperty
    private String title;

    @JsonProperty
    private String repository;

    @JsonProperty("created_at")
    @JsonSerialize(using=DateSerializer.class)
    private Date createdDate;

    public Issue(long id, String state, String title, String repository, Date createdDate) {
        this.id = id;
        this.state = state;
        this.title = title;
        this.repository = repository;
        this.createdDate = createdDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

}
