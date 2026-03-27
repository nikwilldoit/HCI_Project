package com.example.phasmatic.data.model;

public class Conversation {
    public String id;
    public String lastMessage;
    public String leftUser_id;
    public String rightUser_id;
    public String timeLastMessage;

    public String otherName;
    public String otherUserId;
    public String otherProfileUrl;

    public Conversation() {
    }

    public Conversation(String id, String lastMessage, String leftUser_id, String rightUser_id, String timeLastMessage) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.leftUser_id = leftUser_id;
        this.rightUser_id = rightUser_id;
        this.timeLastMessage = timeLastMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLeftUser_id() {
        return leftUser_id;
    }

    public void setLeftUser_id(String leftUser_id) {
        this.leftUser_id = leftUser_id;
    }

    public String getRightUser_id() {
        return rightUser_id;
    }

    public void setRightUser_id(String rightUser_id) {
        this.rightUser_id = rightUser_id;
    }

    public String getTimeLastMessage() {
        return timeLastMessage;
    }

    public void setTimeLastMessage(String timeLastMessage) {
        this.timeLastMessage = timeLastMessage;
    }
}
