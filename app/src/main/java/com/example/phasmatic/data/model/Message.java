package com.example.phasmatic.data.model;

public class Message {
    public String id;
    public String conversationId;
    public String sender_id;
    public String receiver_id;
    public String messageText;
    public String timeMessage;
    public String statusOfMessage;

    public Message() {
    }

    public Message(String id, String conversationId, String sender_id, String receiver_id, String messageText, String timeMessage, String statusOfMessage) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.messageText = messageText;
        this.timeMessage = timeMessage;
        this.statusOfMessage = statusOfMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
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

    public String getStatusOfMessage() {
        return statusOfMessage;
    }

    public void setStatusOfMessage(String statusOfMessage) {
        this.statusOfMessage = statusOfMessage;
    }
}
