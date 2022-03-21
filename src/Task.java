import java.util.Objects;

public class Task {
    private String name;
    private String Description;
    private static int count = 0;
    protected int taskId;
    protected String status;

    public Task(String name, String Description) {
       this.name = name;
       this.Description = Description;
       taskId = count;
       count++;
    }

    public int getId(){
        return taskId;
    }

    public void setId(int taskId){
        this.taskId = taskId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) && Objects.equals(Description, task.Description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Description, taskId, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", Description='" + Description + '\'' +
                ", taskId=" + taskId +
                ", status='" + status + '\'' +
                '}';
    }
}
