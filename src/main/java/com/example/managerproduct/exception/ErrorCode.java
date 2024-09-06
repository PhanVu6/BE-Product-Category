package com.example.managerproduct.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "error.uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "error.invalidInput", HttpStatus.BAD_REQUEST),
    LIST_STUDENT_NOT_FOUND(1002, "error.listStudentNotFound", HttpStatus.NOT_FOUND),
    STUDENT_NOT_FOUND(1003, "error.get.not.found", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND(1004, "error.courseNotFound", HttpStatus.NOT_FOUND),
    LIST_COURSE_NOT_FOUND(1005, "error.listCourseNotFound", HttpStatus.NOT_FOUND),
    STUDENT_EMAIL_EXISTS(1006, "error.studentEmailExists", HttpStatus.BAD_REQUEST),
    STUDENT_DISCONECT(1007, "error.studentDisconnect", HttpStatus.BAD_REQUEST),
    ALREADY_DELETED(1008, "error.alreadyDeleted", HttpStatus.BAD_REQUEST),
    STUDENT_EXISTS_COURSE(1009, "error.existsCourse", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTS(1004, "error.product.exists", HttpStatus.BAD_REQUEST),
    PRODUCT_CODE_ALREADY_EXISTS(1010, "error.product_code.exists", HttpStatus.BAD_REQUEST),
    CATEGORY_CODE_ALREADY_EXISTS(1011, "error.category_code.exists", HttpStatus.BAD_REQUEST);

    private Integer code;
    private String message;
    private HttpStatus status;

    ErrorCode(Integer code, String message, HttpStatus status) {
        this.code = code;
        this.status = status;
        this.message = message;
    }


}
