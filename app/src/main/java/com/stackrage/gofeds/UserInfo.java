package com.stackrage.gofeds;

public class UserInfo {
    private String photo;
    private String receiverId;
    private String name;
    private String lastMsg;
    private String time;
    private String roomId;
    private Long timestamp;

    public UserInfo(String photo, String receiver, String name, String msg, String time, String roomid, Long timestamp) {
        this.photo = photo;
        this.receiverId = receiver;
        this.name = name;
        this.lastMsg = msg;
        this.time = time;
        this.roomId = roomid;
        this.timestamp = timestamp;
    }

    public String getPhoto() { return this.photo; }

    public String getReceiverId() { return this.receiverId; }

    public String getName() { return this.name; }

    public String getLastMsg() { return this.lastMsg; }

    public String getTime() { return this.time; }

    public String getRoomId() { return this.roomId; }

    public Long getTimestamp() { return this.timestamp; }
}
