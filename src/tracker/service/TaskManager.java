package tracker.service;

import tracker.task.EpicTask;
import tracker.task.SubTask;
import tracker.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    long generateNewId();


    void addSomeTask(Task task);

    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubTask(SubTask subTask);

    void getTasks();

    void getEpicTasks();

    void getSubTasks();

    Object getTaskById(Long idTask);

    //Методы удаления задач
    void deleteTasks();

    void deleteEpicTasks();

    void deleteAllTasks();

    void deleteTaskById(Long idTask);

    //Методы для управления статусами
    //Для смены статуса будет передаваться новый объект с новым статусом
    void changeStatusTask(Task task);

    void changeStatusSubTask(SubTask subTask);

    //Метод для обновления статуса у эпик задачи
    void updateStatusEpicTask(EpicTask epicTask);

    List<Task> getHistory();
}
