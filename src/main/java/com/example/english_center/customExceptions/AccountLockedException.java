package com.example.english_center.customExceptions;

public class AccountLockedException extends RuntimeException {

    public static final String MESSAGE = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ với bộ phận quản trị để xử lý";

    private AccountLockedException() {
        super(MESSAGE);
    }

    public static AccountLockedException locked() {
        return new AccountLockedException();
    }
}
