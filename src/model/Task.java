package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private static int count = 0;
    protected LocalDateTime startTime;
    protected Duration duration;
    private int taskId;
    private Status status;
    protected TaskTypes type; // добавил для удобства работы с файлом

    public Task(String name, String description) {
       this.name = name;
       this.description = description;
       this.type = TaskTypes.TASK;
       this.taskId = count++;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.type = TaskTypes.TASK;
        this.taskId = count++;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getId(){
        return taskId;
    }

    public TaskTypes getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
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

    public static void setCount(int count) {
        Task.count = count;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration) && status == task.status && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, startTime, duration, taskId, status, type);
    }

    @Override
    public String toString() {
        if (startTime == null) {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", taskId=" + taskId +
                    ", status=" + status +
                    ", type=" + type +
                    '}';
        } else {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", startTime=" + startTime +
                    ", duration=" + duration +
                    ", taskId=" + taskId +
                    ", status=" + status +
                    ", type=" + type +
                    '}';
        }

    }

}
