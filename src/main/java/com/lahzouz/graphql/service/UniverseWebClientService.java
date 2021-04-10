package com.lahzouz.graphql.service;

import java.util.Collections;

import com.lahzouz.graphql.util.DateGraphQLAdapter;
import com.lahzouz.graphql.util.ApolloWebClientUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ScalarTypeAdapters;
import com.lahzouz.graphql.client.universe.FindEventQuery;
import com.lahzouz.graphql.client.universe.type.CustomType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UniverseWebClientService {
    private final WebClient webClient;
    private final DateGraphQLAdapter dateGraphQLAdapter;

    public UniverseWebClientService(@Qualifier("universeWebClient") final WebClient webClient, final DateGraphQLAdapter dateGraphQLAdapter) {
        this.webClient = webClient;
        this.dateGraphQLAdapter = dateGraphQLAdapter;
    }

    public Mono<Response<FindEventQuery.Data>> findEvent(final String id) {
        Query<FindEventQuery.Data, FindEventQuery.Data, FindEventQuery.Variables> query = new FindEventQuery(id);
        ScalarTypeAdapters scalarTypeAdapters = new ScalarTypeAdapters(Collections.singletonMap(CustomType.DATE, dateGraphQLAdapter));

        return webClient
                .post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApolloWebClientUtils.createBody(query, scalarTypeAdapters, true, true), String.class)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .map(httpResponse -> ApolloWebClientUtils.parse(query, httpResponse));
    }

}
