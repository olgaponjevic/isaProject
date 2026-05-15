package com.example.isaBack.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}