package manager.history;

import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class HistoryManagerTest {

    private static HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeAll
    static void getHistory_WhenHistoryIsEmpty() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();
        System.out.println(history.size());
        assertEquals(0, history.size(), "В истории что-то записано!");
    }

    @BeforeEach
    void addTaskToHistory() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task1", "Description1");
        task2 = new Task("Task2", "Description1");
        task3 = new Task("Task3", "Description1");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(),"История неверно записана");
    }

    @Test
    void ShouldDeleteDuplicate() {
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        List<Task> trueHistory = new ArrayList<>();
        trueHistory.add(task2);
        trueHistory.add(task3);
        trueHistory.add(task1);

        assertEquals(history.size(),trueHistory.size(), "Дубликат не удалился");
        for (int i = 0; i < trueHistory.size(); i++) {
            Task fromHistory = history.get(i);
            Task fromTrueHistory = trueHistory.get(i);
            assertEquals(fromTrueHistory, fromHistory, "История не совпадает");
        }
    }

    @Test
    void TaskShouldDeleteFromBegin() {
        List<Task> trueHistory = new ArrayList<>();

        trueHistory.add(task1);
        trueHistory.add(task2);
        trueHistory.add(task3);

        historyManager.remove(task1.getId());
        trueHistory.remove(0);
        List<Task> history = historyManager.getHistory();

        assertEquals(trueHistory.size(), history.size(), "История не удалилась");
        for (int i = 0; i < trueHistory.size(); i++) {
            Task fromHistory = history.get(i);
            Task fromTrueHistory = trueHistory.get(i);
            assertEquals(fromTrueHistory, fromHistory, "История не совпадает");
        }
    }

    @Test
    void TaskShouldDeleteFromCenter() {
        List<Task> trueHistory = new ArrayList<>();

        trueHistory.add(task1);
        trueHistory.add(task2);
        trueHistory.add(task3);

        historyManager.remove(task2.getId());
        trueHistory.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(trueHistory.size(), history.size(), "История не удалилась");
        for (int i = 0; i < trueHistory.size(); i++) {
            Task fromHistory = history.get(i);
            Task fromTrueHistory = trueHistory.get(i);
            assertEquals(fromTrueHistory, fromHistory, "История не совпадает");
        }
    }

    @Test
    void TaskShouldDeleteFromEnd() {
        List<Task> trueHistory = new ArrayList<>();

        trueHistory.add(task1);
        trueHistory.add(task2);
        trueHistory.add(task3);

        historyManager.remove(task3.getId());
        trueHistory.remove(2);
        List<Task> history = historyManager.getHistory();

        assertEquals(trueHistory.size(), history.size(), "История не удалилась");
        for (int i = 0; i < trueHistory.size(); i++) {
            Task fromHistory = history.get(i);
            Task fromTrueHistory = trueHistory.get(i);
            assertEquals(fromTrueHistory, fromHistory, "История не совпадает");
        }
    }


}
