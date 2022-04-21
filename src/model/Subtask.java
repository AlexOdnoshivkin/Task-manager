package model;

public class Subtask extends Task{
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getHeadEpicId(){
        return epicId;
    }

    public void setHeadEpic(int epicId) {
        this.epicId = epicId;
    }


}
