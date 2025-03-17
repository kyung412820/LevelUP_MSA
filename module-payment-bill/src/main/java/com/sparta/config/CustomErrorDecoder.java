package com.sparta.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = defaultErrorDecoder.decode(methodKey, response);
      if(response.status() == HttpStatus.SERVICE_UNAVAILABLE.value() || exception instanceof NoHttpResponseException){

          throw new RetryableException(
              response.status(),
              response.reason(),
              response.request().httpMethod(),
              (Long) null,
              response.request());
      }
      return exception;
    }
}
