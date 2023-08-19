package tracker;

import tracker.service.TaskManager;
import tracker.task.EpicTask;
import tracker.service.Managers;
import tracker.task.SubTask;
import tracker.task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Запустить приложение", "Run tracker.Main", "NEW", 0);
        Task task2 = new Task("Переезд", "Собрать коробки",
                "NEW", 0);
        EpicTask epicTask = new EpicTask("Поездка", "Поездка на дачу",
                "NEW", 0);
        SubTask subTask1 = new SubTask("Уборка", "Сделать уборку перед отъездом",
                "NEW", 0, 3);
        SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
                "NEW", 0, 3);
        SubTask subTask3 = new SubTask("Билеты", "Купить билеты",
                "NEW", 0, 3);
        EpicTask epicTask2 = new EpicTask("Void", "voiding",
                "NEW", 1);

        //проверка наполнения задачами
        manager.addSomeTask(task1);
        manager.addSomeTask(task2);
        manager.addSomeTask(epicTask);
        manager.addSomeTask(subTask1);
        manager.addSomeTask(subTask2);
        manager.addSomeTask(subTask3);
        manager.addSomeTask(epicTask2);




        // Проверка вывода истории
        manager.getTaskById(4L);
        manager.getTaskById(5L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(4L);
        manager.getTaskById(7L);

        System.out.println("История просмотра задач:");
        for (Task task:manager.getHistory())
            System.out.println(task);


        manager.deleteTaskById(3L);
        System.out.println("История просмотра задач после удаления:");
        for (Task task:manager.getHistory())
            System.out.println(task);

        System.out.println("Запросы получения:");
        manager.getTasks();
        manager.getEpicTasks();
        manager.getSubTasks();

        //Проверка удаления задач
        manager.deleteTasks();
        manager.deleteEpicTasks();

        //Проверим что вернёт после удаления
        manager.getTasks();
        manager.getEpicTasks();
        manager.getSubTasks();
    }
}

