package com.mockproject.group3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNKNOWN_ERROR(-1, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    OUT_OF_ERROR_HANDLE(-2, "Unknow Error Code", HttpStatus.BAD_REQUEST),
    METHOD_UNSUPPORTED(-3, "This method type is unsupport", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNAUTHORIZED(101, "Require login first", HttpStatus.UNAUTHORIZED),
    CATEGORY_NOT_FOUND(102, "No category found", HttpStatus.BAD_REQUEST),
    INSTRUCTOR_NOT_FOUND(103, "Invalid instructor. Please ensure you have instructor role", HttpStatus.BAD_REQUEST),
    INVALID_ID(104, "Invalid id, id cannot contains any character...", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(105, "Course not found", HttpStatus.BAD_REQUEST),
    ACTION_NOT_ALLOW(106, "You cannot access it", HttpStatus.FORBIDDEN),
    INCORRECT_PRICE(107, "{name} cannot less than {value}", HttpStatus.BAD_REQUEST),
    INVALID_PAGE(108, "{name} cannot less than {value}",
            HttpStatus.BAD_REQUEST),
    INVALID_PAGESIZE(109, "Page size must be {in}", HttpStatus.BAD_REQUEST),
    INVALID_MINVALUE(108, "{name} cannot less than {value}", HttpStatus.BAD_REQUEST),
    INVALID_MAXVALUE(109, "{name} cannot higher than {value}", HttpStatus.BAD_REQUEST),
    INVALID_PARAM(110, "Param {name} cannot accept. Expected: {regexp}", HttpStatus.BAD_REQUEST),
    SUBMIT_COURSE_FAIL(111, "Course cannot submit. Check status...", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(112, "Field {name} is required.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(113, "Your fee is not enough.", HttpStatus.BAD_REQUEST),
    LESSON_NOT_FOUND(114, "Lesson not found.", HttpStatus.BAD_REQUEST),
    COURSE_UPDATE_DENIED(115, "Course cannot update now.", HttpStatus.BAD_REQUEST),
    STUDENT_NOT_FOUND(116, "Student not found.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_EXIST(117, "Enrollment exists.", HttpStatus.BAD_REQUEST),
    STUDENT_AND_COURSE_EXIST(118, "Student and course exist.", HttpStatus.BAD_REQUEST),
    SAVED_COURSE_EXIST(119, "Saved course exists.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(121, "Enrollment not found.", HttpStatus.BAD_REQUEST),
    NOT_ENROLL_YET(122, "You have not enrolled in this course yet", HttpStatus.BAD_REQUEST),
    ENROLLMENT_LESSON_DETAIL_EXIST(123, "Enrollment lesson detail exists.", HttpStatus.BAD_REQUEST),
    LESSON_NOT_IN_COURSE(124, "Lesson not found in Course", HttpStatus.BAD_REQUEST),
    ENROLLMENT_LESSON_DETAIL_NOTFOUND(125, "Enrollment lesson detail not found.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(126, "Wrong Email or Password", HttpStatus.BAD_REQUEST),
    INVALID_CURRENTPASSWORD(127, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    INVALID_NEWPASSWORD(128, "The new password must not be the same as the current password", HttpStatus.BAD_REQUEST),
    INVALID_COMFIRMNEWPASSWORD(129, "The new password and confirm new password must be the same",
            HttpStatus.BAD_REQUEST),
    REVIEW_EXIST(130, "Review exists.", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FINISHED(131, "Course is not finished yet.", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(132, "Review not found.", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_BELONG_TO_STUDENT(133, "Review does not belong to student.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(134, "Email already exists", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(135, "User not found", HttpStatus.BAD_REQUEST),
    COURSE_NOT_PURCHASED(136, "Course not purchased", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_NOT_FOUND(137, "Subcription not found", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED(138, "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(139, "Payment not found", HttpStatus.BAD_REQUEST),
    CATEGORY_NOTFOUND(140,"Category not found", HttpStatus.BAD_REQUEST),
    SUBCATEGORY_EXIST(141,"Subcategory exist", HttpStatus.BAD_REQUEST),
    CATEGORY_EXIST(142,"Category exist", HttpStatus.BAD_REQUEST),
    PAYMENT_DETAIL_NOT_FOUND(143, "Payment detail not found", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

}
