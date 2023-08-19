package tracker.service;

import tracker.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class InMemoryHistoryManager implements HistoryManager{

    private Node<Task> head;

    private Node<Task> tail;

    private int size = 0;

    private final List<Task> taskView = new ArrayList<>();

    private final HashMap<Long, Task> customLinkedList = new HashMap<>();

    public void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        size++;
    }

    public Task getLast() {
        final Node<Task> curTail = tail;
        if (curTail == null)
            throw new NoSuchElementException();
        taskView.add(tail.data);
        return curTail.data;
    }

    public int size() {
        return size;
    }

    @Override
    public void add(Task task) {
        linkLast(task);
        customLinkedList.put(task.getId(), task);
        if (taskView.contains(task)) removeNode(task);
        getLast();
    }

    @Override
    public void remove(Long id) {
        taskView.remove(customLinkedList.get(id));
    }

    public void removeNode(Task node) { //будет быстро удалять задачу из списка, если она там есть
        taskView.remove(node);
        size--;
    }

    @Override
    public List<Task> getHistory() {
        return taskView;
    }
}
