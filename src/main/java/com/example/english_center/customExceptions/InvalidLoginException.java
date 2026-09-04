package com.example.english_center.customExceptions;

public class InvalidLoginException extends RuntimeException {

    public static final String MESSAGE = "Tài khoản hoặc mật khẩu chưa đúng, vui lòng thử lại";

    private InvalidLoginException() {
        super(MESSAGE);
    }

    public static InvalidLoginException invalidCredentials() {
        return new InvalidLoginException();
    }
}
