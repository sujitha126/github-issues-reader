package com.alienvault.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "top_day")
public class TopDay {

    @JsonProperty
    private String day;

    @JsonProperty
    private Occurrences occurrences;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Occurrences getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Occurrences occurrences) {
        this.occurrences = occurrences;
    }
}
