package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerCreateDTO {

    @NotBlank(message = "firstName is required")
    private String firstName;

    private String lastName;

    // Optional email → must match only if not empty
    @Pattern(
        regexp = "^$|^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$",
        message = "Email must be empty or must end with @gmail.com, @yahoo.com, or @outlook.com"
    )
    private String email;

    // Phone must be exactly 10 digits
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must be exactly 10 digits"
    )
    private String phone;
}
