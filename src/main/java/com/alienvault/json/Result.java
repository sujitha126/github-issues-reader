package com.alienvault.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Result {

    private List<Issue> issues;

    @JsonProperty("top-day")
    private TopDay topDay;

    public void addIssue(Issue issue) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        getIssues().add(issue);
    }

    public void addIssues(List<Issue> issues) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        getIssues().addAll(issues);
    }


    public List<Issue> getIssues() {
        return this.issues;
    }

    public TopDay getTopDay() {
        return topDay;
    }

    public void setTopDay(TopDay topDay) {
        this.topDay = topDay;
    }
}
