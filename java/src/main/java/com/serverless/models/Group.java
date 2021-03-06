package com.serverless.models;

import java.util.ArrayList;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDBDocument
public class Group {
    
    @JsonProperty("questions")
    private ArrayList<Question> questions;

    @JsonProperty("description")
    private String description;

    @JsonProperty("label")
    private String label;

    @DynamoDBAttribute(attributeName = "questions")
    public ArrayList<Question> getQuestions() {
        return this.questions;
    }
    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
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
}
