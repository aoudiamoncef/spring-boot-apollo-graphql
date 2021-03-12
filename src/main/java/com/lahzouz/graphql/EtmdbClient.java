package com.lahzouz.graphql;

import com.lahzouz.graphql.apollo.client.etmdb.queries.CinemasQuery;
import org.springframework.stereotype.Component;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class EtmdbClient {
    private final ApolloClient apolloClient;

    public Response<CinemasQuery.Data> cinemas(final String before, final String after) {
        final ApolloQueryCall<CinemasQuery.Data> findEventCall = apolloClient
                .query(CinemasQuery.builder()
                        .before(before)
                        .after(after)
                        .build())
                .toBuilder().httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall).join();
    }


}
