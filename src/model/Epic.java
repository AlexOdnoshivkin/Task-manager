package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private final List<Subtask> subTaskInEpic;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskTypes.EPIC;
        subTaskInEpic = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTask(Subtask subtask){
        subTaskInEpic.add(subtask);
        calculateStartAndEndTime();
    }

    public List<Subtask> getSubTaskInEpic() {
        return subTaskInEpic;
    }

    public void removeSubtask(Subtask subtask) {
        subTaskInEpic.remove(subtask);
        calculateStartAndEndTime();
    }

    private void calculateStartAndEndTime() {
        for (Subtask sub : subTaskInEpic) { //Вычисляем времена начала, конца, а также продолжительности эпика
            if (sub.startTime != null) { //Проверка, указан ли в подзадаче startTime
                LocalDateTime subtaskEndTime = sub.getEndTime();
                if (startTime == null) { //Проверка, если startTime ещё не записан, записываем туда StartTime подзадачи
                    startTime = sub.startTime;
                } else {
                    if (startTime.isAfter(sub.startTime)) { //Если текущий startTime позже, чем у подзадачи, перезаписываем
                        startTime = sub.startTime;
                    }
                }
                if (endTime == null) { //Конструкция, аналогичная при расчёте startTime
                    endTime = subtaskEndTime;
                } else {
                    if (endTime.isBefore(subtaskEndTime)) {
                        endTime = subtaskEndTime;
                    }
                }
                if (duration == null) { //Вычисляем продолжительность Эпика
                    duration = sub.duration; // если null, записываем данные подзадачи
                } else {
                    duration = duration.plus(sub.duration); // иначе прибавляем
                }
            } else {
                duration = null; //Если у подзадачи не указан startTime, что и Duration тоже
                endTime = null; // В этом случае присваиваем у Эпика этим полям null, если хоть у одной задачи не
                // указаны данные времени
                if (subTaskInEpic.size() == 1) {
                    startTime = null;
                }
            }
        }
    }
}
