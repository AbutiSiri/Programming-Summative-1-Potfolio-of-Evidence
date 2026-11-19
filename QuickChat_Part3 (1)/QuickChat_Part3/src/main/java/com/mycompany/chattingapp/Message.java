package com.mycompany.chattingapp;

import java.util.UUID;

public class Message {
    private final String messageID;
    private final int messageNumber;
    private final String sender;
    private final String recipient;
    private final String message;
    private final String messageHash;

    public Message(int messageNumber, String recipient, String message) {
        this(messageNumber, "Developer", recipient, message, null, null);
    }

    public Message(int messageNumber, String sender, String recipient, String message) {
        this(messageNumber, sender, recipient, message, null, null);
    }

    public Message(int messageNumber, String sender, String recipient, String message,
                   String messageID, String messageHash) {
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.messageID = (messageID == null || messageID.isBlank())
                ? UUID.randomUUID().toString().replace("-", "").substring(0, 10) : messageID;
        this.messageHash = (messageHash == null || messageHash.isBlank())
                ? createMessageHash() : messageHash;
    }

    public boolean checkMessageID() { return messageID != null && messageID.length() <= 10; }

    public String checkRecipientCell() {
        return recipient != null && recipient.matches("^\\+[0-9]{10,12}$")
                ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        if (message == null) return "Message ready to send.";
        if (message.length() <= 250) return "Message ready to send.";
        return "Message exceeds 250 characters by " + (message.length() - 250) + " characters; please reduce the size.";
    }

    public String createMessageHash() {
        String[] words = message == null ? new String[0] : message.trim().split("\\s+");
        String first = words.length == 0 ? "" : words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words.length == 0 ? "" : words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last;
    }

    public String SentMessage(int choice) {
        return switch (choice) {
            case 1 -> "Message successfully sent";
            case 2 -> "Message disregarded";
            case 3 -> "Message successfully stored";
            default -> "Invalid choice selected.";
        };
    }

    public String printMessages() {
        return "Message ID: " + messageID + "\nMessage Hash: " + messageHash
                + "\nSender: " + sender + "\nRecipient: " + recipient + "\nMessage: " + message;
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public int getMessageNumber() { return messageNumber; }
}
