package com.example.phasmatic.data.model;

public class Call {

    public String id;
    public String caller_id;
    public String receiver_id;
    public String channelName;
    public String status;
    public String created_at;

    public Call() {

    }

    public Call(String id, String caller_id, String receiver_id,
                String channelName, String status, String created_at) {
        this.id = id;
        this.caller_id = caller_id;
        this.receiver_id = receiver_id;
        this.channelName = channelName;
        this.status = status;
        this.created_at = created_at;
    }
}