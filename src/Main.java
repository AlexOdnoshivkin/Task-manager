import java.util.ArrayList;

public class Main {
    private static Manager manager  = new Manager();

    public static void main(String[] args) {

        //Создаём объекты
        Task task = new Task("Java sprint 2 hw", "do this");
        Task task2 = new Task("Приготовить ужин", "Салат");
        Epic epic = new Epic("Починить машину", "Сломалась подвеска");
        Epic epic1 = new Epic("Сделать ремонт в комнате", "Завершить до лета");
        Subtask subtask = new Subtask("Провести диагностику","В гараже");
        Subtask subtask1 = new Subtask("Купить комплектующие", "В магазине Би-Би");
        Subtask subtask2 = new Subtask("Устранить неисправность", "Можно попросить помощи у Кирилла");
        Subtask subtask3 = new Subtask("Купить обои", "Цвет обоев - изумрудный");
        //Проверка создания задач всех типов
        manager.createTask(task, "NEW");
        manager.createTask(task2, "IN_PROGRESS");
        manager.createEpic(epic);
        manager.createEpic(epic1);
        manager.createSubtask(subtask,"NEW", epic.getId());
        manager.createSubtask(subtask1, "NEW", epic.getId());
        manager.createSubtask(subtask2, "NEW", epic.getId());
        manager.createSubtask(subtask3,"NEW", epic1.getId());
        //Проверка вывода списка всех задач каждого типа
        System.out.println("Проверка вывода списка всех задач каждого типа");
        printAllTask();

        //Проверка получения задач по идентификатору
        System.out.println("Проверка получения задач по идентификатору");
        System.out.println(manager.getTask(0));
        System.out.println(manager.getEpic(2));
        System.out.println(manager.getSubTask(5));
        System.out.println("");
        //Получение списка подзадач Эпика
        System.out.println("Проверка получения всех подзадач эпика");
        ArrayList<Subtask> subtasksInEpicList = new ArrayList<>(manager.getEpicSubtask(2));
        for (Subtask subtaskInEpic : subtasksInEpicList) {
            System.out.println(subtaskInEpic);
        }
        System.out.println("");

        //Проверка обновления задач
        System.out.println("Проверка обновления задач");
        System.out.println("Обновление обычной задачи");
        task = new Task("Java sprint 2 hw", "done!");
        manager.updateTask(task, 0, "DONE");
        System.out.println(manager.getTask(0));
        System.out.println("Обновление эпика");
        epic = new Epic("Починить машину", "Сломались тормоза");
        manager.updateEpic(epic, 2);
        System.out.println(manager.getEpic(2));
        System.out.println("Обновление подзадач и статуса эпика");
        subtask = new Subtask("Провести диагностику","На сервисе");
        manager.updateSubtask(subtask, 3, "DONE");
        subtask1 = new Subtask("Купить комплектующие", "На авторынке");
        manager.updateSubtask(subtask1, 4, "DONE");
        subtask2 = new Subtask("Устранить неисправность", "");
        manager.updateSubtask(subtask2, 5, "DONE");
        System.out.println(manager.getEpic(2));

        //Удаление задач по идентификатору
        System.out.println("Удаляем задачи по идентификатору");
        manager.deleteTask(0);
        manager.deleteSubtask(4);
        manager.deleteEpic(2);
        printAllTask();

        //Удаление всех задач определённого типа
        System.out.println("Удаляем все задачи определённого типа");
        manager.deleteAllTask();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        printAllTask();

    }

    static void printAllTask (){
        ArrayList<Task> taskList = new ArrayList<>(manager.getAllTask());
        ArrayList<Epic> epicList = new ArrayList<>(manager.getAllEpic());
        ArrayList<Subtask> subtaskList = new ArrayList<>(manager.getAllSubTask());

        for (Task taskForPrint : taskList) {
            System.out.println(taskForPrint);
        }
        for (Task epicForPrint : epicList) {
            System.out.println(epicForPrint);
        }
        for (Task subtaskForPrint : subtaskList) {
            System.out.println(subtaskForPrint);
        }
        System.out.println("");
    }

}
