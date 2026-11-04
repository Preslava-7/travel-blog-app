package com.travelblog.app.service;

import com.travelblog.app.dto.auth.AuthResponse;
import com.travelblog.app.dto.auth.LoginRequest;
import com.travelblog.app.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
