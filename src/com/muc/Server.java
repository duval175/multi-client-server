package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{ //The server is a thread
    private int serverPort = 0;

    private final ArrayList <ServerWorker> workerlist = new ArrayList<>();

    public Server(int ServerPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList() {
        return workerlist;
    } //Server workers to access all other server workers.

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(serverPort); //Creating the server socket which is listening on the port 8818.
            while (true) { //Infinite loop which will accepts incoming connections
                System.out.println("About to accept connection...");
                Socket clientSocket = ss.accept(); //calling accept will accept any client connection
                System.out.println("Accepted Connection from" + clientSocket);
                ServerWorker Sworker = new ServerWorker(this,clientSocket);
                workerlist.add(Sworker);
                Sworker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
