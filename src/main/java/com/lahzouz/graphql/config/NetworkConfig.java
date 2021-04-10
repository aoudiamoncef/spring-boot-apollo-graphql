package com.lahzouz.graphql.config;

import com.lahzouz.graphql.util.DateGraphQLAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.apollographql.apollo.ApolloClient;
import com.lahzouz.graphql.client.universe.type.CustomType;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class NetworkConfig {

    @Bean
    public ApolloClient universeApolloClient(final AppProperties appProperties, final DateGraphQLAdapter dateGraphQLAdapter) {
        final AppProperties.Service universe = appProperties.getUniverse();
        return ApolloClient.builder()
                .okHttpClient(getOkHttpClient(universe))
                .addCustomTypeAdapter(CustomType.DATE, dateGraphQLAdapter)
                .serverUrl(universe.getUrl())
                .build();
    }

    @Bean
    public ApolloClient etmdbApolloClient(final AppProperties appProperties, final DateGraphQLAdapter dateGraphQLAdapter) {
        final AppProperties.Service etmdb = appProperties.getEtmdb();
        return ApolloClient.builder()
                .okHttpClient(getOkHttpClient(etmdb))
                .addCustomTypeAdapter(CustomType.DATE, dateGraphQLAdapter)
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
                        .addHeader(HttpHeaders.USER_AGENT, "etmdb-client")
                        .addHeader("apiKey", service.getKey())
                        .addHeader("Connection", "close")
                        .build());
    }

    @Bean
    public WebClient etmdbWebClient(final AppProperties appProperties) {
        final AppProperties.Service etmdb = appProperties.getEtmdb();
        return getWebClient(etmdb);
    }

    @Bean
    public WebClient universeWebClient(final AppProperties appProperties) {
        final AppProperties.Service universe = appProperties.getUniverse();
        return getWebClient(universe);
    }

    private WebClient getWebClient(final AppProperties.Service service) {
        final AppProperties.Timeout timeout = service.getTimeout();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(timeout.getConnect().toMillis()))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(Math.toIntExact(timeout.getRead().toMillis())));
                    connection.addHandlerLast(new WriteTimeoutHandler(Math.toIntExact(timeout.getWrite().toMillis())));
                });

        return WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.set("User-Agent", service.getName());
                    httpHeaders.set("apiKey", service.getKey());
                })
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
