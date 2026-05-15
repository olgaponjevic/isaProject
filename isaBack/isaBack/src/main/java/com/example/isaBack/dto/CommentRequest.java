package com.example.isaBack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Komentar ne sme biti prazan")
    @Size(max = 2000, message = "Komentar može imati najviše 2000 karaktera")
    private String content;
}