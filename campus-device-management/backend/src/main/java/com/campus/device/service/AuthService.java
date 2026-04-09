package com.campus.device.service;

import com.campus.device.model.dto.LoginRequest;
import com.campus.device.model.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout(String token);
}
