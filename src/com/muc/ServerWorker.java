package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;


public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private String Login;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server; //passing the server instance to each ServerWorker
        this.clientSocket = clientSocket;
        }

    @Override
    public void run() {
        try {
            clientSocketHandle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clientSocketHandle() throws IOException, InterruptedException {
        InputStream clientInput = clientSocket.getInputStream(); //Using an input stream to accept input from the client
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientInput));
        String line;
        while ( ( line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line); //Will split the line into individual tokens based on the white space characters.
            if (tokens != null && tokens.length > 0) {
                String command = tokens[0];
                if ("quit".equalsIgnoreCase(command)) {
                    break;
                } else if ("login".equalsIgnoreCase(command)) {
                    handlelogin(outputStream, tokens); //Creating a new function to handle the login - This is to keep the loop simple!!
                } else {
                    String msg = "Unsupported Command: " + command + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handlelogin(OutputStream outputStream, String[] tokens) throws IOException { //Creating a new function to handle the login
        if (tokens.length == 3) { // Tokens = Like args which need to be passed in order for the program to run? 3 tokens need to be passed for a successful login.
            String login = tokens [1];
            String pw = tokens[2];

            if ((login.equals("guest") && pw.equals("guest")) || login.equals("jim") && pw.equals("jim")) { //Two users, Jim/Jim and Guest/guest
                String msg = "ok login\n"; //msg will return "ok login" if what the user enters equals pw
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("Login Successful, Welcome, " + login);

                //send user all other online logins
                List<ServerWorker> workerList = server.getWorkerList(); //This message will be sent to every user connected to the server.
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) { //Will ensure the user logging in doesn't get notification of their own prescence.
                            String msg2 = "online " + worker.getLogin() + "\n";
                            worker.send(msg2);
                        }
                    }
                }
                //send other online users the user's status.
                String onlineMsg = "online" + login + "\n";
                for(ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) { //Will ensure the user logging in doesn't get notification of their own prescence.
                        worker.send(onlineMsg);
                    }
                }

            } else {
                String msg = "error!!\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException { //Accesses the current outputStream of the client and then sends
        outputStream.write(msg.getBytes());
    }
}