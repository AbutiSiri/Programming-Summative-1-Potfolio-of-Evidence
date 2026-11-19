package com.mycompany.chattingapp;
import java.util.List;
import java.util.Scanner;
public class QuickChatApp {
    private static final MessageManager manager=new MessageManager();
    public static void main(String[] args){run(new Scanner(System.in));}
    public static void run(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat."); boolean running=true;
        while(running) {
            System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Stored Messages\n4) Quit");
            System.out.print("Enter choice: "); int choice=readInt(scanner);
            switch(choice) {
                case 1 -> sendMessages(scanner);
                case 2 -> { for(Message m:manager.getSentMessages()) System.out.println(m.printMessages()+"\n"); if(manager.getSentMessages().isEmpty()) System.out.println("No sent messages yet."); }
                case 3 -> storedMenu(scanner);
                case 4 -> {running=false;System.out.println("Exiting QuickChat. Goodbye!");}
                default -> System.out.println("Invalid selection. Please try again.");
            }
        }
    }
    private static void sendMessages(Scanner s) {
        System.out.print("How many messages would you like to enter? "); int n=readInt(s);
        for(int i=0;i<n;i++) {
            System.out.print("Recipient (international format, e.g. +27834557896): "); String recipient=s.nextLine();
            System.out.print("Message (maximum 250 characters): "); String body=s.nextLine();
            Message m=new Message(manager.returnTotalMessages()+i+1,"Developer",recipient,body);
            if(!m.checkRecipientCell().equals("Cell phone number successfully captured.")){System.out.println(m.checkRecipientCell());i--;continue;}
            if(!m.checkMessageLength().equals("Message ready to send.")){System.out.println(m.checkMessageLength());i--;continue;}
            System.out.println("1 Send | 2 Disregard | 3 Store"); int action=readInt(s);
            System.out.println(manager.SentMessage(m,action));
            if(action==1||action==3) System.out.println(m.printMessages());
        }
    }
    private static void storedMenu(Scanner s) {
        boolean back=false;
        while(!back) {
            System.out.println("\nSTORED MESSAGES\n1 Display sender and recipient\n2 Display longest stored message\n3 Search by message ID\n4 Search by recipient\n5 Delete by message hash\n6 Display full report\n7 Back to main menu");
            System.out.print("Choose: "); int c=readInt(s);
            switch(c) {
                case 1 -> System.out.println(manager.displayStoredSenderRecipient());
                case 2 -> System.out.println("Longest message: "+manager.displayLongestStoredMessage());
                case 3 -> {System.out.print("Enter message ID: ");String id=s.nextLine();Message m=manager.searchByMessageID(id);System.out.println(m==null?"Message not found.":"Recipient: "+m.getRecipient()+"\nMessage: "+m.getMessage());}
                case 4 -> {System.out.print("Enter recipient number: ");String r=s.nextLine();List<Message> found=manager.searchByRecipient(r);if(found.isEmpty())System.out.println("No messages found for this recipient.");else for(Message m:found)System.out.println(m.printMessages()+"\n");}
                case 5 -> {System.out.print("Enter message hash: ");String h=s.nextLine();System.out.println(manager.deleteByHash(h)?"Message successfully deleted.":"Message hash not found or file could not be updated.");}
                case 6 -> System.out.println(manager.displayStoredReport());
                case 7 -> back=true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    private static int readInt(Scanner s){while(true){String line=s.nextLine();try{return Integer.parseInt(line.trim());}catch(NumberFormatException e){System.out.print("Please enter a number: ");}}}
}
