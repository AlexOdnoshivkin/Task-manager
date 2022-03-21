public class Subtask extends Task{
    int epicId;

    public Subtask(String name, String Description) {
        super(name, Description);
    }

    public String getStatus(){
        return status;
    }

    public int getHeadEpicId(){
        return epicId;
    }

    public void setHeadEpic(int epicId) {
        this.epicId = epicId;
    }


}
