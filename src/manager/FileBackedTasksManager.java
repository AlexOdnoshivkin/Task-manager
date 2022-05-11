package manager;

import exceptions.ManagerSaveException;
import manager.formatting.Deserialization;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import manager.formatting.Serialization;
import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTasksManager(String file) {
        super();
        if (Files.notExists(Paths.get(file))) {
            try {
                this.path = Files.createFile(Paths.get(file));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи файла менеджера");
            }
        } else {
            this.path = Paths.get(file);
        }
    }

    public static FileBackedTasksManager loadFromFile(String file) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedManager(file);
        try {

            String data = Files.readString(Paths.get(file));
            String[] splitData = data.split("\\n");
            int maxId = -1; // Переменная для максимального id для корректного создания новых задач
            for (int i = 1; i < splitData.length - 2; i++) { //создаём задачи из файла
                Task task = Deserialization.taskFromString(splitData[i]);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                fileBackedTasksManager.allTaskMap.put(task.getId(), task);
            }
            Task.setCount(maxId + 1);
            String savedHistory = splitData[splitData.length - 1]; // восстанавливаем историю из файла
            List<Integer> loadHistory = new ArrayList<>(Deserialization.historyFromString(savedHistory));
            for (Integer taskId : loadHistory) {
                Task task = fileBackedTasksManager.getTaskById(taskId);
                fileBackedTasksManager.historyManager.add(task);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла менеджера");
        }
        return fileBackedTasksManager;
    }

    private void save() {
        StringBuilder stringBuilder = new StringBuilder();
        String title = String.format("%-3s %-8s %-25s %-12s %-40s %-2s",
                "id,", "type,", "name,", "status,",
                "description,", "epic\n");
        stringBuilder.append(title); //добавляем шапку
        // добавляем все задачи по очереди: Task,  Epic, Subtask
        Collection<Task> tasks = new ArrayList<>(getAllTasks());

        for (Task task : tasks) {
            String taskString = Serialization.taskToString(task);
            stringBuilder.append(taskString).append("\n");
        }
        tasks = new ArrayList<>(getAllEpics());

        for (Task task : tasks) {
            String taskString = Serialization.taskToString(task);
            stringBuilder.append(taskString).append("\n");
        }
        tasks = new ArrayList<>(getAllSubTasks());

        for (Task task : tasks) {
            String taskString = Serialization.taskToString(task);
            stringBuilder.append(taskString).append("\n");
        }
        stringBuilder.append("\n"); // добавляем пустую строку
        String historyString = Serialization.historyToString(historyManager); // преобразуем историю в строку
        stringBuilder.append(historyString);
        String data = stringBuilder.toString();
        try (FileWriter fileWriter = new FileWriter(String.valueOf(path))) { // записываем файл
            fileWriter.write(data);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла менеджера");
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = allTaskMap.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        save();
        return task;
    }

    @Override
    public void createTask(Task task, Status status) {
        super.createTask(task, status);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, Status status, int headEpicId) {
        super.createSubtask(subtask, status, headEpicId);
        save();
    }

    @Override
    public void updateTask(Task task, int id, Status status) {
        super.updateTask(task, id, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        super.updateEpic(epic, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id, Status status) {
        super.updateSubtask(subtask, id, status);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }
}
