package com.lahzouz.graphql;

import com.lahzouz.graphql.client.universe.FindEventQuery;
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
public class UniverseClient  {
    private final ApolloClient apolloClient;

    public Response<FindEventQuery.Data> findEvent(final String id) {
        final ApolloQueryCall<FindEventQuery.Data> findEventCall = apolloClient
                .query(new FindEventQuery(id))
                .toBuilder()
                .httpCachePolicy(HttpCachePolicy.NETWORK_ONLY).build();

        return ApolloClientUtils.toCompletableFuture(findEventCall).join();
    }

}
