package service;

import org.springframework.http.ResponseEntity;

import model.LoginRequest;

public interface IAuthenticationService {

	ResponseEntity<?> login(LoginRequest loginRequest);
}
