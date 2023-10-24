package tracker.service;

import tracker.task.EpicTask;
import tracker.task.SubTask;
import tracker.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {
    /**
     * Метод генерации уникального номера
     */
    long generateNewId();

    /**
     * Методы добаления задач
     */
    void addSomeTask(Task task) throws AddTaskException;

    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubTask(SubTask subTask);

    /**
     * Методы получения задач
     */
    void getTasks();

    void getEpicTasks();

    void getSubTasks();

    Object getTaskById(Long idTask);

    List<Task> getTasksList();

    List<EpicTask> getEpicTasksList();

    List<SubTask> getSubTaskForEpic(Long idEpicTask);

    /**
     * Методы удаления задач
     */
    void deleteTasks();

    void deleteEpicTasks();

    void deleteAllTasks();

    void deleteTaskById(Long idTask);

    //Методы для управления статусами
    //Для смены статуса будет передаваться новый объект с новым статусом
    void changeStatusTask(Task task);

    void changeStatusSubTask(SubTask subTask);

    void updateTask(Task task);

    //Метод для обновления статуса у эпик задачи
    void updateStatusEpicTask(EpicTask epicTask);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
