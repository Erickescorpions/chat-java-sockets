package bin.client;

import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;
import java.lang.Thread;

class Message extends Thread {

    String type;
    Client c;
    String msg = "";
    Scanner sc = new Scanner(System.in);

    Message(Client c, String type) {

        this.c = c;
        this.type = type;
    }

    public void run() {
        if(type.equals("RECEIVE")) {
            try {
                while(true) 
                    System.out.println(c.input.readUTF());    

            } catch(IOException ioe) {
                ioe.printStackTrace();
            }

        } else { // SEND
            try {
                while(!msg.equals("EXIT")) {
                    
                    msg = sc.nextLine();
                    c.output.writeUTF(msg); 
                } 
        
                c.input.close();
                c.output.close();
                c.cs.close();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
    }
}


public class Client {
    
    Socket cs;
    Scanner sc = new Scanner(System.in);
    DataOutputStream output;
    DataInputStream input;
    
    Client(String host, int port) {
        try {
            cs = new Socket(host, port);
        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    void Connection() throws IOException {
        
        output = new DataOutputStream(cs.getOutputStream());
        input = new DataInputStream(cs.getInputStream());

        System.out.println("EXIT to end the connection");
        System.out.print("Choose a nickname: ");

        output.writeUTF(sc.nextLine());

        if(input.readUTF().equals("connected")) {

            Message r = new Message(this, "RECEIVE");
            Message s = new Message(this, "SEND");

            r.start();
            s.start();
        } else {

            System.out.println("an error occurred while connecting");
        }
    }

}

class MainC {
    public static void main(String[] args) {
        Client c = new Client("localhost", 8200);
        try {
            c.Connection();
        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}