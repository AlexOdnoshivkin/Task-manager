import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subTaskInEpic;

    public Epic(String name, String Description) {
        super(name, Description);
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
