package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import http.handlers.EpicHandler;
import http.handlers.SubtaskHandler;
import http.handlers.TaskHandler;
import manager.HTTPTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;
    private final HTTPTaskManager manager;
    private static final Gson gson = TasksGson.gson;

    public HttpTaskServer(HTTPTaskManager manager) throws IOException {
        this.manager = manager;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::tasksHandler);
        httpServer.createContext("/tasks/task", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic", new EpicHandler(manager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(manager));
        httpServer.createContext("/tasks/history", this::historyHandler);


        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private void tasksHandler(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getPrioritizedTasks());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void historyHandler (HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(manager.getHistory());
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void stopHttpServer() {
        httpServer.stop(0);
    }
}
