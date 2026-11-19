package com.mycompany.chattingapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();
    private final Path jsonPath;
    private int totalMessagesSent;

    public MessageManager() { this(Path.of("messages.json")); }
    public MessageManager(Path jsonPath) {
        this.jsonPath = jsonPath;
        loadStoredMessages();
    }

    public String SentMessage(Message message, int choice) {
        if (message == null) return "Message cannot be empty.";
        switch (choice) {
            case 1 -> { sentMessages.add(message); addIdentifiers(message); totalMessagesSent++; return "Message successfully sent."; }
            case 2 -> { disregardedMessages.add(message); addIdentifiers(message); return "Message disregarded."; }
            case 3 -> {
                if (!storedMessages.contains(message)) storedMessages.add(message);
                addIdentifiers(message);
                if (saveStoredMessages()) return "Message successfully stored.";
                storedMessages.remove(message); return "Message could not be stored.";
            }
            default -> { return "Invalid option."; }
        }
    }

    private void addIdentifiers(Message m) {
        if (!messageHashes.contains(m.getMessageHash())) messageHashes.add(m.getMessageHash());
        if (!messageIDs.contains(m.getMessageID())) messageIDs.add(m.getMessageID());
    }

    public boolean saveStoredMessages() {
        try {
            StringBuilder json = new StringBuilder("[\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                Message m = storedMessages.get(i);
                json.append("  {\"messageID\":\"").append(escape(m.getMessageID()))
                    .append("\",\"messageHash\":\"").append(escape(m.getMessageHash()))
                    .append("\",\"sender\":\"").append(escape(m.getSender()))
                    .append("\",\"recipient\":\"").append(escape(m.getRecipient()))
                    .append("\",\"message\":\"").append(escape(m.getMessage())).append("\"}");
                if (i < storedMessages.size() - 1) json.append(',');
                json.append('\n');
            }
            json.append(']');
            Path parent = jsonPath.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(jsonPath, json.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException ex) { System.err.println("Could not write JSON: " + ex.getMessage()); return false; }
    }

    public void loadStoredMessages() {
        storedMessages.clear();
        if (!Files.exists(jsonPath)) return;
        try {
            String content = Files.readString(jsonPath, StandardCharsets.UTF_8);
            Pattern object = Pattern.compile("\\{([^{}]*)\\}");
            Matcher matcher = object.matcher(content);
            while (matcher.find()) {
                String part = matcher.group(1);
                String id = field(part, "messageID"), hash = field(part, "messageHash");
                String sender = field(part, "sender"), recipient = field(part, "recipient"), body = field(part, "message");
                if (id != null && recipient != null && body != null) {
                    Message m = new Message(storedMessages.size(), sender == null ? "Developer" : sender,
                            recipient, body, id, hash);
                    storedMessages.add(m); addIdentifiers(m);
                }
            }
        } catch (IOException ex) { System.err.println("Could not read JSON: " + ex.getMessage()); }
    }

    private static String field(String jsonObject, String key) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"").matcher(jsonObject);
        return m.find() ? unescape(m.group(1)) : null;
    }
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    private static String unescape(String s) {
        StringBuilder out = new StringBuilder(); boolean slash = false;
        for (int i=0;i<s.length();i++) { char c=s.charAt(i); if (slash) { out.append(switch(c){case 'n'->'\n';case 'r'->'\r';case 't'->'\t';default->c;}); slash=false; } else if(c=='\\') slash=true; else out.append(c); }
        if (slash) out.append('\\'); return out.toString();
    }

    public String displayStoredSenderRecipient() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        StringBuilder b = new StringBuilder();
        for (Message m : storedMessages) b.append("Sender: ").append(m.getSender()).append(" | Recipient: ").append(m.getRecipient()).append('\n');
        return b.toString();
    }
    public String displayLongestStoredMessage() {
        Message longest = null;
        for (Message m : storedMessages) if (longest == null || m.getMessage().length() > longest.getMessage().length()) longest=m;
        return longest == null ? "No stored messages." : longest.getMessage();
    }
    public Message searchByMessageID(String id) {
        for (Message m : storedMessages) if (m.getMessageID().equals(id)) return m;
        return null;
    }
    public List<Message> searchByRecipient(String recipient) {
        List<Message> found = new ArrayList<>();
        for (Message m : storedMessages) if (m.getRecipient().equals(recipient)) found.add(m);
        return found;
    }
    public boolean deleteByHash(String hash) {
        for (int i=0;i<storedMessages.size();i++) if (storedMessages.get(i).getMessageHash().equals(hash)) {
            storedMessages.remove(i); rebuildIdentifiers(); return saveStoredMessages();
        }
        return false;
    }
    private void rebuildIdentifiers() {
        messageHashes.clear(); messageIDs.clear();
        for (Message m: sentMessages) addIdentifiers(m);
        for (Message m: disregardedMessages) addIdentifiers(m);
        for (Message m: storedMessages) addIdentifiers(m);
    }
    public String displayStoredReport() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        StringBuilder b = new StringBuilder("STORED MESSAGES REPORT\n");
        for (Message m: storedMessages) b.append(m.printMessages()).append("\n-------------------------\n");
        return b.toString();
    }
    public String printMessages() { return displayStoredReport(); }
    public int returnTotalMessages() { return totalMessagesSent; }
    public List<Message> getSentMessages() { return Collections.unmodifiableList(sentMessages); }
    public List<Message> getDisregardedMessages() { return Collections.unmodifiableList(disregardedMessages); }
    public List<Message> getStoredMessages() { return Collections.unmodifiableList(storedMessages); }
    public List<String> getMessageHashes() { return Collections.unmodifiableList(messageHashes); }
    public List<String> getMessageIDs() { return Collections.unmodifiableList(messageIDs); }
    public boolean populateTestData() {
        sentMessages.clear(); disregardedMessages.clear(); storedMessages.clear(); messageHashes.clear(); messageIDs.clear(); totalMessagesSent=0;
        addTest(new Message(1,"Developer","+27834557896","Did you get the cake?"),1);
        addTest(new Message(2,"Developer","+27838884567","Where are you? You are late! I have asked you to be on time."),3);
        addTest(new Message(3,"Developer","+27834484567","Yohoooo, I am at your gate."),2);
        addTest(new Message(4,"0838884567","0838884567","It is dinner time !"),1);
        addTest(new Message(5,"Developer","+27838884567","Ok, I am leaving without you."),3);
        return saveStoredMessages();
    }
    private void addTest(Message m, int status) {
        switch (status) {
            case 1 -> {
                sentMessages.add(m);
                totalMessagesSent++;
            }
            case 2 -> disregardedMessages.add(m);
            default -> storedMessages.add(m);
        }
        addIdentifiers(m);
    }
}
