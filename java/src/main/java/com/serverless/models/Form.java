package com.serverless.models;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "forms")
public class Form {

    public static final String PARTITION_KEY = "PK";
    public static final String SORT_KEY = "SK";

    @JsonProperty(PARTITION_KEY)
    private String PK;

    @JsonProperty(SORT_KEY)
    private String SK;

    @JsonProperty("GSI1PK")
    private String GSI1PK;

    @JsonProperty("description")
    private String description;

    @JsonProperty("title")
    private String title;

    @JsonProperty("groups")
    private ArrayList<Group> groups;

    public Form() {
    }

    @JsonProperty(PARTITION_KEY)
    @DynamoDBHashKey(attributeName = "PK")
    public String getPK() {
        return this.PK;
    }
    public void setPK(String PK) {
        this.PK = PK;
    }

    @JsonProperty(SORT_KEY)
    @DynamoDBRangeKey(attributeName = "SK")
    public String getSK() {
        return this.SK;
    }
    public void setSK(String SK) {
        this.SK = SK;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "GSI1PK")
    public String getGSI1PK() {
        return this.GSI1PK;
    }
    @JsonProperty("GSI1PK")
    public void setGSI1PK(String GSI1PK) {
        this.GSI1PK = GSI1PK;
    }

    @DynamoDBAttribute(attributeName = "title")
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "groups")
    public ArrayList<Group> getGroups() {
        return this.groups;
    }
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
    
    @Override
    public String toString() {
        return "Form[PK=" + PK + ", SK=" + SK + ", title=" + title + ", description=" + description + ", GSI1PK=" + GSI1PK + "]";
    }
}