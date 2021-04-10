package com.lahzouz.graphql.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ScalarTypeAdapters;

import okio.ByteString;
import reactor.core.Exceptions;

public class ApolloWebClientUtils {

    @NotNull
    public static <T extends Operation.Data, B extends Operation.Data, V extends Operation.Variables> Response<B> parse(
            final Query<T, B, V> query, final byte[] httpResponse) {
        try {
            return query.parse(ByteString.of(httpResponse));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    @NotNull
    public static <T extends Operation.Data, B extends Operation.Data, V extends Operation.Variables> String createBody(
            final Query<T, B, V> query, final ScalarTypeAdapters scalarTypeAdapters) {

        return "{"
                + "\"operationName\": " + query.name().name() + ", "
                + "\"query\": " + query.queryDocument() + ", "
                + "\"variables\": " + marshal(query.variables(), scalarTypeAdapters)
                + "}";
    }

    @NotNull
    private static <V extends Operation.Variables> String marshal(final V variables,
            final ScalarTypeAdapters scalarTypeAdapters) {
        try {
            return variables.marshal(scalarTypeAdapters);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    @NotNull
    public static <T extends Operation.Data, B extends Operation.Data, V extends Operation.Variables> String createBody(
            final Query<T, B, V> query,
            final ScalarTypeAdapters scalarTypeAdapters,
            final Boolean autoPersistQueries,
            final Boolean withQueryDocument) {

        return query.composeRequestBody(autoPersistQueries, withQueryDocument, scalarTypeAdapters)
                .string(StandardCharsets.UTF_8);
    }
}
