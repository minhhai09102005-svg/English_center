package com.example.english_center.customExceptions;

public class ConflictException extends RuntimeException {

    private ConflictException(String message) {
        super(message);
    }

    public static ConflictException duplicateEmail() {
        return new ConflictException("Email đã tồn tại");
    }

    public static ConflictException duplicatePhoneNumber() {
        return new ConflictException("Số điện thoại đã tồn tại");
    }

    public static ConflictException duplicateCourseCode() {
        return new ConflictException("Mã khóa học đã tồn tại");
    }

    public static ConflictException duplicateCourseName() {
        return new ConflictException("Tên khóa học đã tồn tại");
    }

}
