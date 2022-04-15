package com.serverless.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverless.utils.DynamoDBAdapter;

@DynamoDBTable(tableName = "PLACEHOLDER_POLLS_TABLE_NAME")
public class Result {

    private static final String POLLS_TABLE_NAME = System.getenv("POLLS_TABLE_NAME");
    public static final String PARTITION_KEY = "PK";
    public static final String SORT_KEY = "SK";

    private DynamoDBAdapter dbAdapter;
    private DynamoDBMapper mapper;
    // private AmazonDynamoDB client;

    @JsonProperty(PARTITION_KEY)
    private String PK;

    @JsonProperty(SORT_KEY)
    private String SK;

    @JsonProperty("description")
    private String description;

    @JsonProperty("title")
    private String title;

    @JsonProperty("groups")
    private ArrayList<Group> groups;

    public Result() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
            .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(POLLS_TABLE_NAME))
            .build();

            this.dbAdapter = DynamoDBAdapter.getInstance(this.dbAdapter);
            // this.client = this.dbAdapter.getDbClient();
            this.mapper = this.dbAdapter.createDbMapper(mapperConfig);
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
        return "Result[PK=" + PK + ", SK=" + SK + ", title=" + title + ", description=" + description + "]";
    }

    public void save(Result result) throws IOException {
        this.mapper.save(result);
    }

    public void deleteResult(Result result) throws IOException {

        DynamoDBDeleteExpression dbDeleteExpression = new DynamoDBDeleteExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(this.PK);
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbDeleteExpression.setExpected(expectedAttribute);

        this.mapper.delete(result, dbDeleteExpression);
    }

    public void updateResult(Result result) {
        DynamoDBSaveExpression dbSaveExpression = new DynamoDBSaveExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(PK);
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbSaveExpression.setExpected(expectedAttribute);

        this.mapper.save(result, dbSaveExpression);
    }
}