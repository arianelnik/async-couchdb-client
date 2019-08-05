package com.equiron.acc.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.equiron.acc.exception.CouchDbResponseException;

public class ExceptionHandler {
    public static <T> T handleFutureResult(Future<T> future) {
        try {
            return future.get(1, TimeUnit.MINUTES);
        } catch (ExecutionException e) {
            Throwable originalException = e;
            
            while (originalException.getCause() != null) {
                originalException = originalException.getCause();
            }

            if (originalException instanceof CouchDbResponseException) {
                originalException.fillInStackTrace();

                throw (CouchDbResponseException) originalException;
            } 

            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (@SuppressWarnings("unused") TimeoutException e) {
            throw new RuntimeException("Unexpected future timeout");
        }
    }
}
