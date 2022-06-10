package manager;

import com.google.gson.*;
import http.KVTaskClient;
import http.TasksGson;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager{
    private final KVTaskClient kvTaskClient;
    private final Gson gson = TasksGson.gson;

    public HTTPTaskManager(String url) {
        super("files/data1.csv");
        kvTaskClient = new KVTaskClient(url);
        load();
    }

    private void load() {
        String loadTasks = kvTaskClient.load("Tasks");
        String loadHistory = kvTaskClient.load("History");

        if (!loadTasks.isEmpty()) {
            JsonElement jsonTasks = JsonParser.parseString(loadTasks);
            JsonArray TasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonElement : TasksArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String type = jsonObject.get("type").getAsString();
                if (type.equals(TaskTypes.TASK.toString())) {
                    Task task = gson.fromJson(jsonObject, Task.class);
                    createTask(task, task.getStatus());
                } else if (type.equals(TaskTypes.EPIC.toString())) {
                    Epic epic = gson.fromJson(jsonObject, Epic.class);
                    createEpic(epic);
                } else if (type.equals(TaskTypes.SUBTASK.toString())) {
                    Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
                    createSubtask(subtask, subtask.getStatus(), subtask.getHeadEpicId());
                }
            }
            if (!loadHistory.isEmpty()) {
                JsonElement jsonHistory = JsonParser.parseString(loadHistory);
                JsonArray jsonArray = jsonHistory.getAsJsonArray();
                for (JsonElement jsonElement : jsonArray) {
                    Integer taskId = gson.fromJson(jsonElement, Integer.class);
                    Task task = getTaskById(taskId);
                    historyManager.add(task);
                }
            }
        }
    }

    @Override
    protected void save() {
        List<Task> tasksList = new ArrayList<>(allTaskMap.values());
        kvTaskClient.put("Tasks", gson.toJson(tasksList));
        List<Integer> historyId = getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        kvTaskClient.put("History", gson.toJson(historyId));
    }
}

