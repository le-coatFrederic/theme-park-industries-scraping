package com.fredlecoat.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "webaccess")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSiteAccessConfig {

    private String url;
    private String email;
    private String password;

    public void checkAttributes() {
        if (url == null || email == null || password == null) {
            throw new IllegalStateException(
                "WebSiteAccessConfig requires url, email, and password properties"
            );
        }   
    }

}
