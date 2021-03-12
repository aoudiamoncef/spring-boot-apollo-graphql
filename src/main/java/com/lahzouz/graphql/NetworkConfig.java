package com.lahzouz.graphql;

import com.lahzouz.graphql.client.universe.type.CustomType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import com.apollographql.apollo.ApolloClient;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


@Configuration
@RequiredArgsConstructor
public class NetworkConfig {

    private final DateGraphQLAdapter dateGraphQLAdapter;

    @Bean
    public ApolloClient buildApolloClient(final AppProperties appProps, final OkHttpClient okHttpClient) {
        return ApolloClient.builder()
                .okHttpClient(okHttpClient)
                .addCustomTypeAdapter(CustomType.DATE, this.dateGraphQLAdapter)
                .serverUrl(appProps.getApim().getUrl())
                .build();
    }

    @Bean
    protected OkHttpClient getOkHttpClient(final AppProperties appProps) {
        final AppProperties.Apim apim = appProps.getApim();
        final AppProperties.Timeout timeout = apim.getTimeout();
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpLoggingInterceptor.redactHeader("apiKey");

        return new OkHttpClient.Builder()
                .callTimeout(timeout.getCall())
                .connectTimeout(timeout.getConnect())
                .readTimeout(timeout.getRead())
                .writeTimeout(timeout.getWrite())
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(getApiKeyInterceptor(apim))
                .build();

    }

    private Interceptor getApiKeyInterceptor(final AppProperties.Apim apim) {
        return chain -> chain
                .proceed(chain.request().newBuilder()
                        .addHeader(HttpHeaders.USER_AGENT, "univers-client")
                        .addHeader("apiKey", apim.getKey())
                        .addHeader("Connection", "close")
                        .build());
    }
}
