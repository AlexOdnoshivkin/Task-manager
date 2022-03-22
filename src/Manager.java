import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    public Collection<Task> getAllTask() {
        return taskMap.values();
    }

    public Collection<Epic> getAllEpic() {
        return epicMap.values();
    }

    public Collection<Subtask> getAllSubTask() {
        return subtaskMap.values();
    }

    public void deleteAllTask() {
        taskMap.clear();
    }

    public void deleteAllEpic() {
        subtaskMap.clear();
        epicMap.clear();
    }

    public void deleteAllSubtask() {
        subtaskMap.clear();
    }

    public Task getTask(int taskId) {
        return taskMap.get(taskId);
    }

    public Task getEpic(int taskId) {
        return epicMap.get(taskId);
    }

    public Task getSubTask(int taskId) {
        return subtaskMap.get(taskId);
    }

    public void createTask(Task task, String status) {
        task.setStatus(status);
        taskMap.put(task.getId(), task);

    }

    public void createEpic(Epic epic) {
        setEpicStatus(epic); // Вычисляем и устанавливаем статус эпика
        epicMap.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask, String status, int headEpicId) {
        subtask.setStatus(status);
        subtask.setHeadEpic(headEpicId);
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(headEpicId);
        epic.addSubTask(subtask); //Добавляем подзадачу в соответсвующий эпик
        setEpicStatus(epic); // Вычисляем и устанавливаем статус эпика

    }

    public void updateTask(Task task, int id, String status) {
        task.setStatus(status);
        if (taskMap.containsKey(id)) {
            task.setId(id);
            taskMap.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic, int id) {
        if (epicMap.containsKey(id)){
            Epic oldEpic = epicMap.get(id);
            epic.setId(id);
            for (Subtask subtask : oldEpic.getSubTaskInEpic()){ //Записываем подзадачи из старого эпика в обновлённый
                epic.addSubTask(subtask);
            }
            setEpicStatus(epic);
            epicMap.put(epic.getId(), epic);
        }
    }

    public void updateSubtask (Subtask subtask, int id, String status) {
        if (subtaskMap.containsKey(id)){
            Subtask subtask1 = subtaskMap.get(id);
            subtask.setId(id);
            subtask.setHeadEpic(subtask1.getHeadEpicId());
            subtask.setStatus(status);
            subtaskMap.put(id, subtask);
            Epic epic = epicMap.get(subtask1.getHeadEpicId());
            epic.removeSubtask(subtask1);
            epic.addSubTask(subtask);
            setEpicStatus(epic);
        }
    }

    public void deleteTask (int taskId) {
            taskMap.remove(taskId);
    }

    public void deleteEpic (int epicId) {
            Epic epic = epicMap.get(epicId);
            for (Subtask subtask : epic.getSubTaskInEpic()) { //Удаляем все подзадачи
                subtaskMap.remove(subtask.getId());
            }
            epicMap.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
            Subtask subtask = subtaskMap.get(subtaskId);
            Epic epic = epicMap.get(subtask.getHeadEpicId());
            epic.removeSubtask(subtask); // Удаляем подзадачу из эпика
            setEpicStatus(epic); // Обновляем статус эпика
            subtaskMap.remove(subtaskId);
    }

    public ArrayList<Subtask> getEpicSubtask(int epicId) {
        Epic epic = epicMap.get(epicId);
        return epic.getSubTaskInEpic();
    }


    private void setEpicStatus (Epic epic) { //Вычисляем и устанавливаем статус соотв. эпика
        boolean isSubtaskDone =true;
        boolean isSubTaskNew = true;
        if (epic.getSubTaskInEpic().isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        for (Subtask subtask : epic.getSubTaskInEpic()) {
            if (!subtask.getStatus().equals("NEW")) {
                isSubTaskNew = false;
            }
            else if (!subtask.getStatus().equals("DONE")) {
                isSubtaskDone = false;
            }
        }
        if (isSubTaskNew && !isSubtaskDone) {
            epic.setStatus("NEW");
        }
        else if (!isSubTaskNew && !isSubtaskDone) {
            epic.setStatus("IN_PROGRESS");
        }
        else  {
            epic.setStatus("DONE");
        }
    }
}
