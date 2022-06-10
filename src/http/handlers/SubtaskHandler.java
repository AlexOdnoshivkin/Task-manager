package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import http.TasksGson;
import manager.TaskManager;
import model.Subtask;
import model.TaskTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager manager;
    private final Gson gson = TasksGson.gson;
    public SubtaskHandler (TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        //извлекаем метод
        String method = exchange.getRequestMethod();
        //извлекаем тело из запроса
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        String query = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath();
        System.out.println(path);

        switch (method) {
            case "GET":
                if (exchange.getRequestURI().getQuery() == null) {
                    List<Subtask> tasks = manager.getAllSubTasks();
                    response = gson.toJson(tasks);
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    int id = Integer.parseInt(query.split("=")[1]);
                    response = gson.toJson(manager.getTaskById(id));
                    System.out.println(response);
                    if (manager.getTaskById(id) != null) {
                        if (manager.getTaskById(id).getClass() != Subtask.class) {
                            exchange.sendResponseHeaders(400, 0);
                            exchange.close();
                        }
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                }
                break;

            case "POST":
                try {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (!subtask.getType().equals(TaskTypes.SUBTASK)) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                    }
                    if (exchange.getRequestURI().getQuery() == null) {
                        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(),
                                subtask.getStartTime(), subtask.getDuration());
                        newSubtask.setId(subtask.getId());
                        manager.createSubtask(newSubtask, subtask.getStatus(), subtask.getHeadEpicId());
                        exchange.sendResponseHeaders(201, 0);
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (manager.getTaskById(id) == null) {
                            exchange.sendResponseHeaders(404, 0);
                            exchange.close();
                        } else {
                            manager.updateSubtask(subtask, id, subtask.getStatus());
                            exchange.sendResponseHeaders(201, 0);
                        }
                    }
                } catch (ManagerSaveException e) {
                    exchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                    }
                } finally {
                    exchange.close();
                }
                break;

            case "DELETE":
                if (exchange.getRequestURI().getQuery() == null) {
                    manager.deleteAllSubtasks();
                } else {
                    int id = Integer.parseInt(query.split("=")[1]);
                    if (manager.getTaskById(id) == null) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    if (manager.getTaskById(id).getClass() != Subtask.class) {
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                    } else {
                        manager.deleteTaskById(id);
                    }
                }
                exchange.sendResponseHeaders(204, 0);
                exchange.close();
                break;
        }
    }
}
