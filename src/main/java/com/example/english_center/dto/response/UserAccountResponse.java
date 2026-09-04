package com.example.english_center.dto.response;

import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountResponse {

    private Long id;
    private String fullname;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private Role role;
    private UserStatus status;
    private String avatarUrl;
}
