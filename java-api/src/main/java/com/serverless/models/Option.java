package com.serverless.models;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Option {

    @JsonProperty("label")
    private String label;

    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
}
