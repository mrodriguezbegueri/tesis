package com.serverless.interfaces;

import com.serverless.models.Result;

public interface ResultsInterface {

    void saveResult(Result result);
    
    void updateResult(Result result);

    void deleteResult(Result result);
}
