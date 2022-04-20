package model;

public class Subtask extends Task{
    private int epicId;

    public Subtask(String name, String Description) {
        super(name, Description);
    }

    public int getHeadEpicId(){
        return epicId;
    }

    public void setHeadEpic(int epicId) {
        this.epicId = epicId;
    }


}
