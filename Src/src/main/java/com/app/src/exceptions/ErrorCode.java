package com.app.src.exceptions;


//các exception lên quan đến quá trình xử lý code - unchecked exception
public enum ErrorCode {
    DATABASE_ERROR("DB_001", "Khong the truy cap du lieu. Vui long thu lai."),
    VALIDATION_ERROR("VAL_001", "Du lieu khong hop le."),
    AUTHENTICATION_ERROR("AUTH_001", "Sai ten dang nhap hoac mat khau."),
    SCENE_LOAD_ERROR("UI_001", "Khong the tai giao dien."),
    UNKNOWN_ERROR("SYS_001", "Da xay ra loi khong mong muon.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

