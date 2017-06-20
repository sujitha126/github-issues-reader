package com.alienvault.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.Map;

@JsonRootName(value = "occurrences")
public class Occurrences {

    @JsonIgnore
    private Map<String, Integer> additionalProperties =
        new HashMap<>();

    @JsonAnyGetter
    public Map<String, Integer> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Integer value) {
        this.additionalProperties.put(name, value);
    }

    public void setAdditionalProperties(Map<String, Integer> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @JsonIgnore
    public int getTotal() {
        return this.additionalProperties.values().stream().mapToInt(i->i).sum();
    }
}
