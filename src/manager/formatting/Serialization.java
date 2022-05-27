package manager.formatting;

import manager.history.HistoryManager;
import model.Subtask;
import model.Task;
import model.TaskTypes;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Serialization {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static String taskToString(Task task) {
        if (task.getType() == TaskTypes.SUBTASK) {
            if (task.getStartTime() != null) {
                return String.format("%-3s %-8s %-10s %-12s %-25s %-5s %-20s %-10s", //форматируем строки задач для красоты
                        task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                        task.getDescription() + ",", ((Subtask) task).getHeadEpicId() + ",",
                        formatter.format(task.getStartTime()) + ",", task.getDuration().toMinutes());
            } else {
                return String.format("%-3s %-8s %-10s %-12s %-25s %-5s %-20s %-10s", //форматируем строки задач для красоты
                        task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                        task.getDescription() + ",", ((Subtask) task).getHeadEpicId() + ",", ",", "");
            }


        } else {
            if (task.getStartTime() != null) {
                return String.format("%-3s %-8s %-10s %-12s %-25s %-5s %-20s %-10s", //форматируем строки задач для красоты
                        task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                        task.getDescription() + ",", ",",
                        formatter.format(task.getStartTime()) + ",", task.getDuration().toMinutes());
            } else {
                return String.format("%-3s %-8s %-10s %-12s %-25s %-5s %-20s %-10s", //форматируем строки задач для красоты
                        task.getId() + ",", task.getType() + ",", task.getName() + ",", task.getStatus() + ",",
                        task.getDescription() + ",", ",", ",", "");
            }

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
