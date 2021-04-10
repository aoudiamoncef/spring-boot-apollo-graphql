package com.lahzouz.graphql.service;

import com.lahzouz.graphql.apollo.client.etmdb.queries.CinemasQuery;
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
public class EtmdbApolloService {
    private final ApolloClient apolloClient;

    public EtmdbApolloService(@Qualifier("etmdbApolloClient") final ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public Response<CinemasQuery.Data> cinemas(final String before, final String after) {
        final ApolloQueryCall<CinemasQuery.Data> findEventCall = apolloClient
                .query(CinemasQuery.builder()
                        .before(before)
                        .after(after)
                        .build())
                .toBuilder().httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall).join();
    }

    public CompletableFuture<Response<CinemasQuery.Data>> cinemasCf(final String before, final String after) {
        final ApolloQueryCall<CinemasQuery.Data> findEventCall = apolloClient
                .query(CinemasQuery.builder()
                        .before(before)
                        .after(after)
                        .build())
                .toBuilder().httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall);
    }

    public Mono<Response<CinemasQuery.Data>> cinemasMono(final String before, final String after) {
        final ApolloQueryCall<CinemasQuery.Data> findEventCall = apolloClient
                .query(CinemasQuery.builder()
                        .before(before)
                        .after(after)
                        .build())
                .toBuilder().httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toMono(findEventCall);
    }
}
