package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest {
    final File file = new File("test/testFiles/test1.csv");

    @Override
    @BeforeEach
    void addNewManager() throws IOException {
        file.delete();
        manager = FileBackedTasksManager.loadFromFile(file);
        super.addNewManager();
    }

    @AfterEach
    void clear() {
        file.delete();
    }

    @Test
    void fileBackedTaskManager_WhileFileNotExist_ShouldCreateFile() {
        final File file = new File("test/testFiles/test.csv");
        final FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);

        assertTrue(Files.exists(Path.of("test/testFiles/test.csv")), "Файл не создан");

        file.delete();
    }

    @Test
    void fileBackedTaskManager_loadFromFile_WhileFileIsExist_ShouldLoadData() {
        final File file = new File("test/testFiles/data.csv");
        final FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        final Task task = new Task("Task1", "Description task1",
                LocalDateTime.of(2022, 5, 27, 1, 0), Duration.ofMinutes(120));
        task.setId(1);
        task.setStatus(Status.NEW);

        final Epic epic = new Epic("Epic2", "Description epic2");
        epic.setId(2);
        epic.setStatus(Status.DONE);

        final Subtask subtask1 = new Subtask("SubTask2", "Description sub task3",
                LocalDateTime.of(2022, 5, 21, 11, 0), Duration.ofMinutes(90));
        subtask1.setId(3);
        subtask1.setStatus(Status.DONE);
        subtask1.setHeadEpic(epic.getId());
        epic.addSubTask(subtask1);

        assertNotNull(fileBackedTasksManager.getTaskById(1), "Задача не загружена");
        Task loadTask = fileBackedTasksManager.getTaskById(1);
        assertEquals(task, loadTask, "Ошибка загрузки задачи");

        assertNotNull(fileBackedTasksManager.getTaskById(2), "Эпик не загружен");
        assertNotNull(fileBackedTasksManager.getTaskById(3), "Подзадача не загружена");
        Epic loadEpic = (Epic) fileBackedTasksManager.getTaskById(2);
        Subtask loadSubtask = (Subtask) fileBackedTasksManager.getTaskById(3);
        assertEquals(epic, loadEpic, "Ошибка загрузки Эпика");
        assertEquals(subtask1, loadSubtask, "Ошибка загрузки подзадачи");
        List<Subtask> epicSubtasks = fileBackedTasksManager.getEpicSubtask(2);
        assertNotNull(epicSubtasks, "Пустой список подзадач у Эпика");
        assertTrue(epicSubtasks.contains(subtask1), "Подзадача не добавлена в Эпик");
        int headEpicId = loadSubtask.getHeadEpicId();
        assertEquals(2, headEpicId, "Эпик не добавлен в подзадачу");

        //Проверка загрузки истории
        List<Task> trueHistory = new ArrayList<>();
        trueHistory.add(task);
        trueHistory.add(epic);
        trueHistory.add(subtask1);
        List<Task> loadHistory = fileBackedTasksManager.getHistory();
        assertEquals(trueHistory.size(), loadHistory.size(), "Размер истории неверный");
        for (int i = 0; i < trueHistory.size(); i++) {
            Task fromTrueHistory = trueHistory.get(i);
            Task fromLoadHistory = loadHistory.get(i);
            assertEquals(fromLoadHistory, fromTrueHistory, "История записана неверно");
        }
    }
}


