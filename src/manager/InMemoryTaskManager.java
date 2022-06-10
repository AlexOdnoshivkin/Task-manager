package manager;

import exceptions.ManagerSaveException;
import manager.history.HistoryManager;
import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> allTaskMap;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> comparator = (t1, t2) -> {
        LocalDateTime time1 = t1.getStartTime();
        LocalDateTime time2 = t2.getStartTime();
        if (time1 == null) {
            time1 = LocalDateTime.MAX.minusMinutes(Integer.MAX_VALUE).minusNanos(t1.hashCode());
        }
        if (time2 == null) {
            time2 = LocalDateTime.MAX.minusMinutes(Integer.MAX_VALUE).minusNanos(t2.hashCode());
        }
        return time1.compareTo(time2);
    };

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    public InMemoryTaskManager() {
        this.allTaskMap = new HashMap<>();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> taskCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Task.class) {
                taskCollection.add(task);
            }
        }
        return taskCollection;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Epic.class) {
                epicCollection.add((Epic) task);
            }
        }
        return epicCollection;
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        List<Subtask> subtaskCollection = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Subtask.class) {
                subtaskCollection.add((Subtask) task);
            }
        }
        return subtaskCollection;
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Task> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Task.class) {
                tasksForDelete.add(task);
            }
        }
        for (Task task : tasksForDelete) {
            allTaskMap.remove(task.getId());
            historyManager.remove(task.getId()); //Удаляем задачи из истории
        }
    }

    @Override
    public void deleteAllEpics() {
        ArrayList<Task> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() != Task.class) {
                tasksForDelete.add(task);
            }
        }
        for (Task task : tasksForDelete) {
            allTaskMap.remove(task.getId());
            historyManager.remove(task.getId()); //Удаляем задачи из истории
        }
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Subtask> tasksForDelete = new ArrayList<>();
        for (Task task : allTaskMap.values()) {
            if (task.getClass() == Subtask.class) {
                tasksForDelete.add((Subtask) task);
            }
        }
        for (Subtask task : tasksForDelete) {
            allTaskMap.remove(task.getId());
            int headEpicId = task.getHeadEpicId();
            Epic headEpic = (Epic) allTaskMap.get(headEpicId);
            headEpic.removeSubtask(task);
            historyManager.remove(task.getId()); //Удаляем задачи из истории
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = allTaskMap.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }


    @Override
    public void createTask(Task task, Status status) {
        task.setStatus(status);
        allTaskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        checkingForIntersections();
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
        prioritizedTasks.add(subtask);
        checkingForIntersections();
    }

    @Override
    public void updateTask(Task task, int id, Status status) {
        task.setStatus(status);
        if (allTaskMap.containsKey(id)) {
            Task oldTask = allTaskMap.get(id);
            prioritizedTasks.remove(oldTask);
            task.setId(id);
            allTaskMap.put(task.getId(), task);
            prioritizedTasks.add(task);
            checkingForIntersections();
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        if (allTaskMap.containsKey(id)) {
            Epic oldEpic = (Epic) allTaskMap.get(id);
            epic.setId(id);
            for (Subtask subtask : oldEpic.getSubTaskInEpic()) { //Записываем подзадачи из старого эпика в обновлённый
                epic.addSubTask(subtask);
            }
            setEpicStatus(epic);
            allTaskMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, int id, Status status) {
        if (allTaskMap.containsKey(id)) {
            Subtask subtask1 = (Subtask) allTaskMap.get(id);
            prioritizedTasks.remove(subtask1);
            subtask.setId(id);
            subtask.setHeadEpic(subtask1.getHeadEpicId());
            subtask.setStatus(status);
            allTaskMap.put(id, subtask);
            Epic epic = (Epic) allTaskMap.get(subtask1.getHeadEpicId());
            epic.removeSubtask(subtask1);
            epic.addSubTask(subtask);
            setEpicStatus(epic);
            prioritizedTasks.add(subtask);
            checkingForIntersections();
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (allTaskMap.get(taskId) == null) {
            return;
        }
        Task task = allTaskMap.get(taskId);
        if (task.getClass() == Task.class) {
            historyManager.remove(taskId);
            allTaskMap.remove(taskId);
            prioritizedTasks.remove(task);
        } else if (task.getClass() == Epic.class) {
            Epic epic = (Epic) allTaskMap.get(taskId);
            for (Subtask subtask : epic.getSubTaskInEpic()) { //Удаляем все подзадачи
                historyManager.remove(subtask.getId());
                allTaskMap.remove(subtask.getId());

            }
            historyManager.remove(taskId);
            allTaskMap.remove(taskId);
            prioritizedTasks.remove(task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) allTaskMap.get(taskId);
            Epic epic = (Epic) allTaskMap.get(subtask.getHeadEpicId());
            epic.removeSubtask(subtask); // Удаляем подзадачу из эпика
            setEpicStatus(epic); // Обновляем статус эпика
            historyManager.remove(taskId);
            allTaskMap.remove(taskId);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Subtask> getEpicSubtask(int epicId) {
        Epic epic = (Epic) allTaskMap.get(epicId);
        return epic.getSubTaskInEpic();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    private void setEpicStatus(Epic epic) { //Вычисляем и устанавливаем статус соотв. эпика
        boolean isSubtaskDone = true;
        boolean isSubTaskNew = true;
        if (epic.getSubTaskInEpic().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Subtask subtask : epic.getSubTaskInEpic()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                isSubtaskDone = false;
                isSubTaskNew = false;
            } else if (subtask.getStatus() == Status.NEW) {
                isSubtaskDone = false;
            } else if (subtask.getStatus() == Status.DONE) {
                isSubTaskNew = false;
            }
        }
        if (isSubTaskNew && !isSubtaskDone) {
            epic.setStatus(Status.NEW);
        } else if (!isSubTaskNew && !isSubtaskDone) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.DONE);
        }
    }

    private void checkingForIntersections() throws ManagerSaveException {
        for (Task task : prioritizedTasks) {
            Task higherTask = prioritizedTasks.higher(task);
            if (higherTask == null) {
                return;
            }
            if (higherTask.getStartTime() == null || task.getStartTime() == null) {
                return;
            }
            if (task.getEndTime().isAfter(higherTask.getStartTime())) {
                throw new ManagerSaveException("Задачи пересекаются по времени");
            }
        }
    }
}
