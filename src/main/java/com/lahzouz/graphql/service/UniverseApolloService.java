package com.lahzouz.graphql.service;

import com.lahzouz.graphql.client.universe.FindEventQuery;
import com.lahzouz.graphql.util.ApolloClientUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
public class UniverseApolloService {
    private final ApolloClient apolloClient;

    public UniverseApolloService(@Qualifier("universeApolloClient") final ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public Response<FindEventQuery.Data> findEvent(final String id) {
        final ApolloQueryCall<FindEventQuery.Data> findEventCall = apolloClient
                .query(new FindEventQuery(id))
                .toBuilder()
                .httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall).join();
    }

    public CompletableFuture<Response<FindEventQuery.Data>> findEventCf(final String id) {
        final ApolloQueryCall<FindEventQuery.Data> findEventCall = apolloClient
                .query(new FindEventQuery(id))
                .toBuilder()
                .httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall);
    }

    public Mono<Response<FindEventQuery.Data>> findEventMono(final String id) {
        final ApolloQueryCall<FindEventQuery.Data> findEventCall = apolloClient
                .query(new FindEventQuery(id))
                .toBuilder()
                .httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toMono(findEventCall);
    }
}
