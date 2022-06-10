import http.HttpTaskServer;
import http.KVServer;
import manager.HTTPTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class Main {
    /* Оставил пока методы тестирования в классе main, у меня тут уже были написаны необходимые методы, и не хочется
       загромождать класс FileBackedTasksManager, если это необходимо - перенесу */
    public static void main(String[] args) throws IOException {
       /* KVServer server = new KVServer();
        server.start();

        TaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8078");
        Task task1 = new Task("Task1", "description1",
                LocalDateTime.of(2022, 5, 27, 1, 0), Duration.ofMinutes(120));
        httpTaskManager.createTask(task1, Status.NEW);
        Task task2 = new Task("Task2", "description2",
                LocalDateTime.of(2022, 4, 12, 14, 30), Duration.ofMinutes(200));
        httpTaskManager.createTask(task2, Status.NEW);
        Epic epic1 = new Epic("Epic1", "description1");
        httpTaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic2", "description2");
        httpTaskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask1", "description1",
                LocalDateTime.of(2022, 5, 26, 1, 0), Duration.ofMinutes(900));
        Subtask subtask2 = new Subtask("Subtask2", "description2");
        httpTaskManager.createSubtask(subtask1, Status.NEW, epic1.getId());
        httpTaskManager.createSubtask(subtask2, Status.NEW, epic2.getId());
        httpTaskManager.getTaskById(task1.getId());
        httpTaskManager.getTaskById(task2.getId());
        httpTaskManager.getTaskById(epic2.getId());
        httpTaskManager.getTaskById(task1.getId());
        httpTaskManager.getTaskById(subtask1.getId());

        System.out.println("Сохранённая история");
        for (Task task : httpTaskManager.getHistory()) {
            System.out.println(task.getId() + " ");
        }


        List<Task> httpTasks = httpTaskManager.getAllTasks();
        System.out.println(httpTasks);
        TaskManager httpTaskManager1 = new HTTPTaskManager("http://localhost:8078");
        System.out.println(httpTaskManager1.getAllTasks());
        System.out.println("Загруженная история");
        for (Task task : httpTaskManager1.getHistory()) {
            System.out.println(task.getId() + " ");
        }

        HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager1);*/

    }
}


