package com.example.english_center.builder;

import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;

public class UserAccountSearchBuilder {

    private String keyword;
    private Role role;
    private UserStatus status;
    private Gender gender;

    public String getKeyword() {
        return keyword;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Gender getGender() {
        return gender;
    }

    private UserAccountSearchBuilder(Builder builder) {
        this.keyword = builder.keyword;
        this.role = builder.role;
        this.status = builder.status;
        this.gender = builder.gender;
    }

    public static class Builder {
        private String keyword;
        private Role role;
        private UserStatus status;
        private Gender gender;

        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setStatus(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public UserAccountSearchBuilder build() {
            return new UserAccountSearchBuilder(this);
        }
    }
}
