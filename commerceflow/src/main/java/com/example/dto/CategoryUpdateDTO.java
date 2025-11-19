package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CategoryUpdateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
