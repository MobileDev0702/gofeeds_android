package com.stackrage.gofeds;

public class MessageInfo {
    private String message;
    private Integer isUser;

    public MessageInfo(String message, Integer isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public MessageInfo() {

    }

    public String getMessage() {
        return this.message;
    }

    public Integer getUser() {
        return this.isUser;
    }
}
