package Manager;
import Model.Status;
import Model.Task;
import Model.Epic;
import Model.Subtask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> allTaskMap;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.allTaskMap = new HashMap<>();
    }

    @Override
    public Collection<Task> getAllTask() {
        Collection<Task> epicCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Task.class){
                epicCollection.add(task);
            }
        }
        return epicCollection;
    }

    @Override
    public Collection<Epic> getAllEpic() {
        Collection<Epic> epicCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Epic.class){
                epicCollection.add((Epic)task);
            }
        }
        return epicCollection;
    }

    @Override
    public Collection<Subtask> getAllSubTask() {
        Collection<Subtask> epicCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Subtask.class){
                epicCollection.add((Subtask) task);
            }
        }
        return epicCollection;
    }

    @Override
    public void deleteAllTask() {
        ArrayList<Task> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Task.class){
                tasksForDelete.add(task);
            }
        }
        for (Task task : tasksForDelete) {
            allTaskMap.remove(task.getId());
        }
    }

    @Override
    public void deleteAllEpic() {
        ArrayList<Task> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() != Task.class){
                tasksForDelete.add(task);
            }
        }
        for (Task task : tasksForDelete) {
            allTaskMap.remove(task.getId());
        }
    }

    @Override
    public void deleteAllSubtask() {
        ArrayList<Task> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Subtask.class){
                tasksForDelete.add(task);
            }
        }
        for (Task task : tasksForDelete) {
            allTaskMap.remove(task.getId());
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = allTaskMap.get(taskId);
        historyManager.add(task);
        return task;
    }


    @Override
    public void createTask(Task task, Status status) {
        task.setStatus(status);
        allTaskMap.put(task.getId(), task);

    }

    @Override
    public void createEpic(Epic epic) {
        setEpicStatus(epic); // Вычисляем и устанавливаем статус эпика
        allTaskMap.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, Status status, int headEpicId) {
        subtask.setStatus(status);
        subtask.setHeadEpic(headEpicId);
        allTaskMap.put(subtask.getId(), subtask);
        Epic epic = (Epic) allTaskMap.get(headEpicId);
        epic.addSubTask(subtask); //Добавляем подзадачу в соответсвующий эпик
        setEpicStatus(epic); // Вычисляем и устанавливаем статус эпика

    }

    @Override
    public void updateTask(Task task, int id, Status status) {
        task.setStatus(status);
        if (allTaskMap.containsKey(id)) {
            task.setId(id);
            allTaskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        if (allTaskMap.containsKey(id)){
            Epic oldEpic = (Epic) allTaskMap.get(id);
            epic.setId(id);
            for (Subtask subtask : oldEpic.getSubTaskInEpic()){ //Записываем подзадачи из старого эпика в обновлённый
                epic.addSubTask(subtask);
            }
            setEpicStatus(epic);
            allTaskMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask (Subtask subtask, int id, Status status) {
        if (allTaskMap.containsKey(id)){
            Subtask subtask1 = (Subtask) allTaskMap.get(id);
            subtask.setId(id);
            subtask.setHeadEpic(subtask1.getHeadEpicId());
            subtask.setStatus(status);
            allTaskMap.put(id, subtask);
            Epic epic = (Epic) allTaskMap.get(subtask1.getHeadEpicId());
            epic.removeSubtask(subtask1);
            epic.addSubTask(subtask);
            setEpicStatus(epic);
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (allTaskMap.get(taskId).getClass() == Task.class) {
            allTaskMap.remove(taskId);
        }
        else if (allTaskMap.get(taskId).getClass() == Epic.class) {
            Epic epic = (Epic) allTaskMap.get(taskId);
            for (Subtask subtask : epic.getSubTaskInEpic()) { //Удаляем все подзадачи
                allTaskMap.remove(subtask.getId());
            }
            allTaskMap.remove(taskId);
        }
        else if (allTaskMap.get(taskId).getClass() == Subtask.class) {
            Subtask subtask = (Subtask) allTaskMap.get(taskId);
            Epic epic = (Epic) allTaskMap.get(subtask.getHeadEpicId());
            epic.removeSubtask(subtask); // Удаляем подзадачу из эпика
            setEpicStatus(epic); // Обновляем статус эпика
            allTaskMap.remove(taskId);
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtask(int epicId) {
        Epic epic = (Epic) allTaskMap.get(epicId);
        return epic.getSubTaskInEpic();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    private void setEpicStatus (Epic epic) { //Вычисляем и устанавливаем статус соотв. эпика
        boolean isSubtaskDone =true;
        boolean isSubTaskNew = true;
        if (epic.getSubTaskInEpic().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Subtask subtask : epic.getSubTaskInEpic()) {
            if (subtask.getStatus() != Status.NEW) {
                isSubTaskNew = false;
            }
            else if (subtask.getStatus() != Status.DONE) {
                isSubtaskDone = false;
            }
        }
        if (isSubTaskNew && !isSubtaskDone) {
            epic.setStatus(Status.NEW);
        }
        else if (!isSubTaskNew && !isSubtaskDone) {
            epic.setStatus(Status.IN_PROGRESS);
        }
        else  {
            epic.setStatus(Status.DONE);
        }
    }
}
