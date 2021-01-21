package org.example.services.websocketservice;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientConnection implements Closeable, Runnable {

    private final DataOutputStream out;
    private final DataInputStream in;
    private String name;
    private List<ClientConnection> pool;
    private boolean running = true;

    public ClientConnection(final Socket accept, List<ClientConnection> pool) throws IOException {
        this.pool = pool;
        System.out.println(accept.getInetAddress() + " connected");
        in = new DataInputStream(accept.getInputStream());
        out = new DataOutputStream(accept.getOutputStream());
    }

    @Override
    public void close() throws IOException {
        System.out.println("Disconnecting " + name);
        try {
            in.close();
            out.close();
        } finally {
            running = false;
            pool.remove(this);
        }
    }

    @Override
    public void run() {
        try {
            Scanner s = new Scanner(in, "UTF-8");
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

            while (running) {
                if (in.available() > 0) {
                    Frame inF = new Frame(in);
                    System.out.println("data:" + new String(inF.getData(), StandardCharsets.UTF_8));

                    Frame outF = new Frame("welcome!");
                    outF.send(out);

                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            try {
                close();
            } catch (IOException ignored) {
            }
        }
    }

    public void sendMessage(String message) {
        try {
            Frame outF = new Frame(message);
            outF.send(out);
            out.flush();
        } catch (IOException e) {
            try {
                this.close();
            } catch (IOException ignored) {
            }
        }

    }
}