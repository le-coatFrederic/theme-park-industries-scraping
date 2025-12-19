package com.fredlecoat.backend.services.implementations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fredlecoat.backend.services.LoginService;

@Service
public class WebClientLoginServiceImpl implements LoginService {
    @Autowired
    private WebClient webClient;

    @Override
    public boolean isLogged() {
        try {
            Connection.Response response = Jsoup.connect("https://themeparkindustries.com/tpiv4/game/dashboard.php")
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(10000)
                .execute(); 
            
            if (
                response.statusCode() != 200 
                || response.url().getPath().contains("play.php")
            ) {
                return false;
            }

            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, String> login(String username, String password) {
        Map<String, String> cookies = new HashMap<>();
        
        try {
            Connection.Response response = Jsoup.connect("https://themeparkindustries.com/tpiv4/play.php")
                .data("login-username", "danaleight2000.jeux@gmail.com")
                .data("login-password", "2C%YyF5pe&#ii^ylCPGogw%#AN2DAbB8")
                .method(Connection.Method.POST)
                .execute();

            cookies = response.cookies();

            System.out.println(cookies);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cookies;
    }
}
