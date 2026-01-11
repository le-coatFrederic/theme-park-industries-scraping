package com.fredlecoat.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fredlecoat.backend.services.DashboardService;
import com.fredlecoat.backend.services.LoginService;
import com.fredlecoat.backend.services.implementations.SeleniumTPIOldInterfaceDashboardServiceImpl;
import com.fredlecoat.backend.services.implementations.SeleniumTPIOldInterfaceLoginServiceImpl;

@Configuration
public class WebSiteServiceConfig {

    @Autowired
    WebSiteAccessConfig accessConfig;

    @Bean
    public LoginService loginService() {
        accessConfig.checkAttributes();
        return new SeleniumTPIOldInterfaceLoginServiceImpl();
    }

    @Bean
    public DashboardService dashboardService() {
        accessConfig.checkAttributes();
        return new SeleniumTPIOldInterfaceDashboardServiceImpl();
    }
}