package com.mockproject.group3.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseApiResponse<T> {
    private int code;
    private String message;
    private Map<String, String> messages;
    private T payload;

    public BaseApiResponse() {
    }

    public BaseApiResponse(int code, String message, T payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public BaseApiResponse(int code, String message, Map<String, String> messages, T payload) {
        this.code = code;
        this.message = message;
        this.messages = messages;
        this.payload = payload;
    }

    public BaseApiResponse(int code, Map<String, String> messages) {
        this.code = code;
        this.messages = messages;
    }

    public BaseApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
