package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int taskId);

    void createTask(Task task, Status status);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask, Status status, int headEpicId);

    void updateTask(Task task, int id, Status status);

    void updateEpic(Epic epic, int id);

    void updateSubtask (Subtask subtask, int id, Status status);

    void deleteTaskById(int taskId);

    ArrayList<Subtask> getEpicSubtask(int epicId);
    List<Task> getHistory();

}
