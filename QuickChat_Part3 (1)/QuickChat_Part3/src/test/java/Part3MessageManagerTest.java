package com.mycompany.chattingapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class Part3MessageManagerTest {
    @TempDir Path temp;
    private MessageManager manager(){ return new MessageManager(temp.resolve("messages.json")); }

    @Test void sentMessagesArrayCorrectlyPopulated(){
        MessageManager m=manager(); Message a=new Message(1,"Developer","+27834557896","Did you get the cake?");
        Message b=new Message(2,"Developer","+27838884567","It is dinner time !");
        m.SentMessage(a,1);m.SentMessage(b,1);
        assertEquals(2,m.getSentMessages().size());
        assertEquals("Did you get the cake?",m.getSentMessages().get(0).getMessage());
        assertEquals("It is dinner time !",m.getSentMessages().get(1).getMessage());
    }
    @Test void displayLongestMessage(){
        MessageManager m=manager(); m.SentMessage(new Message(1,"+27838884567","short"),3);
        String longest="Where are you? You are late! I have asked you to be on time.";
        m.SentMessage(new Message(2,"+27838884567",longest),3);
        assertEquals(longest,m.displayLongestStoredMessage());
    }
    @Test void searchForMessageID(){
        MessageManager m=manager(); Message msg=new Message(4,"0838884567","0838884567","It is dinner time !");
        m.SentMessage(msg,3); Message found=m.searchByMessageID(msg.getMessageID());
        assertNotNull(found); assertEquals("0838884567",found.getRecipient()); assertEquals("It is dinner time !",found.getMessage());
    }
    @Test void searchAllMessagesForRecipient(){
        MessageManager m=manager(); m.SentMessage(new Message(1,"+27838884567","Where are you?"),3);
        m.SentMessage(new Message(2,"+27838884567","Ok, I am leaving without you."),3);
        m.SentMessage(new Message(3,"+27834484567","Different recipient"),3);
        assertEquals(2,m.searchByRecipient("+27838884567").size());
    }
    @Test void deleteMessageUsingHash(){
        MessageManager m=manager(); Message msg=new Message(2,"+27838884567","Where are you?");
        m.SentMessage(msg,3); assertTrue(m.deleteByHash(msg.getMessageHash()));
        assertNull(m.searchByMessageID(msg.getMessageID())); assertTrue(m.getStoredMessages().isEmpty());
    }
    @Test void displayFullReport(){
        MessageManager m=manager(); Message msg=new Message(2,"+27838884567","Ok, I am leaving without you.");m.SentMessage(msg,3);
        String report=m.displayStoredReport();
        assertTrue(report.contains(msg.getMessageHash())); assertTrue(report.contains("+27838884567")); assertTrue(report.contains("Ok, I am leaving without you."));
    }
    @Test void storedMessagesAreReadBackFromJson(){
        Path file=temp.resolve("persist.json"); MessageManager first=new MessageManager(file);
        first.SentMessage(new Message(1,"+27838884567","Saved message"),3);
        MessageManager second=new MessageManager(file);
        assertEquals(1,second.getStoredMessages().size()); assertEquals("Saved message",second.getStoredMessages().get(0).getMessage());
    }
}
