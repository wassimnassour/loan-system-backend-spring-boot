package com.yourcompany.yourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);       // Logs IP address
        loggingFilter.setIncludeQueryString(true);      // Logs query params
        loggingFilter.setIncludePayload(true);          // Logs request body (be careful)
        loggingFilter.setIncludeHeaders(false);         // Don't log headers
        loggingFilter.setMaxPayloadLength(10000);       // Limit body size in logs
        return loggingFilter;
    }
}
