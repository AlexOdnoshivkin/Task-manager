package Manager.History;

import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
   private final List<Task> history;
   private final HashMap<Integer, Node> historyMap;
   private Node tail;
   private Node head;

    public InMemoryHistoryManager(){
        history = new ArrayList<>();
        historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        historyMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node cut = historyMap.get(id);

            if (cut.equals(head)) {//Если это первый Node
                if(head == tail) { //Проверяем, если этот Node единственный
                    head = null;
                    tail = null;
                }
                else {
                    head = cut.getNext(); //Записываем следующий Node как первый
                    head.setPrev(null);
                }
            } else if (cut.equals(tail)) { //Если это последний Node
                tail = cut.getPrev(); //Записываем предыдущий Node как последний
                tail.setNext(null);
            } else {
                Node prev = cut.getPrev(); //Получаем Node перед удаляемым
                Node next = cut.getNext(); //Получаем Node после удаляемого
                prev.setNext(next); //Записываем ссылку на следующий Node в предыдущий
                next.setPrev(prev);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        //LinkedList<Integer> linkedList
        history.clear();
        if (head != null) {
            Node node = head;
            while (node != null) {
                history.add(node.getTask());
                node = node.getNext();
            }
        }
        return history;
    }

    private void linkLast(Task task) {
           Node oldTail = tail;
           Node newNode = new Node(oldTail, task, null);
           tail = newNode;
           if (oldTail == null) {
               head = newNode; // Если последнего нода пока нет, записываем текущий как первый
           }
           else {
               tail.getPrev().setNext(tail); // получаем предыдущий нод и записываем в него текущий как следующий
           }
        }
}
