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
import com.serverless.interfaces.PollsInterface;
import com.serverless.models.Poll;
// import org.apache.log4j.Logger;

public class DynamoDBPolls implements PollsInterface {

    // private static final Logger log = Logger.getLogger(DynamoDBPolls.class);

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBPolls instance;

    private DynamoDBPolls() {
    }

    public static DynamoDBPolls instance() {

        if (instance == null) {
            synchronized (DynamoDBPolls.class) {
                if (instance == null)
                    instance = new DynamoDBPolls();
            }
        }
        return instance;
    }

    @Override
    public List<Poll> findAllPolls() {
        return mapper.scan(Poll.class, new DynamoDBScanExpression());
    }

    @Override
    public void savePoll(Poll poll) {
        mapper.save(poll);
    }

    @Override
    public Poll getPoll(String PK) {
        Poll poll = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v", new AttributeValue().withS(PK));

        DynamoDBQueryExpression<Poll> queryExpression = new DynamoDBQueryExpression<Poll>()
                .withKeyConditionExpression("PK = :v")
                .withExpressionAttributeValues(av);

        PaginatedQueryList<Poll> result = mapper.query(Poll.class, queryExpression);

        if (result.size() == 1) {
            poll = result.get(0);
        }

        return poll;
    }

    @Override
    public void updatePoll(Poll poll) {
        DynamoDBSaveExpression dbSaveExpression = new DynamoDBSaveExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(poll.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbSaveExpression.setExpected(expectedAttribute);

        mapper.save(poll, dbSaveExpression);
    }

    @Override
    public void deletePoll(Poll poll) {
        DynamoDBDeleteExpression dbDeleteExpression = new DynamoDBDeleteExpression();
        
        HashMap<String, ExpectedAttributeValue> expectedAttribute = new HashMap<String, ExpectedAttributeValue>();
        AttributeValue pkExpectedValue = new AttributeValue().withS(poll.getPK());
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(pkExpectedValue); 
        expectedAttribute.put("PK", expectedAttributeValue);

        dbDeleteExpression.setExpected(expectedAttribute);

        mapper.delete(poll, dbDeleteExpression);
    }

    @Override
    public Poll getPollByTitle(String title) {
        Poll poll = null;

        HashMap<String, AttributeValue> av = new HashMap<String, AttributeValue>();
        av.put(":v", new AttributeValue().withS(title));
        av.put(":v1", new AttributeValue().withS(DYNAMO_POLLS_ID));

        DynamoDBQueryExpression<Poll> queryExpression = new DynamoDBQueryExpression<Poll>()
            .withIndexName("SearchPollByTitle")
            .withKeyConditionExpression("title = :v AND begins_with(PK, :v1)")
            .withExpressionAttributeValues(av)
            .withConsistentRead(false);
        
        PaginatedQueryList<Poll> result = mapper.query(Poll.class, queryExpression);

        if(result.size() == 1) {
            poll = result.get(0);
        }

        return poll;
    }
}