package com.sparta.levelup_backend.config;

import feign.RetryableException;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRetryer implements Retryer {

    private final int maxAttempts;
    private final long backoff;
    private int attempt;

    public CustomRetryer(int maxAttempts, long backoff) {
        this.maxAttempts = maxAttempts;
        this.backoff = backoff;
        this.attempt = 1;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt < maxAttempts) {
            log.error("Retrying after {} attempts", attempt);
            try {
                TimeUnit.MILLISECONDS.sleep(backoff);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            attempt++;

        }
        if(attempt >= maxAttempts) {
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new CustomRetryer(maxAttempts, backoff);
    }
}
