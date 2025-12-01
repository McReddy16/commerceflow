package com.example.authentication;

import com.example.entity.Customer;
import com.example.repository.CustomerRepository;
import com.example.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1) Validate phone number exists
        Customer customer = customerRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Phone number not found"));

        // 2) Generate JWT token using phone + id
        String token = jwtUtil.generateToken(customer.getPhone(), customer.getId());

        // 3) Return token
        return new LoginResponseDTO(token, "Bearer", 3600);
    }
}
