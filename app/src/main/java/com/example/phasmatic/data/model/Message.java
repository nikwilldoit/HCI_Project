package com.example.phasmatic.data.model;

public class Message {
    public long id;
    public long conversationId;
    public long senderId;
    public long receiverId;
    public String messageText;
    public String timeMessage;
    public int statusOfMessage;

    public Message() {
    }

    public Message(long id, long conversationId, long senderId, long receiverId,
                   String messageText, String timeMessage, int statusOfMessage) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timeMessage = timeMessage;
        this.statusOfMessage = statusOfMessage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }

    public int getStatusOfMessage() {
        return statusOfMessage;
    }

    public void setStatusOfMessage(int statusOfMessage) {
        this.statusOfMessage = statusOfMessage;
    }
}
