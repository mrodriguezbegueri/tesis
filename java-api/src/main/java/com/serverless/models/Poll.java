package com.serverless.models;

import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverless.utils.DynamoDBAdapter;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

// import java.util.Objects;

@DynamoDBTable(tableName = "PLACEHOLDER_POLLS_TABLE_NAME")
public class Poll {

    private static final String POLLS_TABLE_NAME = System.getenv("POLLS_TABLE_NAME");
    public static final String PARTITION_KEY = "PK";
    public static final String SORT_KEY = "SK";

    private DynamoDBAdapter dbAdapter;
    private DynamoDBMapper mapper;
    private AmazonDynamoDB client;

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

    // @JsonProperty("groups")
    // private ArrayList<Group> groups;

    public Poll() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
            .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(POLLS_TABLE_NAME))
            .build();

            this.dbAdapter = DynamoDBAdapter.getInstance(this.dbAdapter);
            this.client = this.dbAdapter.getDbClient();
            this.mapper = this.dbAdapter.createDbMapper(mapperConfig);
    }



    @DynamoDBHashKey(attributeName = "PK")
    public String getPK() {
        return this.PK;
    }
    public void setPK(String PK) {
        this.PK = PK;
    }

    @DynamoDBRangeKey(attributeName = "SK")
    public String getSK() {
        return this.SK;
    }
    public void setSK(String SK) {
        this.SK = SK;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"GSI1PK"})
    public String getGSI1PK() {
        return this.GSI1PK;
    }
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

    @Override
    public String toString() {
        return "Poll[pk=" + PK + ", sk=" + SK + ", title=" + title + ", description=" + description + ", gsi1pk=" + GSI1PK + "]";
    }

    // public ArrayList<Group> getGroups() {
    //     return this.groups;
    // }
    // public void setGroups(ArrayList<Group> groups) {
    //     this.groups = groups;
    // }

    public void save(Poll poll) throws IOException {
        this.mapper.save(poll);
    }
    

    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || getClass() != o.getClass()) return false;
    //     Poll poll = (Poll) o;
    //     return isbn.equals(poll.isbn);
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(isbn);
    // }

}