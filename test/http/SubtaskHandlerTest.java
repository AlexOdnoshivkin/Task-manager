package http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHandlerTest {

    private HttpTaskServer httpTaskServer;
    private TaskManager manager;
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
            httpTaskServer = new HttpTaskServer(this.manager);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    void serverStop() {
        httpTaskServer.httpServer.stop(0);
        server.stop();
    }

    @Test
    void GetSubtasks_WhileCorrectResponse_ShouldReturnSubtasks() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse);
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            List<Subtask> epics = manager.getAllSubTasks();
            String trueTasks = gson.toJson(epics);
            String tasksFromServer = httpResponse.body();
            assertEquals(trueTasks, tasksFromServer, "Неверный возврат задач");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetSubtaskById_WhileCorrectResponse_ShouldReturnSubtask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1.getId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(subtask1.getId());
            JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Subtask.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetSubtaskById_WhileRequestIncorrectTypeTaskId_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=" + task1.getId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(400, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetSubtaskById_WhileRequestNonExistentId_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=100");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(404, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void PostSubtask_WhileCorrectResponse_ShouldCrateEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Subtask newSubtask = new Subtask("NewSubtask", "description",
                LocalDateTime.of(2022, 8, 2, 16, 0), Duration.ofMinutes(100));
        newSubtask.setHeadEpic(epic1.getId());
        String json = gson.toJson(newSubtask);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        URI GetUri = URI.create("http://localhost:8080/tasks/subtask?id=" + newSubtask.getId());
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(newSubtask.getId());
            JsonElement jsonElement = JsonParser.parseString(GetResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Subtask.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void PostSubtask_WhileIncorrectTaskType_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Task newEpic = new Task("Task1", "description1",
                LocalDateTime.of(2023, 5, 27, 1, 0), Duration.ofMinutes(120));
        String json = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(400, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void UpdateSubtask_WhileEpicNotCreated_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Subtask newSubtask = new Subtask("NewSubtask", "description",
                LocalDateTime.of(2022, 8, 2, 16, 0), Duration.ofMinutes(100));
        newSubtask.setHeadEpic(epic1.getId());
        String json = gson.toJson(newSubtask);
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=100");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(404, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void UpdateSubtask_WhileCorrectResponse_ShouldUpdateSubtask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Subtask newSubtask = new Subtask("NewSubtask", "description",
                LocalDateTime.of(2022, 8, 2, 16, 0), Duration.ofMinutes(100));
        newSubtask.setId(subtask1.getId());

        String json = gson.toJson(newSubtask);
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1.getId());
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        URI GetUri = URI.create("http://localhost:8080/tasks/subtask?id=" + newSubtask.getId());
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(newSubtask.getId());
            JsonElement jsonElement = JsonParser.parseString(GetResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Subtask.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteAllSubtask_WhileCorrectResponse_ShouldDeleteAllSubtask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(204, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        URI GetUri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            String tasksFromServer = GetResponse.body();
            List<Task> t = gson.fromJson(tasksFromServer, ArrayList.class);
            assertTrue(t.isEmpty());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteSubtaskById_WhileCorrectResponse_ShouldDeleteEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1.getId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(204, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        URI GetUri = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1.getId());
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(404, GetResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteSubtaskById_WhileIncorrectTaskType_ShouldResponseError() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=" + task1.getId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(400, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteSubtaskById_WhileRequestNonExistentId_ShouldResponseError() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=100");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(404, httpResponse.statusCode(), "Неверный статус");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
