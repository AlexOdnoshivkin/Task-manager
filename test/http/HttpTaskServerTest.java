package http;

import com.google.gson.Gson;
import manager.HTTPTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private HTTPTaskManager manager;
    private final Gson gson = TasksGson.gson;
    private KVServer server;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;




    @BeforeEach
    void serverStart() {
        try {
            server = new KVServer();
            server.start();
            manager = new HTTPTaskManager("http://localhost:8078");

            task1 = new Task("Task1", "description1",
                    LocalDateTime.of(2022, 5, 27, 1, 0), Duration.ofMinutes(120));
            manager.createTask(task1, Status.NEW);
            task2 = new Task("Task2", "description2",
                    LocalDateTime.of(2022, 4, 12, 14, 30), Duration.ofMinutes(200));
            manager.createTask(task2, Status.NEW);
            epic1 = new Epic("Epic1", "description1");
            manager.createEpic(epic1);
            epic2 = new Epic("Epic2", "description2");
            manager.createEpic(epic2);
            subtask1 = new Subtask("Subtask1", "description1",
                    LocalDateTime.of(2022, 5, 26, 1, 0), Duration.ofMinutes(900));
            subtask2 = new Subtask("Subtask2", "description2");
            manager.createSubtask(subtask1, Status.NEW, epic1.getId());
            manager.createSubtask(subtask2, Status.NEW, epic2.getId());
            manager.getTaskById(task1.getId());
            manager.getTaskById(task2.getId());
            manager.getTaskById(epic2.getId());
            manager.getTaskById(task1.getId());
            manager.getTaskById(subtask1.getId());
            httpTaskServer = new HttpTaskServer(manager);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stopHttpServer();
        server.stop();
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse);
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            Set<Task> tasks = manager.getPrioritizedTasks();
            String trueTasks = gson.toJson(tasks);
            String tasksFromServer = httpResponse.body();
            assertEquals(trueTasks, tasksFromServer, "Неверный возврат задач");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldReturnHistory() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse);
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            List<Task> tasks = manager.getHistory();
            String trueTasks = gson.toJson(tasks);
            String tasksFromServer = httpResponse.body();
            assertEquals(trueTasks, tasksFromServer, "Неверный возврат задач");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
