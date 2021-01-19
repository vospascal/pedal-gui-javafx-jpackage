package org.example;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

public class CustomWebSocket extends WebSocketServer {

    private static final List<WebSocket> clients = new ArrayList<>();

    public CustomWebSocket(InetSocketAddress address) {
        super(address);
    }

    public CustomWebSocket(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public CustomWebSocket(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        conn.send("Welcome to the custom Server!"); //This method sends a message to the new client
        System.out.println("new connection to {}" + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info" + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from " + conn.getRemoteSocketAddress() + " message: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        clients.remove(conn);
        System.out.println("an error occurred on connection " + conn.getRemoteSocketAddress() + " error: " + ex);
    }

    @Override
    public void onStart() {
        broadcastOnlinePlayers();
        System.out.println("server started successfully");
    }

    public void sndmsg (String msg, List<WebSocket> clientList) {
        broadcast(msg, clientList);
    }

    private void broadcastOnlinePlayers() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                broadcast("online");
            }
        }, 0, 1000);
    }

//    public static void initialize(final String host, final int port) {
//        new Thread(() -> {
//            WebSocketServer server = new CustomWebSocket(new InetSocketAddress(host, port));
//            server.run();
//        }, "CustomWebSocketServer").start();
//    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 3001; // 843 flash policy port
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
        }
        CustomWebSocket s = new CustomWebSocket(port);
        s.start();
        System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            s.broadcast(in);
            if (in.equals("exit")) {
                s.stop(1000);
                break;
            }
        }
    }

    public static List<WebSocket> getClients() {
        return new ArrayList<>(clients);
    }
}

