package com.example.english_center.customExceptions;

public class DataNotFoundException extends RuntimeException {

    private DataNotFoundException(String message) {
        super(message);
    }

    public static DataNotFoundException userAccount() {
        return new DataNotFoundException("Không tìm thấy tài khoản");
    }

    public static DataNotFoundException user() {
        return new DataNotFoundException("Không tìm thấy user");
    }

    public static DataNotFoundException course() {
        return new DataNotFoundException("Không tìm thấy khóa học");
    }

    public static DataNotFoundException resource(String resourceName) {
        return new DataNotFoundException("Không tìm thấy " + resourceName);
    }
}
