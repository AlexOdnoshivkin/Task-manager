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

public class EpicHandlerTest {
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
    void GetEpics_WhileCorrectResponse_ShouldReturnAllTasks() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse);
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            List<Epic> epics = manager.getAllEpics();
            String trueTasks = gson.toJson(epics);
            String tasksFromServer = httpResponse.body();
            assertEquals(trueTasks, tasksFromServer, "Неверный возврат задач");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetEpicById_WhileCorrectResponse_ShouldReturnTask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getId());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(epic1.getId());
            JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Epic.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void GetEpicById_WhileRequestIncorrectTypeTaskId_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=" + task1.getId());
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
    void GetTaskById_WhileRequestNonExistentId_ShouldReturnErrorCode() {
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
    void PostEpic_WhileCorrectResponse_ShouldCrateEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Epic newEpic = new Epic("NewEpic", "description");
        String json = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
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

        URI GetUri = URI.create("http://localhost:8080/tasks/epic?id=" + newEpic.getId());
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(newEpic.getId());
            JsonElement jsonElement = JsonParser.parseString(GetResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Epic.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void PostEpic_WhileIncorrectTaskType_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Task newEpic = new Task("Task1", "description1",
                LocalDateTime.of(2023, 5, 27, 1, 0), Duration.ofMinutes(120));
        String json = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
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
    void UpdateEpic_WhileEpicNotCreated_ShouldReturnErrorCode() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Epic newEpic = new Epic("Epic1", "description1");
        String json = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=100");
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
    void UpdateTask_WhileCorrectResponse_ShouldUpdateEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        Epic newEpic = new Epic("Epic1", "new description");
        newEpic.setId(epic1.getId());

        String json = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getId());
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

        URI GetUri = URI.create("http://localhost:8080/tasks/epic?id=" + newEpic.getId());
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            Task task = manager.getTaskById(newEpic.getId());
            JsonElement jsonElement = JsonParser.parseString(GetResponse.body());
            Task taskFromServer = gson.fromJson(jsonElement, Epic.class);
            assertEquals(task, taskFromServer, "Неверный возврат задачи");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteAllEpic_WhileCorrectResponse_ShouldDeleteAllEpics() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
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
        URI GetUri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest GetRequest = HttpRequest.newBuilder()
                .uri(GetUri)
                .GET()
                .build();
        try {
            HttpResponse<String> GetResponse = httpClient.send(GetRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(GetUri);
            assertEquals(200, GetResponse.statusCode(), "Неверный статус");
            JsonElement jsonElement = JsonParser.parseString(GetResponse.body());
            String tasksFromServer = GetResponse.body();
            List<Task> t = gson.fromJson(tasksFromServer, ArrayList.class);
            assertTrue(t.isEmpty());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void DeleteEpicById_WhileCorrectResponse_ShouldDeleteEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getId());
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
        URI GetUri = URI.create("http://localhost:8080/tasks/epic?id=" + epic1.getId());
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
    void DeleteEpicById_WhileIncorrectTaskType_ShouldResponseError() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=" + task1.getId());
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
    void DeleteEpicById_WhileRequestNonExistentId_ShouldResponseError() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=100");
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
