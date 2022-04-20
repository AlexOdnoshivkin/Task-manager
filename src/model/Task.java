package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private static int count = 0;
    private int taskId;
    private Status status;

    public Task(String name, String description) {
       this.name = name;
       this.description = description;
       this.taskId = count++;
    }

    public int getId(){
        return taskId;
    }

    public void setId(int taskId){
        this.taskId = taskId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus(){
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskId, status);
    }

    @Override
    public String toString() {
        return  this.getClass() + "{" +
                "name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status='" + status + '\'' +
                '}';
    }
}
