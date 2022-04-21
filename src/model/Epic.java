package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subTaskInEpic;

    public Epic(String name, String description) {
        super(name, description);
        subTaskInEpic = new ArrayList<>();
    }

    public void addSubTask(Subtask subtask){
        subTaskInEpic.add(subtask);
    }

    public ArrayList<Subtask> getSubTaskInEpic() {
        return subTaskInEpic;
    }

    public void removeSubtask(Subtask subtask) {
        subTaskInEpic.remove(subtask);
    }
}
