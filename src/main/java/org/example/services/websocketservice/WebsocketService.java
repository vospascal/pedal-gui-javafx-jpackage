package org.example.services.websocketservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketService implements Runnable {

    private final ServerSocket socket;
    private final ExecutorService executorService;
    private static List<ClientConnection> clients = new CopyOnWriteArrayList<>();
    private boolean running = true;

    public static List<ClientConnection> getClients() {
        return clients;
    }

    public static boolean hasClients() {
        int size = clients.size();
        return size > 0;
    }

    public WebsocketService() throws IOException {
        socket = new ServerSocket(9000);
//        executorService = Executors.newCachedThreadPool();
        executorService = Executors.newFixedThreadPool(2);
    }

    public static void main(String[] args) throws IOException {
        WebsocketService server = new WebsocketService();
        server.start();
    }

    public void start() throws IOException {
        while (running) {
            Socket accept = socket.accept();
            ClientConnection connection = new ClientConnection(accept, clients);
            executorService.execute(connection);
            clients.add(connection);
//            broadcastMessages();
        }

        List<Runnable> runnables = executorService.shutdownNow();
        for (final Runnable runnable : runnables) {
            try {
                ((ClientConnection) runnable).close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            running = false;
        }
    }

    private static void doBroadcast(String text, List<ClientConnection> clients) {
        for (ClientConnection client : clients) {
            if (client != null) {
                client.sendMessage(text);
            }
        }
    }

    public static void broadcast(String text) {
        List<ClientConnection> clients = null;
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
}