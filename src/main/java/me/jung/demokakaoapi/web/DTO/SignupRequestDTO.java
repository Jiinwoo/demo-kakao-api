package me.jung.demokakaoapi.web.DTO;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String email;
    private String name;
    private String password;
}

