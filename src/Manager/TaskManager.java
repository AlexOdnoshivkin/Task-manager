package Manager;

import Model.Epic;
import Model.Subtask;
import Model.Task;

import java.util.ArrayList;
import java.util.Collection;

interface TaskManager {
    Collection<Task> getAllTask();

    Collection<Epic> getAllEpic();

    Collection<Subtask> getAllSubTask();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int taskId);

    void createTask(Task task, String status);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask, String status, int headEpicId);

    void updateTask(Task task, int id, String status);

    void updateEpic(Epic epic, int id);

    void updateSubtask (Subtask subtask, int id, String status);

    void deleteTaskById(int taskId);

    ArrayList<Subtask> getEpicSubtask(int epicId);

}
