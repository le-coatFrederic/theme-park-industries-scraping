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
        if (url == null || url.isEmpty()) {
            throw new RuntimeException();
        } 

        if (email == null || email.isEmpty()) {
            throw new RuntimeException();
        } 

        if (password == null || password.isEmpty()) {
            throw new RuntimeException();
        } 
        
        System.out.println("URL : " + url + "\nEmail : " + email + "\nPassword : " + password);
    }

}
