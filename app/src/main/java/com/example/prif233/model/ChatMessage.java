package com.example.prif233.model;



import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;


public class ChatMessage {

    private int messageId;
    private String messageText;
    private LocalDateTime dateCreated;

    @SerializedName("read")
    private boolean isRead = false;
    private Integer senderId;
    private String senderLogin;

    public Integer getSenderId() { return senderId; }
    public String getSenderLogin() { return senderLogin; }

    public String getMessageText() { return messageText; }
    public LocalDateTime getDateCreated() { return dateCreated; } // or String
    public boolean isRead() { return isRead; }


}
