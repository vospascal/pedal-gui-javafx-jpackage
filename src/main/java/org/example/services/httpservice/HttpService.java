package org.example.services.httpservice;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class HttpService implements Executor {
    protected HttpServer server;
    protected ThreadPoolExecutor tpe;
    protected int port;
    protected int maxConnectionQueue;

    public HttpService(int port, int maxConnectionQueue) throws IOException {
        this.tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.maxConnectionQueue = maxConnectionQueue;
        this.port = port;
        this.initializeServer();
    }

    protected void initializeServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(this.port), this.maxConnectionQueue);
        server.setExecutor(this);
//        server.createContext("/", new RootHandler());
        server.createContext("/", new IndexHtmlHandler());
        server.createContext("/index.js", new IndexJsHandler());
    }

//    public static class RootHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange he) throws IOException {
//            String response = "<h1>Static Response</h1>";
//            he.sendResponseHeaders(200, response.length());
//            OutputStream os = he.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }
//    }


    public static class IndexHtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            String line;
            String resp = "";
//            System.out.println("test2: "+this.getClass().getResource("index.html"));

            try {
                URL url = this.getClass().getResource("index.html");
                File newFile = new File(url.getPath());
//                System.out.println("newFile: " + newFile.getName());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
                while ((line = bufferedReader.readLine()) != null) {
//                    System.out.println(line);
                    resp += line;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Content-type", "text/html");

            httpExchange.sendResponseHeaders(200, resp.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(resp.getBytes());
            os.close();
        }
    }

    public static class IndexJsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            String line;
            String resp = "";
//            System.out.println("test2: "+this.getClass().getResource("index.html"));

            try {
                URL url = this.getClass().getResource("index.js");
                File newFile = new File(url.getPath());
//                System.out.println("newFile: " + newFile.getName());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
                while ((line = bufferedReader.readLine()) != null) {
//                    System.out.println(line);
                    resp += line;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Content-type", "text/javascript");

            httpExchange.sendResponseHeaders(200, resp.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(resp.getBytes());
            os.close();
        }
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.stop(5);
    }

    public void stop(int graceTime) {
        this.server.stop(graceTime);
    }

    public void restart() {
        stop();
        start();
    }

    /**
     * @see java.util.concurrent.Executor
     */
    @Override
    public void execute(Runnable command) {
        tpe.execute(command);
    }

}