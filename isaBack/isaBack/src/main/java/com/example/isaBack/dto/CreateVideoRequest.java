package com.example.isaBack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateVideoRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private Set<String> tags;

    private Double latitude;

    private Double longitude;
}