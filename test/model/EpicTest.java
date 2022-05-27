package model;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    public void shouldStatusNewWhenNotHaveSubtasks() {
        Epic epic1 = new Epic("epic1", "description1");
        manager.createEpic(epic1);
        Status status = epic1.getStatus();
        assertEquals(status,Status.NEW);
    }

    @Test
    public void shouldStatusNewWhenAllSubtasksIsNew() {
        Epic epic1 = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1");
        Subtask subtask2 = new Subtask("subtask1", "description1");
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, Status.NEW, epic1.getId());
        manager.createSubtask(subtask2, Status.NEW, epic1.getId());
        Status status = epic1.getStatus();
        assertEquals(status,Status.NEW);
    }

    @Test
    public void shouldStatusDoneWhenAllSubtasksIsDone() {
        Epic epic1 = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1");
        Subtask subtask2 = new Subtask("subtask1", "description1");
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, Status.DONE, epic1.getId());
        manager.createSubtask(subtask2, Status.DONE, epic1.getId());
        Status status = epic1.getStatus();
        assertEquals(status,Status.DONE);
    }

    @Test
    public void shouldStatusInProgressWhenSubtasksIsDoneAndIsNEW() {
        Epic epic1 = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1");
        Subtask subtask2 = new Subtask("subtask1", "description1");
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, Status.DONE, epic1.getId());
        manager.createSubtask(subtask2, Status.NEW, epic1.getId());
        Status status = epic1.getStatus();
        assertEquals(status,Status.IN_PROGRESS);
    }

    @Test
    public void shouldStatusInProgressWhenSubtasksInProgress() {
        Epic epic1 = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1");
        Subtask subtask2 = new Subtask("subtask1", "description1");
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, Status.IN_PROGRESS, epic1.getId());
        manager.createSubtask(subtask2, Status.IN_PROGRESS, epic1.getId());
        Status status = epic1.getStatus();
        assertEquals(status,Status.IN_PROGRESS);
    }

    @Test
    public void shouldEpicEndTimeEqualsLastSubtaskEndTime() {
        Epic epic1 = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1", LocalDateTime.of(2022, Month.MAY, 26, 0, 0), Duration.ofHours(8));
        Subtask subtask2 = new Subtask("subtask1", "description1", LocalDateTime.of(2022, Month.MAY, 27, 0, 0), Duration.ofHours(10));
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, Status.NEW, epic1.getId());
        manager.createSubtask(subtask2, Status.NEW, epic1.getId());

        System.out.println(subtask2.getEndTime());
        System.out.println(epic1.getEndTime());
        assertEquals(epic1.getEndTime(), subtask2.getEndTime(), "Неверное время окончания эпика");
    }

}