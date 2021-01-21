package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebSocket {
    static ServerSocket serverSocket;
    static ExecutorService executorService;
    private static final List<Socket> clients = new ArrayList<Socket>();

    public static List<Socket> getClients() {
        return new ArrayList<>(clients);
    }

    private static void doBroadcast(String text, List<Socket> clients) {
        for (Socket client : clients) {
            if (client != null) {
                try {
                    OutputStream out = client.getOutputStream();;
                    DataOutputStream outStream = new DataOutputStream(out);
                    Frame outF = new Frame("test", text);
                    outF.send(outStream);
                } catch (IOException e) {
                    //Ignore this exception in this case
                }
            }
        }
    }

    public static void broadcast(String text) {
        List<Socket> clients = null;
        clients = getClients();
        if (text == null || clients == null) {
            throw new IllegalArgumentException();
        }

        doBroadcast(text, clients);
    }

//    private static void broadcastMessages() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                broadcast("online");
//            }
//        }, 0, 1000);
//    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        try {
            serverSocket = new ServerSocket(9000);
            System.out.println("Server has started on localhost:9000.\r\nWaiting for a connection...");
            executorService = Executors.newFixedThreadPool(10);
//            broadcastMessages();
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new Thread(new WebSocket.SocketThread(socket)));
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    static class SocketThread implements Runnable {
        private Socket socket;

        public SocketThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            System.out.println("A client connected.");
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                Scanner s = new Scanner(in, "UTF-8");

                clients.add(socket);

                try {
                    String data = s.useDelimiter("\\r\\n\\r\\n").next();
                    Matcher get = Pattern.compile("^GET").matcher(data);
                    if (get.find()) {
                        String wsKeyConst = "Sec-WebSocket-Key:";
                        Matcher match = Pattern.compile(wsKeyConst + " (.*)").matcher(data);
                        match.find();
                        String wsKey = match.group(1);
                        System.out.println("Sec-Websocket-Key: " + wsKey);
                        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                                + "Connection: Upgrade\r\n"
                                + "Upgrade: websocket\r\n"
                                + "Sec-WebSocket-Accept: "
                                + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((wsKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                                + "\r\n\r\n").getBytes("UTF-8");
                        out.write(response, 0, response.length);
                    }

                    DataInputStream inStream = new DataInputStream(in);
                    DataOutputStream outStream = new DataOutputStream(out);

                    System.out.println(getClients() + "getClients");

                    while (socket.isConnected()) {

                        if (inStream.available() > 0) {
                            Frame inF = new Frame(inStream);
                            System.out.println("data:" + new String(inF.getData(), StandardCharsets.UTF_8));

                            Frame outF = new Frame("test");
                            outF.send(outStream);

                            outF = new Frame("test", "HUUGE!");
                            outF.send(outStream);

                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (SocketException e) {
                    clients.remove(socket);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }  finally {
                    s.close();
                }

            } catch (SocketException e) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    clients.remove(socket);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}