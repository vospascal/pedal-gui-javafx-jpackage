package org.example.services.httpservice;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
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
        server.createContext("/", new RootHandler());
        server.createContext("/pedals", new IndexHtmlHandler());
        server.createContext("/pedals/index.js", new IndexJsHandler());
    }

    public static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "<h1>Static Response</h1>";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static String getStringFromInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null ) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static class IndexHtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            String response = "";

            response = getStringFromInputStream(HttpService.class.getResourceAsStream("index.html"));

            if (!response.equals("")) {
                headers.add("Content-type", "text/html");
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }


    public static class IndexJsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            Headers headers = httpExchange.getResponseHeaders();
            String response = "";

            response = getStringFromInputStream(HttpService.class.getResourceAsStream("index.js"));

            if (!response.equals("")) {
                headers.add("Content-type", "text/javascript");
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
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