package com.user.account.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.data")
public class DataConfig {

    private int numberOfAuthors = 100;

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }
}
