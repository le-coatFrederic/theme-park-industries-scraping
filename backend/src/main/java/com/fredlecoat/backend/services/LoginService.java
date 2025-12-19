package com.fredlecoat.backend.services;

import java.util.Map;

public interface LoginService {
    boolean isLogged();
    Map<String, String> login(String username, String password);
}
