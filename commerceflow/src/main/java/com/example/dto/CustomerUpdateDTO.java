package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerUpdateDTO {

    @NotBlank(message = "firstName is required")
    private String firstName;

    private String lastName;

    @Pattern(
        regexp = "(^$)|(^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$)",
        message = "Email must be empty or a valid email with domain gmail.com, yahoo.com, or outlook.com"
    )
    private String email;

    @Pattern(
        regexp = "(^$)|(^\\+?[0-9]{5,20}$)",
        message = "Phone must be digits (optional +), length 5-20"
    )
    private String phone;
}
