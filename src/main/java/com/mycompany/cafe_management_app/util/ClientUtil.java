package com.mycompany.cafe_management_app.util;

import com.mysql.cj.xdevapi.Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class ClientUtil {
    public static ClientUtil instance;
    private static Socket socket;
    private BufferedWriter out;
    private BufferedReader in;

    private ClientUtil() {
        try {
            this.socket = new Socket("localhost", 8080);
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Client: Connection established!");

        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    public static ClientUtil getInstance() {
        if (instance == null) {
            instance = new ClientUtil();
        }

        return instance;
    }

//    public void sendRequest(String request) {
//        try {
//            out.write(request);
//            out.newLine();
//            out.flush();
//        } catch (Exception e) {
//            closeEverything(socket, in, out);
//        }
//    }

    public CompletableFuture<Object> sendRequestAsync(String request) {
        CompletableFuture<Object> future = new CompletableFuture<>();

        try {
            out.write(request);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            closeEverything(socket, in, out);
        }

//        Listen for response
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket != null && socket.isConnected()) {
                    try {
                        String response = in.readLine();  //blocking
                        future.complete(response);

                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();

        return future;
    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
