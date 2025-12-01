package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerUpdateDTO {

    /**
     * Only address is updatable per requirements.
     * firstName, lastName, phone and email are immutable and must NOT be present here.
     */
    @NotBlank(message = "address is required")
    private String address;
}
