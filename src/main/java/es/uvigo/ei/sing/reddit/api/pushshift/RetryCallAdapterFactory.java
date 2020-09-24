package es.uvigo.ei.sing.reddit.api.pushshift;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Request;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * When you need something to be tried once more, with this adapter and {@see Retry} annotation, you can retry.
 * <p>
 * NOTE THAT: You can only retry asynchronous {@see Call} or {@see java.util.concurrent.CompletableFuture} implementations.
 *
 * @author dtodt
 */
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class RetryCallAdapterFactory extends CallAdapter.Factory {

    /**
     * You can setup a default max retry count for all connections.
     */
    private int maxRetries = 0;

    @Override
    public CallAdapter<?, ?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
        int itShouldRetry = maxRetries;

        final Retry retry = getRetry(annotations);
        if (retry != null) {
            itShouldRetry = retry.max();
        }
        log.debug("Starting a CallAdapter with {} retries.", itShouldRetry);
        return new RetryCallAdapter<>(
                retrofit.nextCallAdapter(this, returnType, annotations),
                itShouldRetry
        );
    }

    private Retry getRetry(final Annotation[] annotations) {
        return Arrays.stream(annotations).parallel()
                .filter(annotation -> annotation instanceof Retry)
                .map(annotation -> ((Retry) annotation))
                .findFirst()
                .orElse(null);
    }

    @RequiredArgsConstructor
    private class RetryCallAdapter<R, T> implements CallAdapter<R, T> {

        private final CallAdapter<R, T> delegated;
        private final int maxRetries;

        @Override
        public Type responseType() {
            return delegated.responseType();
        }

        @Override
        public T adapt(final Call<R> call) {
            return delegated.adapt(maxRetries > 0 ? new RetryingCall<>(call, maxRetries) : call);
        }
    }

    @RequiredArgsConstructor
    private class RetryingCall<R> implements Call<R> {

        private final Call<R> delegated;
        private final int maxRetries;

        @Override
        public Response<R> execute() throws IOException {
            return delegated.execute();
        }

        @Override
        public void enqueue(final Callback<R> callback) {
            delegated.enqueue(new RetryCallback<>(delegated, callback, maxRetries));
        }

        @Override
        public boolean isExecuted() {
            return delegated.isExecuted();
        }

        @Override
        public void cancel() {
            delegated.cancel();
        }

        @Override
        public boolean isCanceled() {
            return delegated.isCanceled();
        }

        @Override
        public Call<R> clone() {
            return new RetryingCall<>(delegated.clone(), maxRetries);
        }

        @Override
        public Request request() {
            return delegated.request();
        }
    }

    @RequiredArgsConstructor
    private class RetryCallback<T> implements Callback<T> {

        private final Call<T> call;
        private final Callback<T> callback;
        private final int maxRetries;

        private final AtomicInteger retryCount = new AtomicInteger(0);

        @Override
        public void onResponse(final Call<T> call, final Response<T> response) {
            if (!response.isSuccessful() && retryCount.incrementAndGet() <= maxRetries) {
                log.debug("Call with no success result code: {}", response.code());
                retryCall();
            } else {
                callback.onResponse(call, response);
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            log.debug("Call failed with message: {}", t.getLocalizedMessage(), t);
            if (retryCount.incrementAndGet() <= maxRetries) {
                retryCall();
            } else if (maxRetries > 0) {
                log.debug("No retries left sending timeout up.");
                callback.onFailure(call, new TimeoutException(String.format("No retries left after %s attempts.", maxRetries)));
            } else {
                callback.onFailure(call, t);
            }
        }

        private void retryCall() {
            log.warn("{}/{} Retrying...", retryCount.get(), maxRetries);
            call.clone().enqueue(this);
        }
    }
}
