package com.stackrage.gofeds;

public class MessageInfo {
    private String message;
    private Integer isUser;
    private String image;

    public MessageInfo(String message, Integer isUser, String image) {
        this.message = message;
        this.isUser = isUser;
        this.image = image;
    }

    public MessageInfo() {

    }

    public String getMessage() {
        return this.message;
    }

    public Integer getUser() {
        return this.isUser;
    }

    public String getImage() { return this.image; }
}
