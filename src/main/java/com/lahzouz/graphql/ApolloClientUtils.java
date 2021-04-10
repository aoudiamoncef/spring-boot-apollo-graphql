package com.lahzouz.graphql;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import reactor.core.publisher.Mono;

public class ApolloClientUtils {

    public static <T> CompletableFuture<Response<T>> toCompletableFuture(ApolloCall<T> apolloCall) {
        CompletableFuture<Response<T>> completableFuture = new CompletableFuture<>();

        completableFuture.whenComplete((tResponse, throwable) -> {
            if (completableFuture.isCancelled()) {
                completableFuture.cancel(true);
            }
        });

        apolloCall.enqueue(new ApolloCall.Callback<T>() {
            @Override
            public void onResponse(@NotNull Response<T> response) {
                completableFuture.complete(response);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public static <T> Mono<Response<T>> toMono(ApolloCall<T> apolloCall) {
        return Mono.create(sink -> apolloCall.enqueue(new ApolloCall.Callback<T>() {
            @Override
            public void onResponse(@NotNull Response<T> response) {
                sink.success(response);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                sink.error(e);
            }
        }));
    }
}
