package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {
    public static void main(String[] args) {
        int port = 7848;
        Server server = new Server(port);
        server.start(); //begins the server thread
    }
}