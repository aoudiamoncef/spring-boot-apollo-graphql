package com.lahzouz.graphql;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    /**
     * API manager configuration.
     */
    private Apim apim;

    @Getter
    @Setter
    public static final class Apim {
        /**
         * server URL.
         */
        private String url;
        /**
         * Consumer api key
         */
        private String key;
        /**
         * HTTP timeouts.
         */
        private Timeout timeout;
    }

    @Getter
    @Setter
    public static final class Timeout {
        /**
         * HTTP call timeout.
         */
        private Duration call;
        /**
         * HTTP connect timeout.
         */
        private Duration connect;
        /**
         * HTTP read timeout.
         */
        private Duration read;
        /**
         * HTTP write timeout.
         */
        private Duration write;
    }

}
