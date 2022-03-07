package com.serverless.models;


import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {
    
    @JsonProperty("options")
    private ArrayList<Option> options;

    @JsonProperty("description")
    private String description;

    @JsonProperty("label")
    private String label;

    @JsonProperty("type")
    private String type;

    @JsonProperty("required")
    private Boolean required;


    public ArrayList<Option> getOptions() {
        return this.options;
    }
    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return this.required;
    }
    public void setRequired(Boolean required) {
        this.required = required;
    }
}
