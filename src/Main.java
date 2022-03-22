import java.util.ArrayList;

public class Main {
    private static Manager manager  = new Manager();

    public static void main(String[] args) {
        //Создаём задачи всех типов
        createTasks();
        //Проверка вывода списка всех задач каждого типа
        System.out.println("Проверка вывода списка всех задач каждого типа");
        printAllTask();
        //Проверка получения задач по идентификатору
        checkTasksRetrievalById();
        //Получение списка подзадач Эпика
        getAllSubtasksOfEpc();
        //Проверка обновления задач
        checkTasksUpdate();
        //Удаление задач по идентификатору
        deleteTasksById();
        printAllTask();
        //Удаление всех задач определённого типа
        deleteTasksByType();
        printAllTask();
    }

    static void printAllTask (){
        for (Task taskForPrint : manager.getAllTask()) {
            System.out.println(taskForPrint);
        }
        for (Task epicForPrint : manager.getAllEpic()) {
            System.out.println(epicForPrint);
        }
        for (Task subtaskForPrint : manager.getAllSubTask()) {
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
        Subtask buyWallpaper = new Subtask("Купить обои", "Цвет обоев - изумрудный");

        manager.createTask(homework, "NEW");
        manager.createTask(dinner, "IN_PROGRESS");
        manager.createEpic(carFix);
        manager.createSubtask(carDiagnostic,"NEW", carFix.getId());
        manager.createSubtask(buyComponents,"NEW", carFix.getId());
        manager.createSubtask(fixTheMalfunction,"NEW", carFix.getId());
        manager.createEpic(roomRepair);
        manager.createSubtask(buyWallpaper,"NEW", roomRepair.getId());
    }

    static void checkTasksRetrievalById(){
        System.out.println("Проверка получения задач по идентификатору");
        System.out.println(manager.getTask(0));
        System.out.println(manager.getEpic(2));
        System.out.println(manager.getSubTask(5));
        System.out.println(manager.getSubTask(7));
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
        manager.updateTask(task, 0, "DONE");
        System.out.println(manager.getTask(0));
        System.out.println("Обновление эпика");
        Epic epic = new Epic("Починить машину", "Сломались тормоза");
        manager.updateEpic(epic, 2);
        System.out.println(manager.getEpic(2));
        System.out.println("Обновление подзадач и статуса эпика");
        Subtask carDiagnostic = new Subtask("Провести диагностику","На сервисе");
        manager.updateSubtask(carDiagnostic, 3, "DONE");
        Subtask buyComponents = new Subtask("Купить комплектующие", "На авторынке");
        manager.updateSubtask(buyComponents, 4, "DONE");
        Subtask fixTheMalfunction = new Subtask("Устранить неисправность", "");
        manager.updateSubtask(fixTheMalfunction, 5, "DONE");
        System.out.println(manager.getSubTask(3));
        System.out.println(manager.getSubTask(4));
        System.out.println(manager.getSubTask(5));
        System.out.println(manager.getEpic(2));
        System.out.println("");
    }

    static void deleteTasksById() {
        System.out.println("Удаляем задачи по идентификатору");
        manager.deleteTask(0);
        manager.deleteSubtask(4);
        manager.deleteEpic(2);
    }

    static void deleteTasksByType() {
        System.out.println("Удаляем все задачи определённого типа");
        manager.deleteAllTask();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
    }

}
