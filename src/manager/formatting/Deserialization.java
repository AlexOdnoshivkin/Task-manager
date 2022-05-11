package manager.formatting;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Deserialization {

    public static Task taskFromString(String value) {
        String[] taskFields = value.split(","); //Разбиваем строку и создаём задачи на основе данных
        for (int i = 0; i < taskFields.length; i++) {
            taskFields[i] = taskFields[i].strip();
        }
        TaskTypes types = TaskTypes.valueOf(taskFields[1]);
        int id = Integer.parseInt(taskFields[0]);
        Status status = Status.valueOf(taskFields[3]);
        switch (types) { // проверяем, какой класс задачи надо создать
            case TASK:
                Task task = new Task(taskFields[2], taskFields[4]);
                task.setId(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(taskFields[2], taskFields[4]);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(taskFields[2], taskFields[4]);
                String headEpicId = taskFields[5].strip();
                subtask.setHeadEpic(Integer.parseInt(headEpicId));
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
        }
        return null;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isBlank()) { //Проверяем, если строка истории пуста
            return history;
        } else {
            String[] tasksId = value.split(",");
            for (String taskId : tasksId) {
                history.add(Integer.parseInt(taskId));
            }
        }
        return history;
    }
}
