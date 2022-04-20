import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final TaskManager manager = Managers.getDefault();


    public static void main(String[] args) {
        //Создаём задачи всех типов
        createTasks();
        //Проверка вывода списка всех задач каждого типа
        testHistory();
    }

    static void printAllTask (){
        for (Task taskForPrint : manager.getAllTasks()) {
            System.out.println(taskForPrint);
        }
        for (Task epicForPrint : manager.getAllEpics()) {
            System.out.println(epicForPrint);
        }
        for (Task subtaskForPrint : manager.getAllSubTasks()) {
            System.out.println(subtaskForPrint);
        }
        System.out.println("");
    }

    static void createTasks() {
        System.out.println("Создаём задачи всех типов...\n");
        Task homework = new Task("Java sprint 2 hw", "do this");
        Task dinner = new Task("Приготовить ужин", "Салат");
        Epic carFix = new Epic("Починить машину", "Сломалась подвеска");
        Subtask carDiagnostic = new Subtask("Провести диагностику","В гараже");
        Subtask buyComponents = new Subtask("Купить комплектующие", "В магазине Би-Би");
        Subtask fixTheMalfunction = new Subtask("Устранить неисправность", "Можно попросить помощи у Кирилла");
        Epic  roomRepair = new Epic("Сделать ремонт в комнате", "Завершить до лета");

        manager.createTask(homework, Status.NEW);
        manager.createTask(dinner,Status.IN_PROGRESS);
        manager.createEpic(carFix);
        manager.createSubtask(carDiagnostic,Status.NEW, carFix.getId());
        manager.createSubtask(buyComponents,Status.NEW, carFix.getId());
        manager.createSubtask(fixTheMalfunction,Status.NEW, carFix.getId());
        manager.createEpic(roomRepair);
    }

    static void checkTasksRetrievalById(){
        System.out.println("Проверка получения задач по идентификатору");
        System.out.println(manager.getTaskById(0));
        printHistory();
        System.out.println(manager.getTaskById(2));
        printHistory();
        System.out.println(manager.getTaskById(5));
        printHistory();
        System.out.println(manager.getTaskById(7));
        printHistory();
        System.out.println(manager.getTaskById(4));
        printHistory();
        System.out.println("");
    }

    static void getAllSubtasksOfEpc() {
        System.out.println("Проверка получения всех подзадач эпика");
        ArrayList<Subtask> subtasksInEpicList = new ArrayList<>(manager.getEpicSubtask(2));
        for (Subtask subtaskInEpic : subtasksInEpicList) {
            System.out.println(subtaskInEpic);
        }
        System.out.println("");
    }

    static void checkTasksUpdate() {
        System.out.println("Проверка обновления задач");
        System.out.println("Обновление обычной задачи");
        Task task = new Task("Java sprint 2 hw", "done!");
        manager.updateTask(task, 0, Status.DONE);
        System.out.println(manager.getTaskById(0));
        printHistory();
        System.out.println("Обновление эпика");
        Epic epic = new Epic("Починить машину", "Сломались тормоза");
        manager.updateEpic(epic, 2);
        System.out.println(manager.getTaskById(2));
        printHistory();
        System.out.println("Обновление подзадач и статуса эпика");
        Subtask carDiagnostic = new Subtask("Провести диагностику","На сервисе");
        manager.updateSubtask(carDiagnostic, 3, Status.DONE);
        Subtask buyComponents = new Subtask("Купить комплектующие", "На авторынке");
        manager.updateSubtask(buyComponents, 4, Status.DONE);
        Subtask fixTheMalfunction = new Subtask("Устранить неисправность", "");
        manager.updateSubtask(fixTheMalfunction, 5, Status.DONE);
        System.out.println(manager.getTaskById(3));
        printHistory();
        System.out.println(manager.getTaskById(4));
        printHistory();
        System.out.println(manager.getTaskById(5));
        printHistory();
        System.out.println(manager.getTaskById(2));
        printHistory();
    }

    static void deleteTasksById() {
        System.out.println("Удаляем задачи по идентификатору");
        manager.deleteTaskById(0);
        printHistory();
        manager.deleteTaskById(4);
        printHistory();
        manager.deleteTaskById(2);
        printHistory();
    }

    static void deleteTasksByType() {
        System.out.println("Удаляем все задачи определённого типа");
        manager.deleteAllTasks();
        printHistory();
        manager.deleteAllSubtasks();
        printHistory();
        manager.deleteAllEpics();
        printHistory();
    }

    static void printHistory() {
        System.out.println("История запросов:");
        List<Task> history = manager.getHistory();
        for (Task task : history){
            System.out.print(task.getId() + " ");
        }
        System.out.println("");
    }

    static void testHistory() {
        System.out.println("Проверка вывода истории запросов:");
        manager.getTaskById(2);
        printHistory();
        manager.getTaskById(0);
        printHistory();
        manager.getTaskById(3);
        printHistory();
        manager.getTaskById(1);
        printHistory();
        manager.getTaskById(6);
        printHistory();
        manager.getTaskById(5);
        printHistory();
        manager.getTaskById(0);
        printHistory();
        manager.getTaskById(2);
        printHistory();
        manager.getTaskById(1);
        printHistory();
        manager.getTaskById(4);
        printHistory();
        manager.getTaskById(3);
        printHistory();
        manager.getTaskById(5);
        printHistory();
        manager.getTaskById(6);
        printHistory();
        manager.getTaskById(0);
        printHistory();
        manager.getTaskById(6);
        printHistory();
        manager.getTaskById(3);
        printHistory();
        System.out.println("Удаляем задачу (id = 0)");
        manager.deleteTaskById(0);
        printHistory();
        System.out.println("Удаляем эпик (id = 2, id подзадач = 3, 4, 5)");
        manager.deleteTaskById(2);
        printHistory();
    }

}
