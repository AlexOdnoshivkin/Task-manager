package model;

public class Subtask extends Task{
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
        this.type = TaskTypes.SUBTASK;
    }

    public int getHeadEpicId(){
        return epicId;
    }

    public void setHeadEpic(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString() +  '\'' +
                "epicId=" + epicId +
                '}';
    }
}
