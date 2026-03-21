package com.example.phasmatic.data.model;

public class Conversation {
    public long id;
    public String lastMessage;
    public long leftUser_id;
    public long rightUser_id;
    public String timeLastMessage;

    public Conversation() {
    }

    public Conversation(long id, String lastMessage, long leftUser_id, long rightUser_id, String timeLastMessage) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.leftUser_id = leftUser_id;
        this.rightUser_id = rightUser_id;
        this.timeLastMessage = timeLastMessage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLeftUser_id() {
        return leftUser_id;
    }

    public void setLeftUser_id(long leftUser_id) {
        this.leftUser_id = leftUser_id;
    }

    public long getRightUser_id() {
        return rightUser_id;
    }

    public void setRightUser_id(long rightUser_id) {
        this.rightUser_id = rightUser_id;
    }

    public String getTimeLastMessage() {
        return timeLastMessage;
    }

    public void setTimeLastMessage(String timeLastMessage) {
        this.timeLastMessage = timeLastMessage;
    }
}
