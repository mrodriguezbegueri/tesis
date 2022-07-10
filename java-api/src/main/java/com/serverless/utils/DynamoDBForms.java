package com.serverless.utils;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.serverless.interfaces.FormsInterface;
import com.serverless.models.Form;
// import org.apache.log4j.Logger;

public class DynamoDBForms implements FormsInterface {

    private final static String DYNAMO_FORMS_ID = System.getenv("FORMS_ID");

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBForms instance;

    private DynamoDBForms() {
    }

    public static DynamoDBForms instance() {

        if (instance == null) {
            synchronized (DynamoDBForms.class) {
                if (instance == null)
                    instance = new DynamoDBForms();
            }
        }
        return instance;
    }

    @Override
    public List<Form> findAllForms() {
        return mapper.scan(Form.class, new DynamoDBScanExpression());
    }

    @Override
    public void saveForm(Form form) {
        mapper.save(form);
    }

    @Override
    public Form getForm(String PK) {
        Form form = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v", new AttributeValue().withS(PK));

        DynamoDBQueryExpression<Form> queryExpression = new DynamoDBQueryExpression<Form>()
                .withKeyConditionExpression("PK = :v")
                .withExpressionAttributeValues(av);

        PaginatedQueryList<Form> result = mapper.query(Form.class, queryExpression);

        if (result.size() == 1) {
            form = result.get(0);
        }

        return form;
    }

    @Override
    public void updateForm(Form form) {
        DynamoDBSaveExpression dbSaveExpression = new DynamoDBSaveExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(form.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbSaveExpression.setExpected(expectedAttribute);

        mapper.save(form, dbSaveExpression);
    }

    @Override
    public void deleteForm(Form form) {
        DynamoDBDeleteExpression dbDeleteExpression = new DynamoDBDeleteExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(form.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbDeleteExpression.setExpected(expectedAttribute);

        mapper.delete(form, dbDeleteExpression);
    }

    @Override
    public Form getFormByTitle(String title) {
        Form form = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v", new AttributeValue().withS(title));
        av.put(":v1", new AttributeValue().withS(DYNAMO_FORMS_ID));

        DynamoDBQueryExpression<Form> queryExpression = new DynamoDBQueryExpression<Form>()
            .withIndexName("SearchFormByTitle")
            .withKeyConditionExpression("title = :v AND begins_with(PK, :v1)")
            .withExpressionAttributeValues(av)
            .withConsistentRead(false);
        
        PaginatedQueryList<Form> result = mapper.query(Form.class, queryExpression);

        if(result.size() == 1) {
            form = result.get(0);
        }

        return form;
    }
}