package com.example.english_center.dto.request;

import com.example.english_center.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountUpdateRequest {

    @NotBlank
    private String fullname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private Gender gender;

    @Size(max = 1000)
    private String avatarUrl;
}
