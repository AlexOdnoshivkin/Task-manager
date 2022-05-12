package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getFileBackedManager(Path path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
