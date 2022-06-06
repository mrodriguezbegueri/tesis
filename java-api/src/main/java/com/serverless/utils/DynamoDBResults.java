package com.serverless.utils;

import java.util.HashMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

import com.serverless.interfaces.ResultsInterface;
import com.serverless.models.Result;

public class DynamoDBResults implements ResultsInterface {

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBResults instance;

    private DynamoDBResults() {
    }

    public static DynamoDBResults instance() {

        if (instance == null) {
            synchronized (DynamoDBResults.class) {
                if (instance == null)
                    instance = new DynamoDBResults();
            }
        }
        return instance;
    }

    @Override
    public void saveResult(Result result) {
        mapper.save(result);
    }

    @Override
    public void deleteResult(Result result) {

        DynamoDBDeleteExpression dbDeleteExpression = new DynamoDBDeleteExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(result.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbDeleteExpression.setExpected(expectedAttribute);

        mapper.delete(result, dbDeleteExpression);
    }

    @Override
    public void updateResult(Result result) {
        DynamoDBSaveExpression dbSaveExpression = new DynamoDBSaveExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(result.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbSaveExpression.setExpected(expectedAttribute);

        mapper.save(result, dbSaveExpression);
    }

}
