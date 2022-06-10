package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void addNewManager() throws IOException {
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
    }

    @Test
    void getAllTasksTest() {
        List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество подзадач");
        assertTrue(tasks.contains(task1) && tasks.contains(task2), "Неверный возврат задач");
    }

    @Test
    void getAllEpicsTest() {
        List<Epic> epics = manager.getAllEpics();

        assertEquals(2, epics.size(), "Неверное количество подзадач");
        assertTrue(epics.contains(epic1) && epics.contains(epic2), "Неверный возврат задач");
    }

    @Test
    void getAllSubTasksTest() {
        List<Subtask> subtasks = manager.getAllSubTasks();

        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertTrue(subtasks.contains(subtask1) && subtasks.contains(subtask2), "Неверный возврат задач");
    }

    @Test
    void deleteAllTasksTest() {
        manager.deleteAllTasks();
        List<Task> tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Задачи не удалены");
    }

    @Test
    void deleteAllEpicsTest() {
        manager.deleteAllEpics();
        List<Epic> epics = manager.getAllEpics();

        assertEquals(0, epics.size(), "Эпики не удалены");
    }

    @Test
    void deleteAllSubtasksTest() {
        manager.deleteAllSubtasks();
        List<Subtask> subtasks = manager.getAllSubTasks();

        assertEquals(0, subtasks.size(), "Подзадачи не удалены");
    }

    @Test
    void getTaskByIdTest() {
        final int task1Id = task1.getId();
        final int task2Id = task2.getId();

        Task savedTask1 = manager.getTaskById(task1Id);
        Task savedTask2 = manager.getTaskById(task2Id);

        assertEquals(task1, savedTask1, "Получена неверная подзадача");
        assertEquals(task2, savedTask2, "Получена неверная подзадача");
    }

    @Test
    void createTaskTest() {
        final int taskId = task1.getId();
        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void createEpicTest() {
        final int epicId = epic1.getId();
        final Task savedEpic = manager.getTaskById(epicId);

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic1, savedEpic, "Задачи не совпадают");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(2, epics.size(), "Неверное количество задач");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают");

    }

    @Test
    void createSubtaskTest() {
        final int epicId = epic1.getId();
        final int subtaskId = subtask1.getId();
        final int headEpicId = subtask1.getHeadEpicId();
        final Task savedSubtask = manager.getTaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают");

        final List<Subtask> subtasks = manager.getAllSubTasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertEquals(headEpicId, epicId, "Неверный эпик");
    }

    @Test
    void updateTaskTest() {
        final int taskId = task1.getId();
        Task task2 = new Task("name1", "description2");
        manager.updateTask(task2, taskId, Status.NEW);
        final Task updatedTask = manager.getTaskById(taskId);
        final int updatedTaskId = updatedTask.getId();

        assertNotNull(updatedTask, "Задача не найдена");
        assertEquals(task2, updatedTask, "Задачи не совпадают");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(taskId, updatedTaskId, "Неверный Id обновлённой задачи");
    }

    @Test
    void updateEpicTest() {
        final int epicId = epic1.getId();
        Epic epic2 = new Epic("name1", "description2");
        manager.updateEpic(epic2, epicId);
        final int updatedEpicId = epic2.getId();
        final Task updatedEpic = manager.getTaskById(updatedEpicId);
        final int subtaskId = subtask1.getId();
        final Task subtaskInEpic = manager.getTaskById(subtaskId);

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epic2, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(2, epics.size(), "Неверное количество задач");
        assertEquals(epic2, epics.get(0), "Задачи не совпадают");
        assertEquals(subtask1, subtaskInEpic, "Подзадачи не перезаписываются");
    }

    @Test
    void updateSubtaskTest() {
        final int headEpicId = subtask1.getHeadEpicId();
        final int subtaskId = subtask1.getId();
        Subtask newSubtask = new Subtask("name1", "description2");
        manager.updateSubtask(newSubtask, subtaskId, Status.NEW);
        final Subtask updatedSubtask = (Subtask) manager.getTaskById(subtaskId);
        final int updatedHeadEpicId = updatedSubtask.getHeadEpicId();

        assertNotNull(updatedSubtask, "Подзадача не найдена");
        assertEquals(newSubtask, updatedSubtask, "Задачи не совпадают");

        final List<Subtask> subtasks = manager.getAllSubTasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertEquals(updatedSubtask, subtasks.get(0), "Задачи не совпадают");
        assertEquals(headEpicId, updatedHeadEpicId, "Неверно перезаписывается Эпик");
    }

    @Test
    void deleteTaskById_Test() {
        manager.deleteTaskById(task1.getId());
        assertNull(manager.getTaskById(task1.getId()), "Задача не удалилась");
        final List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не удалилась из списка");

        manager.deleteTaskById(subtask1.getId());
        assertNull(manager.getTaskById(subtask1.getId()), "Задача не удалилась");
        List<Subtask> subtasks = manager.getAllSubTasks();
        assertEquals(1, subtasks.size(), "Подзадача не удалилась из списка");

        manager.deleteTaskById(epic1.getId());
        assertNull(manager.getTaskById(epic1.getId()), "Задача не удалилась");
        final List<Epic> epics = manager.getAllEpics();
        subtasks = manager.getAllSubTasks();
        assertEquals(1, epics.size(), "Эпик не удалился из списка");
        assertEquals(1, subtasks.size(), "Не удалились подзадачи при удалении эпика");
    }

    @Test
    void getEpicSubtask_Test() {
        final int epicId = epic1.getId();

        List<Subtask> subtasksInEpic = manager.getEpicSubtask(epicId);
        assertEquals(1, subtasksInEpic.size(), "Неверное количество подзадач");
        assertTrue(subtasksInEpic.contains(subtask1),
                "Не совпадают подзадачи");
    }

    @Test
    void getHistoryTest() {
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не вернулась");
    }


    @Test
    void getPrioritizedTasksTest() {
        Set<Task> tasks = manager.getPrioritizedTasks();
        List<Task> prioritizedTasks = new ArrayList<>(tasks);
        List<Task> truePrioritizedTasks = new ArrayList<>();
        truePrioritizedTasks.add(task2);
        truePrioritizedTasks.add(subtask1);
        truePrioritizedTasks.add(task1);
        truePrioritizedTasks.add(subtask2);

        for (int i = 0; i < truePrioritizedTasks.size(); i++) {
            Task task = prioritizedTasks.get(i);
            Task trueTask = truePrioritizedTasks.get(i);
            assertEquals(trueTask, task, "Неверный вывод списка задач в порядке приоритета");
        }
    }

    @Test
    void shouldThrowExceptionWhenTasksOverlapInTime() {
        Task task3 = new Task("Task3", "description3",
                LocalDateTime.of(2022, 5, 27, 0, 0), Duration.ofMinutes(120));

        ManagerSaveException exception = assertThrows(ManagerSaveException.class,
                () -> manager.createTask(task3, Status.NEW));

        assertEquals("Задачи пересекаются по времени", exception.getMessage());
    }
}