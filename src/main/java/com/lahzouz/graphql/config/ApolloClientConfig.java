package com.lahzouz.graphql.config;

import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.network.http.DefaultHttpEngine;
import com.lahzouz.graphql.util.DateGraphQLAdapter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class ApolloClientConfig {

    @Bean
    public ApolloClient universeApolloClient(final AppProperties appProperties) {
        final AppProperties.Service universe = appProperties.getUniverse();
        return new ApolloClient.Builder()
                .httpEngine(new DefaultHttpEngine(getOkHttpClient(universe)))
                .serverUrl(universe.getUrl())
                .build();
    }

    @Bean
    public ApolloClient etmdbApolloClient(final AppProperties appProperties) {
        final AppProperties.Service etmdb = appProperties.getEtmdb();
        return new ApolloClient.Builder()
                .httpEngine(new DefaultHttpEngine(getOkHttpClient(etmdb)))
                .serverUrl(etmdb.getUrl())
                .build();
    }

    private OkHttpClient getOkHttpClient(final AppProperties.Service service) {
        final AppProperties.Timeout timeout = service.getTimeout();
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
                .addInterceptor(getApiKeyInterceptor(service))
                .build();

    }

    private Interceptor getApiKeyInterceptor(final AppProperties.Service service) {
        return chain -> chain
                .proceed(chain.request().newBuilder()
                        .addHeader(HttpHeaders.USER_AGENT, service.getName())
                        .addHeader("apiKey", service.getKey())
                        .addHeader("Connection", "close")
                        .build());
    }

}
