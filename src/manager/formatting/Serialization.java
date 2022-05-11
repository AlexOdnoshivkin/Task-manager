package manager.formatting;

import manager.history.HistoryManager;
import model.Subtask;
import model.Task;
import model.TaskTypes;

import java.util.List;

public class Serialization {

    public static String taskToString(Task task) {
        if (task.getType() == TaskTypes.SUBTASK) {
            return String.format("%-3s %-8s %-25s %-12s %-40s %-2s", //форматируем строки задач для красоты
                    task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                    task.getDescription() + ",", ((Subtask) task).getHeadEpicId());

        } else {
            return String.format("%-3s %-8s %-25s %-12s %-40s ",
                    task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                    task.getDescription() + ",");
        }

    }

    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder historyString = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            Task task = history.get(i);
            if (i == history.size() - 1) {
                historyString.append(task.getId());
            } else {
                historyString.append(task.getId()).append(",");
            }
        }
        return historyString.toString();
    }
}
