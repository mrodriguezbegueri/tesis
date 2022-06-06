package com.serverless.interfaces;

import java.util.List;

import com.serverless.models.Poll;

public interface PollsInterface {
    
    List<Poll> findAllPolls();

    void savePoll(Poll poll);

    Poll getPoll(String PK);

    Poll getPollByTitle(String title);
    
    void updatePoll(Poll poll);

    void deletePoll(Poll poll);
}
