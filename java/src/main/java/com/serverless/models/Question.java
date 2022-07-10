package com.serverless.models;


import java.util.ArrayList;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDBDocument
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

    @JsonProperty("value")
    private String value;


    @DynamoDBAttribute(attributeName = "options")
    public ArrayList<Option> getOptions() {
        return this.options;
    }
    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "label")
    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    @DynamoDBAttribute(attributeName = "type")
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = "required")
    public Boolean getRequired() {
        return this.required;
    }
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @DynamoDBAttribute(attributeName = "value")
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
