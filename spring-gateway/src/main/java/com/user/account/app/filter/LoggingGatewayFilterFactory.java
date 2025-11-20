package com.user.account.app.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom Gateway Filter Factory for logging requests and responses
 */
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (config.isPreLogger()) {
                logger.info("Pre-filter: Request Method: {}, URI: {}, Headers: {}",
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getURI(),
                        exchange.getRequest().getHeaders());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    logger.info("Post-filter: Response Status Code: {}, Headers: {}",
                            exchange.getResponse().getStatusCode(),
                            exchange.getResponse().getHeaders());
                }
            }));
        };
    }

    public static class Config {
        private boolean preLogger = true;
        private boolean postLogger = true;

        public boolean isPreLogger() {
            return preLogger;
        }

        public void setPreLogger(boolean preLogger) {
            this.preLogger = preLogger;
        }

        public boolean isPostLogger() {
            return postLogger;
        }

        public void setPostLogger(boolean postLogger) {
            this.postLogger = postLogger;
        }
    }
}
