package com.romantymchyk.omguwreader.async;

public interface AsyncResponse {

    void processFinish(Object result);

    void processFinishWithError();
    
}