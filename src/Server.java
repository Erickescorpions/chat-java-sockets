package bin.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;

class ClientThread extends Thread {
    
    Server server;
    Socket cs;

    String msg = "";

    DataInputStream input;
    DataOutputStream output;
    
    ClientThread(Server server, Socket cs) {
        
        this.server = server;
        this.cs = cs;
    }

    public void run() {

        try {

            input = new DataInputStream(this.cs.getInputStream());
            output = new DataOutputStream(this.cs.getOutputStream());

            if(!server.clients.containsKey(cs)) {

                String nickname = input.readUTF();

                server.clients.put(cs, nickname);
                output.writeUTF("connected");
                System.out.println("Making connection with: " + nickname);
            } 

            while(!msg.equals("EXIT")) {

                //HASTA QUE NO HAYA UN NUEVO MENSAJE ESPERAR AQUI. 
                //input = new DataInputStream(cs.getInputStream());
                msg = input.readUTF();

                if(msg.equals("EXIT")) {
                    
                    output = new DataOutputStream(cs.getOutputStream());
                    output.writeUTF("DISCONNECTED");
                    System.out.println(server.clients.get(cs) + " disconnected");
                    server.clients.remove(cs);

                    input.close();
                    output.close();
                    cs.close();

                    continue;
                }

                String info = server.clients.get(cs) + " : " + msg;

                server.clients.forEach( 
                    (s, v) -> { 
                        if(!s.equals(cs)) {
                            try {
                                output = new DataOutputStream(s.getOutputStream());
                                output.writeUTF(info); 
                                System.out.println(info);
                                //output.close();
                            } catch(IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                });
            }
        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}


public class Server extends Thread {
    
    ServerSocket ss;
    Map<Socket, String> clients;

    // inicialize the server 
    Server(int port) {
        try {
            ss = new ServerSocket(port);
            clients = new HashMap<Socket, String>();
        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    void Connection() {
        while(true) {
            try {
                Socket cs = ss.accept();

                ClientThread client = new ClientThread(this, cs);

                client.start();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}

class MainS {
    public static void main(String[] args) {
        Server s = new Server(8200);
        System.out.println("Server started");
        s.Connection();
    }
}
